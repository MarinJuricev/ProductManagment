import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.varabyte.kobweb.gradle.application.extensions.AppBlock.LegacyRouteRedirectStrategy
import com.varabyte.kobweb.gradle.application.util.configAsKobwebApplication
import kotlinx.html.link
import kotlinx.html.script

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.kobwebApplication)
    alias(libs.plugins.buildKonfig)
    id(libs.plugins.mokoResources.get().pluginId)
    id(libs.plugins.inventory.versioning.get().pluginId)
}

group = "org.product.inventory.web"
version = "1.0-SNAPSHOT"

versioning {
    versionFile.set(File(project.rootDir, "release/version_web.properties"))
}

val kobwebEnvironment: String = project.findProperty("kobwebEnvironment")?.toString() ?: "DEV"
val validEnvironmentNames: List<String> = enumValues<KobwebEnvironment>().map(KobwebEnvironment::name)

val environment: String = when (validEnvironmentNames.contains(kobwebEnvironment)) {
    true -> kobwebEnvironment
    false -> "DEV"
}

buildkonfig {
    packageName = "org.product.inventory"

    defaultConfigs {
        buildConfigField(FieldSpec.Type.STRING, "environment", environment)
    }
}

kobweb {
    app {
        index {
            description.set("Powered by Kobweb")

            head.add {
                link(rel = "preconnect", href = "https://fonts.googleapis.com")
                link(rel = "preconnect", href = "https://fonts.gstatic.com") { attributes["crossorigin"] = "" }
                link(
                    href = "https://fonts.googleapis.com/css2?family=Montserrat&display=swap",
                    rel = "stylesheet"
                )
                link(
                    href = "https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.snow.css",
                    rel = "stylesheet",
                )
                script {
                    type = "text/javascript"
                    src = "https://cdn.jsdelivr.net/npm/quill@2.0.2/dist/quill.js"
                }
            }
        }

        // Only legacy sites need this set. Sites built after 0.16.0 should default to DISALLOW.
        // See https://github.com/varabyte/kobweb#legacy-routes for more information.
        legacyRouteRedirectStrategy.set(LegacyRouteRedirectStrategy.DISALLOW)
    }
}

kotlin {
    task("testClasses")

    configAsKobwebApplication("web")

    js(IR) {
        browser()
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(libs.koin.core)
            implementation(projects.shared)
            implementation(libs.firebase.auth)
            implementation(libs.mokoResources.core)
        }

        jsMain.dependencies {
            implementation(compose.html.core)
            implementation(libs.kobweb.core)
            implementation(libs.kobweb.silk)
            implementation(libs.silk.icons.fa)
            implementation(libs.arrow.core)
            implementation(npm("firebase", libs.versions.firebaseNpm.get()))
            implementation(libs.kotlinBootstrap)
        }
    }
}

multiplatformResources {
    resourcesPackage.set("org.product.inventory.web")
}

// expand this in future to include more environments
enum class KobwebEnvironment {
    DEV, PROD;
}