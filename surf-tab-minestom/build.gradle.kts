plugins {
    id("dev.slne.surf.api.gradle.minestom")
}

surfMinestomApi {
    withCoreMinestom()
    withSurfRedis()
}

minestomPluginFile {
    main = "dev.slne.surf.tab.minestom.TabMinestomPlugin"
    authors = listOf("red")

    pluginDependencies {
        register("surf-redis-minestom")
        register("surf-clan-minestom")
        register("surf-playtime-minestom")
        register("surf-content-creator-minestom")
    }
}

dependencies {
    api(project(":surf-tab-core-client"))

    compileOnly(libs.luckperms.api)
    compileOnly("dev.slne.surf.playtime:surf-playtime-api-minestom:+")
    compileOnly("dev.slne.surf.clan:surf-clan-api:+")
    compileOnly("dev.slne.surf.content.creator:surf-content-creator-api:+")
}
