package fr.allycs.app.Controller.Core.Databse;

import android.util.Log;

import com.activeandroid.query.From;
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
        From from = new Select()
                .from(Host.class)
                .where("mac = \"" + MAC + "\"");
        //Log.d(TAG, "SQL STRING [" + from.toSql() + "]");
        Host tmp = from.executeSingle();
        if (tmp != null) {
            Log.d(TAG, "SQL STRING [" + from.toSql() + "]: FOUND");
        } else {
            Log.d(TAG, "SQL STRING [" + from.toSql() + "]: NOT FOUND");
        }
        return tmp;
    }

    public static Host                      saveOrGetInDatabase(Host myDevice) {
        Host deviceFromDB = DBHost.getDevicesFromMAC(myDevice.getMac());
        if (deviceFromDB == null) {
            myDevice.save();
            return myDevice;
        } else {
            deviceFromDB.setIp(myDevice.getIp());
            deviceFromDB.setName(myDevice.getName());
            deviceFromDB.init();
            deviceFromDB.save();
            return deviceFromDB;
        }
    }

}
