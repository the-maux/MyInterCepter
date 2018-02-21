package fr.allycs.app.Model.Target;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

import fr.allycs.app.Core.Configuration.Singleton;

@Deprecated
@Table(name = "AccessPoint", id = "_id")
public class                AccessPoint extends Model {
    public static String    TAG = "AccessPoint";

    @Column(name = "Ssid")
    public String           Ssid;
    public int              nbrSession;

    public List<Network>    sessions() {
        List<Network> sessions = getMany(Network.class, "AccessPoint");
        nbrSession = sessions.size();
        return sessions;
    }

    public void             dumpSessions() {
        if (Singleton.getInstance().DebugMode) {
            for (Network session : sessions()) {
                if (session.Gateway == null) {
                    Log.d(TAG, "Network is empty");
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

    public String           toString() {
        return Ssid + " with " + sessions().size() + " session recorded";
    }
}
