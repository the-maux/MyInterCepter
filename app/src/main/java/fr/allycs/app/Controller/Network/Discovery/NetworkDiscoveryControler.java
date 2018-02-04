package fr.allycs.app.Controller.Network.Discovery;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.allycs.app.Controller.AndroidUtils.Utils;
import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.Nmap.NmapControler;
import fr.allycs.app.Controller.Network.BonjourService.BonjourManager;
import fr.allycs.app.Controller.Network.IPv4CIDR;
import fr.allycs.app.Controller.Network.NetUtils;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.View.HostDiscovery.FragmentHostDiscoveryScan;
import fr.allycs.app.View.HostDiscovery.HostDiscoveryActivity;

public class                        NetworkDiscoveryControler {
    private String                  TAG = "NetworkDiscoveryControler";
    public enum typeScan {          Arp, Services, Historic }
    private FragmentHostDiscoveryScan mFragment;
    private Singleton               mSingleton = Singleton.getInstance();
    private HostDiscoveryActivity   mActivity;
    public boolean                  inLoading = false;
    private Date                    startScanning;

    private static                  NetworkDiscoveryControler            mInstance = null;

    public static synchronized      NetworkDiscoveryControler getInstance(final FragmentHostDiscoveryScan fragmentHostDiscoveryScan) {
        if(mInstance == null)
            mInstance = new NetworkDiscoveryControler();
        mInstance.mActivity = (HostDiscoveryActivity) fragmentHostDiscoveryScan.getActivity();
        mInstance.mFragment = fragmentHostDiscoveryScan;
        return mInstance;
    }

    public void                     run(List<Host> listOfHosts) {
        if (!inLoading) {
            inLoading = true;
            startScanning = Calendar.getInstance().getTime();
            mSingleton.resetActualSniffSession();
            startArpScan();
        } else {
            Log.e(TAG, "Trying to launch multiple scan at same time");
        }
    }

    private void                    startArpScan() {
        startScanning = Calendar.getInstance().getTime();
        new Thread(new Runnable() {
            @Override
            public void run() {
                new IcmpScanNetmask(new IPv4CIDR(mSingleton.network), mInstance);
            }
        }).start();
    }
    private void                    startBonjourScan(List<Host> listOfHosts) {
        new BonjourManager(mActivity, listOfHosts, this);
    }

    public void                     onReachableScanOver(ArrayList<String> ipReachable) {
        Log.d(TAG, "onReachableScanOver with : "+ ipReachable.size() + " ip(s) reachable");
        ArrayList<String> tmpAntiConcurentExecptionFFS = new ArrayList<>();
        tmpAntiConcurentExecptionFFS.addAll(ipReachable);
        mActivity.setToolbarTitle(null, tmpAntiConcurentExecptionFFS.size() + " hosts detected");
        mActivity.setProgressState(1500);
        mActivity.setToolbarTitle(null,"Scanning " + tmpAntiConcurentExecptionFFS.size() + " devices");
        new NmapControler(NetUtils.readARPTable(mActivity, tmpAntiConcurentExecptionFFS),this);
        mActivity.setProgressState(2000);
    }

    public void                     onHostActualized(ArrayList<Host> hosts) {
        Log.d(TAG, "Full scanning in " + Utils.TimeDifference(startScanning));
        mFragment.onHostActualized(hosts);
    }

    public void                     setToolbarTitle(String title, String subtitle) {
        mActivity.setToolbarTitle(title, subtitle);
    }

}

