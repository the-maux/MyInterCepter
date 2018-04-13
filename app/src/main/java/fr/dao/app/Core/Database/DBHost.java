package fr.dao.app.Core.Database;

import android.util.Log;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Nmap.Fingerprint;
import fr.dao.app.Model.Target.Host;

public class                                DBHost {
    private static String                   TAG = "DBHost";

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
        if (Singleton.getInstance().Settings.UltraDebugMode) {
            if (tmp != null) {
                Log.d(TAG, "SQL STRING [" + from.toSql() + "]: FOUND");
            } else {
                Log.d(TAG, "SQL STRING [" + from.toSql() + "]: NOT FOUND");
            }
        }
        //if (tmp != null)
            //tmp.dumpMe();
        if (tmp != null)
            Fingerprint.initHost(tmp);
        return tmp;
    }

    public static Host                      saveOrGetInDatabase(Host myDevice) {
        Host deviceFromDB = DBHost.getDevicesFromMAC(myDevice.mac);
        if (deviceFromDB == null) {
            deviceFromDB = myDevice;
            deviceFromDB.firstSeen = Calendar.getInstance().getTime();
            deviceFromDB.mac = deviceFromDB.mac.toUpperCase();
            deviceFromDB.save();
        } else {
            deviceFromDB.ip = myDevice.ip;
            deviceFromDB.copy(myDevice);
        }
        Fingerprint.initHost(deviceFromDB);
        return deviceFromDB;
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

    public static ArrayList<Host>            getListFromSerialized(String listSerializedId) {
        ArrayList<Host> hosts = new ArrayList<>();
        for (String id : listSerializedId.split(";")) {
            Host device = findDeviceById(id.replace(";", ""));
            try {
                Fingerprint.initHost(device);
                device.build();
                hosts.add(device);
            } catch (NullPointerException e) {
                Log.e(TAG, "id[" + id + "] was not found in BDD");
                Log.e(TAG, "id[" + id + "] from " + listSerializedId);
            }
        }
        return hosts;
    }

    public static int                           getPositionFromMacaddress(ArrayList<Host> hostList, String macAddress) {

        for (int i = 0; i < hostList.size(); i++) {
            if (hostList.get(i).mac.contentEquals(macAddress))
                return i;
        }
        return -1;
    }
}
