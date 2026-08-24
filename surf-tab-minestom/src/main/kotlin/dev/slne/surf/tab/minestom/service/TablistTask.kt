package dev.slne.surf.tab.minestom.service

import dev.slne.minestom.lobby.api.coroutine.minestomAsyncScope
import dev.slne.surf.api.core.util.runAtFixedRate
import dev.slne.surf.tab.core.client.service.tablistService
import kotlinx.coroutines.Job
import kotlin.time.Duration.Companion.seconds

val tablistTask = TablistTask()

class TablistTask {
    @Volatile
    private var task: Job? = null

    fun startTask() {
        task = minestomAsyncScope.runAtFixedRate(1.seconds, taskName = "tab-header-footer") {
            tablistService.sendAdditionsToAll()
        }
    }

    fun cancelTask() {
        task?.cancel()
        task = null
    }
}
