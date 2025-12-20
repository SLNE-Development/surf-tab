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
        register("miniplaceholders") {
            optional = true
        }
    }
}

dependencies {
    compileOnly(libs.mini.placeholders)
    compileOnly(libs.mini.placeholders.kotlin)
    compileOnly(libs.luckperms.api)

    api(project(":surf-tab-api"))
}