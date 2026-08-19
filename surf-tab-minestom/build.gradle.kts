plugins {
    id("dev.slne.surf.api.gradle.minestom")
}

surfMinestomApi {
    withCoreMinestom()
    withSurfRedis()
}

dependencies {
    api(project(":surf-tab-core-client"))

    compileOnly(libs.luckperms.api)
    compileOnly("dev.slne.surf.playtime:surf-playtime-api-minestom:+")
    compileOnly("dev.slne.surf.clan:surf-clan-api:+")
    compileOnly("dev.slne.surf.content.creator:surf-content-creator-api:+")
}
