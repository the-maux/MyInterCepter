package fr.dao.app.Core.Database;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.Model.Unix.Os;

public class                                DBNetwork {
    private static String                   TAG = "DBNetwork";

    public static List<Network>             getAllAccessPoint() {
        return new Select()
                .all()
                .from(Network.class)
                .orderBy("ssid ASC")
                .execute();
    }
    public static Network                   getAPFromSSID(String SSID) {
        Network network;
        try {
            network = new Select()
                    .from(Network.class)
                    .where("ssid = \"" + SSID + "\"").executeSingle();
//            Log.d(TAG, "getAPFromSSID::" + new Select()
//                    .from(Network.class)
//                    .where("ssid = \"" + SSID + "\"").toSql());
            if (network != null) {
                if (Singleton.getInstance().Settings.DebugMode)
                    Log.d(TAG, "AccessPoint::" + SSID + " already knew with " + network.nbrScanned + " previous scan");
                network.nbrScanned = network.nbrScanned + 1;
                network.save();
                return network;
            }
        } catch (NullPointerException e) {
            e.getStackTrace();
        }
        if (Singleton.getInstance().Settings.DebugMode)
            Log.d(TAG, "AccessPoint::" + SSID + " is new ");
        network = new Network();
        network.Ssid = SSID;
        network.save();
        return network;
    }

    public static List<Network>             getAllAPWithDeviceIn(Host host) {
        List<Network> AllApWithDeviceIn = new ArrayList<>();

        for (Network accessPoint : DBNetwork.getAllAccessPoint()) {
            if (accessPoint.listDevicesSerialized.contains("" + host.getId()))
                AllApWithDeviceIn.add(accessPoint);
        }
        Log.i(TAG, "getAllAPWith(" + host.getName() + ")In:: returning " + AllApWithDeviceIn.size() + " Network ");
        return AllApWithDeviceIn;
    }

    private static boolean                  isTheDeviceIn(Host host, List<Network> sessions) {
        if (sessions != null)
            for (Network session : sessions) {
                if (session.listDevicesSerialized.contains("" + host.getId()))
                    return true;
            }
        return false;
    }
    public static Network                   updateHostOfSessions(Network accessPoint, List<Host> hosts, ArrayList<Os> osList) {
        accessPoint.lastScanDate = Calendar.getInstance().getTime();
        accessPoint.listDevicesSerialized = DBHost.SerializeListDevices(hosts);
        accessPoint.nbrOs = osList.size();
        accessPoint.save();
        return accessPoint;
    }

    static void                             updateNetworkInfoInBDD(Network accessPoint, String Gateway,
                                                                   List<Host> devicesConnected, String TypeScan, ArrayList<Os> osList) {
        ActiveAndroid.beginTransaction();
        if (Singleton.getInstance().Settings.DebugMode)
            Log.d(TAG, "Updating Network::" + accessPoint.Ssid + " discovered " + devicesConnected.size() + " host");
        accessPoint.lastScanDate = Calendar.getInstance().getTime();
        accessPoint.listDevicesSerialized = DBHost.SerializeListDevices(devicesConnected);
        accessPoint.nbrOs = osList.size();
        for (Host host : devicesConnected) {
            if (host.ip.contains(Gateway)) {
                accessPoint.Gateway = host;
                break;
            }
        }
        accessPoint.save();
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();
    }

}
