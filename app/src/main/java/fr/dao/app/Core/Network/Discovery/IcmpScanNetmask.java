package fr.dao.app.Core.Network.Discovery;

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

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Network.IPv4Utils;
/*
 * TODO: Challenge make this ok :
 *         - Free wifi:
 *                      + Ip : 10.50.41.195
 *                      + Gateway : 10.55.255.254
 *                      mNumberOfHosts:524286 ipAvailable:524285
 */
public class                        IcmpScanNetmask {
    private String                  TAG = "IcmpScanNetmask";
    private Integer                 mNumberOfHosts;
    private volatile int            mNbrHostScanned = 0;
    private volatile boolean        alreadySend = false;
    private ArrayList<String>       mListIpReachable = new ArrayList<>();
    private boolean                 debuglog = Singleton.getInstance().Settings.DebugMode;
    private NetworkDiscoveryControler mScanner;
    private Date                    startScanning;

    IcmpScanNetmask(IPv4Utils iPv4CIDR, NetworkDiscoveryControler scanner) {
        ExecutorService service = Executors.newCachedThreadPool();
        this.mScanner = scanner;
        try {
            startScanning = Calendar.getInstance().getTime();
            mNumberOfHosts = iPv4CIDR.getNumberOfHosts() - 2;
            List<String> availableIPs = iPv4CIDR.getAvailableIPs(mNumberOfHosts);
            Log.i(TAG, "mNumberOfHosts:" + mNumberOfHosts + " ipAvailable:" + availableIPs.size());
            int rax = 0;
            for (final String ip : availableIPs) {
                mNbrHostScanned = mNbrHostScanned + 1;
                rax = rax + 1;
                service.execute(runnableReachable(ip, mNbrHostScanned));
            }
            service.shutdown();
            service.awaitTermination(10000, TimeUnit.MILLISECONDS);

        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.e(TAG, "Icmp scan was interupted");
            IcmpScanOver();
        }
    }

    private void                    IcmpScanOver() {
        alreadySend = true;
        if (Singleton.getInstance().Settings.UltraDebugMode) {
            for (String ipReachable : mListIpReachable) {
                Log.d(TAG, ipReachable + " reachable");
            }
        }
        Log.i(TAG, "Icmp scan last for " + getTimeSpend());
        mScanner.onArpScanOver(mListIpReachable);
    }

    private Runnable                runnableReachable(final String ip, final int nbrHostScanned) {
       return new Runnable() {
            public void run() {
                try {
                    if (InetAddress.getByName(ip).isReachable(null, 64, 2000)) {//Timeout 2s
                        mListIpReachable.add(ip + ":");
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

    private String                  getTimeSpend() {
        Date now = Calendar.getInstance().getTime();
        long restDatesinMillis = now.getTime() - startScanning.getTime();
        Date restdate = new Date(restDatesinMillis);
        return new SimpleDateFormat("mm:ss", Locale.FRANCE).format(restdate);
    }

}
