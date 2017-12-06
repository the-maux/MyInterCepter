package fr.allycs.app.Controller.Core.Databse;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.Model.Target.SniffSession;

public class                                DBSession {
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

}
