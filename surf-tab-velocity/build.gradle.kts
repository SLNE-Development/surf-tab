plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

repositories {
    maven("https://repo.william278.net/releases/")
}

surfVelocityApi {
    withSurfRedis()
}

velocityPluginFile {
    main = "dev.slne.surf.tab.velocity.VelocityMain"
    authors = listOf("red")

    pluginDependencies {
        register("miniplaceholders")
        register("luckperms")
        register("surf-clan-velocity") {
            optional = true
        }
    }
}

dependencies {
    compileOnly(libs.mini.placeholders)
    compileOnly(libs.mini.placeholders.kotlin)
    compileOnly(libs.luckperms.api)
    api(project(":surf-tab-api"))
    compileOnly(files("libs/surf-clan-api-1.21.11-1.3.0-SNAPSHOT.jar"))
    implementation("dev.slne.surf.vanish:surf-vanish-api-redis:1.21.11-1.0.7-SNAPSHOT")
}