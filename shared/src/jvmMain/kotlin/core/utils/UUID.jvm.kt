package core.utils

import java.util.UUID

actual fun UUID(): String = UUID.randomUUID().toString()
