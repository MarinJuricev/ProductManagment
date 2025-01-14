plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinxSerialization)
}

group = "org.product.inventory.server"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += listOf("-opt-in=kotlin.io.encoding.ExperimentalEncodingApi")
    }
}

dependencies {
    implementation(projects.shared) {
        exclude(group = libs.firebase.firestore.get().group)
    }
    implementation(project.dependencies.platform(libs.ktor.bom))
    implementation(libs.bundles.ktor.server)
    implementation(libs.postgresql)
    implementation(libs.h2)
    implementation(libs.logback.classic)
    implementation(libs.firebase.admin)
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger)
    implementation(libs.arrow.core)
}