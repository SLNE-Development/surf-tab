plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

repositories {
    maven("https://repo.william278.net/releases/")
}

velocityPluginFile {
    main = "dev.slne.surf.tab.velocity.VelocityMain"
    authors = listOf("red")


    pluginDependencies {
        register("miniplaceholders")
        register("luckperms")
    }
}

dependencies {
    compileOnly(libs.mini.placeholders)
    compileOnly(libs.mini.placeholders.kotlin)
    compileOnly(libs.luckperms.api)
    
    implementation("dev.slne:surf-redis:1.0.0-SNAPSHOT")

    api(project(":surf-tab-api"))
}