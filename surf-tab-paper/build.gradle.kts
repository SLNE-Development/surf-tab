import dev.slne.surf.surfapi.gradle.util.registerRequired

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
    }
}

dependencies {
    compileOnly(libs.mini.placeholders)
    compileOnly(libs.mini.placeholders.kotlin)
    compileOnly(libs.luckperms.api)
    api(project(":surf-tab-api"))
    implementation("dev.slne.surf.vanish:surf-vanish-api-redis:1.21.11-1.0.7-SNAPSHOT")
}