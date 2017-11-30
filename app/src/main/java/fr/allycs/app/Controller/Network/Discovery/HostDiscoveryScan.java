package fr.allycs.app.Controller.Network.Discovery;

import android.util.Log;

import java.util.ArrayList;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Network.BonjourService.BonjourManager;
import fr.allycs.app.Controller.Network.IPv4CIDR;
import fr.allycs.app.Controller.Network.NetUtils;
import fr.allycs.app.View.HostDiscovery.HostDiscoveryActivity;

public class                        HostDiscoveryScan {
    private String                  TAG = "HostDiscoveryScan";
    public enum typeScan {          Arp, Services, Nmap }
    private HostDiscoveryScan       mInstance = this;
    private Singleton               mSingleton = Singleton.getInstance();
    private HostDiscoveryActivity   mActivity;

    public HostDiscoveryScan(final HostDiscoveryActivity activity) {
        this.mActivity = activity;
    }

    public void                    run(typeScan typeOfScan) {
        switch (typeOfScan) {
            case Arp:
                startArpScan();
                break;
            case Services:
                startBonjourScan();
                break;
            case Nmap:
                startNmapScan();
                break;
        }
    }

    private void                    startBonjourScan() {
        BonjourManager bonjourManager = new BonjourManager(mActivity, mSingleton.hostsList);
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
        Log.d(TAG, "onReachableScanOver");
        mActivity.monitor(ipReachable.size() + " hosts detected");
        NetUtils.dumpListHostFromARPTableInFile(mActivity, ipReachable);
        mActivity.monitor(ipReachable.size() + " hosts detected");
        mActivity.setProgressState(1500);
        mActivity.monitor("Scanning " + ipReachable.size() + " host");
        Fingerprint.guessHostFingerprint(mActivity);
        mActivity.setProgressState(2000);
    }

}
