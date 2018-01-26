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
    @Column(name ="vendor")
    public String           vendor = "Unknown";
    @Column(name = "dump")
    public String           dumpInfo;
    @Column(name = "Notes")
    public List<String> Notes = new ArrayList<>();

    private List<Port>      portList;
    public boolean          selected = false;
    public boolean          isItMyDevice = false;
    public Os               osType;

    public                  ExternalHost() {
        super();
    }

    public                  ExternalHost(String buffer) {
        super();
        try {
            //TODO: Parse this !
        } catch (StringIndexOutOfBoundsException e) {
            Log.e(TAG, buffer);
            e.getStackTrace();
        }
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
}
