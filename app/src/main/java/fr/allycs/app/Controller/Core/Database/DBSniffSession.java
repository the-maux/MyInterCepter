package fr.allycs.app.Controller.Core.Database;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.Model.Target.SniffSession;

public class                        DBSniffSession {
    private static String           TAG = "DBSniffSession";


    public static List<SniffSession> getAllSniffSession() {
        return new Select()
                .all()
                .from(SniffSession.class)
                .execute();
    }

    public static SniffSession      buildSniffSession() {
        Log.d(TAG, "buildSniffSession");
        Session session = Singleton.getInstance().actualSession;
        SniffSession sniffSession = new SniffSession();
        sniffSession.listDevicesSerialized = DBHost.SerializeListDevices(Singleton.getInstance().hostsList);
        sniffSession.date = Calendar.getInstance().getTime();
        sniffSession.session = session;
        sniffSession.save();
        session.isSniffed = true;
        session.SniffSessions().add(sniffSession);
        session.save();
        Log.d(TAG, sniffSession.toString());
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

    public void                     loadingFromDBB(final List<SniffSession> sniffSessions,
                                                   final ProgressBar progressBar,
                                                   final RecyclerView recyclerView) {
        recyclerView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(sniffSessions.size());
        new Thread(new Runnable() {
            @Override
            public void run() {
                int rax = 0;
                for (SniffSession session : sniffSessions) {
                    session.listDevices();
                    session.logDnsSpoofed();
                    session.listPcapRecorded();
                    progressBar.setProgress(++rax);
                }
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }).start();
    }
}
