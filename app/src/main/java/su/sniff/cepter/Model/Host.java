package su.sniff.cepter.Model;

import android.icu.text.LocaleDisplayNames;
import android.util.Log;
import java.util.List;

/**
 * Created by AdeTek on 07/07/17.
 */

public class Host {
    private String          TAG = "Host";
    private String          ip = "Unknown";
    private String          name = "Unknown";
    private String          mac = "Unknown";
    private String          os = "Unknown";
    private String          vendor = "Unknown";
    private boolean         selected = false;
    private String          dumpInfo;

    /**
     * Format : 192.168.0.12 	(theMaux) : [E8-B1-FC-A6-CF-11] [Windows 7\8\10] ; Intel Corporate
     * @param buffer buffer
     */
    public Host(String buffer) {
        buffer = buffer.replace("\t", " ").replace("  ", " ");
        String beg = buffer.substring(0, buffer.indexOf(":") - 1);
        String mid = buffer.substring(buffer.indexOf(":") + 2, buffer.indexOf(";") - 1);
        String end = buffer.substring(buffer.indexOf(";") + 2);
        ip = beg.substring(0, beg.indexOf("(") - 1);
        name = beg.substring(beg.indexOf("("));
        mac = mid.substring(0, mid.indexOf(" "));
        os = mid.substring(mid.indexOf(" ") + 1);
        vendor = end;
        logHost(buffer);
        this.dumpInfo = buffer;
    }

    /**
     * Log host created in console
     * @param buffer buffer
     */
    private void logHost(String buffer) {
        Log.d(TAG, "buffer " + buffer + "");
        Log.d(TAG, "==> ip " + ip + "");
        Log.d(TAG, "==> name " + name + "");
        Log.d(TAG, "==> mac " + mac + "");
        Log.d(TAG, "==> os " + os + "");
        Log.d(TAG, "==> vendor " + vendor + "");
        Log.d(TAG, "----------------");
    }

    private List<Port>      portList;

    public Host(String ip, String mac, String os, String Vendor) {
        this.ip = ip;
        this.mac = mac;
        this.os = os;
        this.vendor = Vendor;
    }

    public Host(String ip, String mac, String os) {
        this.ip = ip;
        this.mac = mac;
        this.os = os;
    }

    public String           getIp() {
        return ip;
    }

    public void             setIp(String ip) {
        this.ip = ip;
    }

    public String           getMac() {
        return mac.replace("[", "").replace("]", "");
    }

    public String           getOS() {
        return os;
    }

    public String           getVendor() {
        return vendor;
    }

    public List<Port>       getPortList() {
        return portList;
    }

    public void             setPortList(List<Port> portList) {
        this.portList = portList;
    }

    public String           getName() {
        return name;
    }

    public void             setName(String name) {
        this.name = name;
    }

    public boolean          isSelected() {
        return this.selected;
    }

    public void             setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getDumpInfo() {
        return dumpInfo;
    }
}