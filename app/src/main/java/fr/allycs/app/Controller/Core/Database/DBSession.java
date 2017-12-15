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
import fr.allycs.app.Model.Target.SniffSession;

public class                                DBSession {
    private static String                   TAG = "DBSession";
    public static List<AccessPoint>         getAllAccessPoint() {
        return new Select()
                .all()
                .from(AccessPoint.class)
                .execute();
    }
    public static List<Session>             getAllSessions() {
        return new Select()
                .all()
                .from(Session.class)
                .execute();
    }
    public static List<SniffSession>        getAllSniffedSessions() {
        return new Select()
                .all()
                .from(SniffSession.class)
                .execute();
    }

    public static List<SniffSession>        getSniffedSessionsWithDeviceIn(Host host) {
        List<SniffSession>  sniffedsessions = DBSession.getAllSniffedSessions();
        List<SniffSession>  sniffedSessionWithDeviceIn = new ArrayList<>();
        for (SniffSession sniffedsession : sniffedsessions) {
            for (Host device : sniffedsession.listDevices) {
                if (host.mac.equals(device.mac)) {//TODO: try with .contains instead of for loop
                    sniffedSessionWithDeviceIn.add(sniffedsession);
                    break;
                }
            }
        }
        return sniffedSessionWithDeviceIn;
    }

    public static List<Session>             getAllSessionsWithDeviceIn(Host host) {
        List<Session> allSessions = DBSession.getAllSessions();
        List<Session> sessionWithDeviceIn = new ArrayList<>();
        if (allSessions == null)
            return sessionWithDeviceIn;
        for (Session session : allSessions) {
            for (Host device : session.listDevices()) {
                if (host.mac.equals(device.mac)) {//TODO: try with .contains instead of for loop
                    sessionWithDeviceIn.add(session);
                    break;
                }
            }
        }
        return sessionWithDeviceIn;
    }

    private static boolean                  isTheDeviceIn(Host host, List<Session> sessions) {
        if (sessions == null)
            return false;
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
        Log.d(TAG, "getAllAPWithDeviceIn:: returning " + AllApWithDeviceIn.size() + " AccessPoin");
        return AllApWithDeviceIn;
    }

    static Session                          saveSession(AccessPoint ap, String Gateway,
                                                    List<Host> devicesConnected, String TypeScan) {
        ActiveAndroid.beginTransaction();
        Session session = new Session();
        if (Singleton.getInstance().DebugMode)
            Log.d(TAG, "SaveSession::" + ap.Ssid + ", new sesssion with " + devicesConnected.size() + " new devices");
        session.Date = Calendar.getInstance().getTime();
        session.typeScan = TypeScan;
        //session.Ap = ap;
        session.name = ap.Ssid + " " + new SimpleDateFormat("dd MMMM k", Locale.FRANCE).format(new Date()) + "H";
        session.services = new ArrayList<>();
        session.listDevicesSerialized = DBHost.SerializeListDevices(devicesConnected);
        session.sniffedSession= new ArrayList<>();
        for (Host host : devicesConnected) {
            if (host.ip.contains(Gateway)) {
                session.Gateway = host;
                break;
            }
        }
        session.Ap = ap;
        session.save();
        ap.sessions().add(session);
        ap.save();
        ap.dumpSessions();
        ActiveAndroid.setTransactionSuccessful();
        ActiveAndroid.endTransaction();
        return session;
    }

    public static List<Session>             getAllSessionFromApWithDeviceIn(List<Session> sessions, Host mFocusedHost) {
        List<Session> allSessionWithDeviceIn = new ArrayList<>();
        for (Session session : sessions) {
            if (session.listDevicesSerialized.contains("" + mFocusedHost.getId())) {
                allSessionWithDeviceIn.add(session);
            }
        }
        return allSessionWithDeviceIn;
    }

}
