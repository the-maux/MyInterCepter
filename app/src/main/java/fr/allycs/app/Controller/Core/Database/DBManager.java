package fr.allycs.app.Controller.Core.Database;

import android.util.Log;

import java.util.List;

import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;

public class                    DBManager {
    private static String       TAG = "DBManager";

    public static Session       saveSession(String ssid, String gateway, List<Host> hosts, String typeScan) {
        Log.d(TAG, "saveSession(" + ssid + ", " + gateway + ", " + hosts.size()+ "host)");
        AccessPoint ap = DBAccessPoint.getAccessPoint(ssid);
        Session session = DBSession.saveSession(ap, gateway, hosts, typeScan);
        Log.d(TAG, hosts.size() + " new client to save on this session");
        return session;
    }
}
