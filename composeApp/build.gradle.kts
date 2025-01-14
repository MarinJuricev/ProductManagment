import versioning.Versioning

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.firebaseCrashlytics)
    id(libs.plugins.mokoResources.get().pluginId)
    id(libs.plugins.inventory.versioning.get().pluginId)
}

versioning {
    versionFile.set(File(project.rootDir, "release/version.properties"))
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.androidx.compose)
            implementation(libs.voyager.koin)
            implementation(libs.coil.compose)
        }
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.arrow.core)
            implementation(libs.arrow.coroutines)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(projects.shared)
            implementation(libs.androidx.credentials)
            implementation(libs.androidx.credentials.play.services.auth)
            implementation(libs.googleid)
            implementation(libs.bundles.voyager)
            implementation(libs.richeditor.compose)
        }
    }
}

android {
    buildFeatures.buildConfig = true
    namespace = "org.product.inventory"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.product.inventory"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        val versioning = Versioning(File(project.rootDir.path, "/release/version.properties")).readVersion()
        versionCode = versioning.versionCode
        versionName = versioning.versionName
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            buildConfigField(name = "ENVIRONMENT", value = "\"development\"", type = "String")
        }
        create("prod") {
            dimension = "environment"
            buildConfigField(name = "ENVIRONMENT", value = "\"production\"", type = "String")
        }
    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("../release/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }

        create("release") {
            storeFile = project.rootProject.file("release/release.keystore")
            keyAlias = "inventoryapp"
            storePassword = System.getenv("ANDROID_INVENTORY_STORE_PASSWORD")
            keyPassword = System.getenv("ANDROID_INVENTORY_KEY_PASSWORD")
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }
        getByName("release") {
            isMinifyEnabled = true
            isDebuggable = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs += listOf(
                "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
                "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
                "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
            )
        }
    }
}
