package su.sniff.cepter.Model;

import java.util.List;

/**
 * Created by the-maux on 07/07/17.
 */
public class                Client {
    private String          Ip = "Unknown";
    private String          Mac = "Unknown";
    private String          OS = "Unknown";
    private String          Vendor = "Unknown";
    private List<Port>      portList;

    public                  Client(String ip, String mac, String os, String Vendor) {
        this.Ip = ip;
        this.Mac = mac;
        this.OS = os;
        this.Vendor = Vendor;
    }

    public                  Client(String ip, String mac, String os) {
        this.Ip = ip;
        this.Mac = mac;
        this.OS = os;
    }

    public String           getIp() {
        return Ip;
    }

    public void             setIp(String ip) {
        Ip = ip;
    }

    public String           getMac() {
        return Mac;
    }

    public String           getOS() {
        return OS;
    }

    public String           getVendor() {
        return Vendor;
    }

    public List<Port>       getPortList() {
        return portList;
    }

    public void             setPortList(List<Port> portList) {
        this.portList = portList;
    }
}
