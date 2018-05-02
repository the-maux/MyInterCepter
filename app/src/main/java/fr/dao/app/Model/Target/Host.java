package fr.dao.app.Model.Target;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.dao.app.Core.Scan.Fingerprint;
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
    @Column(name = "Deepest_Scan")
    public int              Deepest_Scan = 0; //0:VENDOR | 1:NMAP_BASIC | 2:NMAP_SCRIPT | 3:VulsScan?

    public List<VulnerabilityScan> VulnerabilityScan() {
        return getMany(VulnerabilityScan.class, "Host");
    }


    private ArrayList<Service> ServiceActivOnHost = new ArrayList<>();

    public boolean          selected = false;
    public boolean          isItMyDevice = false;
    public Os               osType = Os.Unknow;
    public State            state = State.OFFLINE;

    private Ports           listPorts = null;
    public Ports            getPorts() {
        /*  Build Ports from String dumped in BDD */
        if (listPorts == null && dumpPort != null && !dumpPort.isEmpty())
            listPorts = new Ports(this);
        return listPorts;
    }
    public void            buildPorts(ArrayList<String> dumpsPorts) {
        dumpPort = StringUtils.join(dumpsPorts, "\n");
        listPorts = new Ports(this);
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
        if (getPorts() != null)
            getPorts().dump();
        else
            Log.d(TAG, "getPorts Not found...");
        if (osType == Os.Unknow)
            Log.d(TAG, toString() + " isItWindowsPort() => " + Fingerprint.isItWindows(this));
        Log.i(TAG, "END DUMP ---------------------------------------" + ip);
    }

    public String           toString() {
        return ip + ":" + mac;
    }

    public void             copy(Host myDevice) {
        /*  General */
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
        /*  Dump    */
        if (dumpPort == null)
            dumpPort = myDevice.dumpPort;
        if (dumpInfo == null)
            dumpInfo = myDevice.dumpInfo;
        /*  Hostname*/
        if (Hostname.contains("Unknown"))
            Hostname = myDevice.Hostname;
        /*  NetBios */
        if (NetBIOS_Domain.contains("Unknown"))
            NetBIOS_Domain = myDevice.NetBIOS_Domain;
        if (NetBIOS_Name.contains("Unknown"))
            NetBIOS_Name = myDevice.NetBIOS_Name;
        if (NetBIOS_Role.contains("Unknown"))
            NetBIOS_Role = myDevice.NetBIOS_Role;
        /*  Bonjour */
        if (Bonjour_Name.contains("Unknown"))
            Bonjour_Name = myDevice.Bonjour_Name;
        if (Bonjour_Services.contains("Unknown"))
            Bonjour_Services = myDevice.Bonjour_Services;
        /*  Upnp    */
        if (UPnP_Name.contains("Unknown"))
            UPnP_Name = myDevice.UPnP_Name;
        if (UPnP_Services.contains("Unknown"))
            UPnP_Services = myDevice.UPnP_Services;
        dumpInfo = myDevice.dumpInfo;
    }

    public String           getDateString() {
        if (firstSeen == null)
            return "Not recorded";
        return new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE).format(firstSeen);
    }


}