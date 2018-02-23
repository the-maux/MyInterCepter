package fr.dao.app.Core.Database;

import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Model.Net.Pcap;
import fr.dao.app.Model.Target.Host;

public class                    DBManager {
    private static String       TAG = "DBManager";

    public static List<Pcap>    getListPcapFormHost(Host host) {
        List<Pcap> allPcapsInDdd = new Select()
                .from(Pcap.class)
                .orderBy("lastScanDate ASC")
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
