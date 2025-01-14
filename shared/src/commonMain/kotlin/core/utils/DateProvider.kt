package core.utils

import kotlinx.datetime.Clock

fun interface DateProvider {
    fun generateDate(): Long
}

class CurrentDateProvider : DateProvider {
    override fun generateDate() = Clock.System.now().toEpochMilliseconds()
}
