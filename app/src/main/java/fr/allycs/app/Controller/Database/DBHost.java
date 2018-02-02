package fr.allycs.app.Controller.Database;

import android.util.Log;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.Nmap.Fingerprint;
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
            Fingerprint.initHost(myDevice);
            myDevice.save();
            return myDevice;
        } else {
            deviceFromDB.ip = myDevice.ip;
            if (!myDevice.getName().equals("(-)"))
                deviceFromDB.setName(myDevice.getName());
            deviceFromDB.deviceType = myDevice.deviceType;
            deviceFromDB.dumpInfo = myDevice.dumpInfo;
            deviceFromDB.NetworkDistance = myDevice.NetworkDistance;
            deviceFromDB.osDetail = myDevice.osDetail;
            deviceFromDB.vendor = myDevice.vendor;
            deviceFromDB.deviceType = myDevice.deviceType;
            deviceFromDB.TooManyFingerprintMatchForOs = myDevice.TooManyFingerprintMatchForOs;
            Fingerprint.initHost(deviceFromDB);
            deviceFromDB.save();
//            ActiveAndroid.setTransactionSuccessful();
//            ActiveAndroid.endTransaction();
        }
        return deviceFromDB;
    }

    public static List<Session>             getAllDiscovered() {
        return new Select()
                .all()
                .from(Host.class)
                .execute();
    }

    public static String                    SerializeListDevices(List<Host> hosts) {
        if (hosts == null)
            return "";
        StringBuilder dump = new StringBuilder("");
        for (Host host : hosts) {
            dump.append(host.getId());
            dump.append(";");
        }
        return dump.toString();
    }

    public static List<Host>                getListFromSerialized(String listSerializedId) {
        List<Host> hosts = new ArrayList<>();
        for (String id : listSerializedId.split(";")) {
            Host device = findDeviceById(id.replace(";", ""));
            try {
                Fingerprint.initHost(device);
                hosts.add(device);
            } catch (NullPointerException e) {
                Log.e(TAG, "id[" + id + "] was not found in BDD");
                Log.e(TAG, "id[" + id + "] from " + listSerializedId);
            }
        }
        return hosts;
    }
}
