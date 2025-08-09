plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

repositories {
    maven("https://repo.william278.net/releases/")
}

velocityPluginFile {
    main = "dev.slne.surf.tab.velocity.VelocityMain"
    id = "surf-tab-velocity"
    name = "surf-tab-velocity"
    authors = listOf("red")
}

dependencies {
    api(project(":surf-tab-core"))
    compileOnly(libs.papi.proxy)
}