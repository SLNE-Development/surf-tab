package dev.slne.surf.tab.velocity.hook

import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.tab.velocity.plugin
import io.github.miniplaceholders.api.Expansion
import io.github.miniplaceholders.api.provider.ExpansionProvider
import io.github.miniplaceholders.api.provider.LoadRequirement
import io.github.miniplaceholders.api.types.Platform
import net.kyori.adventure.identity.Identity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.tag.Tag
import kotlin.jvm.optionals.getOrNull

class VelocityMiniPlaceholderExpansion : ExpansionProvider {
    override fun provideExpansion() =
        Expansion
            .builder("tab")
            .author("red")
            .version("1.0.0")
            .audiencePlaceholder("server") { audience, _, _ ->
                Tag.selfClosingInserting {
                    audience.get(Identity.UUID).getOrNull()?.let {
                        plugin.proxy.getPlayer(it).getOrNull()?.let { player ->
                            buildText {
                                variableValue(
                                    player.currentServer.getOrNull()?.server?.serverInfo?.name
                                        ?: "N/A"
                                )
                            }
                        }
                    } ?: Component.empty()
                }
            }
            .globalPlaceholder("players_online") { _, _ ->
                Tag.selfClosingInserting {
                    buildText {
                        variableValue(plugin.proxy.allPlayers.size.toString())
                    }
                }
            }
            .globalPlaceholder("players_max") { _, _ ->
                Tag.selfClosingInserting {
                    buildText {
                        variableValue(plugin.proxy.configuration.showMaxPlayers.toString())
                    }
                }
            }
            .build()

    override fun loadRequirement(): LoadRequirement = LoadRequirement.platform(Platform.VELOCITY)
}