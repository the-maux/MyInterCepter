package fr.allycs.app.Controller.Core.Databse;


import java.util.ArrayList;
import java.util.Calendar;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.Model.Target.SniffSession;

public class                    DBSniffSession {

    public static SniffSession  buildSniffSession() {
        Session session = Singleton.getInstance().actualSession;
        SniffSession sniffSession = new SniffSession();
        sniffSession.listDevices = Singleton.getInstance().hostsList;
        sniffSession.listPcapRecorded = new ArrayList<>();
        sniffSession.date = Calendar.getInstance().getTime();
        sniffSession.logDnsSpoofed = new ArrayList<>();
        sniffSession.save();
        session.sniffedSession.add(sniffSession);
        session.save();
        Singleton.getInstance().actualSniffSession = sniffSession;
        return sniffSession;
    }
}
