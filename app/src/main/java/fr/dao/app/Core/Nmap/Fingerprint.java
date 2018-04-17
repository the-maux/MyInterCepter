package fr.dao.app.Core.Nmap;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;

import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Unix.Os;

/**
 * Supprimer les duplicata External & Host
 */
public class                            Fingerprint {
    private static String               TAG = "Fingerprint";

    public static void                  initHost(Host host) {
        host.build();
        guessosType(host);
        if (host.name.contains("My Device") ||
                host.ip.contentEquals(Singleton.getInstance().network.myIp)) {/* host.isItMyDevice is not saved on BDD for optimiz*/
            host.isItMyDevice = true;
            host.state = Host.State.ONLINE;
            host.osType = Os.Android;
            host.name = "My Device";//Need reafect in case of nmap_mode == 1
        } else if (Fingerprint.isItWindows(host)) {
            host.osType = Os.Windows;
            host.os = "Windows";
            host.osDetail = "Windows";
        } else if (host.osType == Os.Unknow) {
            host.osType = Os.fromString(host.osDetail);
        }
    }

    private static void                 guessosType(Host host) {
        if (host.isItMyDevice) {
            host.osType = Os.Android;
           return;
        }
        if (host.dumpInfo == null)
            host.dumpInfo = host.vendor.toLowerCase() + " ";
         else
            host.dumpInfo = host.dumpInfo.toLowerCase();
        if (host.vendor.contains("Sony")) {
            /**
             * TODO: faire un Thread qui check les port, si c'est open, c'est une ps4
             * 9295/tcp  open  unknown
             * 41800/tcp open  unknown
             * Sinon c'est une TV ou téléphone
             */
            host.osType = Os.Ps4;
            host.os = "FreeBSD 10.X, Sony embedded";
        } else if (host.dumpInfo.contains("bluebird")) {
            host.osType = Os.Bluebird;
            host.os = "Unix/(Aosp)";
        } else if (host.dumpInfo.contains("cisco")) {
            host.osType = Os.Cisco;
            host.os = "BSD/(Cisco NX-OS)";
        } else if (host.dumpInfo.contains("raspberry")) {
            host.osType = Os.Raspberry;
            host.os = "Unix/(Raspbian)";
        } else if (host.dumpInfo.contains("quanta")) {
            host.osType = Os.QUANTA;
            host.os = "Unix/(RedHat 3)";
        } else if (host.dumpInfo.contains("android") || host.dumpInfo.contains("mobile") || host.dumpInfo.contains("samsung") ||
                host.dumpInfo.contains("murata") || host.dumpInfo.contains("huawei") || host.dumpInfo.contains("oneplus") ||
                host.dumpInfo.contains("lg") || host.dumpInfo.contains("motorola")) {
            fingerprintMobile(host, host.dumpInfo);
        } else if (host.dumpInfo.contains("apple") || host.vendor.toLowerCase().contains("apple") || host.osType == Os.Apple) {
            fingerprintApple(host, host.dumpInfo);
        } else if (!(!host.dumpInfo.contains("unix") && !host.dumpInfo.contains("linux") && !host.dumpInfo.contains("bsd"))) {
            host.osType = Os.Linux_Unix;
        } else if (host.dumpInfo.contains("windows") || host.dumpInfo.contains("microsoft")) {
            host.osType = Os.Windows;
        } else {
            host.osType = Os.Unknow;
        }
    }

    private static void                 fingerprintApple(Host host, String infoDevice) {
        host.os = "FreeBSD";
        host.osType = Os.Apple;
        host.os = "Unix/(Mac OS X)";//TODO FINGERPRINT WITH MAC NAME ON zeroconf
        if (host.getName().isEmpty() && host.dumpPort.contains("model=")) {
            String name = host.mac.split(":")[4] + host.mac.split(":")[5];
            host.name = host.vendor.toUpperCase().replaceAll("\\d","") + "-" + name;
        }
    }

    private static void                 fingerprintMobile(Host host, String infoDevice) {
        host.osType = Os.Android;
        host.os = "Unix/(AOSP)";
    }

    public static boolean               isItWindows(Host host) {
        return host.Ports() != null &&
                host.Ports().isPortOpen(135) &&
                host.Ports().isPortOpen(445);
    }

    public static boolean               isItMyGateway(Host host) {
        return host.ip.contains(Singleton.getInstance().network.gateway) &&
                host.ip.length() == Singleton.getInstance().network.gateway.length();
    }

    public static String                getVendorFrom(String mac) {
        String tmp = mac.contains(":") ? mac.replaceAll(":", "").substring(0, 6) : mac ;
        BufferedReader reader = new RootProcess("Nmap")
                .exec("grep \"" + tmp.substring(0, 6) + "\" " + Singleton.getInstance().Settings.FilesPath + "nmap/nmap-mac-prefixes").getReader();
        String buffer;
        StringBuilder s = new StringBuilder("");
        try {
            while (reader != null && (buffer = reader.readLine()) != null) {
                s.append(buffer);
            }
            tmp = s.toString();
            if (tmp.contains(" ")) {
                Log.d(TAG, "HOST[" + mac + "] -> VENDOR[" + tmp.substring(tmp.indexOf(" ")+1, tmp.length()) + "]");
                return tmp.substring(tmp.indexOf(" ")+1, tmp.length());
            } else {
                Log.i(TAG, "HOST[" + mac + "] -> VENDOR[" + "Unknown vendor" + "]");
                return "Unknown vendor";
            }
        } catch (IOException e) {
            Log.e(TAG+"::MAC", "get Mac root error:");
            e.printStackTrace();
        }
        Log.e(TAG, "HOST[" + mac + "] -> VENDOR[" + "Unknown vendor error" + "]");
        return "Unknown";
    }
}
