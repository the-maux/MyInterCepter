package fr.allycs.app.Controller.Network.BonjourService;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.net.InetAddress;
import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Model.Net.Service;
import fr.allycs.app.Model.Target.Host;

public class                ResolvListener implements NsdManager.ResolveListener {
    private String          TAG = "ResolvListener";
    private BonjourManager  manager;
    private List<Host>      mListClient;

    public                  ResolvListener(BonjourManager manager, List<Host> listClient) {
        this.manager = manager;
        this.mListClient = listClient;
    }

    @Override
    public void             onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
        // Called when the resolve fails. Use the error code to debug.
        Log.e(TAG, "Resolve failed " + errorCode);
        Log.e(TAG, "serivce = " + serviceInfo);
    }

    private Host            getclientNatifByIp(String ip) {
        for ( Object o : this.mListClient) {
            if (((Host)o).ip.contains(ip))
                return (Host) o;
        }
        return null;
    }
    @Override
    public void             onServiceResolved(NsdServiceInfo nsdService) {
        Log.d(TAG, "Resolve Succeeded. " + nsdService);
        InetAddress addr = nsdService.getHost();
        if (addr != null) {
            Host client = getclientNatifByIp(addr.getHostAddress());
            if (client != null) {
                if (Singleton.getInstance().DebugMode) {
                    Log.d(TAG, "addr HostAddress: " + addr.getHostAddress());
                    Log.d(TAG, "addr CanonicalHostName: " + addr.getCanonicalHostName());
                    Log.d(TAG, "addr Address: " + addr.getAddress());
                    Log.d(TAG, "addr HostName: " + addr.getHostName());
                    Log.d(TAG, "service.getHost: " + nsdService.getHost());
                    Log.d(TAG, "service.getPort: " + nsdService.getPort());
                    Log.d(TAG, "service.getServiceName: " + nsdService.getServiceName());
                    Log.d(TAG, "service.getServiceType" + nsdService.getServiceType());
                    Log.d(TAG, "service: " + nsdService);
                }
                Service service = new Service(addr.getHostAddress(),
                        addr.getCanonicalHostName(),
                        addr.getAddress(),
                        addr.getHostName(),
                        "" + nsdService.getPort(),
                        nsdService.getServiceName(),
                        nsdService.getServiceType(),
                        addr, nsdService, mListClient);
                client.updateServiceHost(service);
                manager.bingo(addr.getHostAddress(), nsdService.getServiceName(), service);
            }
        }
    }

}
