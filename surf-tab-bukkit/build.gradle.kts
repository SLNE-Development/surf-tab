plugins {
    id("dev.slne.surf.surfapi.gradle.paper-plugin")
}

surfPaperPluginApi {
    withCloudClientPaper()
    mainClass("dev.slne.surf.tab.bukkit.PaperMain")
    bootstrapper("dev.slne.surf.tab.bukkit.PaperBootstrap")

    authors.add("red")
}

dependencies {
    api(project(":surf-tab-core:surf-tab-core-client"))
}
