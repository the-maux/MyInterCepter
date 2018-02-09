package fr.allycs.app.Core.Database;

import android.util.Log;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Model.Net.Pcap;
import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;

public class                    DBManager {
    private static String       TAG = "DBManager";

    public static Session       saveSession(String ssid, String gateway, List<Host> hosts, String typeScan) {
        Log.d(TAG, "saveNewSession(" + ssid + ", " + gateway + ", " + hosts.size()+ "host)");
        AccessPoint ap = DBAccessPoint.getAccessPoint(ssid);
        Session session = DBSession.saveNewSession(ap, gateway, hosts, typeScan);
        Log.d(TAG, hosts.size() + " new client to save on this session");
        return session;
    }

    public static List<Pcap>    getListPcapFormHost(Host host) {
        List<Pcap> allPcapsInDdd = new Select()
                .from(Pcap.class)
                .orderBy("Date ASC")
                .execute();
        List<Pcap> listPcapWithHostIn = new ArrayList<>();
        for (Pcap pcap : allPcapsInDdd) {
            if (isDeviceInPcap(pcap, host))
                listPcapWithHostIn.add(pcap);
        }
        return listPcapWithHostIn;
    }

    private static boolean      isDeviceInPcap(Pcap pcap, Host host) {
        for (Host hostInPcap : pcap.listDevices()) {
            if (hostInPcap.equals(host))
                return true;
        }
        return false;
    }
}
