plugins {
    id("dev.slne.surf.surfapi.gradle.velocity")
}

repositories {
    maven("https://repo.william278.net/releases/")
}

surfVelocityApi {
    withCloudClientVelocity()
}

velocityPluginFile {
    main = "dev.slne.surf.tab.velocity.VelocityMain"
    authors = listOf("red")
}

dependencies {
    api(project(":surf-tab-core:surf-tab-core-client"))
    compileOnly(libs.mini.placeholders)
    compileOnly(libs.mini.placeholders.kotlin)
    compileOnly(libs.luckperms.api)
}