package su.sniff.cepter.Controller.Network.Discovery;

import java.util.ArrayList;

import su.sniff.cepter.Controller.Core.Conf.Singleton;
import su.sniff.cepter.Controller.Network.IPv4CIDR;
import su.sniff.cepter.Controller.Network.NetUtils;
import su.sniff.cepter.View.HostDiscovery.HostDiscoveryActivity;

public class                        HostDiscoveryScan {
    public enum typeScan {          Arp, Icmp, Nmap }
    private HostDiscoveryScan       mInstance = this;
    private Singleton               mSingleton = Singleton.getInstance();
    private HostDiscoveryActivity   mActivity;

    public HostDiscoveryScan(final HostDiscoveryActivity activity) {
        this.mActivity = activity;
    }

    private void                    run(typeScan typeOfScan) {
        switch (typeOfScan) {
            case Arp:
                startArpScan();
                break;
            case Icmp:
                startIcmpScan();
                break;
            case Nmap:
                startNmapScan();
                break;
        }
    }

    private void                    startIcmpScan() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new ScanNetmask(new IPv4CIDR(mSingleton.network.myIp, mSingleton.network.netmask), mInstance);
            }
        }).start();
    }
    private void                    startNmapScan() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new ScanNetmask(new IPv4CIDR(mSingleton.network.myIp, mSingleton.network.netmask), mInstance);
            }
        }).start();
    }
    private void                    startArpScan() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new ScanNetmask(new IPv4CIDR(mSingleton.network.myIp, mSingleton.network.netmask), mInstance);
            }
        }).start();
    }

    public void                     onReachableScanOver(ArrayList<String> ipReachable) {
        mActivity.monitor("Target Identification");
        NetUtils.dumpListHostFromARPTableInFile(mActivity, ipReachable);
        mActivity.setProgressState(1500);
        mActivity.monitor("Fingerprint scan");
        Fingerprint.guessHostFingerprint(mActivity);
        mActivity.setProgressState(2000);
    }

}

