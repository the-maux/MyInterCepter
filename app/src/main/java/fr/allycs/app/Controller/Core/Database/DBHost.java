package fr.allycs.app.Controller.Core.Database;

import android.util.Log;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Network.Discovery.Fingerprint;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;

public class                                DBHost {
    private static String                   TAG = "DBHost";

    public static Host                      getRandomDevices() {
        return new Select()
                .from(Host.class)
                .orderBy("RANDOM()")
                .executeSingle();
    }

    public static Host                      findDeviceById(String id) {
        return new Select()
                .from(Host.class)
                .where("_id = \"" + id + "\"")
                .executeSingle();
    }

    public static Host                      getDevicesFromMAC(String MAC) {
        From from = new Select()
                .from(Host.class)
                .where("mac = \"" + MAC + "\"");
        Host tmp = from.executeSingle();
        if (Singleton.getInstance().UltraDebugMode) {
            if (tmp != null) {
                Log.d(TAG, "SQL STRING [" + from.toSql() + "]: FOUND");
            } else {
                Log.d(TAG, "SQL STRING [" + from.toSql() + "]: NOT FOUND");
            }
        }
        return tmp;
    }

    public static Host                      saveOrGetInDatabase(Host myDevice) {
        //ActiveAndroid.beginTransaction();
        Host deviceFromDB = DBHost.getDevicesFromMAC(myDevice.mac);
        if (deviceFromDB == null) {
            myDevice.save();
            return myDevice;
        } else {
            deviceFromDB.ip = myDevice.ip;
            if (!myDevice.getName().equals("(-)"))
                deviceFromDB.setName(myDevice.getName());
            Fingerprint.initHost(deviceFromDB);
            deviceFromDB.save();
//            ActiveAndroid.setTransactionSuccessful();
//            ActiveAndroid.endTransaction();
            return deviceFromDB;
        }
    }

    public static List<Session>             getAllDiscovered() {
        return new Select()
                .all()
                .from(Host.class)
                .execute();
    }

    public static String                    SerializeListDevices(List<Host> hosts) {
        StringBuilder dump = new StringBuilder("");
        for (Host host : hosts) {
            dump.append(host.getId());
            dump.append(";");
        }
        return dump.toString();
    }

    public static List<Host>                getListFromSerialized(String list) {
        List<Host> hosts = new ArrayList<>();
        for (String id : list.split(";")) {
            Host device = findDeviceById(id.replace(";", ""));
            hosts.add(device);
        }
        return hosts;
    }
}
