package com.naibeck.encryptedsharedpreferences

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import com.naibeck.encryptedsharedpreferences.databinding.ActivityEncryptedFileBinding
import com.naibeck.encryptedsharedpreferences.extensions.markDown
import okhttp3.Callback
import okhttp3.Call
import okhttp3.Response
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.File
import java.net.UnknownHostException
import java.security.GeneralSecurityException


class EncryptedFileActivity : AppCompatActivity() {

    private var binding: ActivityEncryptedFileBinding? = null
    private var file: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        file = File(filesDir, FILE_NAME)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_encrypted_file)
        binding?.apply {
            downloadFileButton.setOnClickListener {
                try {
                    file?.let {
                        if (it.exists()) {
                            it.delete()
                        }
                    }
                    downloadFile()
                } catch (ex: GeneralSecurityException) {
                    Log.d("SecurityException", "File faced a security error: ${ex.message}")
                } catch (ex: IOException) {
                    Log.d("SecurityException", "Error: ${ex.message}")
                }
            }
        }
    }

    @Throws(IOException::class, GeneralSecurityException::class, UnknownHostException::class)
    private fun downloadFile() {
        Request.Builder().url(FILE_URL).build().also { request ->
            OkHttpClient.Builder().build().newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    throw e
                }

                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        writeToEncryptedFile(response)
                        readToEncryptedFile()
                    }
                }
            })
        }
    }

    @Throws(IOException::class, GeneralSecurityException::class)
    private fun writeToEncryptedFile(response: Response) {
        var encryptedOutputStream: FileOutputStream? = null
        try {
            encryptedOutputStream = generateEncryptedFile()?.openFileOutput()
            response.body()?.bytes()?.let { bytes ->
                encryptedOutputStream?.write(bytes)
            }
        } catch (securityEx: GeneralSecurityException) {
            throw securityEx
        } catch (ioEx: IOException) {
            throw ioEx
        } finally {
            encryptedOutputStream?.close()
        }
    }

    @Throws(IOException::class, GeneralSecurityException::class)
    private fun readToEncryptedFile() {
        var encryptedInputStream: FileInputStream? = null
        try {
            encryptedInputStream = generateEncryptedFile()?.openFileInput()
            encryptedInputStream?.let { stream ->
                val reader = BufferedReader(InputStreamReader(stream))
                var text = ""
                reader.forEachLine { line ->
                    text += "$line \n"
                }
                runOnUiThread {
                    binding?.markDownText?.markDown(markdownText = text)
                }
            }
        } catch (securityEx: GeneralSecurityException) {
            throw securityEx
        } catch (ioEx: IOException) {
            throw ioEx
        } finally {
            encryptedInputStream?.close()
        }
    }

    private fun generateEncryptedFile(): EncryptedFile? {
        // Creates or gets the key to encrypt and decrypt.
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

        // Creates the instance for the encrypted file.
        return file?.let {
            EncryptedFile.Builder(
                it,
                this,
                masterKeyAlias,
                EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
            ).build()
        }
    }

    companion object {
        const val FILE_URL = "https://raw.githubusercontent.com/Naibeck/Android-Security/master/README.md"
        const val FILE_NAME = "README.md"
    }
}
