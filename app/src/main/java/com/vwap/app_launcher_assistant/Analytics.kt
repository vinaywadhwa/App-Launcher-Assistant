package com.vwap.app_launcher_assistant

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics

class Analytics {
    companion object {
        private val TAG: String? = Analytics::class.java.simpleName
        private lateinit var application: Application
        private var firebaseAnalytics: FirebaseAnalytics? = null

        fun initialise(application: Application) {
            this.application = application
            application.registerActivityLifecycleCallbacks(lifecycleCallbacks)
        }

        /**
         * Disables all analytics collection. To re-enable, call [enable] .
         */
        fun disable() {
            firebaseAnalytics?.setAnalyticsCollectionEnabled(false)
                    ?: Log.i(TAG, "Analytics : attempted to disable but not initialised.")
        }

        /**
         * Enables collection of analytics events. To disable, call [disable] .
         */
        fun enable() {
            firebaseAnalytics?.setAnalyticsCollectionEnabled(true)
                    ?: Log.i(TAG, "Analytics : attempted to enable but not initialised.")
        }

        /**
         * Enables collection of analytics events. To disable, call [disable] .
         */
        @JvmStatic
        fun event(key: String, value: String) {
            firebaseAnalytics?.logEvent(key, Bundle().apply { putString(key, value) })
                    ?: Log.i(TAG, "Analytics : attempted to logEvent but not initialised.")
        }

        /**
         * You can manually set the screen name and optionally override the class name when screen transitions occur. After setting the screen name, events that occur on these screens are additionally tagged with the parameter firebase_screen. For example, you could name a screen "Main Menu" or "Friends List". The following example shows how to manually set the screen name.
         */
        @JvmStatic
        fun screenName(activity: Activity, screenName: String) {
            firebaseAnalytics?.setCurrentScreen(activity, screenName, null /* class override */)
        }

        private val lifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {

            override fun onActivityPaused(activity: Activity) {}

            override fun onActivityResumed(activity: Activity) {}

            override fun onActivityStarted(activity: Activity) {}

            override fun onActivityDestroyed(activity: Activity) {}

            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle?) {}

            override fun onActivityStopped(activity: Activity) {}

            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

                if (firebaseAnalytics == null) {
                    firebaseAnalytics = FirebaseAnalytics.getInstance(application)
                    enable()
                }
            }
        }
    }


}