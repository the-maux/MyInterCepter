package fr.dao.app.Core.Network.Discovery;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Network.IPv4Utils;
import fr.dao.app.Core.Network.NetDiscovering;
import fr.dao.app.Core.Nmap.NmapControler;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.View.HostDiscovery.HostDiscoveryScanFrgmnt;
import fr.dao.app.View.HostDiscovery.HostDiscoveryActivity;

public class                        NetworkDiscoveryControler {
    private String                  TAG = "NetworkDiscoveryControler";
    private HostDiscoveryScanFrgmnt mFragment;
    private Singleton               mSingleton = Singleton.getInstance();
    private HostDiscoveryActivity   mActivity;
    public boolean                  inLoading = false;
    private Date                    startScanning;

    private static                  NetworkDiscoveryControler            mInstance = null;

    public static boolean           over() {
        return mInstance != null && !mInstance.inLoading;
    }

    public static synchronized      NetworkDiscoveryControler getInstance(final HostDiscoveryScanFrgmnt fragmentHostDiscoveryScan) {
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
            public void run() {
                new IcmpScanNetmask(new IPv4Utils(mSingleton.network), mInstance);
            }
        }).start();
    }

    synchronized void               onArpScanOver(ArrayList<String> ipReachable) {
        ArrayList<String> ipsreachables = new ArrayList<>();
        ipsreachables.addAll(ipReachable);
        Log.d(TAG, "onIcmpScanOver with : "+ ipReachable.size() + " device(s) reachable");
        mActivity.setToolbarTitle(null, ipsreachables.size() + " hosts detected");
        ArrayList<String> basicHost = NetDiscovering.readARPTable(ipsreachables);
        Log.d(TAG, "onArpScanOver with : "+ ipReachable.size() + " device(s) reachable");
        Singleton.getInstance().actualNetwork = mFragment.updateStateOfHostAfterIcmp(basicHost);
        mActivity.setMAXIMUM_PROGRESS(basicHost.size());
        if (mSingleton.Settings.getUserPreferences().NmapMode > 0) {
            Log.d(TAG, "Nmap_Mode[" + mSingleton.Settings.getUserPreferences().NmapMode + "] so starting Nmap");
            new NmapControler(Singleton.getInstance().actualNetwork.listDevices(), this, Singleton.getInstance().actualNetwork, mActivity);
        } else {

            Log.d(TAG, "Nmap_Mode[" + mSingleton.Settings.getUserPreferences().NmapMode + "] so bypass Nmap");
            onNmapScanOver(Singleton.getInstance().actualNetwork.listDevices());
        }
    }

    public void                     onNmapScanOver(ArrayList<Host> hosts) {
        Log.d(TAG, "Scan took " + Utils.TimeDifference(startScanning));
        mFragment.onHostActualized(hosts);
    }

    public void                     setToolbarTitle(String title, String subtitle) {
        mActivity.setToolbarTitle(title, subtitle);
    }
/*
    private void                    startBonjourScan(List<Host> listOfHosts) {
        new BonjourManager(mActivity, listOfHosts, this);
    }
 */
}

