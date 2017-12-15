package fr.allycs.app.Controller.Core.Database;


import android.util.Log;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.util.List;

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
        //ActiveAndroid.beginTransaction();
        From request = new Select()
                .from(AccessPoint.class)
                .innerJoin(Session.class)
                .on(AccessPoint.TAG+"._id=AccessPoint._id")
                .where("Ssid = \"" + SSID + "\"");
        AccessPoint ap = request.executeSingle();
        Log.d(TAG, "getAccessPoint::" + request.toSql());
        if (ap == null) {
            if (Singleton.getInstance().DebugMode)
                Log.d(TAG, "AccessPoint::" + SSID + " is new ");
          //  ActiveAndroid.beginTransaction();
            ap = new AccessPoint();
            ap.Ssid = SSID;
           // ap.Sessions = new ArrayList<>();
            ap.save();
//            ActiveAndroid.setTransactionSuccessful();
//            ActiveAndroid.endTransaction();
        } else {
            if (Singleton.getInstance().DebugMode)
                Log.d(TAG, "AccessPoint::" + SSID + " already knew with " + ap.sessions().size() + "session");
        }
        return ap;
    }

    public static List<AccessPoint>    getApIfAlreadyRecorded(AccessPoint ap) {
        From request = new Select()
                .from(AccessPoint.class)
                .where("Ssid = \"" + ap.Ssid + "\"");
        List<AccessPoint> aps = request.execute();
        Log.d(TAG, "getApIfAlreadyRecorded::" + request.toSql() + " return " + aps.size() + " AccessPoint");
        return aps;
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
