package fr.allycs.app.Core.Database;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Network;
import fr.allycs.app.Model.Unix.Os;

public class                                DBNetwork {
    private static String                   TAG = "DBNetwork";

    public static List<Network>             getAllAccessPoint() {
        return new Select()
                .all()
                .from(Network.class)
                .orderBy("Ssid ASC")
                .execute();
    }
    public static Network getAPFromSSID(String SSID) {
        From request = new Select()
                .from(Network.class)
                .where("Ssid = \"" + SSID + "\"");
        Network accessPoint = request.executeSingle();
        Log.d(TAG, "getAPFromSSID::" + request.toSql());
        if (accessPoint == null) {
            if (Singleton.getInstance().DebugMode)
                Log.d(TAG, "AccessPoint::" + SSID + " is new ");
            accessPoint = new Network();
            accessPoint.Ssid = SSID;
        } else {
            if (Singleton.getInstance().DebugMode)
                Log.d(TAG, "AccessPoint::" + SSID + " already knew with " + accessPoint.nbrScanned + " previous scan");
            accessPoint.nbrScanned = accessPoint.nbrScanned + 1;
        }
        accessPoint.save();
        return accessPoint;
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
        if (Singleton.getInstance().DebugMode)
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
