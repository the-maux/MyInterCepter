package fr.allycs.app.Core.Network.Discovery;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Core.Network.IPv4CIDR;

/**
 * Scan Netmask check the rechability of devices in file hostfile
 */
public class                        IcmpScanNetmask {
    private String                  TAG = "IcmpScanNetmask";
    private Integer                 mNumberOfHosts;
    private volatile int            mNbrHostScanned = 0;
    private volatile boolean        alreadySend = false;
    private ArrayList<String>       mListIpReachable = new ArrayList<>();
    private boolean                 debuglog = Singleton.getInstance().DebugMode;
    private NetworkDiscoveryControler mScanner;
    private Date                        startScanning, endScanning;

    IcmpScanNetmask(IPv4CIDR iPv4CIDR, NetworkDiscoveryControler scanner) {
        ExecutorService service = Executors.newCachedThreadPool();
        this.mScanner = scanner;
        try {
            reachableLoop(iPv4CIDR, service);
        } catch (InterruptedException e) {
            e.printStackTrace();
            IcmpScanOver();
        }
    }

    private void                    reachableLoop(IPv4CIDR iPv4CIDR, ExecutorService service) throws InterruptedException {
        startScanning = Calendar.getInstance().getTime();
        mNumberOfHosts = iPv4CIDR.getNumberOfHosts() - 2;
        List<String> availableIPs = iPv4CIDR.getAvailableIPs(mNumberOfHosts);
        Log.i(TAG, "mNumberOfHosts:" + mNumberOfHosts + " ipAvailable:" + availableIPs.size());
        int rax = 0;
        for (final String ip : availableIPs) {
            mNbrHostScanned = mNbrHostScanned + 1;
            rax = rax + 1;
            service.execute(runnableReachable(ip, mNbrHostScanned));
            ;
/*            if (rax >= 12) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                rax = 0;
            }*/
        }
        service.shutdown();
        service.awaitTermination(10000, TimeUnit.MILLISECONDS);
    }

    private void                    IcmpScanOver() {
        alreadySend = true;
        if (Singleton.getInstance().UltraDebugMode) {
            for (String ipReachable : mListIpReachable) {
                Log.d(TAG, ipReachable + " reachable");
            }
        }
        Log.d(TAG, "Scanned in " + getTimeSpend());
        mScanner.onReachableScanOver(mListIpReachable);
    }

    private Runnable                    runnableReachable(final String ip, final int nbrHostScanned) {
       return new Runnable() {
            public void run() {
                try {
                    if (InetAddress.getByName(ip).isReachable(null, 64, 2000)) {//Timeout 2s
                        mListIpReachable.add(ip + ":");
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
                        Log.e(TAG, "IcmpScanOver: " + ip + " (" + nbrHostScanned + "/" + mNumberOfHosts + ") with " + mListIpReachable.size() + " host reached");
                        IcmpScanOver();
                    }
                }
            }
        };
    }

    public String                   getTimeSpend() {
        Date now = Calendar.getInstance().getTime();
        long restDatesinMillis = now.getTime() - startScanning.getTime();
        Date restdate = new Date(restDatesinMillis);
        return new SimpleDateFormat("mm:ss", Locale.FRANCE).format(restdate);
    }
    /*public boolean                  ping(String domain) {
        RootProcess pingProces = new RootProcess("ScanNetMaskPING");
        pingProces.exec(Singleton.getInstance().BinaryPath + "/busybox ping -c 1 " + domain + "; exit");
        int res = pingProces.waitFor();
        Log.d(TAG, domain + " WAITFOR PING = " + res);
        if (res == 0) {
            Log.d(TAG, "ping " + domain + " TRUE");
            return true;
        } else {
            return false;
        }
    }*/
}
