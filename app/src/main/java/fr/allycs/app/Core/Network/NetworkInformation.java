package fr.allycs.app.Core.Network;

import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;


public class                    NetworkInformation {
    private String              TAG = "NetworkInformation";
    public String               myIp = "";
    public String               gateway = "";
    public String               mac = "";
    public String               netmask = "";
    public String               dns1 = "", dns2 = "";
    public String               dhcp = "";
    public String               Ssid = "";
    public DhcpInfo             dhcpInfo;
    private WifiManager         mWifiManager;

    public                      NetworkInformation(WifiManager wifiManager, String mac) {
        this.mWifiManager = wifiManager;
        this.mac = mac;
        init();
    }

    public boolean              isConnectedToNetwork() {
        return !(myIp.contains("0.0.0.0") || gateway.contains("0.0.0.0"));
    }

    private void                init() {
        this.dhcpInfo = mWifiManager.getDhcpInfo();
        myIp = intADDRtoStringHostname(dhcpInfo.ipAddress);
        gateway = intADDRtoStringHostname(dhcpInfo.gateway);
        netmask = intADDRtoStringHostname(dhcpInfo.netmask);
        if (netmask.contains("0.0.0.0"))
            netmask = "255.255.255.0";
        dns1 = intADDRtoStringHostname(dhcpInfo.dns1);
        dns2 = intADDRtoStringHostname(dhcpInfo.dns2);
        dhcp = intADDRtoStringHostname(dhcpInfo.serverAddress);
        Log.d(TAG, "IP:" + myIp + "&GW:" + gateway + "&netmask=" + netmask + "&mac="+mac);
    }
    public NetworkInformation   updateInfo() {
        init();
        return this;
    }

    public String               intADDRtoStringHostname(int hostAddress) {
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
