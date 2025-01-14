package core.utils.crypto

import android.util.Base64
import core.utils.Crypto
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal class AndroidCrypto : Crypto {
    override fun encrypt(input: String): String {
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        val secretKeySpec = SecretKeySpec(ENCRYPTION_KEY.toByteArray(), ALGORITHM)
        val ivParameterSpec = IvParameterSpec(INITIALIZATION_VECTOR.toByteArray())
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec)
        val encryptedBytes = cipher.doFinal(input.toByteArray())
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
            .lines()
            .joinToString("")
    }

    override fun decrypt(input: String): String {
        val cipher = Cipher.getInstance(CIPHER_TRANSFORMATION)
        val secretKeySpec = SecretKeySpec(ENCRYPTION_KEY.toByteArray(), ALGORITHM)
        val ivParameterSpec = IvParameterSpec(INITIALIZATION_VECTOR.toByteArray())
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec)
        val encryptedBytes = Base64.decode(input, Base64.DEFAULT)
        val decryptedBytes = cipher.doFinal(encryptedBytes)
        return String(decryptedBytes)
    }
}

private const val CIPHER_TRANSFORMATION = "AES/CBC/PKCS5Padding"
private const val ALGORITHM = "AES"
