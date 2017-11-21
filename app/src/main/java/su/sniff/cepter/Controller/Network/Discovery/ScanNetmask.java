package su.sniff.cepter.Controller.Network.Discovery;

import android.util.Log;

import su.sniff.cepter.Controller.Core.BinaryWrapper.RootProcess;
import su.sniff.cepter.Controller.Core.Conf.Singleton;
import su.sniff.cepter.Controller.Network.IPv4CIDR;

import java.io.IOException;
import java.net.InetAddress;
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
    private Integer                 mNumberOfHosts;
    private volatile int            mNbrHostScanned = 0;
    private volatile boolean        alreadySend = false;
    private ArrayList<String>       mListIpReachable = new ArrayList<>();
    private boolean                 debuglog = Singleton.getInstance().DebugMode;
    private HostDiscoveryScan       mScanner;

    ScanNetmask(IPv4CIDR iPv4CIDR, HostDiscoveryScan scanner) {
        ExecutorService service = Executors.newCachedThreadPool();
        reachableLoop(iPv4CIDR, service);
        this.mScanner = scanner;
    }

    private void                    reachableLoop(IPv4CIDR iPv4CIDR, ExecutorService service) {
        mNumberOfHosts = iPv4CIDR.getNumberOfHosts() - 2;
        List<String> availableIPs = iPv4CIDR.getAvailableIPs(mNumberOfHosts);
        Log.i(TAG, "mNumberOfHosts:" + mNumberOfHosts + " ipAvailable:" + availableIPs.size());
        int rax = 0;
        for (final String ip : availableIPs) {
            mNbrHostScanned = mNbrHostScanned + 1;
            rax = rax + 1;
            runnableReachable(service, ip, mNbrHostScanned);
            if (rax >= 12) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rax = 0;
            }
        }
    }

    private void                    ScanOver() {
        alreadySend = true;
        mScanner.onReachableScanOver(mListIpReachable);
    }

    public boolean                  ping(String host) {
        RootProcess pingProces = new RootProcess("ScanNetMaskPING");
        pingProces.exec(Singleton.getInstance().BinaryPath + "/busybox ping -c 1 " + host + "; exit");
        int res = pingProces.waitFor();
        Log.d(TAG, host + " WAITFOR PING = " + res);
        if (res == 0) {
            Log.d(TAG, "ping " + host + " TRUE");
            return true;
        } else {
            return false;
        }
    }

    private void                    runnableReachable(ExecutorService service, final String ip, final int nbrHostScanned) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    //ping(ip);
                    if (InetAddress.getByName(ip).isReachable(null, 64, 2000)) {//Timeout 2s
                        if (debuglog)
                            Log.d(TAG, ip + " is reachable (" + nbrHostScanned + "/" + mNumberOfHosts + ")");
                        mListIpReachable.add(ip + ":");
                        //NetworkInterface ni = NetworkInterface.getByInetAddress(host);// send always null
                        /*if (ni != null) {
                            byte[] mac = ni.getHardwareAddress();
                            String MAC = "";
                            if (mac != null) {
                                for (int i = 0; i < mac.length; i++) {
                                    MAC += String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : "") + "";
                                }
                                Log.d(TAG, ip + ":" + MAC.replace("-", ":").toLowerCase());
                                mListIpReachable.add(ip + ":" + MAC.replace("-", ":").toLowerCase());
                            } else {
                                Log.e(TAG, ip + " doesn't exist or is not accessible. (" + mNbrHostScanned + "/" + mNumberOfHosts + ")");
                            }
                        } else {
                            if (debuglog)
                                Log.e(TAG, "PersistanteConfiguration Interface for " + ip + " is null (" + mNbrHostScanned + "/" + mNumberOfHosts + ")");
                        }*/
                    } else {

                    }
                }  catch (UnknownHostException e) {
                    Log.e(TAG, "UnknownHostException: " + ip + " (" + nbrHostScanned + "/" + mNumberOfHosts + ")");
                    e.printStackTrace();
                } catch (SocketException e) {
                    Log.e(TAG, "SocketException: " + ip + " (" + nbrHostScanned + "/" + mNumberOfHosts + ")");
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "IOException: " + ip + " (" + nbrHostScanned + "/" + mNumberOfHosts + ")");
                } finally {
                    if (nbrHostScanned >= (mNumberOfHosts -1) && nbrHostScanned < mNumberOfHosts && !alreadySend) {
                        Log.e(TAG, "ScanOver: " + ip + " (" + nbrHostScanned + "/" + mNumberOfHosts + ") with " + mListIpReachable.size() + " host reached");
                        ScanOver();
                    }
                }
            }
        }).start();
    }

}
