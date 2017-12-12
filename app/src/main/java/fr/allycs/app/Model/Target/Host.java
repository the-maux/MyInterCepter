package fr.allycs.app.Model.Target;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Network.Discovery.Fingerprint;
import fr.allycs.app.Model.Net.Port;
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
    @Column(name ="vendor")
    public String           vendor = "Unknown";
    @Column(name = "dump")
    public String           dumpInfo;
    @Column(name = "Notes")
    public ArrayList<String> Notes = new ArrayList<>();

    public ArrayList<Service> ServiceActivOnHost = new ArrayList<>();
    public boolean          isServiceActiveOnHost = false;
    private List<Port>      portList;
    public boolean          selected = false;
    public boolean          isItMyDevice = false;
    public Os               osType;

    public                  Host(String buffer) {
        super();
        try {
            buffer = buffer.replace("\t", " ").replace("  ", " ");
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
            Fingerprint.initHost(this);
        } catch (StringIndexOutOfBoundsException e) {
            Log.e(TAG, buffer);
            e.getStackTrace();
        }
    }

    public String           getName() {
        return (name.contains("Unknown") ? ip : name);
    }

    public void             setName(String name) {
        this.name = name;
    }

    public String           getGenericId() {
        return mac.replace(":", "");
    }

    public static Comparator<Host> comparator = new Comparator<Host>() {
        @Override
        public int compare(Host o1, Host o2) {
            String ip1[] = o1.ip.replace(".", "::").split("::");
            String ip2[] = o2.ip.replace(".", "::").split("::");
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
    };

    public boolean          isServiceActiveOnHost() {
        return !ServiceActivOnHost.isEmpty();
    }
    /**
     * Update service by BonjourManager
     */
    public void             updateServiceHost(Service service) {
        ServiceActivOnHost.add(service);
    }

    public List<Port>       getPortList() {
        return portList;
    }

    public void             setPortList(List<Port> portList) {
        this.portList = portList;
    }

    private void            dumpHost() {
        Log.i(TAG, "Buffer Device: " + dumpInfo + "");
        Log.i(TAG, "\t  ip " + ip + "");
        Log.i(TAG, "\t  name " + name + "");
        Log.i(TAG, "\t  mac " + mac + "");
        Log.i(TAG, "\t  os " + os + "");
        Log.i(TAG, "\t  vendor " + vendor + "");
    }

    @Override public boolean equals(Object obj) {
        return mac.equals(((Host) obj).mac);
    }

    @Override public String toString() {
        return ip + ":" + mac;
    }

    public                  Host() {
        super();
    }
}