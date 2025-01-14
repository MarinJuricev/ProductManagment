package core.utils

import platform.Foundation.NSUUID

actual fun UUID(): String = NSUUID().UUIDString()
