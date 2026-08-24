package dev.slne.surf.tab.paper.service

import com.github.shynixn.mccoroutine.folia.scope
import dev.slne.surf.api.core.util.runAtFixedRate
import dev.slne.surf.tab.core.client.service.tablistService
import dev.slne.surf.tab.paper.plugin
import kotlinx.coroutines.Job
import kotlin.time.Duration.Companion.seconds

val tablistTask = TablistTask()

class TablistTask {
    @Volatile
    private var task: Job? = null

    fun startTask() {
        task = plugin.scope.runAtFixedRate(1.seconds, taskName = "tab-header-footer") {
            tablistService.sendAdditionsToAll()
        }
    }

    fun cancelTask() {
        task?.cancel()
        task = null
    }
}
