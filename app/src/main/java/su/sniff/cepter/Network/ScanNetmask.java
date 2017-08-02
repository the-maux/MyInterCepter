package su.sniff.cepter.Network;

import android.os.SystemClock;
import android.util.Log;
import su.sniff.cepter.Controller.IPv4CIDR;
import su.sniff.cepter.View.ScanActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Scan Netmask check the rechability of devices in file hostfile
 */
public class                        ScanNetmask {
    private String                  TAG = "ScanNetmask";
    private ExecutorService         service;
    private Integer                 NumberOfHosts;
    private int                     nbrHostScanned = 0;
    private ArrayList<String>       ipReachable = new ArrayList<>();
    private ScanActivity            activity;

    public                          ScanNetmask(IPv4CIDR iPv4CIDR, ScanActivity activity) {
        this.activity = activity;
        service = Executors.newCachedThreadPool();
        reachableLoop(iPv4CIDR, service);
    }

    private void                    reachableLoop(IPv4CIDR iPv4CIDR, ExecutorService service) {
        NumberOfHosts = iPv4CIDR.getNumberOfHosts() - 2;
        List<String> availableIPs = iPv4CIDR.getAvailableIPs(NumberOfHosts);
        Log.i(TAG, "NumberOfHosts:" + NumberOfHosts + " ipAvailable:" + availableIPs.size());
        if (NumberOfHosts  < 300) {
            for (final String ip : availableIPs) {
                runnableReachable(service, ip);
            }
        }
    }

    private void                    ScanOver() {
        activity.onReachableScanOver(ipReachable);
        Log.d(TAG, "Scan over with " + ipReachable.size() + " host reached");
    }

    private void                    runnableReachable(ExecutorService service, final String ip) {
        service.submit(new Runnable() {
            public void run() {
                try {
                    ++nbrHostScanned;
                    InetAddress host = InetAddress.getByName(ip);
                    if (InetAddress.getByName(ip).isReachable(50)) {//Timeout 10s
                        Log.d(TAG, ip + " is reachable");
                        ipReachable.add(ip);
                        NetworkInterface ni = NetworkInterface.getByInetAddress(host);
                        if (ni != null) {
                            byte[] mac = ni.getHardwareAddress();
                            String MAC = "";
                            if (mac != null) {
                                for (int i = 0; i < mac.length; i++) {
                                    MAC += String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : "") + "";
                                }
                                Log.d(TAG, ip + ":" + MAC);
                            } else {
                                Log.e(TAG, ip + " doesn't exist or is not accessible.");
                            }
                        } else {
                            Log.e(TAG, "Network Interface for " + ip);
                        }
                    }
                }  catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (nbrHostScanned > NumberOfHosts)
                        ScanOver();
                }
            }
        });
    }


}
