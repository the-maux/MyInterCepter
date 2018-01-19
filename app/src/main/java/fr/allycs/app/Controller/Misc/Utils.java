package fr.allycs.app.Controller.Misc;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import fr.allycs.app.Controller.Core.BinaryWrapper.RootProcess;


public class                Utils {
    private static String   TAG = "Utils";

    public static int       ReadOnlyFileSystemOFF() {
        return new RootProcess("initialisation ").exec("mount -o rw,remount /system").closeProcess();
    }

    public static void      vibrateDevice(Context context) {
        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibe != null) {
            vibe.vibrate(80);
        }
    }

    public static void      hideKeyboard(MyActivity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null && activity.getCurrentFocus() != null) {
                IBinder binder = activity.getCurrentFocus().getWindowToken();
                if (binder != null)
                    inputMethodManager.hideSoftInputFromWindow(binder, 0);
            }
        } catch (Exception e) {
            Log.e(TAG, "hidding keyboard failed, cause no focus");
        }
    }
}
