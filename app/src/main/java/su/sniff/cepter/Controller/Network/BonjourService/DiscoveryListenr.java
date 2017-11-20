package su.sniff.cepter.Controller.Network.BonjourService;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

public class                            DiscoveryListenr implements NsdManager.DiscoveryListener {
    private String                      TAG = "DiscoveryListenr";
    private BonjourManager              BonjourManager;
    private String                      type;
    private DiscoveryListenr            instance = this;

    public                              DiscoveryListenr(BonjourManager manager, String type) {
        this.BonjourManager = manager;
        this.type = type;
    }

    @Override
    public void                         onStartDiscoveryFailed(String serviceType, int errorCode) {
        Log.e(TAG, "Discovery failed: Error code:" + errorCode + "for:" + serviceType);
        //this.BonjourManager.stopServiceDiscovery(this, type);
        BonjourManager.createListListener();
    }

    @Override
    public void                         onStopDiscoveryFailed(String serviceType, int errorCode) {
        Log.e(TAG, "Discovery failed: Error code:" + errorCode);
        //this.BonjourManager.stopServiceDiscovery(this, type);
    }

    @Override
    public void                         onDiscoveryStarted(String serviceType) {
        Log.d(TAG, "Service discovery started:" + serviceType);
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

    @Override
    public void                         onDiscoveryStopped(String serviceType) {
        BonjourManager.createListListener();
    }

    @Override
    public void                         onServiceFound(NsdServiceInfo service) {
        BonjourManager.resolveService(service);
    }

    @Override
    public void                         onServiceLost(NsdServiceInfo service) {
        Log.e(TAG, "service lost" + service);
    }

}
