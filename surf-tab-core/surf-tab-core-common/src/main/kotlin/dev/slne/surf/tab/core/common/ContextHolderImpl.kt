package dev.slne.surf.tab.core.common

import com.google.auto.service.AutoService
import dev.slne.surf.tab.api.InternalContextHolder
import dev.slne.surf.tab.api.InternalTablistApi
import org.springframework.context.ApplicationContext

@OptIn(InternalTablistApi::class)
@AutoService(InternalContextHolder::class)
class ContextHolderImpl : InternalContextHolder {
    override lateinit var context: ApplicationContext

    companion object {
        val instance = InternalContextHolder.instance as ContextHolderImpl
    }
}