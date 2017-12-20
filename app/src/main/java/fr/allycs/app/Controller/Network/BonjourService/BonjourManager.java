package fr.allycs.app.Controller.Network.BonjourService;


import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.allycs.app.Controller.Network.Discovery.HostDiscoveryScan;
import fr.allycs.app.Model.Net.Service;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.View.HostDiscovery.HostDiscoveryActivity;

public class                        BonjourManager {
    private String                  TAG = "BonjourManager";
    private NsdManager              mNsdManager;
    private HostDiscoveryScan       mScannerControler;
    private HashMap<String, DiscoveryListenr> listDiscoveryListener = new HashMap<>();
    private BonjourManager          instance = this;
    private String[]                listServiceType;
    private int                     offsetServiceType = 0;
    private List<Host>              listClient;
    private HostDiscoveryActivity   mActivity;
    private List<Service>           listOfServiceFound = new ArrayList<>();
    private String[]                getAllType() {
        //all in mac _services._dns-sd._udp.local.
        // _workstation._tcp.local.
        return (new String[]{"_workstation._tcp",
                "_airplay._tcp.",
                "_raop._tcp", //Also known as AirTunes.
                "_screencast._udp",
                "_airplay._tcp",
                "_nfs._tcp", //The Finder browses for NFS servers starting in Mac OS X 10.2.
                "_ftp._tcp", //
                "_http._tcp", //
                "_telnet._tcp", //
                "_printer._tcp", //
                "_ipp._tcp", //Print Center browses for IPP printers starting in Mac OS X 10.2.
                "_daap._tcp", //Also known as iTunes Music Sharing.
                "_dpap._tcp", //Also known as iPhoto Photo Sharing.
                "_airport._tcp", //sed by the AirPort Admin Utility
                "_mdns._tcp",
                "_mdnsresponder._tcp",
                "_ssh._tcp"});
    }

    public                          BonjourManager(HostDiscoveryActivity activity, List<Host> listClient, HostDiscoveryScan scannerControler) {
        Log.d(TAG, "Bonjour Manager starting");
        mScannerControler = scannerControler;
        mNsdManager = (NsdManager) activity.getSystemService(Context.NSD_SERVICE);
        mActivity = activity;
        this.listServiceType = getAllType();
        this.listClient = listClient;
        createListListener();
    }
    void                            createListListener() {
        if (offsetServiceType < listServiceType.length) {
            String type = listServiceType[offsetServiceType++];
            //TODO:maybe cant be 1 DiscoveryListener for all Servicediscovery
            DiscoveryListenr listener = new DiscoveryListenr(instance, type);
            mNsdManager.discoverServices(type,
                    NsdManager.PROTOCOL_DNS_SD, listener);
            listDiscoveryListener.put(type, listener);
        } else {
            mActivity.notifiyServiceAllScaned(listOfServiceFound);
            mScannerControler.inLoading = false;
        }
    }
    void                            stopServiceDiscovery(NsdManager.DiscoveryListener listene) {
        this.mNsdManager.stopServiceDiscovery(listene);
    }
    void                            resolveService(NsdServiceInfo service) {
        this.mNsdManager.resolveService(service,  new ResolvListener(this, listClient));
    }

    public void                     bingo(String hostAddress, String serviceName, Service service) {
        listOfServiceFound.add(service);
        Snackbar.make(mActivity.mCoordinatorLayout, "Service: " + serviceName + " on "+ hostAddress , Snackbar.LENGTH_LONG).show();
    }
}
