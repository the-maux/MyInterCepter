package fr.allycs.app.Controller.Core.Database;

import android.util.Log;

import com.activeandroid.ActiveAndroid;
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

public class                                DBSession {
    private static String                   TAG = "DBSession";

    public static Session                   buildSession(final String SSID, final String gateway,
                                                         final List<Host> hosts, String protoUsed,
                                                         final ArrayList<String> osList) {
//        new Thread(new Runnable() {
//            public void run() {
        Session mActualSession = DBManager.saveSession(SSID, gateway, hosts, protoUsed);
        mActualSession.nbrOs = osList.size();
        mActualSession.save();
//            }
//        }).start();
        return mActualSession;
    }

    public static List<AccessPoint>         getAllAccessPoint() {
        return new Select()
                .all()
                .from(AccessPoint.class)
                .execute();
    }
    private static boolean                  isTheDeviceIn(Host host, List<Session> sessions) {
        if (sessions != null)
            for (Session session : sessions) {
                if (session.listDevicesSerialized.contains("" + host.getId()))
                    return true;
            }
        return false;
    }

    public static List<AccessPoint>         getAllAPWithDeviceIn(Host host) {
        List<AccessPoint> AllAp = DBSession.getAllAccessPoint();
        List<AccessPoint> AllApWithDeviceIn = new ArrayList<>();

        for (AccessPoint accessPoint : AllAp) {
            if (isTheDeviceIn(host, accessPoint.sessions()))
                AllApWithDeviceIn.add(accessPoint);
        }
        Log.i(TAG, "getAllAPWithDeviceIn:: returning " + AllApWithDeviceIn.size() + " AccessPoint");
        return AllApWithDeviceIn;
    }

    public static List<Session>             getAllSessionFromApWithDeviceIn(List<Session> sessions, Host mFocusedHost) {
        List<Session> allSessionWithDeviceIn = new ArrayList<>();
        for (Session session : sessions) {
            if (mFocusedHost == null || session.listDevicesSerialized.contains("" + mFocusedHost.getId())) {
                allSessionWithDeviceIn.add(session);
            }
        }
        return allSessionWithDeviceIn;
    }

    static Session                          saveNewSession(AccessPoint ap, String Gateway,
                                                List<Host> devicesConnected, String TypeScan) {
        ActiveAndroid.beginTransaction();
        Session session = new Session();
        if (Singleton.getInstance().DebugMode)
            Log.d(TAG, "SaveSession::" + ap.Ssid + ", new sesssion with " + devicesConnected.size() + " new devices");
        session.Date = Calendar.getInstance().getTime();
        session.typeScan = TypeScan;
        session.name = ap.Ssid + " " + new SimpleDateFormat("dd MMMM k", Locale.FRANCE).format(new Date()) + "H";
        session.services = new ArrayList<>();
        session.Ap = ap;
        session.listDevicesSerialized = DBHost.SerializeListDevices(devicesConnected);
        for (Host host : devicesConnected) {
            if (host.ip.contains(Gateway)) {
                session.Gateway = host;
                break;
            }
        }
        session.save();
        ap.sessions().add(session);
        ap.save();
        if (Singleton.getInstance().UltraDebugMode)
            ap.dumpSessions();
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();
        return session;
    }

}
