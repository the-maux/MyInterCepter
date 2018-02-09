package fr.allycs.app.Core.Network;

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

import fr.allycs.app.Core.Configuration.Singleton;

public class                NetUtils {
    private static String   TAG = "NetUtils";

    public static ArrayList<String> readARPTable(Context context, ArrayList<String> ipReachable) {
        Log.i(TAG, "Dump list devices from Arp Table");
        ArrayList<String> listIpPlusMac = new ArrayList<>();
        try {
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
                    listIpPlusMac.add(ip + ":" + matcher.group(1));
                }
            }
            Log.d(TAG, listOfIpsAlreadyIn.size() + " new host discovered in /proc/arp");
            boolean already;
            for (String reachable : ipReachable) {//Don't Foreach
                already = false;
                for (String ip : listOfIpsAlreadyIn) {
                    if ((reachable.substring(0, reachable.indexOf(":")) + "..").contains(ip + ".."))
                        already = true;
                }
                if (!already) {
                    if (!reachable.contains(Singleton.getInstance().network.myIp)) {
                        listIpPlusMac.add(reachable);
                    }
                }
            }
            String dumpMyDevice = Singleton.getInstance().network.myIp + ":" + Singleton.getInstance().network.mac;
            listIpPlusMac.add(dumpMyDevice);
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listIpPlusMac;
    }

    public static boolean   initNetworkInfo(Activity activity) {
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
        Singleton.getInstance().network = new NetworkInformation(wifiManager, wifiInfo.getMacAddress());
        if ((activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE)) != null)
            wifiInfo = ((WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        Singleton.getInstance().network.Ssid = wifiInfo.getSSID().replace("\"", "");
        return true;
    }
}
