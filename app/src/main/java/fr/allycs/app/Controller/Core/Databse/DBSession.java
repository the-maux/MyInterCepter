package fr.allycs.app.Controller.Core.Databse;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.Model.Target.SniffSession;

public class                                DBSession {

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

    public static List<Session>             getSessionsWithDeviceIn(Host host) {
        List<Session> allSessions = DBSession.getAllSessions();
        List<Session> sessionWithDeviceIn = new ArrayList<>();
        if (allSessions == null)
            return sessionWithDeviceIn;
        for (Session session : allSessions) {
            for (Host device : session.listDevices) {
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
            for (Host device : session.listDevices) {
                if (device.mac.equals(host.mac)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static List<AccessPoint>         getAllAPWithDeviceIn(Host host) {
        List<AccessPoint> AllAp = DBSession.getAllAccessPoint();
        List<AccessPoint> AllApWithDeviceIn = new ArrayList<>();

        for (AccessPoint accessPoint : AllAp) {
            if (isTheDeviceIn(host, accessPoint.Sessions))
                AllApWithDeviceIn.add(accessPoint);
        }
        return AllApWithDeviceIn;
    }

}
