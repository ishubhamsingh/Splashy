package dev.ishubhamsingh.splashy

import android.app.Application
import dev.ishubhamsingh.splashy.core.utils.initialiseLogging

class SplashyApp : Application() {
    companion object {
        lateinit var INSTANCE: SplashyApp
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        initialiseLogging()
    }
}