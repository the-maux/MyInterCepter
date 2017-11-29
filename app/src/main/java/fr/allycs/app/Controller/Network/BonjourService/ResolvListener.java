package fr.allycs.app.Controller.Network.BonjourService;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;

import fr.allycs.app.Model.Target.Host;

public class ResolvListener implements NsdManager.ResolveListener {
    private String          TAG = "ResolvListener";
    private BonjourManager  manager;
    private ArrayList<Host> listClient;

    public                  ResolvListener(BonjourManager manager, ArrayList<Host> listClient) {
        this.manager = manager;
        this.listClient = listClient;
    }

    @Override
    public void             onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
        // Called when the resolve fails. Use the error code to debug.
        Log.e(TAG, "Resolve failed " + errorCode);
        Log.e(TAG, "serivce = " + serviceInfo);
    }

    private Host            getclientNatifByIp(String ip) {
        for ( Object o : this.listClient) {
            if (((Host)o).getIp().contains(ip))
                return (Host) o;
        }
        return null;
    }
    @Override
    public void             onServiceResolved(NsdServiceInfo service) {
        Log.d(TAG, "Resolve Succeeded. " + service);
        InetAddress addr = service.getHost();
        if (addr != null) {
            Host client = getclientNatifByIp(addr.getHostAddress());
            if (client != null) {
                Log.d(TAG, "addr HostAddress: " + addr.getHostAddress());
                Log.d(TAG, "addr CanonicalHostName: " + addr.getCanonicalHostName());
                Log.d(TAG, "addr Address: " + addr.getAddress());
                Log.d(TAG, "addr HostName: " + addr.getHostName());
                Log.d(TAG, "service.getHost: " + service.getHost());
                Log.d(TAG, "service.getPort: " + service.getPort());
                Log.d(TAG, "service.getServiceName: " + service.getServiceName());
                Log.d(TAG, "service.getServiceType" + service.getServiceType());
                Log.d(TAG, "service: " + service);
                client.updateServiceHost(
                        new Service(addr.getHostAddress(),
                                addr.getCanonicalHostName(),
                                addr.getAddress(),
                                addr.getHostName(),
                                "" + service.getPort(),
                                service.getServiceName(),
                                service.getServiceType(),
                                addr, service));
                manager.bingo(addr.getHostAddress(), service.getServiceName());
            }
        }
    }
    /*
    Log.d(TAG, "addr HostAddress: " + addr.getHostAddress());
        Log.d(TAG, "addr CanonicalHostName: " + addr.getCanonicalHostName());
        Log.d(TAG, "addr Address: " + addr.getAddress());
        Log.d(TAG, "addr HostName: " + addr.getHostName());
        Log.d(TAG, "service.getHost: " + service.getHost());
        Log.d(TAG, "service.getPort: " + service.getPort());
        Log.d(TAG, "service.getServiceName: " + service.getServiceName());
        Log.d(TAG, "service.getServiceType" + service.getServiceType());
        Log.d(TAG, "service: " + service);
     */
}
