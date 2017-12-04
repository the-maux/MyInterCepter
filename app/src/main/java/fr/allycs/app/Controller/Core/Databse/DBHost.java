package fr.allycs.app.Controller.Core.Databse;

import android.util.Log;

import com.activeandroid.query.Select;

import fr.allycs.app.Model.Target.Host;

public class                                DBHost {
    private static String                   TAG = "DBHost";

    public static Host                      getRandomDevices() {
        return new Select()
                .from(Host.class)
                .orderBy("RANDOM()")
                .executeSingle();
    }

    public static Host                      getDevicesFromMAC(String MAC) {
        Host tmp = new Select()
                .from(Host.class)
                .where("mac = ?", MAC)
                .executeSingle();
        if (tmp != null) {
            Log.d(TAG, "Host(" + tmp.getMac() + ") is already know");
        }
        return tmp;
    }

}
