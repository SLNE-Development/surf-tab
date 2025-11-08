plugins {
    id("dev.slne.surf.surfapi.gradle.core")
}

surfCoreApi {
    withCloudCommon()
}

dependencies {
    api(project(":surf-tab-api"))
}