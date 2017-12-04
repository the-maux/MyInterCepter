package fr.allycs.app.Controller.Core.Databse;

import com.activeandroid.query.Select;

import java.util.Calendar;
import java.util.List;

import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.HostDiscoverySession;

public class                                DBManager {

    public static Host                      getRandomDevices() {
        return new Select()
                .from(Host.class)
                .orderBy("RANDOM()")
                .executeSingle();
    }

    public static List<HostDiscoverySession> getAllSessionsRecorded() {
        return new Select()
                .from(HostDiscoverySession.class)
                //.where("Category = ?", category.getId())
                .orderBy("Name ASC")
                .execute();
    }

    public static List<HostDiscoverySession> getAllSessionsRecordedWithSSID(String SSID) {
        return new Select()
                .from(HostDiscoverySession.class)
                .where("Ssid = ?", SSID)
                .orderBy("Name ASC")
                .execute();
    }

    public static Host                      getDevicesFromMAC(String MAC) {
        return new Select()
                .from(Host.class)
                .where("mac = ?", MAC)
                .executeSingle();
    }

    public static HostDiscoverySession      saveCurrentSession(String SSID, String Gateway, List<Host> devicesConnected) {
        HostDiscoverySession session = new HostDiscoverySession();
        session.Ssid = SSID;
        for (Host host : devicesConnected) {
            if (host.getIp().contains(Gateway)) {
                session.AccessPoint = host;
                break;
            }
        }
        session.Date = Calendar.getInstance().getTime();
        session.listDevices().addAll(devicesConnected);
        session.dumpSessions();
        session.save();
        return session;
    }
}
