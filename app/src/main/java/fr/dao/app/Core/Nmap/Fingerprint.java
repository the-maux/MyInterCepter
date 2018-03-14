package fr.dao.app.Core.Nmap;

import android.content.Context;
import android.widget.ImageView;

import java.util.Comparator;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Unix.Os;
import fr.dao.app.R;
import fr.dao.app.View.Behavior.MyGlideLoader;

/**
 * Supprimer les duplicata External & Host
 */
public class                            Fingerprint {
    private static String               TAG = "Fingerprint";

    public static void                  initHost(Host host) {
        host.build();
        isItMyDevice(host);
        guessosType(host);
        if (Fingerprint.isItWindows(host)) {
            host.osType = Os.Windows;
            host.os = "Windows";
            host.osDetail = "Windows";
        }
        if (host.osType == Os.Unknow) {
            host.osType = Os.fromString(host.osDetail);
        }
    }

    private static void                 guessosType(Host host) {
        if (host.isItMyDevice) {
           return;
        }
        if (host.dumpInfo == null) {
            host.osType = Os.Unknow;
            return;
        }
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
        } else
            host.osType = Os.Unknow;
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

    public static boolean               isItMyDevice(Host host) {
        if (host.ip.contains(Singleton.getInstance().network.myIp)) {
            host.isItMyDevice = true;
            host.state = Host.State.ONLINE;
            host.os = "Unix/(AOSP)";
            host.osType = Os.Android;
        }
        return host.isItMyDevice;
    }

    public static boolean               isItMyGateway(Host host) {
        return host.ip.contains(Singleton.getInstance().network.gateway) &&
                host.ip.length() == Singleton.getInstance().network.gateway.length();
    }

    public static void                  setOsIcon(Context context, Host host,  ImageView osImageView) {
        if (host != null && host.osType != null) {
            if (host.state == Host.State.FILTERED && host.vendor.contains("Unknown")) {
                MyGlideLoader.loadDrawableInCircularImageView(context, R.drawable.secure_computer1, osImageView);
                return ;
            }
            setOsIcon(context, host.osType, osImageView);
            return;
        }
        MyGlideLoader.loadDrawableInCircularImageView(context, R.drawable.monitor, osImageView);
    }

    public static void                  setOsIcon(Context context, Os os,  ImageView osImageView) {
        int ImageRessource;
        switch (os) {
            case Windows:
                ImageRessource = R.drawable.windows;
                break;
            case Cisco:
                ImageRessource = R.drawable.cisco;
                break;
            case Raspberry:
                ImageRessource = R.drawable.rasp;
                break;
            case QUANTA:
                ImageRessource = R.drawable.quanta;
                break;
            case Bluebird:
                ImageRessource = R.drawable.bluebird;
                break;
            case Apple://Need MacBOOK, MacAIR, Iphone, AppleTV
                ImageRessource = R.drawable.ios;
                break;
            case Ios:
                ImageRessource = R.drawable.ios;
                break;
            case Unix:
                ImageRessource = R.drawable.linuxicon;
                break;
            case Linux_Unix:
                ImageRessource = R.drawable.linuxicon;
                break;
            case OpenBSD:
                ImageRessource = R.drawable.linuxicon;
                break;
            case Android:
                ImageRessource = R.drawable.android_winner;
                break;
            case Mobile:
                ImageRessource = R.mipmap.ic_logo_android_trans_round;
                break;
            case Samsung:
                ImageRessource = R.mipmap.ic_logo_android_trans_round;
                break;
            case Ps4:
                ImageRessource = R.drawable.ps4;
                break;
            case Gateway:
                ImageRessource = R.drawable.router1;
                break;
            case Unknow:
                ImageRessource = R.mipmap.ic_unknow;
                MyGlideLoader.loadDrawableInImageView(context, ImageRessource, osImageView, false);
                return;
            default:
                ImageRessource = R.drawable.router3;
                break;
        }
        MyGlideLoader.loadDrawableInCircularImageView(context, ImageRessource, osImageView);
    }

}
