package dev.slne.surf.tab.core.client.service

import dev.slne.surf.tab.core.client.service.TablistUpdateReason
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class TablistAdditionsTest {

    private companion object {
        const val CLOCK_HEADER = "<time>"
        const val COUNT_HEADER = "<players_online> / <players_max>"
        const val STATIC_FOOTER = "<gray>on <server>"
        const val PER_PLAYER_FOOTER = "<player_name>"
    }

    @Test
    fun `a player is shown their header and footer as soon as they join`() {
        val tablist = TestTablist(COUNT_HEADER, STATIC_FOOTER)
        val alice = tablist.join(TestPlayer("alice"))

        assertEquals("1 / 1000", alice.header())
        assertEquals("on lobby-1", alice.footer())
    }

    @Test
    fun `an update that changes nothing renders nothing and sends nothing`() {
        val tablist = TestTablist(CLOCK_HEADER, STATIC_FOOTER)
        val alice = tablist.join(TestPlayer("alice"))

        val renders = tablist.renderer.rendered.size
        val packets = alice.received.size

        repeat(5) { tablist.additions.invalidateAll(TablistUpdateReason.CLOCK) }

        assertEquals(renders, tablist.renderer.rendered.size, "the clock did not move")
        assertEquals(packets, alice.received.size, "so nothing had to be sent")
    }

    @Test
    fun `a minute passing updates a header that asks what time it is`() {
        val tablist = TestTablist(CLOCK_HEADER, STATIC_FOOTER)
        val alice = tablist.join(TestPlayer("alice"))
        assertEquals("09:05", alice.header())

        tablist.time = "09:06"
        tablist.additions.invalidateAll(TablistUpdateReason.CLOCK)

        assertEquals("09:06", alice.header())
    }

    @Test
    fun `a minute passing leaves a header that never asks what time it is alone`() {
        val tablist = TestTablist(COUNT_HEADER, STATIC_FOOTER)
        val alice = tablist.join(TestPlayer("alice"))

        val renders = tablist.renderer.rendered.size
        val packets = alice.received.size

        tablist.time = "09:06"
        tablist.additions.invalidateAll(TablistUpdateReason.CLOCK)

        assertEquals(renders, tablist.renderer.rendered.size)
        assertEquals(packets, alice.received.size)
    }

    @Test
    fun `only the template that asked for the clock is rendered again when it moves`() {
        val tablist = TestTablist(CLOCK_HEADER, STATIC_FOOTER)
        tablist.join(TestPlayer("alice"))

        val headerRenders = tablist.renderer.rendersOf(CLOCK_HEADER)
        val footerRenders = tablist.renderer.rendersOf(STATIC_FOOTER)

        tablist.time = "09:06"
        tablist.additions.invalidateAll(TablistUpdateReason.CLOCK)

        assertEquals(headerRenders + 1, tablist.renderer.rendersOf(CLOCK_HEADER))
        assertEquals(footerRenders, tablist.renderer.rendersOf(STATIC_FOOTER), "the footer has no clock in it")
    }

    @Test
    fun `everybody is shown the new number of players when somebody joins`() {
        val tablist = TestTablist(COUNT_HEADER, STATIC_FOOTER)
        val alice = tablist.join(TestPlayer("alice"))
        val bob = tablist.join(TestPlayer("bob"))

        assertEquals("2 / 1000", alice.header())
        assertEquals("2 / 1000", bob.header())
    }

    @Test
    fun `everybody is shown the new number of players when somebody leaves`() {
        val tablist = TestTablist(COUNT_HEADER, STATIC_FOOTER)
        val alice = tablist.join(TestPlayer("alice"))
        val bob = tablist.join(TestPlayer("bob"))

        tablist.leave(bob)

        assertEquals("1 / 1000", alice.header())
    }

    @Test
    fun `a leave announced before it happened is corrected by the update that follows`() {
        val tablist = TestTablist(COUNT_HEADER, STATIC_FOOTER)
        val alice = tablist.join(TestPlayer("alice"))
        val bob = tablist.join(TestPlayer("bob"))
        assertEquals("2 / 1000", alice.header())

        // Both platforms announce a leave while the player is still being counted, so the update
        // that announcement starts can read the number as it was a moment ago.
        tablist.duringUpdate = {
            tablist.duringUpdate = {}
            tablist.players -= bob
        }
        tablist.additions.invalidateAll(TablistUpdateReason.PLAYER_COUNT)

        assertEquals("1 / 1000", alice.header(), "the number that was already stale must not stand")
    }

    @Test
    fun `the number of players a server has room for is shown as it is`() {
        val tablist = TestTablist(COUNT_HEADER, STATIC_FOOTER)
        tablist.maxPlayers = 1234
        val alice = tablist.join(TestPlayer("alice"))

        assertEquals("1 / 1234", alice.header())
    }

    @Test
    fun `one render is shared by everybody when nothing in it can differ per player`() {
        val tablist = TestTablist(COUNT_HEADER, STATIC_FOOTER)
        val alice = tablist.join(TestPlayer("alice"))
        val bob = tablist.join(TestPlayer("bob"))

        assertSame(
            alice.received.last().first,
            bob.received.last().first,
            "a header without anything audience specific is rendered once for the whole server"
        )
    }

    @Test
    fun `a placeholder that differs per player is still rendered per player`() {
        val tablist = TestTablist(COUNT_HEADER, PER_PLAYER_FOOTER)
        val alice = tablist.join(TestPlayer("alice"))
        val bob = tablist.join(TestPlayer("bob"))

        assertEquals("alice", alice.footer())
        assertEquals("bob", bob.footer())
        assertNotSame(alice.received.last().second, bob.received.last().second)
    }

    @Test
    fun `a component that came out the same is not sent again`() {
        // The footer is rendered per player and therefore built freshly every time, so this is the
        // rendered component being compared rather than the same instance being recognised.
        val tablist = TestTablist(COUNT_HEADER, PER_PLAYER_FOOTER)
        val alice = tablist.join(TestPlayer("alice"))

        val packets = alice.received.size
        val renders = tablist.renderer.rendersOf(PER_PLAYER_FOOTER)

        repeat(3) { tablist.additions.invalidateAll(TablistUpdateReason.UnknownPlaceholders) }

        assertTrue(
            tablist.renderer.rendersOf(PER_PLAYER_FOOTER) > renders,
            "the fallback has to render, because it cannot know whether anything moved"
        )
        assertEquals(packets, alice.received.size, "but what it rendered was the same, so nothing was sent")
    }

    @Test
    fun `updates asked for while one is running collapse into a single further update`() {
        val tablist = TestTablist(COUNT_HEADER, STATIC_FOOTER)
        tablist.join(TestPlayer("alice"))

        var updates = 0
        tablist.beforeUpdate = {
            updates++
            if (updates == 1) repeat(5) { tablist.additions.invalidateAll(TablistUpdateReason.PLAYER_COUNT) }
        }

        tablist.additions.invalidateAll(TablistUpdateReason.PLAYER_COUNT)

        assertEquals(2, updates, "five requests during one update are one follow-up, not five")
    }

    @Test
    fun `an update for something no template asks about never starts`() {
        val tablist = TestTablist("<red>hello", "<blue>bye")
        val alice = tablist.join(TestPlayer("alice"))

        var updates = 0
        tablist.beforeUpdate = { updates++ }

        tablist.additions.invalidateAll(TablistUpdateReason.CLOCK)
        tablist.additions.invalidateAll(TablistUpdateReason.PLAYER_COUNT)
        tablist.additions.invalidateAll(TablistUpdateReason.UnknownPlaceholders)

        assertEquals(0, updates)
        assertEquals(1, alice.received.size, "only what they were shown when they joined")
    }

    @Test
    fun `a reload always updates, whatever the templates say`() {
        val tablist = TestTablist("<red>hello", "<blue>bye")
        val alice = tablist.join(TestPlayer("alice"))

        tablist.templates = TablistTemplates.analyze("<red>welcome", "<blue>bye", tablist.miniMessage)
        tablist.additions.invalidateAll(TablistUpdateReason.Configuration)

        assertEquals("welcome", alice.header())
    }

    @Test
    fun `a player who leaves while an update runs is not a problem`() {
        val tablist = TestTablist(COUNT_HEADER, PER_PLAYER_FOOTER)
        val alice = tablist.join(TestPlayer("alice"))
        val bob = tablist.join(TestPlayer("bob"))

        val bobsPackets = bob.received.size

        // Bob is taken off the server after the update chose who to walk, so it renders and sends for
        // somebody who is already gone.
        tablist.duringUpdate = {
            tablist.duringUpdate = {}
            tablist.players -= bob
            tablist.additions.forget(bob.uuid)
        }

        tablist.time = "09:06"
        tablist.additions.invalidateAll(TablistUpdateReason.Configuration)

        assertTrue(alice.received.size >= 1)
        assertTrue(
            bob.received.size >= bobsPackets,
            "sending to somebody who left is harmless, it must not throw"
        )
    }

    @Test
    fun `somebody who reconnects is shown their header and footer again`() {
        val tablist = TestTablist(COUNT_HEADER, STATIC_FOOTER)
        val uuid = UUID.randomUUID()

        val first = tablist.join(TestPlayer("alice", uuid))
        tablist.leave(first)

        val second = tablist.join(TestPlayer("alice", uuid))

        assertEquals(1, second.received.size, "nothing about the player they were is held against them")
        assertEquals("1 / 1000", second.header())
    }

    @Test
    fun `a player nobody reported leaving is dropped by the next update`() {
        val tablist = TestTablist(CLOCK_HEADER, STATIC_FOOTER)
        val alice = tablist.join(TestPlayer("alice"))
        val ghost = TestPlayer("ghost")

        tablist.players += ghost
        tablist.additions.invalidateAll(TablistUpdateReason.Configuration)
        assertEquals(1, ghost.received.size)

        // Taken off the server without anything saying so, then walked over by an update.
        tablist.players -= ghost
        tablist.additions.invalidateAll(TablistUpdateReason.Configuration)

        // Back again, with nothing about them remembered, so they are sent to even though what they
        // are shown did not change in the meantime.
        tablist.players += ghost
        tablist.additions.invalidateAll(TablistUpdateReason.Configuration)

        assertEquals(2, ghost.received.size)
        assertEquals(1, alice.received.size, "alice was shown the same thing throughout")
    }

    @Test
    fun `joins, leaves and updates arriving on many threads at once settle correctly`() {
        val tablist = TestTablist(COUNT_HEADER, PER_PLAYER_FOOTER)
        val failures = CopyOnWriteArrayList<Throwable>()
        val updates = Executors.newFixedThreadPool(4)

        tablist.updateRunner = { block ->
            updates.execute {
                try {
                    runBlocking { block() }
                } catch (throwable: Throwable) {
                    failures += throwable
                }
            }
        }

        val everybody = List(24) { TestPlayer("player-$it") }
        val staying = everybody.take(8)
        val comingAndGoing = everybody.drop(8)

        try {
            staying.forEach(tablist::join)

            val start = CountDownLatch(1)
            val done = CountDownLatch(comingAndGoing.size + 4)

            for (player in comingAndGoing) {
                Thread {
                    start.await()
                    repeat(20) {
                        tablist.join(player)
                        tablist.leave(player)
                    }
                    done.countDown()
                }.start()
            }

            repeat(4) {
                Thread {
                    start.await()
                    repeat(200) { tablist.additions.invalidateAll(TablistUpdateReason.PLAYER_COUNT) }
                    done.countDown()
                }.start()
            }

            start.countDown()
            assertTrue(done.await(60, TimeUnit.SECONDS), "the threads should finish")

            // Everything that was asked for either started an update or was folded into one that had
            // not read the player count yet, so the numbers have to come out right once it is quiet.
            val expected = "${staying.size} / 1000"
            val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(60)

            while (staying.any { it.received.last().first.plain() != expected } &&
                System.nanoTime() < deadline
            ) {
                Thread.onSpinWait()
            }

            assertEquals(emptyList<Throwable>(), failures, "no update may fail")
            for (player in staying) {
                assertEquals(expected, player.header(), "${player.name} was left behind")
                assertEquals(player.name, player.footer(), "${player.name} was shown somebody else")
            }
        } finally {
            updates.shutdownNow()
        }
    }

    @Test
    fun `an update built from an older snapshot cannot undo a newer one`() {
        val tablist = TestTablist(CLOCK_HEADER, STATIC_FOOTER)
        val alice = tablist.join(TestPlayer("alice"))

        tablist.generationOverride = 1_000
        tablist.time = "09:06"
        tablist.additions.invalidateAll(TablistUpdateReason.CLOCK)
        assertEquals("09:06", alice.header())

        // An update that started earlier, and only now gets as far as sending, is carrying a minute
        // that has already been overtaken.
        tablist.generationOverride = 999
        tablist.time = "09:05"
        tablist.additions.invalidatePlayer(alice)

        assertEquals("09:06", alice.header(), "the older snapshot must not win")
    }
}
