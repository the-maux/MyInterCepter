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

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Model.Net.Service;

import static fr.dao.app.Model.Config.Action.ActionType.SCAN;

@Table(name = "NetworkInformation", id = "_id")
public class                Network extends Model {
    public static String    NAME_COLUMN = "NetworkInformation";
    @Column(name = "ssid")
    public String           Ssid;
    @Column(name = "lastScanDate")
    public java.util.Date   lastScanDate;
    @Column(name = "Gateway")
    public Host             Gateway;
    @Column(name = "OsNumber")
    public int              nbrOs;
    @Column(name = "nbrScanned")
    public int              nbrScanned;

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
            Log.d(NAME_COLUMN, "liste NetworkInformation deserialized " + listDevices.size() + " devices");

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
        if (lastScanDate == null)
            return listDevices().size() + " Devices Connected";
        return new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE).format(lastScanDate) + " ";
    }

    public String           getDateString() {
        return new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE).format(lastScanDate);
    }

    public synchronized Host getHostFromMac(String mac) throws UnknownHostException {
        List<Host> hosts = listDevices();
        for (Host host : hosts) {
            if (host.mac.contains(mac)) {
                return host;
            }
        }
        throw new UnknownHostException("Not host found in BDD with this mac[" + mac + "]");
    }

    public                  Network() {
        super();
    }

    public boolean          safeUpdateGateway(Host host) {
        if (Singleton.getInstance().Session != null) {
            Singleton.getInstance().Session.addAction(SCAN, false);
            Log.i(NAME_COLUMN, "Gateway ARP CHECKED AND OK");
        } else
            Log.e(NAME_COLUMN, "defensif action set but Session can be loaded");
        return Gateway == null || Gateway.mac.contains(host.mac);
    }
}
