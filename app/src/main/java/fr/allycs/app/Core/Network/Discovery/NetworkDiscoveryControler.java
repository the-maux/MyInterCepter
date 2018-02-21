package fr.allycs.app.Core.Network.Discovery;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Core.Configuration.Utils;
import fr.allycs.app.Core.Network.IPv4Utils;
import fr.allycs.app.Core.Network.NetDiscovering;
import fr.allycs.app.Core.Nmap.NmapControler;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Network;
import fr.allycs.app.View.Activity.HostDiscovery.FragmentHostDiscoveryScan;
import fr.allycs.app.View.Activity.HostDiscovery.HostDiscoveryActivity;

public class                        NetworkDiscoveryControler {
    private String                  TAG = "NetworkDiscoveryControler";
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
            public void run() {
                new IcmpScanNetmask(new IPv4Utils(mSingleton.network), mInstance);
            }
        }).start();
    }

    synchronized void               onArpScanOver(ArrayList<String> ipReachable) {
        ArrayList<String> ipsreachables = new ArrayList<>();
        ipsreachables.addAll(ipReachable);
        Log.d(TAG, "onIcmpScanOver with : "+ ipReachable.size() + " ip(s) reachable");
        mActivity.setToolbarTitle(null, ipsreachables.size() + " hosts detected");
        mActivity.setMAXIMUM_PROGRESS(ipsreachables.size());
        ArrayList<String> basicHost = NetDiscovering.readARPTable(ipsreachables);
        Log.d(TAG, "onArpScanOver with : "+ ipReachable.size() + " ip(s) reachable");
        Network ap = mFragment.updateStateOfHostAfterIcmp(basicHost);
        new NmapControler(basicHost,this, ap);
    }

    public void                     onNmapScanOver(ArrayList<Host> hosts) {
        Log.d(TAG, "Full scanning in " + Utils.TimeDifference(startScanning));
        String time = Utils.TimeDifference(startScanning);
        mActivity.setToolbarTitle(mSingleton.network.Ssid,
                hosts.size() + " device" + ((hosts.size() > 1) ? "s" : "") +" found in " + time);
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

