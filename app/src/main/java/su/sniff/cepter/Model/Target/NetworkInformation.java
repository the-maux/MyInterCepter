package su.sniff.cepter.Model.Target;

import android.net.DhcpInfo;
import android.util.Log;

import su.sniff.cepter.Controller.Network.NetUtils;

/**
 * Created by maxim on 10/08/2017.
 */

public class                NetworkInformation {
    private String          TAG = "NetworkInformation";
    public String           myIp = "";
    public String           gateway = "";
    public String           mac = "";
    public String           netmask = "";
    public String           dns1 = "", dns2 = "";
    public String           dhcp = "";

    public NetworkInformation(DhcpInfo dhcpInfo, String mac) {

        this.myIp = NetUtils.intADDRtoStringHostname(dhcpInfo.ipAddress);
        this.gateway = NetUtils.intADDRtoStringHostname(dhcpInfo.gateway);
        this.netmask = NetUtils.intADDRtoStringHostname(dhcpInfo.netmask);
        this.dns1 = NetUtils.intADDRtoStringHostname(dhcpInfo.dns1);
        this.dns2 = NetUtils.intADDRtoStringHostname(dhcpInfo.dns2);
        this.dhcp = NetUtils.intADDRtoStringHostname(dhcpInfo.serverAddress);
        this.mac = mac;
        Log.d(TAG, "IP:" + myIp + "&GW:" + gateway + "&netmask=" + netmask + "&mac="+mac);
    }

}
