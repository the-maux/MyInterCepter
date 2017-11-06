package su.sniff.cepter.Model.Net;

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

        myIp = NetUtils.intADDRtoStringHostname(dhcpInfo.ipAddress);
        gateway = NetUtils.intADDRtoStringHostname(dhcpInfo.gateway);
        netmask = NetUtils.intADDRtoStringHostname(dhcpInfo.netmask);
        if (netmask.contains("0.0.0.0"))
            netmask = "255.255.255.0";
        dns1 = NetUtils.intADDRtoStringHostname(dhcpInfo.dns1);
        dns2 = NetUtils.intADDRtoStringHostname(dhcpInfo.dns2);
        dhcp = NetUtils.intADDRtoStringHostname(dhcpInfo.serverAddress);
        this.mac = mac;
        Log.d(TAG, "IP:" + myIp + "&GW:" + gateway + "&netmask=" + netmask + "&mac="+mac);
    }

}