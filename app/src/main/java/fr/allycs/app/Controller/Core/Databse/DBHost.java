package fr.allycs.app.Controller.Core.Databse;

import android.content.Context;
import android.util.Log;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.allycs.app.Controller.Misc.MyGlideLoader;
import fr.allycs.app.Controller.Network.Discovery.Fingerprint;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;

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
        Host deviceFromDB = DBHost.getDevicesFromMAC(myDevice.mac);
        if (deviceFromDB == null) {
            myDevice.save();
            return myDevice;
        } else {
            deviceFromDB.ip = myDevice.ip;
            deviceFromDB.setName(myDevice.getName());
            Fingerprint.initHost(deviceFromDB);
            deviceFromDB.save();
            return deviceFromDB;
        }
    }



}
