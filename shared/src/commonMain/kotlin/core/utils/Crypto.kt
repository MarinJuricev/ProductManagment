package core.utils

interface Crypto {
    fun encrypt(input: String): String
    fun decrypt(input: String): String
}
