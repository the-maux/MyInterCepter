package fr.dao.app.Core.Configuration;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.dao.app.View.Behavior.Activity.MyActivity;


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

    public static String     TimeDifference(Date start) {
        Date now = Calendar.getInstance().getTime();
        long restDatesinMillis = now.getTime() - start.getTime();
        Date restdate = new Date(restDatesinMillis);
        return new SimpleDateFormat("mm:ss", Locale.FRANCE).format(restdate)
                .replace("00:", "")
                .replace(":", "m") + "s";
    }
}
