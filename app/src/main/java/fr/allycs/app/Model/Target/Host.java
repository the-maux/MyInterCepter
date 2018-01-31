package fr.allycs.app.Model.Target;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.Nmap.Fingerprint;
import fr.allycs.app.Model.Net.Port;
import fr.allycs.app.Model.Net.Service;
import fr.allycs.app.Model.Net.listPorts;
import fr.allycs.app.Model.Unix.Os;

@Table(name = "Device", id = "_id")
public class                Host extends Model {
    private String          TAG = "Host";
    @Column(name ="ip")
    public String           ip = "Unknown";
    @Column(name ="name")
    public String           name = "";
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
    public List<String>     Notes = new ArrayList<>();
    @Column(name = "deviceType")
    public String           deviceType;
    @Column(name = "TooManyFingerprintMatchForOs")
    public boolean          TooManyFingerprintMatchForOs = false;
    @Column(name = "NetworkDistance")
    public String           NetworkDistance = "Unknow";
    public ArrayList<Service> ServiceActivOnHost = new ArrayList<>();
    public boolean          selected = false;
    public boolean          isItMyDevice = false;
    public Os               osType;

    public List<Session>    Session() {
        return getMany(Session.class, "listDevices");
    }

    private listPorts       listPorts = null;
    public listPorts       Ports() {
//        if (listPorts == null)
//            return null;// CHARGER DEPUIS LA BDD
        return listPorts;
    }

    public listPorts       Ports(ArrayList<String> dumpsPorts) {
        listPorts = new listPorts(dumpsPorts);
        return listPorts;
    }

    public                  Host(String buffer) {
        super();
        try {
            buffer = buffer.trim().replaceAll("\\p{Cntrl}", "?")
                    .replace("\t", " ").replace("  ", " ");
            String beg = buffer.substring(0, buffer.indexOf(":") - 1);
            String mid = buffer.substring(buffer.indexOf(":") + 2, buffer.indexOf(";") - 1);
            String end = buffer.substring(buffer.indexOf(";") + 2);
            ip = beg.substring(0, beg.indexOf("(") - 1).replace("\n", "");
            name = beg.substring(beg.indexOf("(")).replace("\n", "");
            mac = mid.substring(0, mid.indexOf(" ")).replace("\n", "")
                    .replace("[", "").replace("]", "")
                    .replace("-", ":");
            os = mid.substring(mid.indexOf(" ") + 1).replace("\n", "");
            vendor = end.replace("\n", "");
            if (Singleton.getInstance().UltraDebugMode)
                dumpHost();
            dumpInfo = buffer;
            //Fingerprint.initHost(this, ports);
        } catch (StringIndexOutOfBoundsException e) {
            Log.e(TAG, buffer);
            e.getStackTrace();
        }
    }

    public String           getName() {
        return (name.contains("Unknown") ? "-" : name);
    }
    public void             setName(String name) {
        this.name = name;
    }

    public boolean          isServiceActiveOnHost() {
        return !ServiceActivOnHost.isEmpty();
    }
    /**
     * Update service by BonjourManager
     */
    public void             updateServiceHost(Service service) {
        ServiceActivOnHost.add(service);
    }

    private void            dumpHost() {
        Log.i(TAG, "Buffer Device: " + dumpInfo + "");
        Log.i(TAG, "\t  ip " + ip + "");
        Log.i(TAG, "\t  name " + name + "");
        Log.i(TAG, "\t  mac " + mac + "");
        Log.i(TAG, "\t  os " + os + "");
        Log.i(TAG, "\t  vendor " + vendor + "");
    }

    public boolean          equals(Object obj) {
        return mac.equals(((Host) obj).mac);
    }

    public String           toString() {
        return ip + ":" + mac;
    }

    public                  Host() {
        super();
    }

    public void             dumpMe(ArrayList<Host> selectedHostsList) {
        Host sameHost = null;
//        for (Host host : selectedHostsList) {
//            if (host.mac.contentEquals(mac)) {
//                sameHost = host;
//                Log.e(TAG, ip + "IN INTERCEPT SCAN");
//            }
//        }
        if (sameHost == null) {
            Log.e(TAG, ip + " NOT IN INTERCEPT SCAN");
            sameHost = this;
        }
        Log.d(TAG, "ip:[" + ip + "]");
        Log.d(TAG, "mac:[" + mac + "]");
        Log.d(TAG, "vendor:[" + vendor + "]" + "VENDOR[" + sameHost.vendor + "]");
        Log.d(TAG, "os:[" + os + "] OS[" + sameHost.os + "]");
        Log.d(TAG, "osType[" + osType.name() + "] OSTYPE[" + sameHost.osType + "]");
        Log.d(TAG, "osDetail:[" + osDetail + "] OSDETAIL[" + osDetail + "]");
        Log.d(TAG, "name:[" + getName() + "] NAME[" + sameHost.getName() +"]");
        Log.d(TAG, "NetworkDistance:[" + NetworkDistance + "]");
        Log.d(TAG, "TooManyFingerprintMatchForOs:[" + TooManyFingerprintMatchForOs + "]");
        Log.d(TAG, "deviceType:[" + deviceType + "]");
        if (Ports() != null)
            Ports().dump();
        else
            Log.d(TAG, "Ports Not found...");
    }
    public static Comparator<Host> getComparator() {
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