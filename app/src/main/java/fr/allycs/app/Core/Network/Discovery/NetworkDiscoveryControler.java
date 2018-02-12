package fr.allycs.app.Core.Network.Discovery;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Core.Configuration.Utils;
import fr.allycs.app.Core.Network.BonjourService.BonjourManager;
import fr.allycs.app.Core.Network.IPv4CIDR;
import fr.allycs.app.Core.Network.NetUtils;
import fr.allycs.app.Core.Nmap.NmapControler;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.View.Activity.HostDiscovery.FragmentHostDiscoveryScan;
import fr.allycs.app.View.Activity.HostDiscovery.HostDiscoveryActivity;

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

    public boolean                   run(List<Host> listOfHosts) {
        if (!inLoading) {
            inLoading = true;
            startScanning = Calendar.getInstance().getTime();
            mSingleton.resetActualSniffSession();
            startArpScan();
            return true;
        } else {
            Log.e(TAG, "Trying to launch multiple scan at same time");
        }
        return false;
    }

    private void                    startArpScan() {
        startScanning = Calendar.getInstance().getTime();
        mActivity.setToolbarTitle(null, "Icmp scanning");
        mActivity.setProgressState(0);
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

     synchronized void              onReachableScanOver(ArrayList<String> ipReachable) {
        ArrayList<String> threadSafeArray = new ArrayList<>();
        threadSafeArray.addAll(ipReachable);
        Log.d(TAG, "onReachableScanOver with : "+ ipReachable.size() + " ip(s) reachable");
        mActivity.setToolbarTitle(null, threadSafeArray.size() + " hosts detected");
        mActivity.setMAXIMUM_PROGRESS(threadSafeArray.size());
        new NmapControler(NetUtils.readARPTable(mActivity, threadSafeArray),this);
    }

    public void                     onHostActualized(ArrayList<Host> hosts) {
        Log.d(TAG, "Full scanning in " + Utils.TimeDifference(startScanning));
        String time = Utils.TimeDifference(startScanning);
        setToolbarTitle(hosts.size() + " device" + ((hosts.size() > 1) ? "s" : "") + " discovered",
                mSingleton.network.Ssid  + " analyzed in " + time);
        mFragment.onHostActualized(hosts);
    }

    public void                     setToolbarTitle(String title, String subtitle) {
        mActivity.setToolbarTitle(title, subtitle);
    }

}

