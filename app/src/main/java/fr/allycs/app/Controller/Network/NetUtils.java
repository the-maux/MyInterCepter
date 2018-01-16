package fr.allycs.app.Controller.Network;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.allycs.app.Controller.Core.Conf.Singleton;

/**
 * Created by maxim on 29/06/2017.
 */
public class                NetUtils {
    private static String   TAG = "NetUtils";
    private static String   MAC = null;

    private static String   getMac(String myIp, String gateway) throws FileNotFoundException {
        if (MAC == null) {
            Log.d(TAG, "Reading /proc/net/arp");
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
            String mac = "";
            try {
                String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
                while (true) {
                    String read = bufferedReader.readLine();
                    if (read == null) {
                        break;
                    }
                    Matcher matcher = Pattern.compile(String.format(MAC_RE, read.substring(0, read.indexOf(" ")).replace(".", "\\."))).matcher(read);
                    if (matcher.matches()) {
                        mac = matcher.group(1);
                        if (myIp.equals(gateway)) {
                            break;
                        }
                    }
                }
                bufferedReader.close();
                return mac;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            return MAC;
        return "MAC INTROUVABLE";
    }
    public static boolean   isMyDeviceIn() {
        return true;
    }

    public static void      dumpListHostFromARPTableInFile(Context context, ArrayList<String> ipReachable) {
        Log.i(TAG, "Dump list devices from Arp Table");
        try {
            ArrayList<String> listOfIpsAlreadyIn = new ArrayList<>();
            FileOutputStream hostListFile = new FileOutputStream(new File(Singleton.getInstance().FilesPath + "hostlist"));
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
            String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
            String read;
            while ((read = bufferedReader.readLine()) != null) {
                String ip = read.substring(0, read.indexOf(" "));
                Object[] objArr = new Object[1];
                objArr[0] = ip.replace(".", "\\.");
                Matcher matcher = Pattern.compile(String.format(MAC_RE, objArr)).matcher(read);
                if (matcher.matches()) {
                    listOfIpsAlreadyIn.add(ip);
                    hostListFile.write((ip + ":" + matcher.group(1) + "\n").getBytes());
                }
            }
            Log.d(TAG, listOfIpsAlreadyIn.size() + " new host discovered in /proc/arp");
            boolean already;
            for (Iterator<String> iterator = ipReachable.iterator(); iterator.hasNext(); ) {//Don't Foreach
                String reachable = iterator.next();
                already = false;
                for (String s : listOfIpsAlreadyIn) {
                    if ((reachable.substring(0, reachable.indexOf(":")) + "..").contains(s + "..")) {
                        already = true;
                    }
                }
                if (!already) {
                    hostListFile.write((reachable + "\n").getBytes());
                }
            }
            bufferedReader.close();
            hostListFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String    intADDRtoStringHostname(int hostAddress) {
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

    public static boolean   initNetworkInfo(Activity activity) throws FileNotFoundException {
        WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null)
            return false;
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        String data = wifiManager.getDhcpInfo().toString();
        if (!data.contains("ipaddr") || !data.contains("gateway") || !data.contains("netmask") ) {
            return false;
        }
        String[] res = data.split(" ");
        int ip = 0, gw = 0, netmask = 0;
        for (int i = 0; i < res.length; i++) {
            if (res[i].contains("ipaddr")) {
                ip = i + 1;
            } else if (res[i].contains("gateway")) {
                gw = i + 1;
            } else if (res[i].contains("netmask")) {
                netmask = i + 1;
            }
        }
        if (res[netmask].contains("0.0.0.0"))
            res[netmask] = "255.255.255.0";
        Singleton.getInstance().network = new NetworkInformation(dhcpInfo, NetUtils.getMac(res[ip], res[gw]));
        WifiInfo wifiInfo = null;
        if ((activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE)) != null) {
            wifiInfo = ((WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        }
        Singleton.getInstance().network.Ssid = wifiInfo.getSSID().replace("\"", "");
        return true;
    }
}
