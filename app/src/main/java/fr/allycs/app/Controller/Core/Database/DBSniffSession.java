package fr.allycs.app.Controller.Core.Database;


import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.Model.Target.SniffSession;

public class                        DBSniffSession {

    public static SniffSession      buildSniffSession() {
        Session session = Singleton.getInstance().actualSession;
        SniffSession sniffSession = new SniffSession();
        sniffSession.listDevicesSerialized = DBHost.SerializeListDevices(Singleton.getInstance().hostsList);
        sniffSession.date = Calendar.getInstance().getTime();
        sniffSession.session = session;
        sniffSession.save();
        session.SniffSessions().add(sniffSession);
        session.save();
        return sniffSession;
    }

    public static String            SerializeSniffSessions(List<Host> hosts) {
        StringBuilder dump = new StringBuilder("");
        for (Host host : hosts) {
            dump.append(host.getId());
            dump.append(";");
        }
        return dump.toString();
    }

    public static SniffSession      findSniffSessionById(String id) {
        return new Select()
                .from(Host.class)
                .where("_id = \"" + id + "\"")
                .executeSingle();
    }

    public static List<SniffSession> getListFromSerialized(String listSniffSessionSerialized) {
        List<SniffSession> sniffSessions = new ArrayList<>();
        for (String id : listSniffSessionSerialized.split(";")) {
            SniffSession sessionById = findSniffSessionById(id.replace(";", ""));
            sniffSessions.add(sessionById);
        }
        return sniffSessions;
    }
}
