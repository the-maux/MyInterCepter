package fr.allycs.app.Core.Nmap;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import java.util.Comparator;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Unix.Os;
import fr.allycs.app.R;
import fr.allycs.app.View.Behavior.MyGlideLoader;

/**
 * Supprimer les duplicata External & Host
 */
public class                            Fingerprint {
    private static String               TAG = "Fingerprint";

    public static void                  initHost(Host host) {
        //Log.d(TAG, "initHost:\t" + host.toString());
        isItMyDevice(host);
        guessosType(host.dumpInfo, host);
    }

    private static void                 guessosType(String InfoDevice, Host host) {
        if (InfoDevice == null) {
            host.osType = Os.Unknow;
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
            host.os = "BSD/(Cisco NX-OS)";
        } else if (InfoDevice.contains("raspberry")) {
            host.osType = Os.Raspberry;
            host.os = "Unix/(Raspbian)";
        } else if (InfoDevice.contains("quanta")) {
            host.osType = Os.QUANTA;
            host.os = "Unix/(RedHat 3)";
        } else if (InfoDevice.contains("android") || InfoDevice.contains("mobile") || InfoDevice.contains("samsung") ||
                InfoDevice.contains("murata") || InfoDevice.contains("huawei") || InfoDevice.contains("oneplus") ||
                InfoDevice.contains("lg") || InfoDevice.contains("motorola")) {
            fingerprintMobile(host, InfoDevice);
        } else if (InfoDevice.contains("apple") || host.vendor.toLowerCase().contains("apple") || host.osType == Os.Apple) {
            fingerprintApple(host, InfoDevice);
        } else if (!(!InfoDevice.contains("unix") && !InfoDevice.contains("linux") && !InfoDevice.contains("bsd"))) {
            host.osType = Os.Linux_Unix;
        } else if (InfoDevice.contains("windows") || InfoDevice.contains("microsoft")) {
            Log.i(TAG, "WINDOWS OR MICROSOFT HERE:" + InfoDevice);
            host.osType = Os.Windows;
        } else
            host.osType = Os.Unknow;
    }

    private static void                 fingerprintApple(Host host, String infoDevice) {
        host.os = "FreeBSD";
        host.osType = Os.Apple;
        host.os = "Unix/(Mac OS X)";//TODO FINGERPRINT WITH MAC NAME ON zeroconf
        if (host.getName().isEmpty() && host.Ports().dump.contains("model=")) {
            String name = host.mac.split(":")[4] + host.mac.split(":")[5];
            host.name = host.vendor.toUpperCase().replaceAll("\\d","") + "-" + name;
        }
    }

    private static void                 fingerprintMobile(Host host, String infoDevice) {
        host.osType = Os.Android;
        host.os = "Unix/(AOSP)";
    }

    public static boolean               isItWindows(Host host) {
        /*
         ** TODO: Do i have to checkd the proto for microsoft|windows|msrpc ?
         */
        return host.Ports() != null &&
                host.Ports().isPortOpen(135) &&
                host.Ports().isPortOpen(445);
    }

    public static boolean               isItMyDevice(Host host) {
        if (host.ip.contains(Singleton.getInstance().network.myIp)) {
            host.isItMyDevice = true;
            host.state = Host.State.ONLINE;
        }
        return host.isItMyDevice;
    }

    public static boolean               isItMyGateway(Host host) {
        return host.ip.contentEquals(Singleton.getInstance().network.myIp);

    }

    public static void                  setOsIcon(Context context, Host host,  ImageView osImageView) {
        if (host != null && host.osType != null) {
            if (host.state == Host.State.FILTERED && host.vendor.contains("Unknown")) {
                MyGlideLoader.loadDrawableInCircularImageView(context, R.mipmap.ic_secure2, osImageView);
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
            case Gateway:
                ImageRessource = R.drawable.router1;
                break;
            case Unknow:
                ImageRessource = R.mipmap.cyber_security5_rounded2;
                MyGlideLoader.loadDrawableInImageView(context, ImageRessource, osImageView, false);
                return;
            default:
                ImageRessource = R.drawable.router3;
                break;
        }
        MyGlideLoader.loadDrawableInCircularImageView(context, ImageRessource, osImageView);
    }

    public static Comparator<Host>      getComparator() {
        return new Comparator<Host>() {

            public int compare(Host o1, Host o2) {
                if (o1.state == o2.state) {
                    String ip1[] = o1.ip.replace(" ", "").replace(".", "::").split("::");
                    String ip2[] = o2.ip.replace(" ", "").replace(".", "::").split("::");
                    if (Integer.parseInt(ip1[2]) > Integer.parseInt(ip2[2]))
                        return 1;
                    else if (Integer.parseInt(ip1[2]) < Integer.parseInt(ip2[2]))
                        return -1;
                    else if (Integer.parseInt(ip1[3]) > Integer.parseInt(ip2[3]))
                        return 1;
                    else if (Integer.parseInt(ip1[3]) < Integer.parseInt(ip2[3]))
                        return -1;
                } else {
                    if (o1.state == Host.State.ONLINE || o2.state == Host.State.OFFLINE)
                        return -1;
                    else if (o2.state == Host.State.ONLINE || o1.state == Host.State.OFFLINE)
                        return 1;
                }
                return 0;
            }

            ;
        };
    }

}
