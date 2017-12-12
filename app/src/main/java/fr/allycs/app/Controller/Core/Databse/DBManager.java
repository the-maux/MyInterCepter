package fr.allycs.app.Controller.Core.Databse;

import android.util.Log;
import android.view.View;

import java.util.List;

import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;

public class                    DBManager {
    private static String       TAG = "DBManager";

    public static Session       saveSession(String ssid, String gateway, List<Host> hosts, String typeScan) {
        AccessPoint ap = DBAccessPoint.getAccessPoint(ssid);
        Log.d(TAG, hosts.size() + " new client to save on this session");
        return DBAccessPoint.saveSession(ap, gateway, hosts, typeScan);
    }
}
