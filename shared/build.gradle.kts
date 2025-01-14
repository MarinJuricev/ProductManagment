dependencies {
    implementation(libs.credentials)
}
plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.skie)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.kmmbridge)
    id(libs.plugins.mokoResources.get().pluginId)
    `maven-publish`
}

kotlin {
    task("testClasses")

    jvm()

    js {
        browser {
            webpackTask {
                mainOutputFileName = "shared.js"
            }
        }
        binaries.executable()
    }

    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    if (System.getenv("DOCKER_BUILD") == null) {
        listOf(
            iosX64(),
            iosArm64(),
            iosSimulatorArm64(),
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "Shared"
                isStatic = true
                freeCompilerArgs += listOf("-Xoverride-konan-properties=minVersion.ios=16.0;minVersionSinceXcode15.ios=16.0")
                export(libs.mokoResources.core)
                export(libs.mokoResources.graphics) // toUIColor here
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.arrow.core)
            implementation(libs.arrow.coroutines)
            implementation(libs.kermit)
            implementation(libs.keyvalue)
            implementation(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.datetime)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.bundles.firebase)
            api(libs.mokoResources.core)
            implementation(libs.uuid)
            implementation(project.dependencies.platform(libs.ktor.bom))
            implementation(libs.bundles.ktor.client)
        }
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.skie.annotations)
            implementation(libs.firebase.crashlytics)
            implementation(libs.ktor.client.okhttp)
        }
        iosMain.dependencies {
            implementation(libs.skie.annotations)
            implementation(libs.ktor.client.darwin)
        }

        jsMain.dependencies {
            implementation(libs.ktor.client.js)
            api(libs.ksoup)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.okhttp)
        }
    }
}
android {
    namespace = "org.product.inventory.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

addGithubPackagesRepository()

kmmbridge {
    frameworkName.set("Shared")
    mavenPublishArtifacts()
    spm(swiftToolVersion = "5.9") {
        iOS { v("16") }
    }
}

multiplatformResources {
    resourcesPackage.set("org.product.inventory.shared")
}