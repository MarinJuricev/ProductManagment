package core.utils

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
internal class Base64Crypto : Crypto {

    override fun encrypt(input: String) = Base64.encode(
        source = input.encodeToByteArray(),
    )

    override fun decrypt(input: String) = Base64.decode(
        source = input,
    ).decodeToString()
}
