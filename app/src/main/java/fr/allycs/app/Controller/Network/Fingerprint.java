package fr.allycs.app.Controller.Network;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.allycs.app.Controller.AndroidUtils.MyGlideLoader;
import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.RootProcess;
import fr.allycs.app.Controller.Database.DBHost;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Unix.Os;
import fr.allycs.app.R;
import fr.allycs.app.View.HostDiscovery.FragmentHostDiscoveryScan;

/**
 * Supprimer les duplicata External & Host
 */
public class                         Fingerprint {
    private static String            TAG = "Fingerprint";

    public static void               initHost(Host host) {
        isItMyDevice(host);
        guessosType(host.dumpInfo, host);
    }

    private static void              guessosType(String InfoDevice, Host host) {
        if (InfoDevice == null) {
            host.osType = Os.Unknow;
            return;
        }
        InfoDevice = InfoDevice.toLowerCase();
        if (host.isItMyDevice) {
            host.osType = Os.Android;
            host.vendor = "Your Device";
        } else if (InfoDevice.contains("bluebird")) {
            host.osType = Os.Bluebird;
        } else if (InfoDevice.contains("cisco")) {
            host.osType = Os.Cisco;
        } else if (InfoDevice.contains("quanta")) {
            host.osType = Os.QUANTA;
        } else if (InfoDevice.contains("android") || InfoDevice.contains("mobile") || InfoDevice.contains("samsung") ||
                InfoDevice.contains("murata") || InfoDevice.contains("huawei") || InfoDevice.contains("oneplus") ||
                InfoDevice.contains("lg") || InfoDevice.contains("motorola")) {
            host.osType = Os.Android;
        } else if (InfoDevice.contains("windows") || InfoDevice.contains("microsoft")) {
            host.osType = Os.Windows;
        } /*else if (InfoDevice.contains("windows 7")) {
            host.osType = Os.Windows7_8_10;
        } else if (InfoDevice.contains("windows 2000")) {
            host.osType = Os.WindowsXP;
        } */else if (InfoDevice.contains("apple")) {
            host.osType = Os.Apple;
        } else if (InfoDevice.contains("raspberry")) {
            host.osType = Os.Raspberry;
        }  else if (InfoDevice.contains("ios")) {
            host.osType = Os.Ios;
        } else if (!(!InfoDevice.contains("unix") && !InfoDevice.contains("linux") && !InfoDevice.contains("bsd"))) {
            host.osType = Os.Linux_Unix;
        } else
            host.osType = Os.Unknow;
    }

    private static void              isItMyDevice(Host host) {
        if (host.ip.contains(Singleton.getInstance().network.myIp)) {
            host.isItMyDevice = true;
        }
    }

    public static void               setOsIcon(Context context, Host host, CircleImageView osImageView) {
        int                 ImageRessource;
        if (host != null && host.osType != null) {
            switch (host.osType) {
                case Windows2000:
                    ImageRessource = R.drawable.winicon;
                    break;
                case WindowsXP:
                    ImageRessource = R.drawable.winicon;
                    break;
                case Windows:
                    ImageRessource = R.drawable.winicon;
                    break;
                case Windows7_8_10:
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
                case Apple:
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
                    ImageRessource = R.mipmap.ic_logo_android_trans_round;
                    break;
                case Mobile:
                    ImageRessource = R.mipmap.ic_logo_android_trans_round;
                    break;
                case Samsung:
                    ImageRessource = R.mipmap.ic_logo_android_trans_round;
                    break;
                case Unknow:
                    ImageRessource = R.drawable.monitor;
                    break;
                default:
                    ImageRessource = R.drawable.monitor;
                    break;
            }
        } else {
            ImageRessource = R.drawable.monitor;
        }
        MyGlideLoader.loadDrawableInImageView(context, ImageRessource, osImageView);
    }

}
