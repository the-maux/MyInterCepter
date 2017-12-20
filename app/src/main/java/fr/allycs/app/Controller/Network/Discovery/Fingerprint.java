package fr.allycs.app.Controller.Network.Discovery;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.allycs.app.Controller.Core.BinaryWrapper.RootProcess;
import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Database.DBHost;
import fr.allycs.app.Controller.Misc.MyGlideLoader;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Unix.Os;
import fr.allycs.app.R;
import fr.allycs.app.View.HostDiscovery.FragmentHostDiscoveryScan;

public class                         Fingerprint {
    private static String            TAG = "Fingerprint";
    /**
     * Scan with the cepter binary the hostList
     * @param scanActivity activity for callback
     */
    static void                      getDevicesInfoFromCepter(final FragmentHostDiscoveryScan scanActivity) {
        final List<Host> hosts = new ArrayList<>();
        final RootProcess process = new RootProcess("Cepter Scan device", Singleton.getInstance().FilesPath);
        //final BufferedReader bufferedReader = new BufferedReader(process.getInputStreamReader());
        final BufferedReader bufferedReader = process.getReader();
        process.exec(Singleton.getInstance().FilesPath + "cepter scan " + Singleton.getInstance().nbrInteface);
        process.exec("exit");
        new Thread(new Runnable() {
            public void run() {
                    String buffer = null;
                    boolean alreadyIn, over = false;
                    while (!over) {
                        try {
                            buffer = bufferedReader.readLine();
                        } catch (IOException e) {
                            Log.d(TAG, "ERROR INTRANET");
                            e.printStackTrace();
                            scanActivity.onHostActualized(hosts);
                        }
                        if (buffer == null) {
                            over = true;
                        } else if ((buffer.length() - buffer.replace(".", "").length()) >= 3 &&
                                !buffer.contains("wrong interface...")) {
                            alreadyIn = false;
                            Host newDevice = new Host(buffer);
                            if (!hosts.contains(newDevice)) {
                                for (Host host : hosts) {
                                    if (host.mac.equals(newDevice.mac)) {
                                        alreadyIn = true;
                                    }
                                }
                                if (!alreadyIn) {
                                    hosts.add(DBHost.saveOrGetInDatabase(newDevice));
                                }
                            }
                        }
                    }
                    Collections.sort(hosts, Host.comparator);
                    scanActivity.onHostActualized(hosts);
            }
        }).start();
    }

    public static void               initHost(Host host) {
        guessosType(host.dumpInfo, host);
        isItMyDevice(host);
    }

    private static void              guessosType(String InfoDevice, Host host) {
        InfoDevice = InfoDevice.toLowerCase();
        if (InfoDevice.contains("bluebird")) {
            host.osType = Os.Bluebird;
        } else if (InfoDevice.contains("cisco")) {
            host.osType = Os.Cisco;
        } else if (InfoDevice.contains("quanta")) {
            host.osType = Os.QUANTA;
        } else if (InfoDevice.contains("android") || InfoDevice.contains("mobile") || InfoDevice.contains("samsung") ||
                InfoDevice.contains("murata") || InfoDevice.contains("huawei") || InfoDevice.contains("oneplus") ||
                InfoDevice.contains("lg") || InfoDevice.contains("motorola")) {
            host.osType = Os.Android;
        } else if (InfoDevice.contains("windows 7")) {
            host.osType = Os.Windows7_8_10;
        } else if (InfoDevice.contains("windows 2000")) {
            host.osType = Os.WindowsXP;
        } else if (InfoDevice.contains("windows")) {
            host.osType = Os.Windows10;
        } else if (InfoDevice.contains("apple")) {
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

        switch (host.osType) {
            case Windows2000:
                ImageRessource = R.drawable.winicon;
                break;
            case WindowsXP:
                ImageRessource = R.drawable.winicon;
                break;
            case Windows10:
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
        MyGlideLoader.loadDrawableInImageView(context, ImageRessource, osImageView);
    }

}
