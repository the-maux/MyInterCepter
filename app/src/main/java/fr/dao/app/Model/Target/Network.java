package fr.dao.app.Model.Target;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Model.Net.Service;

@Table(name = "Network", id = "_id")
public class                Network extends Model {
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
    @Column(name = "Devices")
    public String           listDevicesSerialized;
    private ArrayList<Host> listDevices = null;
    public ArrayList<Host>  listDevices() {
        if (listDevices == null) {
            if (listDevicesSerialized == null) {
                listDevices = new ArrayList<>();
                return listDevices;
            }
            listDevices = DBHost.getListFromSerialized(listDevicesSerialized);
            Log.d(NAME_COLUMN, "liste Network deserialized " + listDevices.size() + " devices");

        }
        return listDevices;
    }

    public List<Service>    Services() {
        return getMany(Service.class, "Network");
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

    public synchronized Host getHostFromMac(String mac) throws UnknownHostException {
        List<Host> hosts = listDevices();
        for (Host host : hosts) {
            if (host.mac.contains(mac)) {
               // Log.d(NAME_COLUMN, "getHostFromMac(" + host.toString() + ") in list of " + listDevices().size());
                return host;
            }
        }
       // Log.d(NAME_COLUMN, "not found");
        throw new UnknownHostException("Not host found in BDD with this mac[" + mac + "]");
    }

    public              Network() {
        super();
    }
}
