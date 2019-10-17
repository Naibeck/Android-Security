package com.naibeck.encryptedsharedpreferences

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.naibeck.encryptedsharedpreferences.databinding.ActivityEncryptedPreferencesBinding

class EncryptedPreferencesActivity : AppCompatActivity() {

    private val encryptedPreferences by lazy {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val preferences = EncryptedSharedPreferences.create(
            "encrypted_preferences",
            masterKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        preferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityEncryptedPreferencesBinding = DataBindingUtil.setContentView(this, R.layout.activity_encrypted_preferences)
        binding.apply {
            savePreferencesButton.setOnClickListener {
                encryptedPreferences(nameEditText.text.toString(), sessionPin.text.toString())
                if (encryptedPreferences.all.isNotEmpty()) {
                    valuesStoredText.text = """
                        Name stored: ${encryptedPreferences.getString(USER_NAME, "")}
                        Pin stored: ${encryptedPreferences.getString(USER_PIN, "")}
                    """.trimIndent()
                }
                true
            }
        }
    }

    private fun encryptedPreferences(vararg fields: String) {
        encryptedPreferences.edit().apply {
            putString(USER_NAME, fields[0])
            putString(USER_PIN, fields[1])
        }.apply()
    }

    companion object {
        private const val USER_NAME = "user.name"
        private const val USER_PIN = "user.pin"
    }
}
