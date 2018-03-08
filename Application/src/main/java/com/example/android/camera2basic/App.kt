package com.example.android.camera2basic

import android.app.Application
import com.cloudinary.android.MediaManager


/**
 * Created by idorenyin on 1/21/18.
 */

public class App : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Cloudinary
        MediaManager.init(this)
    }
}