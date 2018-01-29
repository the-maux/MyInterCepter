package fr.allycs.app.Model.Target;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import fr.allycs.app.Model.Net.Port;
import fr.allycs.app.Model.Unix.Os;

public class                ExternalHost extends Model {
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
    public List<String>     Notes = new ArrayList<>();
    @Column(name = "deviceType")
    public String           deviceType;
    @Column(name = "TooManyFingerprintMatchForOs")
    public boolean          TooManyFingerprintMatchForOs = false;
    @Column(name = "NetworkDistance")
    public String           NetworkDistance = "Unknow";

    private List<Port>      portList;
    public boolean          selected = false;
    public boolean          isItMyDevice = false;
    public Os               osType;

    public                  ExternalHost() {
        super();
    }

    public List<Port>       getPortList() {
        return portList;
    }

    public void             setPortList(List<Port> portList) {
        this.portList = portList;
    }

    @Override public boolean equals(Object obj) {
        return mac.equals(((Host) obj).mac);
    }

    @Override public String toString() {
        return ip + ":" + mac;
    }

    public static Comparator<Host> comparator = new Comparator<Host>() {
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
    };

    public void             dumpMe() {
        Log.d(TAG, "ip:[" + ip + "]");
        Log.d(TAG, "mac:[" + mac + "]");
        Log.d(TAG, "os:[" + os + "]");
        Log.d(TAG, "osDetail:[" + osDetail + "]");
        Log.d(TAG, "name:[" + name + "]");
        Log.d(TAG, "NetworkDistance:[" + NetworkDistance + "]");
        Log.d(TAG, "TooManyFingerprintMatchForOs:[" + TooManyFingerprintMatchForOs + "]");
        Log.d(TAG, "deviceType:[" + deviceType + "]");
        if (getPortList() != null) {
            Log.d(TAG, "OPENED PORT:");
            for (Port port : getPortList()) {
                Log.d(TAG, "\tPort:" + port.port + " PROTO:[" + port.protocol + "] state:[" + port.state + "]");
            }
        }
    }
}
