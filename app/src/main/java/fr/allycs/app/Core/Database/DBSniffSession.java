package fr.allycs.app.Core.Database;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.activeandroid.query.Select;

import java.util.Calendar;
import java.util.List;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Network;
import fr.allycs.app.Model.Target.SniffSession;

public class                        DBSniffSession {
    private static String           TAG = "DBSniffSession";

    public static SniffSession      buildSniffSession() {
        Log.d(TAG, "buildSniffSession");
        Network session = Singleton.getInstance().actualNetwork;
        SniffSession sniffSession = new SniffSession();
        sniffSession.listDevicesSerialized = DBHost.SerializeListDevices(Singleton.getInstance().selectedHostsList);
        sniffSession.date = Calendar.getInstance().getTime();
        sniffSession.session = session;
        sniffSession.save();
        session.SniffSessions().add(sniffSession);
        session.save();
        Log.d(TAG, sniffSession.toString());
        return sniffSession;
    }

    public static List<SniffSession> getAllSniffSession() {
        return new Select()
                .all()
                .from(SniffSession.class)
                .execute();
    }

    public static String            SerializeSniffSessions(List<Host> hosts) {
        StringBuilder dump = new StringBuilder("");
        for (Host host : hosts) {
            dump.append(host.getId());
            dump.append(";");
        }
        return dump.toString();
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
