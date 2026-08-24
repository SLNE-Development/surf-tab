package dev.slne.surf.tab.core.client.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

class UpdateCoalescerTest {

    private val key: UUID = UUID.randomUUID()

    @Test
    fun `a request runs one update`() {
        val updated = ArrayList<String>()
        val coalescer = UpdateCoalescer<String>(::runHere) { updated.add(it) }

        coalescer.request(key, "first")

        assertEquals(listOf("first"), updated)
    }

    @Test
    fun `requests made while an update runs collapse into a single further update`() {
        var updates = 0
        lateinit var coalescer: UpdateCoalescer<String>

        coalescer = UpdateCoalescer(::runHere) {
            updates++

            if (updates == 1) {
                repeat(5) { coalescer.request(key, "target") }
            }
        }

        coalescer.request(key, "target")

        assertEquals(2, updates)
    }

    @Test
    fun `a follow-up update uses the newest target, not the one that started the loop`() {
        val updated = ArrayList<String>()
        lateinit var coalescer: UpdateCoalescer<String>

        coalescer = UpdateCoalescer(::runHere) { target ->
            updated.add(target)

            when (updated.size) {
                // A target replaced while its update runs - a player who reconnects as a different
                // object - has to be the one the follow-up pass applies to.
                1 -> {
                    coalescer.request(key, "replaced once")
                    coalescer.request(key, "replaced twice")
                }

                2 -> coalescer.request(key, "replaced again")
            }
        }

        coalescer.request(key, "original")

        assertEquals(listOf("original", "replaced twice", "replaced again"), updated)
    }

    @Test
    fun `a target requested mid-loop is updated even when nothing follows it`() {
        val updated = ArrayList<String>()
        lateinit var coalescer: UpdateCoalescer<String>

        coalescer = UpdateCoalescer(::runHere) { target ->
            updated.add(target)
            if (updated.size == 1) coalescer.request(key, "reconnected")
        }

        coalescer.request(key, "original")

        assertEquals(listOf("original", "reconnected"), updated)
    }

    @Test
    fun `a key is free to be updated again once its updates are done`() {
        var updates = 0
        val coalescer = UpdateCoalescer<String>(::runHere) { updates++ }

        coalescer.request(key, "target")
        coalescer.request(key, "target")

        assertEquals(2, updates)
    }

    @Test
    fun `keys do not hold each other up`() {
        val updated = ArrayList<String>()
        val coalescer = UpdateCoalescer<String>(::runHere) { updated.add(it) }

        coalescer.request(UUID.randomUUID(), "first")
        coalescer.request(UUID.randomUUID(), "second")

        assertEquals(listOf("first", "second"), updated)
    }

    @Test
    fun `an update that fails does not stop the key from being updated again`() {
        var updates = 0
        val coalescer = UpdateCoalescer<String>(::runHere) {
            updates++
            if (updates == 1) error("the first update fails")
        }

        assertThrows(IllegalStateException::class.java) { coalescer.request(key, "target") }
        coalescer.request(key, "target")

        assertEquals(2, updates)
    }

    @Test
    fun `a target requested during a failed update is still applied`() {
        val updated = ArrayList<String>()
        lateinit var coalescer: UpdateCoalescer<String>

        coalescer = UpdateCoalescer(::runHere) { target ->
            updated.add(target)

            if (updated.size == 1) {
                coalescer.request(key, "reconnected")
                error("the first update fails")
            }
        }

        assertThrows(IllegalStateException::class.java) { coalescer.request(key, "original") }

        assertEquals(listOf("original", "reconnected"), updated)
    }

    @Test
    fun `the newest state always wins when many threads ask at once`() {
        val threads = 8
        val requestsPerThread = 200

        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        val requested = AtomicInteger()
        val appliedRequestCount = AtomicInteger()
        val appliedTarget = AtomicInteger()
        val updates = AtomicInteger()

        try {
            val coalescer = UpdateCoalescer<Int>({ block -> scope.launch { block() } }) { target ->
                updates.incrementAndGet()
                // Suspending widens the window in which further requests arrive, which is what the
                // collapsing has to survive.
                delay(1)
                appliedTarget.set(target)
                appliedRequestCount.set(requested.get())
            }

            val start = CountDownLatch(1)
            val done = CountDownLatch(threads)

            repeat(threads) {
                Thread {
                    start.await()
                    repeat(requestsPerThread) {
                        coalescer.request(key, requested.incrementAndGet())
                    }
                    done.countDown()
                }.start()
            }

            start.countDown()
            assertTrue(done.await(30, TimeUnit.SECONDS), "the requesting threads should finish")

            // Every request either starts an update or is folded into one that has not applied yet,
            // so an update has to run after the last request was made.
            val deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(30)
            while (appliedRequestCount.get() != requested.get() && System.nanoTime() < deadline) {
                Thread.onSpinWait()
            }

            val total = threads * requestsPerThread
            assertEquals(total, requested.get())
            assertEquals(
                requested.get(),
                appliedRequestCount.get(),
                "the last request must not be lost"
            )
            assertTrue(
                appliedTarget.get() in 1..total,
                "the applied target must be one that was requested, was " + appliedTarget.get()
            )
            assertTrue(
                updates.get() <= requested.get(),
                "collapsing must never cause more updates than requests"
            )
        } finally {
            scope.cancel()
        }
    }

    /**
     * Runs the update loop on the calling thread, so the collapsing can be observed without any
     * timing involved.
     */
    private fun runHere(block: suspend () -> Unit) {
        runBlocking { block() }
    }
}
