package com.vwap.app_launcher_assistant

import android.app.Application
import android.content.Context
import android.os.StrictMode
import androidx.multidex.MultiDex
import com.google.firebase.crashlytics.FirebaseCrashlytics


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        enableCrashlytics()
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        Analytics.initialise(this)
    }


    /**
     * By default, Crashlytics automatically enables crash reporting at app launch.
     * This has been disabled (see "firebase_crashlytics_collection_enabled" in Manifest) to
     * allow Crashlytics to be conditionally initialised
     */
    private fun enableCrashlytics() { //Optional : add a condition here
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

}