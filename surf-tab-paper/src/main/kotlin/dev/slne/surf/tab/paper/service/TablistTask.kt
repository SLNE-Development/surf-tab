package dev.slne.surf.tab.paper.service

import dev.slne.surf.tab.core.client.service.tablistService
import dev.slne.surf.tab.paper.plugin
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import java.util.concurrent.TimeUnit

val tablistTask = TablistTask()

class TablistTask {
    lateinit var task: ScheduledTask

    fun startTask() {
        task = Bukkit.getAsyncScheduler().runAtFixedRate(plugin, {
            tablistService.sendAdditionsToAll()
        }, 0L, 1L, TimeUnit.SECONDS)
    }

    fun cancelTask() {
        if (::task.isInitialized) {
            task.cancel()
        }
    }
}
