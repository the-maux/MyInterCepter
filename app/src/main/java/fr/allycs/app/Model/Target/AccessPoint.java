package fr.allycs.app.Model.Target;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;

@Table(name = "AccessPoint", id = "_id")
public class                AccessPoint extends Model {
    public static String    TAG = "AccessPoint";

    @Column(name = "Ssid")
    public String           Ssid;
    public int              nbrSession;

    public List<Session>    sessions() {
        List<Session> sessions = getMany(Session.class, "AccessPoint");
        nbrSession = sessions.size();
        return sessions;
    }

    public void             dumpSessions() {
        if (Singleton.getInstance().DebugMode) {
            for (Session session : sessions()) {
                if (session.Gateway == null) {
                    Log.d(TAG, "Session is empty");
                } else {
                    Log.d(TAG, "\t " + Ssid + " : " + session.Gateway.ip + "-[" + session.Gateway.mac + "]");
                    Log.d(TAG, "\t\t" + session.toString());
                }
            }
            Log.d(TAG, "END SESSION:--------------------------");
        }
    }

    public                  AccessPoint() {
        super();
    }

    @Override
    public String           toString() {
        return Ssid + " with " + sessions().size() + " session recorded";
    }
}
