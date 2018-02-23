package fr.dao.app.Core.Network.BonjourService;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import fr.dao.app.View.Activity.HostDiscovery.HostDiscoveryActivity;

public class                            DiscoveryListenr implements NsdManager.DiscoveryListener {
    private String                      TAG = "DiscoveryListenr";
    private BonjourManager              BonjourManager;
    private DiscoveryListenr            instance = this;
    private HostDiscoveryActivity       mActivity = null;
    private String                      type;

    public                              DiscoveryListenr(BonjourManager manager, String type, HostDiscoveryActivity activity) {
        this.BonjourManager = manager;
        this.type = type;
        this.mActivity = activity;
    }

    @Override public void               onStartDiscoveryFailed(String serviceType, int errorCode) {
        Log.e(TAG, "Discovery failed: Error code:" + errorCode + "for:" + serviceType);

        //this.BonjourManager.stopServiceDiscovery(this, type);
        BonjourManager.createListListener();
    }

    @Override public void               onStopDiscoveryFailed(String serviceType, int errorCode) {
        Log.e(TAG, "Discovery failed: Error code:" + errorCode);
        //this.BonjourManager.stopServiceDiscovery(this, type);
    }

    @Override public void               onDiscoveryStarted(String serviceType) {
        mActivity.setToolbarTitle("Services discovery", "Searching " + serviceType);
        Log.d(TAG, "Searching " + serviceType);
        new Thread (new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    BonjourManager.stopServiceDiscovery(instance);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }}).start();
    }

    @Override public void               onDiscoveryStopped(String serviceType) {
        BonjourManager.createListListener();
    }

    @Override public void               onServiceFound(NsdServiceInfo service) {
        mActivity.setToolbarTitle("Services discovery", service.getServiceName() + " Found");
        BonjourManager.resolveService(service);
    }

    @Override public void               onServiceLost(NsdServiceInfo service) {
        Log.e(TAG, "service lost" + service);
    }
}
