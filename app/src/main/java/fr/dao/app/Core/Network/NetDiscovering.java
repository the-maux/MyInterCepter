package fr.dao.app.Core.Network;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Singleton;

public class                            NetDiscovering {
    private static String               TAG = "NetDiscovering";

    public static ArrayList<String>     readARPTable(ArrayList<String> ipReachable) {
        ArrayList<String> listIpPlusMac = new ArrayList<>();
        try {
            boolean already;
            ArrayList<String> ipsFromArpFile = readIpsTableFromArp(listIpPlusMac);
            for (String reachable : ipReachable) {
                already = false;
                if (reachable != null) {
                    for (String ip : ipsFromArpFile) {
                        if ((reachable.substring(0, reachable.indexOf(":")) + "..").contains(ip + ".."))
                            already = true;
                    }
                    if (!already) {
                        if (!reachable.contains(Singleton.getInstance().network.myIp)) {
                            if (Singleton.getInstance().UltraDebugMode)
                                Log.d(TAG, "ARP_TABLE FIX:" + reachable);
                            listIpPlusMac.add(reachable);
                        }
                    }
                } else {
                    Log.e(TAG, "WTF reachable is null ?");
                }
            }
            String dumpMyDevice = Singleton.getInstance().network.myIp + ":" + Singleton.getInstance().network.mac;
            listIpPlusMac.add(dumpMyDevice);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return listIpPlusMac;
    }

    private static ArrayList<String>    readIpsTableFromArp(ArrayList<String> listIpPlusMac) throws IOException {
        ArrayList<String> listOfIpsAlreadyIn = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
        String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
        String read;
        while ((read = bufferedReader.readLine()) != null) {
            String ip = read.substring(0, read.indexOf(" "));
            Object[] objArr = new Object[1];
            objArr[0] = ip.replace(".", "\\.");
            Matcher matcher = Pattern.compile(String.format(MAC_RE, objArr)).matcher(read);
            if (matcher.matches() && !ip.contains(Singleton.getInstance().network.myIp)) {
                listOfIpsAlreadyIn.add(ip);
                if (Singleton.getInstance().UltraDebugMode)
                    Log.d(TAG, "ARP_TABLE: " + ip + ":" + matcher.group(1));
                listIpPlusMac.add(ip + ":" + matcher.group(1));
            }
        }
        bufferedReader.close();
        Log.d(TAG, listOfIpsAlreadyIn.size() + " host discovered in /proc/net/arp");
        return listOfIpsAlreadyIn;
    }

    public static boolean               initNetworkInfo(Activity activity) {
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null)
            return false;

        String data = wifiManager.getDhcpInfo().toString();
        if (!data.contains("ipaddr") || !data.contains("gateway") || !data.contains("netmask") ) {
            return false;
        }

        String[] res = data.split(" ");
        int netmask = 0;
        for (int i = 0; i < res.length; i++) {
            if (res[i].contains("netmask")) {
                netmask = i + 1;
            }
        }
        if (res[netmask].contains("0.0.0.0")) res[netmask] = "255.255.255.0";
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Singleton.getInstance().network = new NetworkInformation(wifiManager, getMac(wifiInfo));
        Singleton.getInstance().network.init();
        if ((activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE)) != null)
            wifiInfo = ((WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        Singleton.getInstance().network.ssid = wifiInfo.getSSID().replace("\"", "");
        return true;
    }

    public static String                    getMac(WifiInfo wifiInfo) {
        try {
            return new BufferedReader(new RootProcess("GetMacADDR")
                    .exec("cat /sys/class/net/wlan0/address").getInputStreamReader()).readLine().toUpperCase();
        } catch (IOException e) {
            e.printStackTrace();
            return wifiInfo.getMacAddress().toUpperCase();//Using getMacAddress() is not recommended, gna gna gna
        }
    }
}
