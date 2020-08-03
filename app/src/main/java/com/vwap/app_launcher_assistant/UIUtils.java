package com.vwap.app_launcher_assistant;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
/**
 * Utility methods to ease common UI operations - keyboard hiding, showing Toasts, alerts,
 * snack-bars etc..
 */
@SuppressWarnings ("unused")
public class UIUtils {
    /**
     * Hides the keyboard, if it's visible.
     *
     * @param activity            the activity in which the editable field in focus exists.
     * @param editableViewInFocus the editable field in focus, due to which the keyboard is
     *                            visible.
     */
    static void hideKeyboard(Activity activity, View editableViewInFocus) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if (activity.getCurrentFocus() != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(editableViewInFocus.getWindowToken(), 0);
            }
            activity.getCurrentFocus().clearFocus();
        }
    }

    /**
     * Shows a LONG toast.
     *
     * @param toastText text to be shown in the toast.
     * @param context   Application/Activity context
     */
    public static void showToast(String toastText, Context context) {
        Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
    }
}
