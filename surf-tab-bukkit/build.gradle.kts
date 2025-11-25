plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    withCloudClientPaper()
    mainClass("dev.slne.surf.tab.bukkit.BukkitMain")
    bootstrapper("dev.slne.surf.tab.bukkit.BukkitBootstrap")

    authors.add("red")
}

dependencies {
    api(project(":surf-tab-core:surf-tab-core-client"))
}
