package su.sniff.cepter.Network;

import android.os.SystemClock;
import android.util.Log;
import su.sniff.cepter.Controller.IPv4CIDR;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Scan Netmask check the rechability of devices in file hostfile
 */
public class                        ScanNetmask {
    private String                  TAG = "ScanNetmask";
    private ExecutorService         service;

    public                          ScanNetmask(IPv4CIDR iPv4CIDR) {
        service = Executors.newCachedThreadPool();
        reachableLoop(iPv4CIDR, service);
    }

    private void                    runnableReachable(ExecutorService service, final String str) {
        service.submit(new Runnable() {
            public void run() {
                try {
                    InetAddress.getByName(str).isReachable(1);
                }  catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
        });
    }

    private void                    reachableLoop(IPv4CIDR iPv4CIDR, ExecutorService service) {
        Integer NumberOfHosts = iPv4CIDR.getNumberOfHosts();
        List<String> availableIPs = iPv4CIDR.getAvailableIPs(NumberOfHosts);
        Log.i(TAG, "NumberOfHosts:" + NumberOfHosts + " ipAvailable:" + availableIPs.size());
        int count = 0;
        for (final String ip : availableIPs) {
            runnableReachable(service, ip);
            count++;
            if (count > 100) {
                SystemClock.sleep(20);
                count = 0;
            }
        }
        if (NumberOfHosts  < 300) {
            for (final String ip : availableIPs) {
                runnableReachable(service, ip);
            }
        }
        Log.d(TAG, "All reachable OVER");
    }
}
