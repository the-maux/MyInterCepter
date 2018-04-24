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
import fr.dao.app.Model.Target.Host;

public class                            NetDiscovering {
    private static String               TAG = "NetDiscovering";

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

    public static ArrayList<String>     readARPTable(ArrayList<String> ipReachable) {
        ArrayList<String> listIpPlusMac = new ArrayList<>();
     //   try {
            boolean already;
            ArrayList<String> ipsFromArpFile = NetDiscovering.readIpsTableFromRoot(listIpPlusMac);
            for (String reachable : ipReachable) {
                already = false;
                if (reachable != null) {
                    for (String ip : ipsFromArpFile) {
                        if ((reachable.substring(0, reachable.indexOf(":")) + "..").contains(ip + ".."))
                            already = true;
                    }
                    if (!already) {
                        if (!reachable.contains(Singleton.getInstance().network.myIp)) {
                            if (Singleton.getInstance().Settings.UltraDebugMode)
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
/*
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return listIpPlusMac;
    }

    public static ArrayList<String>     readIpsTableFromRoot(final ArrayList<String> listIpPlusMac) {
     /*   new Thread(new Runnable() {
            @Override
            public void run() {*/
        ArrayList<String> IpExtracted = new ArrayList<>();
        String tmp, MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new RootProcess("Nmap", "/data/data/fr.dao.app/")
                .exec("cat /proc/net/arp").getReader();
        try {

            while ((tmp = reader.readLine()) != null && !tmp.contains("Nmap done")) {
                String ip = tmp.substring(0, tmp.indexOf(" "));
                Object[] objArr = new Object[1];
                objArr[0] = ip.replace(".", "\\.");
                Matcher matcher = Pattern.compile(String.format(MAC_RE, objArr)).matcher(tmp);
                if (matcher.matches() && !ip.contains(Singleton.getInstance().network.myIp) && !tmp.isEmpty()) {
                    IpExtracted.add(ip);
                    stringBuilder.append(ip);
                    listIpPlusMac.add(ip + ":" + matcher.group(1));
                }
            }
            Log.d(TAG, IpExtracted.size() + " host discovered in /proc/net/arp");
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "READ arp root error:");

        }

        /*   }).start();*/
        // return new ArrayList<>();
        return IpExtracted;
    }

    /**
     * Not working on > And 7.1.1
     * @param listIpPlusMac
     * @return
     * @throws IOException
     */
    private static ArrayList<String>    readIpsTableFromArp(ArrayList<String> listIpPlusMac) throws IOException {
        ArrayList<String> listOfIP = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
        String line, MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
        while ((line = bufferedReader.readLine()) != null) {
            String ip = line.substring(0, line.indexOf(" "));
            Object[] objArr = new Object[1];
            objArr[0] = ip.replace(".", "\\.");
            Matcher matcher = Pattern.compile(String.format(MAC_RE, objArr)).matcher(line);
            if (matcher.matches() && !ip.contains(Singleton.getInstance().network.myIp)) {
                listOfIP.add(ip);
                if (Singleton.getInstance().Settings.UltraDebugMode)
                    Log.d(TAG, "ARP_TABLE: " + ip + ":" + matcher.group(1));
                listIpPlusMac.add(ip + ":" + matcher.group(1));
            }
        }
        bufferedReader.close();
        Log.d(TAG, listOfIP.size() + " host discovered in /proc/net/arp");
        return listOfIP;
    }


    public static String                getMac(WifiInfo wifiInfo) {

        BufferedReader reader = new RootProcess("GetMacADDR")
                .exec("cat /sys/class/net/wlan0/address").getReader();
        try {
            String tmp;
            StringBuilder stringBuilder = new StringBuilder();
            while ((tmp = reader.readLine()) != null) {
                stringBuilder.append(tmp);
            }
            Log.i(TAG, "ADDR MAC DETECTED[" + stringBuilder.toString().toUpperCase() + "]");
            return stringBuilder.toString().toUpperCase();
        } catch (IOException e) {
            e.printStackTrace();
            return wifiInfo.getMacAddress().toUpperCase();//Using getMacAddress() is not recommended, gna gna gna
        }
    }

}
