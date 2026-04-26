import dev.slne.surf.api.gradle.util.registerRequired
import dev.slne.surf.api.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.api.gradle.paper-plugin")
}

surfPaperPluginApi {
    mainClass("dev.slne.surf.tab.paper.PaperMain")
    generateLibraryLoader(false)
    foliaSupported(true)

    withSurfRedis()
    withCorePaper()

    authors.add("red")

    serverDependencies {
        registerRequired("LuckPerms")
        registerRequired("MiniPlaceholders")
        registerSoft("surf-playtime-paper")
        registerSoft("surf-clan-paper")
        registerSoft("surf-content-creator-paper")
        registerSoft("surf-vanish-paper")
    }
}

dependencies {
    compileOnly(libs.mini.placeholders)
    compileOnly(libs.mini.placeholders.kotlin)
    compileOnly(libs.luckperms.api)
    api(project(":surf-tab-api"))
    compileOnly("dev.slne.surf.vanish:surf-vanish-api:+")
    compileOnly("dev.slne.surf.playtime:surf-playtime-api-paper:+")
    compileOnly("dev.slne.surf.clan:surf-clan-api:+")
    compileOnly("dev.slne.surf.content.creator:surf-content-creator-api:+")
}