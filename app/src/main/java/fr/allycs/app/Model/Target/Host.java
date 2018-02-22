package fr.allycs.app.Model.Target;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.allycs.app.Core.Nmap.Fingerprint;
import fr.allycs.app.Model.Net.Service;
import fr.allycs.app.Model.Unix.Os;

@Table(name = "Host", id = "_id")
public class                Host extends Model {
    private String          TAG = "Host";
    @Column(name = "ip")
    public String           ip = "Unknown";
    @Column(name = "name")
    public String           name = "Unknown";
    @Column(name = "mac")
    public String           mac = "Unknown";
    @Column(name = "os")
    public String           os = "Unknown";
    @Column(name = "osDetail")
    public String           osDetail = "Unknown";
    @Column(name = "vendor")
    public String           vendor = "Unknown";
    @Column(name = "dump")
    public String           dumpInfo;
    @Column(name = "Notes")
    public String           Notes;
    @Column(name = "deviceType")
    public String           deviceType;
    @Column(name = "TooManyFingerprintMatchForOs")
    public boolean          TooManyFingerprintMatchForOs = false;
    @Column(name = "NetworkDistance")
    public String           NetworkDistance = "Unknown";
    @Column(name = "isSecureComputer")
    public boolean          isSecureComputer = false;

    private ArrayList<Service> ServiceActivOnHost = new ArrayList<>();
   /* public List<Network>    Network() {
        return getMany(Network.class, "listDevices");
    }*/

    public boolean          selected = false;
    public boolean          isItMyDevice = false;
    public Os               osType = Os.Unknow;
    public State            state = State.OFFLINE;

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
        return ((name.isEmpty() || name.contains("Unknown")) ? ip : name);
    }

    public void             setName(String name) {
        this.name = name;
    }

    public boolean          equals(Object obj) {
        return mac.equals(((Host) obj).mac);
    }

    public void             dumpMe(ArrayList<Host> selectedHostsList) {
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
            Log.d(TAG, toString() + " isItWindowsPort() => " + Fingerprint.isItWindows(this));
        Log.i(TAG, "END DUMP ---------");
    }

    public String           toString() {
        return ip + ":" + mac;
    }

    public enum         State   {
        OFFLINE(0), ONLINE(1), FILTERED(2), UNKNOW(3);

        private int value;
        private static Map map = new HashMap<>();

        State(int value) {
            this.value = value;
        }

        static {
            for (Host.State pageType : Host.State.values()) {
                map.put(pageType.value, pageType);
            }
        }
        public static Host.State valueOf(int pageType) {
            return (Host.State) Host.State.map.get(pageType);
        }

        public static Host.State valueOf(String pageType, int a) {
            pageType = pageType.toUpperCase().replace("|", "_");
            switch (pageType) {
                case "FILTERED":
                    return FILTERED;
                case "OFFLINE":
                    return OFFLINE;
                case "ONLINE":
                    return ONLINE;
                default:
                    return FILTERED;
            }
        }

        public int              getValue() {
            return value;
        }

        public String           toString() {
            switch (valueOf(value)) {
                case FILTERED:
                    return "FILTERED";
                case OFFLINE:
                    return "OFFLINE";
                case ONLINE:
                    return "ONLINE";
                default:
                    return "UNKNOW";
            }
        }
    }
    
}