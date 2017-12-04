package fr.allycs.app.Controller.Core.Databse;


import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;

public class                            DBAccessPoint {
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
                .orderBy("Name ASC")
                .executeSingle();
        if (ap == null) {
            ap = new AccessPoint();
            ap.Ssid = SSID;
            ap.Sessions = new ArrayList<>();
            ap.save();
        }
        return ap;
    }

    public static Session               saveSession(AccessPoint ap, String Gateway,
                                                    List<Host> devicesConnected) {
        Session session = new Session();
        for (Host host : devicesConnected) {
            if (host.getIp().contains(Gateway)) {
                session.Gateway = host;
                break;
            }
        }
        session.Date = Calendar.getInstance().getTime();
        session.typeScan = "Icmp";
        session.listDevices = new ArrayList<>();
        session.listDevices.addAll(devicesConnected);
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
