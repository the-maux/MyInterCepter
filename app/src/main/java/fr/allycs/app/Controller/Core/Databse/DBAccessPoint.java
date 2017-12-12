package fr.allycs.app.Controller.Core.Databse;


import android.util.Log;

import com.activeandroid.query.Select;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;

public class                            DBAccessPoint {
    private static String               TAG = "DBAccessPoint";

    public static List<AccessPoint>     getAllSessionsRecorded() {
        return new Select()
                .from(AccessPoint.class)
                //.where("Category = ?", category.getId())
                .orderBy("Name ASC")
                .execute();
    }

    public static AccessPoint           getAccessPoint(String SSID) {
        //TODO: create the object if not present in BDD
        AccessPoint ap = new Select()
                .from(AccessPoint.class)
                .where("Ssid = ?", SSID)
                .executeSingle();
        if (ap == null) {
            Log.d(TAG, "AccessPoint::" + SSID + " first meeting");
            ap = new AccessPoint();
            ap.Ssid = SSID;
            ap.Sessions = new ArrayList<>();
            ap.save();
        } else {
            Log.d(TAG, "AccessPoint::" + SSID + " already knew");
        }
        return ap;
    }

    static Session                      saveSession(AccessPoint ap, String Gateway,
                                             List<Host> devicesConnected, String TypeScan) {
        Session session = new Session();
        if (Singleton.getInstance().DebugMode)
            Log.d(TAG, "SaveSession::" + ap.Ssid + ", new sesssion with " + devicesConnected.size() + " new devices");
        session.Date = Calendar.getInstance().getTime();
        session.typeScan = TypeScan;
        session.Ap = ap;
        session.name = ap.Ssid + "_" + new SimpleDateFormat("MM_dd_HH_mm_ss", Locale.FRANCE).format(new Date());
        session.listDevices = new ArrayList<>();
        session.listDevices.addAll(devicesConnected);
        session.sniffedSession= new ArrayList<>();
        if (ap.Sessions == null)
            ap.Sessions = new ArrayList<>();
        for (Host host : devicesConnected) {
            if (host.ip.contains(Gateway)) {
                session.Gateway = host;
                break;
            }
        }
        session.save();
        ap.Sessions.add(session);
        ap.save();
        ap.dumpSessions();
        return session;
    }

    public static List<AccessPoint>    getApIfAlreadyRecorded(AccessPoint ap) {
        return new Select()
                .from(AccessPoint.class)
                .where("Ssid = ?", ap.Ssid)
                .orderBy("Name ASC")
                .executeSingle();
    }

    public static List<AccessPoint>    getAllSessionsWithDevice(Host device) {
        /*    return new Select()
                .from(AccessPoint.class)
                .where("Ssid = ?", SSID)
                .orderBy("Name ASC")
                .execute();*/
        return null;
    }
}
