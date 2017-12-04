package fr.allycs.app.Model.Target;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.allycs.app.Controller.Core.Conf.Singleton;

@Table(name = "AccessPoint", id = "_id")
public class               AccessPoint extends Model {
    public String          TAG = "AccessPoint";

    @Column(name = "Ssid")
    public String          Ssid;

    @Column(name = "Sessions")
    public List<Session>   Sessions;

    public void             dumpSessions() {
        if (Singleton.getInstance().DebugMode) {
            for (Session session : Sessions) {
                Log.d(TAG, "\t " + Ssid + " : " + session.Gateway.getIp()+ "-[" + session.Gateway.getMac() + "]");
                Log.d(TAG, "\t\t" + new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss", Locale.FRANCE).format(session.Date) + " Devices Connected : " +session.listDevices.size());
            }
            Log.d(TAG, "END:--------------------------");
        }
    }

    public                  AccessPoint() {
        super();
    }

}
