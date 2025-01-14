package versioning

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.Property
import java.io.File

interface VersioningPluginExtension {
    val versionFile: Property<File>
}

class VersioningPlugin : Plugin<Project> {

  override fun apply(project: Project) {
    val extension = project.extensions.create("versioning", VersioningPluginExtension::class.java)
    extension.versionFile.convention(File(project.rootDir, "version.properties"))
    project.addTasks(extension)
  }

  private fun Project.addTasks(versioningPluginExtension: VersioningPluginExtension) {
    task("incrementMajor") {
      doLast {
          val versioning = Versioning(versioningPluginExtension.versionFile.get())
          val version = versioning.readVersion()
          with(version) {
            versioning.setVersion(major + 1, 0, 0, 1)
        }
      }
    }

    task("incrementMinor") {
      doLast {
          val versioning = Versioning(versioningPluginExtension.versionFile.get())
          val version = versioning.readVersion()
          with(version) {
            versioning.setVersion(major, minor + 1, 0, 1)
        }
      }
    }

    task("incrementPatch") {
      doLast {
          val versioning = Versioning(versioningPluginExtension.versionFile.get())
          val version = versioning.readVersion()
          with(version) {
            versioning.setVersion(major, minor, patch + 1, 1)
        }
      }
    }

    task("incrementBuild") {
      doLast {
          val versioning = Versioning(versioningPluginExtension.versionFile.get())
          val version = versioning.readVersion()
          with(version) {
            versioning.setVersion(major, minor, patch, build + 1)
        }
      }
    }

    task("printVersion") {
      doLast {
          val versioning = Versioning(versioningPluginExtension.versionFile.get())
          val version = versioning.readVersion()
          with(version) {
            println(versionName)
            println(versionCode)
        }
      }
    }

      task("printVersionName") {
        doLast {
            val versioning = Versioning(versioningPluginExtension.versionFile.get())
            val version = versioning.readVersion()
            with(version) {
              println(versionName)
            }
        }
      }
  }
}