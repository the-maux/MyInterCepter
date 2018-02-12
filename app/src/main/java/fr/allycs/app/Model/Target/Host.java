package fr.allycs.app.Model.Target;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.allycs.app.Core.Nmap.Fingerprint;
import fr.allycs.app.Model.Net.Service;
import fr.allycs.app.Model.Unix.Os;

@Table(name = "Device", id = "_id")
public class                Host extends Model {
    private String          TAG = "Host";
    @Column(name ="ip")
    public String           ip = "Unknown";
    @Column(name ="name")
    public String           name = "Unknown";
    @Column(name ="mac")
    public String           mac = "Unknown";
    @Column(name ="os")
    public String           os = "Unknown";
    @Column(name ="osDetail")
    public String           osDetail = "Unknown";
    @Column(name ="vendor")
    public String           vendor = "Unknown";
    @Column(name = "dump")
    public String           dumpInfo;
    @Column(name = "Notes")
    public String           Notes;
    @Column(name = "deviceType")
    public String           deviceType;
    @Column(name = "TooManyFingerprintMatchForOs")/*TODO when BDD is false and new scan is true don't save*/
    public boolean          TooManyFingerprintMatchForOs = false;
    @Column(name = "NetworkDistance")
    public String           NetworkDistance = "Unknow";
    public ArrayList<Service> ServiceActivOnHost = new ArrayList<>();
    public List<Session>    Session() {
        return getMany(Session.class, "listDevices");
    }

    public boolean          selected = false;
    public boolean          isItMyDevice = false;
    public Os               osType;
    private Ports           listPorts = null;
    public Ports            Ports() {
        return listPorts;
    }
    public Ports            Ports(ArrayList<String> dumpsPorts) {
        listPorts = new Ports(dumpsPorts, this);
        return listPorts;
    }

    public                  Host() {
        super();
    }

    public void             updateServiceHost(Service service) {
        ServiceActivOnHost.add(service);
    }

    public String           getName() {
        return (name.contains("Unknown") ? "" : name);
    }

    public String           getOneName() {
        return (getName().isEmpty()) ? ip : getName();
    }


    public void             setName(String name) {
        this.name = name;
    }

    public boolean          equals(Object obj) {
        return mac.equals(((Host) obj).mac);
    }

    public void             dumpMe(ArrayList<Host> selectedHostsList) {
//        Host sameHost = null;
//        for (Host host : selectedHostsList) {
//            if (host.mac.contentEquals(mac)) {
//                sameHost = host;
//                Log.e(TAG, ip + "IN INTERCEPT SCAN");
//            }
//        }
//        if (sameHost == null) {
//            Log.e(TAG, ip + " NOT IN INTERCEPT SCAN");
//            sameHost = this;
//        }
        Log.i(TAG, "ip: " + ip);// + "]");
        Log.i(TAG, "mac: " + mac);// + "]");
        Log.i(TAG, "vendor: " + vendor);// + "]" + "VENDOR[" + sameHost.vendor + "]");
        Log.i(TAG, "os: " + os);// + "] OS[" + sameHost.os + "]");
        Log.i(TAG, "osType: " + osType.name());// + "] OSTYPE[" + sameHost.osType + "]");
        Log.i(TAG, "osDetail: " + osDetail);// + "] OSDETAIL[" + osDetail + "]");
        Log.i(TAG, "name: " + getName());// + "] NAME[" + sameHost.getName() +"]");
        Log.i(TAG, "NetworkDistance: " + NetworkDistance );//+ "]");
        Log.i(TAG, "TooManyFingerprintMatchForOs: " + TooManyFingerprintMatchForOs );//+ "]");
        Log.i(TAG, "deviceType: " + deviceType );//+ "]");

        if (dumpInfo == null)
            Log.d(TAG, "NO DUMP /!\\ : " + ip);
        else
            Log.i(TAG, "DUMPINFO::" + dumpInfo);
        if (Ports() != null)
            Ports().dump();
        else
            Log.d(TAG, "Ports Not found...");
        if (osType == Os.Unknow)
            Log.d(TAG, "isItWindowsPort() => " + Fingerprint.isItWindows(this));
        Log.i(TAG, "END DUMP ---------");
    }

    public String           toString() {
        return ip + ":" + mac;
    }
    public static           Comparator<Host> getComparator() {
        return new Comparator<Host>() {
            @Override
            public int compare(Host o1, Host o2) {
                String ip1[] = o1.ip.replace(" ", "").replace(".", "::").split("::");
                String ip2[] = o2.ip.replace(" ", "").replace(".", "::").split("::");
                if (Integer.parseInt(ip1[2]) > Integer.parseInt(ip2[2]))
                    return 1;
                else if (Integer.parseInt(ip1[2]) < Integer.parseInt(ip2[2]))
                    return -1;
                else if (Integer.parseInt(ip1[3]) > Integer.parseInt(ip2[3]))
                    return 1;
                else if (Integer.parseInt(ip1[3]) < Integer.parseInt(ip2[3]))
                    return -1;
                return 0;
            }

            ;
        };
    }

}