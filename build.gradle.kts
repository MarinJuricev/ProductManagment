plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.buildKonfig) apply false
    alias(libs.plugins.detekt)
}

buildscript {
    dependencies {
        classpath(libs.mokoResources.generator)
    }
}

val detektFormatting = libs.detekt.formatting
val detektCompose = libs.detekt.compose
val detektPlugin = libs.plugins.detekt.get().pluginId

val autoVersion = project.property(
    if (project.hasProperty("AUTO_VERSION")) {
        "AUTO_VERSION"
    } else {
        "LIBRARY_VERSION"
    }
) as String

subprojects {

    val GROUP: String by project
    group = GROUP
    version = autoVersion

    apply {
        plugin(detektPlugin)
        from("${rootProject.projectDir}/quality/quality.gradle")
    }

    detekt {
        config.from(rootProject.files("quality/detekt.yml"))
    }

    dependencies {
        detektPlugins(detektFormatting)
        detektPlugins(detektCompose)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    compilerOptions {
        freeCompilerArgs = listOf(
            "-Xexpect-actual-classes", // Ignore expect/actual experimental state
        )
    }
}