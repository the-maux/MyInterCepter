package fr.dao.app.Core.Network.Discovery;

import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Core.Network.IPv4Utils;
import fr.dao.app.Core.Network.NetDiscovering;
import fr.dao.app.Core.Nmap.Fingerprint;
import fr.dao.app.Core.Nmap.NmapControler;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.View.HostDiscovery.HostDiscoveryScanFrgmnt;
import fr.dao.app.View.ZViewController.Activity.MyActivity;

public class                        NetworkDiscoveryControler {
    private String                  TAG = "NetworkDiscoveryControler";
    private HostDiscoveryScanFrgmnt mFragment;
    private Singleton               mSingleton = Singleton.getInstance();
    private MyActivity              mActivity;
    public boolean                  inLoading = false;
    private Date                    startScanning;
    public boolean                  isFromHostDiscoveryActivity = false;
    private static                  NetworkDiscoveryControler            mInstance = null;
    private boolean                 isJustCheckingWhoIsAlive = true;
    private boolean                 isLoadedOnce = false;

    public static boolean           over() {
        return mInstance != null && !mInstance.inLoading;
    }

    public static synchronized      NetworkDiscoveryControler getInstance(final HostDiscoveryScanFrgmnt fragmentHostDiscoveryScan) {
        if(mInstance == null)
            mInstance = new NetworkDiscoveryControler();
        mInstance.mActivity = (MyActivity) fragmentHostDiscoveryScan.getActivity();
        mInstance.mFragment = fragmentHostDiscoveryScan;
        mInstance.isFromHostDiscoveryActivity = true;
        return mInstance;
    }
    public static synchronized      NetworkDiscoveryControler getInstance(final MyActivity activity) {
        if(mInstance == null)
            mInstance = new NetworkDiscoveryControler();
        mInstance.mActivity = activity;
        mInstance.mFragment = null;
        mInstance.isFromHostDiscoveryActivity = false;
        return mInstance;
    }

    public static synchronized  boolean isHostListLoaded() {
        if (mInstance == null)
            return false;
        if (!mInstance.isLoadedOnce)
            return false;
        if (mInstance.mSingleton.hostList == null || mInstance.mSingleton.hostList.isEmpty())
            return false;
        return true;
    }

    public boolean                   run(boolean isJustCheckingWhoIsAlive) {
        Log.i(TAG, "run::WifiInit");
        if (!NetDiscovering.initNetworkInfo(mActivity) || !mSingleton.network.updateInfo().isConnectedToNetwork()) {
            mActivity.showSnackbar("No wifi connection detected");
            Log.i(TAG, "No wifi connection detected");
            return false;
        }
        Log.i(TAG, "run::init");
        if (!inLoading) {
            this.isJustCheckingWhoIsAlive = isJustCheckingWhoIsAlive;
            inLoading = true;
            startScanning = Calendar.getInstance().getTime();
            Log.i(TAG, "run::mSingleton.resetActualSniffSession");
            mSingleton.resetActualSniffSession();
            startScan();
            return true;
        } else {
            Log.i(TAG, "Trying to launch multiple scan at same time");
        }
        return false;
    }

    private void                    startScan() {
        startScanning = Calendar.getInstance().getTime();
        mActivity.setToolbarTitle(null, "Icmp scanning");
        mActivity.setProgressState(0);
        new Thread(new Runnable() {
            public void run() {
                new IcmpScanNetmask(new IPv4Utils(mSingleton.network), mInstance);
            }
        }).start();
        Log.i(TAG, "startScan::IcmpScanNetmask::started");
    }

    synchronized void               onArpScanOver(ArrayList<String> ipReachable) {
        Log.i(TAG, "onArpScanOver:: "+ ipReachable.size() + " device(s) reachable");
        ArrayList<String> ipsreachables = new ArrayList<>();
        ipsreachables.addAll(ipReachable);
        mActivity.setToolbarTitle(null, ipsreachables.size() + " hosts detected");
        ArrayList<String> basicHost = NetDiscovering.readARPTable(ipsreachables);
        Log.i(TAG, "onArpScanOver::readARPTable::"+ ipReachable.size() + " device(s) from ARP");
        Singleton.getInstance().actualNetwork = updateHostStatus(basicHost);
        if (isFromHostDiscoveryActivity) {
            Log.i(TAG, "onArpScanOver::mFragment.updateStateOfHostAfterIcmp");
            mFragment.updateStateOfHostAfterIcmp(Singleton.getInstance().actualNetwork);
        }
        mActivity.MAXIMUM_PROGRESS = basicHost.size();
        if (mSingleton.Settings.getUserPreferences().NmapMode > 0 && isFromHostDiscoveryActivity) {
            Log.i(TAG, "onArpScanOver::Nmap::TypeScan::"+mSingleton.Settings.getUserPreferences().NmapMode+"::StartingNmap");
            new NmapControler(Singleton.getInstance().actualNetwork.listDevices(), this, Singleton.getInstance().actualNetwork, mActivity);
            return;
        }
        if (isJustCheckingWhoIsAlive) {
            Log.i(TAG, "onArpScanOver::Nmap::JustCheckingHostAlive::BypassNmap");
        } else {
            Log.i(TAG, "onArpScanOver::Nmap::TypeScan::"+mSingleton.Settings.getUserPreferences().NmapMode+"::BypassNmap");
        }
        onScanFinished(Singleton.getInstance().actualNetwork.listDevices());
    }

    private Network                    updateHostStatus(ArrayList<String> ipReachables) {
        Network actualNetwork = mSingleton.actualNetwork;
        int rax = 0;
        for (Host host : actualNetwork.listDevices()) {
            host.state = Host.State.OFFLINE;
        }
        if (ipReachables != null)
            for (String ipAndMacReachable : ipReachables) {
                rax = rax + 1;
                String ip = ipAndMacReachable.split(":")[0];
                String mac = ipAndMacReachable.replace(ipAndMacReachable.split(":")[0]+":", "").toUpperCase();
                boolean isHostInList = false;
                for (Host host : actualNetwork.listDevices()) {
                    if (host.mac.contains(mac)) {
                        host.state = Host.State.ONLINE;
                        isHostInList = true;
                        break;
                    }
                }
                if (!isHostInList) {
                    Host host = new Host();
                    host.ip = ip;
                    host.mac = mac;
                    if (mSingleton.Settings.getUserPreferences().NmapMode == 0) {/*No nmap so, Local vendor*/
                        host.vendor = Fingerprint.getVendorFrom(host.mac);//TODO: Thread this
                        Fingerprint.initHost(host);
                    }
                    DBHost.saveOrGetInDatabase(host);
                    host.state = Host.State.ONLINE;
                    host.save();
                    actualNetwork.listDevices().add(host);
                }
            }
        actualNetwork.offensifAction = actualNetwork.offensifAction + 1;
        actualNetwork.save();
        Log.i(TAG, "(" + (actualNetwork.listDevices().size() - rax) + " offline/ " + actualNetwork.listDevices().size() + "inCache) ");
        mSingleton.actualNetwork = actualNetwork;
        return actualNetwork;
    }

    public void                     onScanFinished(ArrayList<Host> hosts) {
        Log.i(TAG, "onScanFinished::"+
                (isFromHostDiscoveryActivity?"mFragment":"mActivity")+".onHostActualized::"+
                hosts.size() + " hosts found on " + Utils.TimeDifference(startScanning));
        isLoadedOnce = true;
        inLoading = false;
        if (isFromHostDiscoveryActivity) {
            mFragment.onHostActualized(hosts);
        } else {
            mActivity.onHostActualized(hosts);
        }
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

