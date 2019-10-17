package com.naibeck.encryptedsharedpreferences

import android.app.Application
import com.facebook.stetho.Stetho

class SecurityApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(this)
        }
    }
}
