package com.example.project

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = mapOf(
            "cloud_name" to "dxabz96jg",
            "secure" to true
        )

        MediaManager.init(this, config)
    }
}
