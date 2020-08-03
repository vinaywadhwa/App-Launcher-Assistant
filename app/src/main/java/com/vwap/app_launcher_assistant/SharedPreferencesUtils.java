package com.vwap.app_launcher_assistant;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
/**
 * Contains helper methods to remove boilerplate around usage of Shared preferences.
 */
@SuppressWarnings ("unused")
class SharedPreferencesUtils {
    private static final String TAG = SharedPreferencesUtils.class.getSimpleName();

    /**
     * Add a String shared preference.
     *
     * @param spKey   key to be used for adding the shared preference
     * @param value   value to be stored in the shared preferences
     * @param context non-null application/activity context. Shared preference are not saved if
     *                context is null.
     */
    static void putSharedPreference(String spKey, String value, Context context) {
        if (context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(spKey, value);
            editor.apply();
        } else {
            Log.e(TAG,
                  "putPref() failed. Called with: " + "spKey = [" + spKey + "], value = [" + value
                          + "], context = [" + null + "]");
        }
    }

    /**
     * Get a String shared preference.
     *
     * @param spKey   key to be used for retrieving the shared preference
     * @param context non-null application/activity context. If context is null, value returned is
     *                null.
     * @return the shared preference mapped with spKey. Returns null if the context passed is null.
     */
    static String getSharedPreference(String spKey, Context context) {
        String toReturn = null;
        if (context != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            toReturn = preferences.getString(spKey, null);
        } else {
            Log.e(TAG,
                  "getPref() failed. Called with: " + "spKey = [" + spKey + "], context = [" + null
                          + "]");
        }
        return toReturn;
    }
}
