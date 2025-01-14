package versioning

import org.gradle.api.GradleException
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.Properties

class Versioning(private val versionFile: File) {

  data class Version(
    val versionName: String,
    val versionCode: Int,
    val major: Int,
    val minor: Int,
    val patch: Int,
    val build: Int
  )

  private fun readVersionFileAsProperties(): Properties {
    val properties = Properties()
    var stream: FileInputStream? = null
    try {
      stream = FileInputStream(versionFile)
      properties.load(stream)
    } catch (ignore: FileNotFoundException) {
      println("${versionFile.path} does not exist!")
    } finally {
      stream?.close()
    }
    return properties
  }

  fun readVersion(): Version {
    val properties = readVersionFileAsProperties()
    return Version(
      versionName = properties[VERSION_NAME]?.toString() ?: "0.0.0.0",
      versionCode = properties[VERSION_CODE]?.toString()?.toInt() ?: 1,
      major = properties[MAJOR]?.toString()?.toInt() ?: 0,
      minor = properties[MINOR]?.toString()?.toInt() ?: 0,
      patch = properties[PATCH]?.toString()?.toInt() ?: 0,
      build = properties[BUILD]?.toString()?.toInt() ?: 0
    )
  }

  fun setVersion(major: Int, minor: Int, patch: Int, build: Int) {
    val versionName = "$major.$minor.$patch.$build"
    val versionCode = ((major * 100 + minor) * 100 + patch) * 100 + build

    println("Version name: $versionName\nVersion code: $versionCode")

    if (versionCode >= 10000000) {
      // Warning: The greatest possible value for versionCode is MAXINT (2 147 483 647).
      // However, if you upload an app with this value, your app can"t ever be updated.
      // This check is only here to warn the developer that version code number is already high
      // At this point consider different strategy for version codes
      throw GradleException("Careful! Version code is already high. Consider changing versioning strategy!")
    }

    val properties = readVersionFileAsProperties()
    properties[MAJOR] = major.toString()
    properties[MINOR] = minor.toString()
    properties[PATCH] = patch.toString()
    properties[BUILD] = build.toString()
    properties[VERSION_NAME] = versionName
    properties[VERSION_CODE] = versionCode.toString()

    // Generate (write) new properties file
    FileOutputStream(versionFile).use { stream ->
      properties.store(stream, null)
    }
  }
}

private const val MAJOR = "MAJOR"
private const val MINOR = "MINOR"
private const val PATCH = "PATCH"
private const val BUILD = "BUILD"
private const val VERSION_NAME = "VERSION_NAME"
private const val VERSION_CODE = "VERSION_CODE"