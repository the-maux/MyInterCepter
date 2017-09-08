package su.sniff.cepter.Controller.Network;

import android.util.Log;

import su.sniff.cepter.View.ScanActivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Scan Netmask check the rechability of devices in file hostfile
 */
public class                        ScanNetmask {
    private String                  TAG = "ScanNetmask";
    private ExecutorService         service;
    private Integer                 NumberOfHosts;
    private volatile int            nbrHostScanned = 0;
    private volatile boolean        alreadySend = false;
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
        for (final String ip : availableIPs) {
            runnableReachable(service, ip);
        }
    }

    private void                    ScanOver() {
        alreadySend = true;
        activity.onReachableScanOver(ipReachable);
        Log.d(TAG, "Scan over with " + ipReachable.size() + " host reached");
    }

    private void                    runnableReachable(ExecutorService service, final String ip) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    ++nbrHostScanned;
                    InetAddress host = InetAddress.getByName(ip);
                    if (InetAddress.getByName(ip).isReachable(100)) {//Timeout 10s
                        Log.d(TAG, ip + " is reachable");
                        NetworkInterface ni = NetworkInterface.getByInetAddress(host);
                        if (ni != null) {
                            byte[] mac = ni.getHardwareAddress();
                            String MAC = "";
                            if (mac != null) {
                                for (int i = 0; i < mac.length; i++) {
                                    MAC += String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : "") + "";
                                }
                                Log.d(TAG, ip + ":" + MAC.replace("-", ":").toLowerCase());
                                ipReachable.add(ip + ":" + MAC.replace("-", ":").toLowerCase());
                            } else {
                                Log.e(TAG, ip + " doesn't exist or is not accessible.");
                            }
                        } else {
                            Log.e(TAG, "Network Interface for " + ip);
                        }

                    }
                }  catch (UnknownHostException e) {
                    nbrHostScanned++;
                    e.printStackTrace();
                } catch (SocketException e) {
                    nbrHostScanned++;
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    nbrHostScanned++;
                } finally {
                    if (nbrHostScanned >= NumberOfHosts - 3 && nbrHostScanned < NumberOfHosts &&
                            !alreadySend)
                        ScanOver();
                }
            }
        }).start();
    }


}
