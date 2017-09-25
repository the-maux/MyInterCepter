package su.sniff.cepter.Controller.Network;

import android.content.Context;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import su.sniff.cepter.Controller.System.Singleton;

/**
 * Created by maxim on 29/06/2017.
 */
public class                NetUtils {
    private static String   TAG = "NetUtils";
    private static String   MAC = null;

    public static String    getMac(String myIp, String gateway) throws FileNotFoundException {
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

    /**
     **  Guessing list of host by reading ARP table and dump it in ./hostlist file
     **/
    public static void      dumpListHostFromARPTableInFile(Context context, ArrayList<String> ipReachable) {
        Log.i(TAG, "Dump list host from Arp Table");
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
                    //Log.d(TAG, "dumpHOSTFILE:" + ip + ":" + matcher.group(1));
                    hostListFile.write((ip + ":" + matcher.group(1) + "\n").getBytes());
                }
            }
            Log.d(TAG, listOfIpsAlreadyIn.size() + " new host discovered in /proc/arp");
            boolean flag;
            for (String reachable : ipReachable) {
                flag = false;
                Log.d(TAG, "asking for:" + reachable);
                for (String s : listOfIpsAlreadyIn) {
                    if ((reachable.substring(0, reachable.indexOf(":")) + "..").contains(s + "..")) {
                        Log.d(TAG, s + "-Alreadyin>" + reachable.substring(0, reachable.indexOf(":")));
                        flag = true;
                    }
                }
                if (!flag) {
                    Log.d(TAG, "dumpHOSTFILE:" + reachable + "& flag == " + flag);
                    hostListFile.write((reachable + "\n").getBytes());
                } else if (flag) {
                    Log.d("ScanNetmask", "Detected but not added::" + reachable + "with flag at :"+flag);
                }
            }
            bufferedReader.close();
            hostListFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String    intADDRtoStringHostname(int hostAddress) {
        byte[] addressBytes = { (byte)(0xff & hostAddress),
                (byte)(0xff & (hostAddress >> 8)),
                (byte)(0xff & (hostAddress >> 16)),
                (byte)(0xff & (hostAddress >> 24)) };
        try {
            return InetAddress.getByAddress(addressBytes).getHostAddress();
        } catch (UnknownHostException e) {
            throw new AssertionError();
        }
    }
}
