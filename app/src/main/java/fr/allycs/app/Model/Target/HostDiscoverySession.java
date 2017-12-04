package fr.allycs.app.Model.Target;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.google.gson.annotations.SerializedName;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.allycs.app.Controller.Core.Conf.Singleton;

@Table(name = "Session", id = "_id")
public class                HostDiscoverySession extends Model {
    public String          TAG = "HostDiscoverySession";

    @Column(name = "Date")
    public Date            Date;

    @Column(name = "Ssid")
    public String          Ssid;

    @Column(name = "AccessPoint")
    public Host            AccessPoint;

    public List<Host>       listDevices() {
        return getMany(Host.class, "Device");
    }

    public void             dumpSessions() {
        if (Singleton.getInstance().DebugMode) {
            SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss", Locale.FRANCE);
            Log.d(TAG, "Dumping(" + Ssid +"):" + simpleDate.format(Date));
            Log.d(TAG, "\t Accesspoint : " + AccessPoint.getIp()+ "-[" + AccessPoint.getMac() + "]");
            Log.d(TAG, "\t Devices Connected :");
            for (Host host : listDevices()) {
                Log.d(TAG, "\t\t " + AccessPoint.getIp()+ "-[" + AccessPoint.getMac() + "]");
            }
            Log.d(TAG, "END:--------------------------");
        }
    }

    public                  HostDiscoverySession() {
        super();
    }

}
