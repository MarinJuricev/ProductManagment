class JsPlatform : Platform {
    override val name: String = "JVM target"
}

actual fun getPlatform(): Platform = JsPlatform()
