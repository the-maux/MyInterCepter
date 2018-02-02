package fr.allycs.app.Controller.Core.Nmap;

import android.content.Context;
import android.util.Log;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.allycs.app.Controller.AndroidUtils.MyGlideLoader;
import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Unix.Os;
import fr.allycs.app.R;

/**
 * Supprimer les duplicata External & Host
 */
public class                         Fingerprint {
    private static String            TAG = "Fingerprint";

    public static void               initHost(Host host) {
        Log.d(TAG, "initHost:\t" + host.toString());
        isItMyDevice(host);
        guessosType(host.dumpInfo, host);
    }

    private static void              guessosType(String InfoDevice, Host host) {
        if (InfoDevice == null) {
            host.osType = Os.Unknow;
            return;
        }
        if (isItWindows(host)) {
            host.osType = Os.Windows;
            return;
        }
        InfoDevice = InfoDevice.toLowerCase();
        if (host.isItMyDevice) {
            host.osType = Os.Android;
            host.vendor = "Your Device";
            host.os = "Unix/(AOSP)";
        } if (host.vendor.contains("Sony")) {
            /**
             * TODO: faire un Thread qui check les port, si c'est open, c'est une ps4
             * 9295/tcp  open  unknown
             * 41800/tcp open  unknown
             * Sinon c'est une TV ou téléphone
             */
            host.osType = Os.Ps4;
            host.os = "FreeBSD 10.X, Sony embedded";
        } else if (InfoDevice.contains("bluebird")) {
            host.osType = Os.Bluebird;
            host.os = "Unix/(Aosp)";
        } else if (InfoDevice.contains("cisco")) {
            host.osType = Os.Cisco;
        } else if (InfoDevice.contains("raspberry")) {
            host.osType = Os.Raspberry;
            host.os = "Unix/(Raspbian)";
        } else if (InfoDevice.contains("quanta")) {
            host.osType = Os.QUANTA;
            host.os = "Unix/(RedHat 3)";
        } else if (InfoDevice.contains("android") || InfoDevice.contains("mobile") || InfoDevice.contains("samsung") ||
                InfoDevice.contains("murata") || InfoDevice.contains("huawei") || InfoDevice.contains("oneplus") ||
                InfoDevice.contains("lg") || InfoDevice.contains("motorola")) {
            host.osType = Os.Android;
            host.os = "Unix/(AOSP)";
        } else if (InfoDevice.contains("apple") || host.vendor.toLowerCase().contains("apple")) {
            host.os = "FreeBSD";
            host.osType = Os.Apple;
            host.os = "Unix/(Mac OS X)";//TODO FINGERPRINT WITH MAC NAME ON zeroconf
        } else if (!(!InfoDevice.contains("unix") && !InfoDevice.contains("linux") && !InfoDevice.contains("bsd"))) {
            host.osType = Os.Linux_Unix;
        } else if (InfoDevice.contains("windows") || InfoDevice.contains("microsoft")) {
            Log.i(TAG, "WINDOWS OR MICROSOFT HERE:" + InfoDevice);
            host.osType = Os.Windows;
        } else
            host.osType = Os.Unknow;
    }

    private static boolean          isItWindows(Host host) {
        /*
               TODO: Do i have to checkd the proto for microsoft|windows|msrpc ?
         */
        if (host.Ports() == null)
            return false;
        return
                host.Ports().isPortOpen(135) &&
                host.Ports().isPortOpen(445);
    }

    public static boolean           isItMyDevice(Host host) {
        if (host.ip.contains(Singleton.getInstance().network.myIp)) {
            host.isItMyDevice = true;
        }
        return host.isItMyDevice;
    }

    public static void               setOsIcon(Context context, Host host, CircleImageView osImageView) {
        int ImageRessource;
        if (host != null && host.osType != null) {
            if (host.ip.equals(Singleton.getInstance().network.gateway)) {
                ImageRessource = R.drawable.router1;
                host.osType = Os.Unix;
            } else {
                switch (host.osType) {
                    case Windows:
                        ImageRessource = R.drawable.winicon;
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
                    case Unknow:
                        ImageRessource = R.drawable.monitor;
                        break;
                    default:
                        ImageRessource = R.drawable.monitor;
                        break;
                }
            }
        } else {
            ImageRessource = R.drawable.monitor;
        }
        MyGlideLoader.loadDrawableInImageView(context, ImageRessource, osImageView);
    }

}
