package core.utils

class IsEmailValid {
    operator fun invoke(email: String): Boolean = email.matches(EMAIL_REGEX)
}

private val EMAIL_REGEX = "^[A-Za-z](.*)([@]{1})(.{1,})(\\.)(.{1,})".toRegex()
