package fr.allycs.app.Controller.Network.Discovery;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Network.BonjourService.BonjourManager;
import fr.allycs.app.Controller.Network.IPv4CIDR;
import fr.allycs.app.Controller.Network.NetUtils;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.View.HostDiscovery.HostDiscoveryActivity;

public class                        HostDiscoveryScan {
    private String                  TAG = "HostDiscoveryScan";
    public enum typeScan {          Arp, Services, Nmap }
    private HostDiscoveryScan       mInstance = this;
    private Singleton               mSingleton = Singleton.getInstance();
    private HostDiscoveryActivity   mActivity;
    public boolean                  inLoading = false;

    public HostDiscoveryScan(final HostDiscoveryActivity activity) {
        this.mActivity = activity;
    }

    public void                    run(typeScan typeOfScan, List<Host> listOfHosts) {
        mSingleton.resetActualSniffSession();
        switch (typeOfScan) {
            case Arp:
                startArpScan();
                break;
            case Services:
                startBonjourScan(listOfHosts);
                break;
            case Nmap:
                startNmapScan();
                break;
        }
    }

    private void                    startBonjourScan(List<Host> listOfHosts) {
        new BonjourManager(mActivity, listOfHosts, this);
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
        ArrayList<String> tmpAntiConcurentExecptionFFS = new ArrayList<>();
        tmpAntiConcurentExecptionFFS.addAll(ipReachable);
        NetUtils.dumpListHostFromARPTableInFile(mActivity, tmpAntiConcurentExecptionFFS);
        mActivity.setToolbarTitle(null, tmpAntiConcurentExecptionFFS.size() + " hosts detected");
        mActivity.setProgressState(1500);
        mActivity.setToolbarTitle(null,"Scanning " + tmpAntiConcurentExecptionFFS.size() + " devices");
        Fingerprint.getDevicesInfoFromCepter(this);
        mActivity.setProgressState(2000);
    }

}

