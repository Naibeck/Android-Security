package com.naibeck.encryptedsharedpreferences

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.naibeck.encryptedsharedpreferences.databinding.ActivitySampleBinding

class SampleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySampleBinding = DataBindingUtil.setContentView(this, R.layout.activity_sample)
        binding.apply {
            encryptedPreferencesButton.setOnClickListener { startActivity(Intent(this@SampleActivity, EncryptedPreferencesActivity::class.java)) }
            encryptedFileButton.setOnClickListener { startActivity(Intent(this@SampleActivity, EncryptedFileActivity::class.java)) }
        }
    }
}
