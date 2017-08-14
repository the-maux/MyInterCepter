package su.sniff.cepter.Model.Target;

/**
 * Created by maxim on 10/08/2017.
 */

public class                NetworkInformation {
    public String           myIp = "";
    public String           gateway = "";
    public String           mac = "";
    public String           netmask = "";

    public NetworkInformation(String ip, String gateway, String netmask, String mac) {
        this.myIp = ip;
        this.gateway = gateway;
        this.mac = mac;
        this.netmask = netmask;
    }
}
