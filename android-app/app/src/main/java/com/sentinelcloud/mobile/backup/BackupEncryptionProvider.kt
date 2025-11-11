package com.sentinelcloud.mobile.backup

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class BackupEncryptionProvider(
    private val alias: String = "SentinelCloudBackupKey"
) {

    fun encrypt(file: File): EncryptedPayload {
        val secretKey = getOrCreateKey()
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val encrypted = cipher.doFinal(file.readBytes())
        val encryptedFile = File(file.parentFile, "${file.nameWithoutExtension}.enc")
        encryptedFile.writeBytes(encrypted)

        return EncryptedPayload(
            file = encryptedFile,
            keyAlias = alias,
            initializationVector = cipher.iv
        )
    }

    private fun getOrCreateKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
        val existingKey = keyStore.getKey(alias, null)
        if (existingKey is SecretKey) return existingKey

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEY_STORE
        )
        val parameterSpec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).apply {
            setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            setRandomizedEncryptionRequired(true)
        }.build()
        keyGenerator.init(parameterSpec)
        return keyGenerator.generateKey()
    }

    data class EncryptedPayload(
        val file: File,
        val keyAlias: String,
        val initializationVector: ByteArray
    ) {
        val ivBase64: String = Base64.encodeToString(initializationVector, Base64.NO_WRAP)
    }

    companion object {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}


