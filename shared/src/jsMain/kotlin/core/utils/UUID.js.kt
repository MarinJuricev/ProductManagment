package core.utils

import com.benasher44.uuid.uuid4

actual fun UUID(): String = uuid4().toString()
