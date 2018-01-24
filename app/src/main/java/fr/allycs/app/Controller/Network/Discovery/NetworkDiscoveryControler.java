package fr.allycs.app.Controller.Network.Discovery;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Network.BonjourService.BonjourManager;
import fr.allycs.app.Controller.Network.IPv4CIDR;
import fr.allycs.app.Controller.Network.NetUtils;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.View.HostDiscovery.FragmentHostDiscoveryScan;
import fr.allycs.app.View.HostDiscovery.HostDiscoveryActivity;

public class                        NetworkDiscoveryControler {
    private String                  TAG = "NetworkDiscoveryControler";
    public enum typeScan {          Arp, Services, Historic }
 //   private NetworkDiscoveryControler mInstance = this;
    private FragmentHostDiscoveryScan mFragment;
    private Singleton               mSingleton = Singleton.getInstance();
    private HostDiscoveryActivity   mActivity;
    private String                  mSSID = null;
    public boolean                  inLoading = false;

    private static NetworkDiscoveryControler            mInstance = null;
    private                             NetworkDiscoveryControler(final FragmentHostDiscoveryScan fragmentHostDiscoveryScan) {

    }
    public static synchronized NetworkDiscoveryControler getInstance(final FragmentHostDiscoveryScan fragmentHostDiscoveryScan) {
        if(mInstance == null)
            mInstance = new NetworkDiscoveryControler(fragmentHostDiscoveryScan);
        mInstance.mActivity = (HostDiscoveryActivity) fragmentHostDiscoveryScan.getActivity();
        mInstance.mFragment = fragmentHostDiscoveryScan;
        WifiManager wifiManager = (WifiManager) fragmentHostDiscoveryScan.getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mInstance.mSSID = (wifiManager) != null ?
                wifiManager.getConnectionInfo().getSSID().replace("\"", "") : "NO SSID";
        return mInstance;
    }

    public void                    run(typeScan typeOfScan, List<Host> listOfHosts) {
        Log.d(TAG, "running: " + typeOfScan.name());
        if (!inLoading) {
            inLoading = true;
            mSingleton.resetActualSniffSession();
            switch (typeOfScan) {
                case Arp:
                    startArpScan();
                    break;
                case Services:
                    startBonjourScan(listOfHosts);
                    break;
            }
        } else {
            Log.e(TAG, "Trying to launch multiple scan at same time");
        }
    }

    private void                    startArpScan() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new ScanNetmask(new IPv4CIDR(mSingleton.network.myIp, mSingleton.network.netmask), mInstance);
            }
        }).start();
    }
    private void                    startBonjourScan(List<Host> listOfHosts) {
        new BonjourManager(mActivity, listOfHosts, this);
    }

    private void                    startNmapScan() {
        mActivity.showSnackbar("Not implémented");

    }

    public void                     onReachableScanOver(ArrayList<String> ipReachable) {
        Log.d(TAG, "onReachableScanOver with : "+ ipReachable.size() + " ip(s) reachable");
        ArrayList<String> tmpAntiConcurentExecptionFFS = new ArrayList<>();
        tmpAntiConcurentExecptionFFS.addAll(ipReachable);
        NetUtils.dumpListHostFromARPTableInFile(mActivity, tmpAntiConcurentExecptionFFS);
        mActivity.setToolbarTitle(null, tmpAntiConcurentExecptionFFS.size() + " hosts detected");
        mActivity.setProgressState(1500);
        mActivity.setToolbarTitle(null,"Scanning " + tmpAntiConcurentExecptionFFS.size() + " devices");
        Fingerprint.getDevicesInfoFromCepter(mFragment);
        mActivity.setProgressState(2000);
    }

    public String                   getSSID() {
        return mSSID;
    }

}

