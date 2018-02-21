package fr.allycs.app.Model.Target;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fr.allycs.app.Core.Database.DBHost;
import fr.allycs.app.Model.Net.Service;

@Table(name = "Network", id = "_id")
public class Network extends Model {
    public static String    NAME_COLUMN = "Network";

    @Column(name = "Ssid")
    public String           Ssid;
    @Column(name = "lastScanDate")
    public java.util.Date   lastScanDate;
    @Column(name = "nbrScanned")
    public int              nbrScanned;
    @Column(name = "Gateway")
    public Host             Gateway;
    @Column(name = "OsNumber")
    public int              nbrOs;
    @Column(name = "service")
    public List<Service>    services;

    @Column(name = "Devices")
    public String           listDevicesSerialized;
    private List<Host>      listDevices = null;
    public List<Host>       listDevices() {
        if (listDevices == null) {
            listDevices = DBHost.getListFromSerialized(listDevicesSerialized);
            Log.d(NAME_COLUMN, "liste Network deserialized " + listDevices.size() + " devices");
        }
        return listDevices;
    }

    public List<SniffSession> SniffSessions() {
        return getMany(SniffSession.class, "Network");
    }

    public String           toString() {
        return new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE)
                .format(lastScanDate) +" " + listDevices().size() + " Devices Connected";
    }

    public String           getDateString() {
        return new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE).format(lastScanDate);
    }

    public Host             getHostFromMac(String mac) {
        for (Host host : listDevices()) {
            if (host.mac.contains(mac))
                return host;
        }
        return null;
    }

    public Network() {
        super();
    }
}
