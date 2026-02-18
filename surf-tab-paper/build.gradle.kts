import dev.slne.surf.surfapi.gradle.util.registerRequired
import dev.slne.surf.surfapi.gradle.util.registerSoft

plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
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
        registerSoft("surf-vanish-paper")
        registerSoft("surf-playtime-paper")
    }
}

dependencies {
    compileOnly(libs.mini.placeholders)
    compileOnly(libs.mini.placeholders.kotlin)
    compileOnly(libs.luckperms.api)
    api(project(":surf-tab-api"))
    implementation("dev.slne.surf.vanish:surf-vanish-api-redis:1.21.11-1.0.9-SNAPSHOT")
    compileOnly("dev.slne.surf.vanish:surf-vanish-api:1.21.11-1.0.9-SNAPSHOT")
    compileOnly("dev.slne.surf.playtime:surf-playtime-api:1.21.11-1.1.8-SNAPSHOT")
    compileOnly("dev.slne.surf.clan:surf-clan-api:1.21.11-1.3.0-SNAPSHOT")
}