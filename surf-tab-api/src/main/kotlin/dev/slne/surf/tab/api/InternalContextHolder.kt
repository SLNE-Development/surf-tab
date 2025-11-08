package dev.slne.surf.tab.api

import dev.slne.surf.surfapi.core.api.util.requiredService
import org.springframework.context.ApplicationContext

@InternalTablistApi
interface InternalContextHolder {
    val context: ApplicationContext

    companion object {
        val instance = requiredService<InternalContextHolder>()
    }
}