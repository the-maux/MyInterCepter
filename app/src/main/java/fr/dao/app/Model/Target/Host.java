package fr.dao.app.Model.Target;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fr.dao.app.Core.Nmap.Fingerprint;
import fr.dao.app.Model.Net.Service;
import fr.dao.app.Model.Unix.Os;

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
    public String           dumpInfo = null;
    @Column(name = "dumpPort")
    public String           dumpPort = null;
    @Column(name = "Notes")
    public String           Notes = null;
    @Column(name = "deviceType")
    public String           deviceType = "Unknown";
    @Column(name = "TooManyFingerprintMatchForOs")
    public boolean          TooManyFingerprintMatchForOs = false;
    @Column(name = "NetworkDistance")
    public String           NetworkDistance = "Unknown";
    @Column(name = "isSecureComputer")
    public boolean          isSecureComputer = false;
    @Column(name = "firstSeen")
    public Date             firstSeen = null;
    @Column(name = "Hostname")
    public String           Hostname = "Unknown";
    @Column(name = "NetBIOS_Domain")
    public String           NetBIOS_Domain = "Unknown";
    @Column(name = "NetBIOS_Name")
    public String           NetBIOS_Name = "Unknown";
    @Column(name = "NetBIOS_Role")
    public String           NetBIOS_Role = "Unknown";
    @Column(name = "Brand_and_Model")
    public String           Brand_and_Model = "Unknown";
    @Column(name = "Bonjour_Name")
    public String           Bonjour_Name = "Unknown";
    @Column(name = "Bonjour_Services")
    public String           Bonjour_Services = "Unknown";
    @Column(name = "UPnP_Name")
    public String           UPnP_Name = "Unknown";
    @Column(name = "UPnP_Device")
    public String           UPnP_Device = "Unknown";
    @Column(name = "UPnP_Services")
    public String           UPnP_Services = "Unknown";
    @Column(name = "UPnP_Infos")
    public String           UPnP_Infos = "Unknown";
    @Column(name = "PortDump")
    public String           PortDump = "";


    private ArrayList<Service> ServiceActivOnHost = new ArrayList<>();

    public boolean          selected = false;
    public boolean          isItMyDevice = false;
    public Os               osType = Os.Unknow;
    public State            state = State.OFFLINE;

    private Ports           listPorts = null;
    public Ports            Ports() {
        return listPorts;
    }
    public Ports            Ports(ArrayList<String> dumpsPorts) throws Exception {
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
        if (!NetBIOS_Name.contains("Unknown"))
            return NetBIOS_Name;
        if (!UPnP_Name.contains("Unknown"))
            return UPnP_Name;
        if (!Bonjour_Name.contains("Unknown"))
            return Bonjour_Name;
        return ((name.isEmpty() || name.contains("Unknown")) ? ip : name);
    }

    public boolean          equals(Object obj) {
        return mac.contains(((Host) obj).mac);
    }

    public void             dumpMe() {
        Log.i(TAG, "ip: " + ip);// + "]");
        Log.i(TAG, "mac: " + mac);// + "]");
        Log.i(TAG, "vendor: " + vendor);// + "]" + "VENDOR[" + sameHost.vendor + "]");
        Log.i(TAG, "os: " + os);// + "] OS[" + sameHost.os + "]");
        Log.i(TAG, "osType: " + osType.name());// + "] OSTYPE[" + sameHost.osType + "]");
        Log.i(TAG, "osDetail: " + osDetail);// + "] OSDETAIL[" + osDetail + "]");
        Log.i(TAG, "name: " + getName());// + "] NAME[" + sameHost.getName() +"]");
        Log.i(TAG, "NetworkDistance: " + NetworkDistance);//+ "]");
        Log.i(TAG, "TooManyFingerprintMatchForOs: " + TooManyFingerprintMatchForOs);//+ "]");
        Log.i(TAG, "deviceType: " + deviceType);//+ "]");
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
        Log.i(TAG, "END DUMP ---------------------------------------" + ip);
    }

    public String           toString() {
        return ip + ":" + mac;
    }

    public void             copy(Host myDevice) {
        if (osType == Os.Unknow)
            osType = myDevice.osType;
        if (mac.contains("Unknown"))
            mac = myDevice.mac;
        if (vendor.contains("Unknown"))
            vendor = myDevice.vendor;
        if (os.contains("Unknown"))
            os = myDevice.os;
        if (osDetail.contains("Unknown"))
            osDetail = myDevice.osDetail;
        if (name.contains("Unknown"))
            name = myDevice.name;
        if (NetworkDistance.contains("Unknown"))
            NetworkDistance = myDevice.NetworkDistance;
        if (deviceType.contains("Unknown"))
            deviceType = myDevice.deviceType;
        if (firstSeen == null)
            firstSeen = myDevice.firstSeen;
        if (dumpPort == null)
            dumpPort = myDevice.dumpPort;
        if (dumpInfo == null)
            dumpInfo = myDevice.dumpInfo;

        if (Hostname.contains("Unknown"))
            Hostname = myDevice.Hostname;

        if (NetBIOS_Domain.contains("Unknown"))
            NetBIOS_Domain = myDevice.NetBIOS_Domain;
        if (NetBIOS_Name.contains("Unknown"))
            NetBIOS_Name = myDevice.NetBIOS_Name;
        if (NetBIOS_Role.contains("Unknown"))
            NetBIOS_Role = myDevice.NetBIOS_Role;

        if (Bonjour_Name.contains("Unknown"))
            Bonjour_Name = myDevice.Bonjour_Name;
        if (Bonjour_Services.contains("Unknown"))
            Bonjour_Services = myDevice.Bonjour_Services;

        if (UPnP_Name.contains("Unknown"))
            UPnP_Name = myDevice.UPnP_Name;
        if (UPnP_Services.contains("Unknown"))
            UPnP_Services = myDevice.UPnP_Services;

        dumpInfo = myDevice.dumpInfo;
    }

    public void             build() {
        listPorts = new Ports(dumpPort, this);
        //dumpMe();
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
                    return "UNKNOWN";
            }
        }
    }
    public String           getDateString() {
        if (firstSeen == null)
            return "Not recorded";
        return new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE).format(firstSeen);
    }


}