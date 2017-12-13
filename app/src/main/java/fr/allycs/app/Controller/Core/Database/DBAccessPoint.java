package fr.allycs.app.Controller.Core.Database;


import android.util.Log;

import com.activeandroid.query.From;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.Model.Target.Host;

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
        From request =new Select()
                .from(AccessPoint.class)
                .where("Ssid = \"" + SSID + "\"");
        AccessPoint ap = request.executeSingle();
        Log.d(TAG, "getAccessPoint::" + request.toSql());
        if (ap == null) {
            ap = new AccessPoint();
            ap.Ssid = SSID;
            ap.Sessions = new ArrayList<>();
            ap.save();
        } else {
            Log.d(TAG, "AccessPoint::" + SSID + " already knew");
        }
        return ap;
    }

    public static List<AccessPoint>    getApIfAlreadyRecorded(AccessPoint ap) {
        From request = new Select()
                .from(AccessPoint.class)
                .where("Ssid = \"" + ap.Ssid + "\"");
        List<AccessPoint> aps = request.executeSingle();
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
