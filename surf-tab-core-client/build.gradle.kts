plugins {
    id("dev.slne.surf.api.gradle.core")
}

surfCoreApi {
    withCoreCommon()
    withSurfRedis()
}

dependencies {
    api(project(":surf-tab-api"))

    compileOnly(libs.mini.placeholders)
    compileOnly(libs.luckperms.api)
    compileOnly("dev.slne.surf.playtime:surf-playtime-api-common:+")
    compileOnly("dev.slne.surf.clan:surf-clan-api:+")
    compileOnly("dev.slne.surf.content.creator:surf-content-creator-api:+")
}

sourceSets.test {
    compileClasspath += sourceSets.main.get().compileClasspath
    runtimeClasspath += sourceSets.main.get().compileClasspath
}
