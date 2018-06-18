package fr.dao.app.Core.Network;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class                    NetworkInformation {
    private String              TAG = "NetworkInformation";
    private WifiManager         mWifiManager;
    public String               myIp = "";
    public String               gateway = "";
    public String               mac = "";
    public String               ssid = "";
    String                      netmask = "";

    NetworkInformation(WifiManager wifiManager, String mac) {
        this.mWifiManager = wifiManager;
        this.mac = mac.replace("\n", "").trim();
    }

    public boolean              isConnectedToNetwork() {
        return !(myIp.contains("0.0.0.0") || gateway.contains("0.0.0.0"));
    }

    public NetworkInformation   init() {
        DhcpInfo dhcpInfo = mWifiManager.getDhcpInfo();
        myIp = intADDRtoStringHostname(dhcpInfo.ipAddress);
        gateway = intADDRtoStringHostname(dhcpInfo.gateway);
        netmask = intADDRtoStringHostname(dhcpInfo.netmask);
        if (netmask.contains("0.0.0.0"))
            netmask = "255.255.255.0";
        String dns1 = intADDRtoStringHostname(dhcpInfo.dns1);
        String dns2 = intADDRtoStringHostname(dhcpInfo.dns2);
        String dhcp = intADDRtoStringHostname(dhcpInfo.serverAddress);
        return this;
    }

    public NetworkInformation   updateInfo() {
        init();
        return this;
    }

    private String              intADDRtoStringHostname(int hostAddress) {
        try {
            byte[] addressBytes = {(byte) (0xff & hostAddress),
                    (byte) (0xff & (hostAddress >> 8)),
                    (byte) (0xff & (hostAddress >> 16)),
                    (byte) (0xff & (hostAddress >> 24))};
            return InetAddress.getByAddress(addressBytes).getHostAddress();
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }

}
