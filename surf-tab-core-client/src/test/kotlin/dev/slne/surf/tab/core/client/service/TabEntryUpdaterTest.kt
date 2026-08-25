package dev.slne.surf.tab.core.client.service

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import net.kyori.adventure.text.Component
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class TabEntryUpdaterTest {

    @Test
    fun `each nonessential part can fail without discarding the other parts`() = runBlocking {
        val cases = listOf(
            FailureCase(TabEntryPart.ORDER, "VnameCLA", 0, 2),
            FailureCase(TabEntryPart.VANISH, "nameCLA", 7, 2),
            FailureCase(TabEntryPart.CLAN, "VnameLA", 7, 1),
            FailureCase(TabEntryPart.LIVE, "VnameCA", 7, 2),
            FailureCase(TabEntryPart.AFK, "VnameCL", 7, 2)
        )

        for (case in cases) {
            val shown = ArrayList<Pair<String, Int>>()
            val failures = ArrayList<TabEntryPart>()
            val updater = TabEntryUpdater<String>(
                baseName = { Component.text(it) },
                order = { case.value(TabEntryPart.ORDER, 7) },
                vanishTag = { case.value(TabEntryPart.VANISH, Component.text("V")) },
                clanTag = { case.value(TabEntryPart.CLAN, Component.text("C")) },
                liveTag = { case.value(TabEntryPart.LIVE, Component.text("L")) },
                afkTag = { case.value(TabEntryPart.AFK, Component.text("A")) },
                show = { _, name, order -> shown += name.plain() to order },
                onPartFailure = { part, _, _ -> failures += part }
            )

            updater.update("name")

            assertEquals(case.expectedWrites, shown.size, "writes after ${case.part} failed")
            assertEquals(
                case.expectedName to case.expectedOrder,
                shown.last(),
                "entry after ${case.part} failed"
            )
            assertEquals(listOf(case.part), failures)
        }
    }

    @Test
    fun `a stuck clan lookup leaves a base entry and a later refresh still succeeds`() = runBlocking {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        val baseShown = CompletableDeferred<Unit>()
        val clanStarted = CompletableDeferred<Unit>()
        val clanCancelled = CompletableDeferred<Unit>()
        val refreshed = CompletableDeferred<Unit>()
        val shown = CopyOnWriteArrayList<String>()
        var clanLookups = 0

        try {
            val updater = TabEntryUpdater<String>(
                baseName = { Component.text(it) },
                order = { 7 },
                vanishTag = { Component.empty() },
                clanTag = {
                    if (clanLookups++ == 0) {
                        clanStarted.complete(Unit)
                        try {
                            awaitCancellation()
                        } finally {
                            clanCancelled.complete(Unit)
                        }
                    } else {
                        Component.text("C")
                    }
                },
                liveTag = { Component.empty() },
                afkTag = { Component.empty() },
                show = { _, name, _ ->
                    shown += name.plain()
                    if (shown.size == 1) baseShown.complete(Unit)
                    if (name.plain() == "nameC") refreshed.complete(Unit)
                },
                onPartFailure = { _, _, _ -> }
            )
            val coalescer = UpdateCoalescer<String>(
                runUpdates = { block -> scope.launch { block() } },
                updateTimeout = 50.milliseconds,
                update = updater::update
            )
            val key = UUID.randomUUID()

            coalescer.request(key, "name")
            withTimeout(1.seconds) { baseShown.await() }
            withTimeout(1.seconds) { clanStarted.await() }
            assertEquals("name", shown.single(), "the base entry must precede clan enrichment")

            withTimeout(1.seconds) { clanCancelled.await() }
            coalescer.request(key, "name")
            withTimeout(1.seconds) { refreshed.await() }

            assertEquals("nameC", shown.last())
        } finally {
            scope.cancel()
        }
    }

    @Test
    fun `a clan timeout keeps the already applied base entry`() = runBlocking {
        val shown = ArrayList<String>()
        val failures = ArrayList<TabEntryPart>()
        val updater = TabEntryUpdater<String>(
            baseName = { Component.text(it) },
            order = { 7 },
            vanishTag = { Component.text("V") },
            clanTag = { awaitCancellation() },
            liveTag = { Component.text("L") },
            afkTag = { Component.text("A") },
            show = { _, name, _ -> shown += name.plain() },
            clanTimeout = 50.milliseconds,
            onPartFailure = { part, _, _ -> failures += part }
        )

        withTimeout(1.seconds) { updater.update("name") }

        assertEquals(listOf("VnameLA"), shown)
        assertEquals(listOf(TabEntryPart.CLAN), failures)
    }

    private data class FailureCase(
        val part: TabEntryPart,
        val expectedName: String,
        val expectedOrder: Int,
        val expectedWrites: Int
    ) {
        fun <T> value(requestedPart: TabEntryPart, value: T): T {
            if (part == requestedPart) error("$part failed")
            return value
        }
    }
}
