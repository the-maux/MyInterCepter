package fr.dao.app.Core.Network.BonjourService;


import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fr.dao.app.Core.Network.Discovery.NetworkDiscoveryControler;
import fr.dao.app.Model.Net.Service;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.View.Activity.HostDiscovery.HostDiscoveryActivity;

public class                        BonjourManager {
    private String                  TAG = "BonjourManager";
    private NsdManager              mNsdManager;
    private NetworkDiscoveryControler mScannerControler;
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

    public                          BonjourManager(HostDiscoveryActivity activity, List<Host> listClient, NetworkDiscoveryControler scannerControler) {
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
            DiscoveryListenr listener = new DiscoveryListenr(instance, type, mActivity);
            mNsdManager.discoverServices(type,
                    NsdManager.PROTOCOL_DNS_SD, listener);
            listDiscoveryListener.put(type, listener);
        } else {
            notifiyServiceAllScaned(listOfServiceFound);
            mScannerControler.inLoading = false;
        }
    }

    private void                    notifiyServiceAllScaned(final List<Service> listOfServiceFound) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.showSnackbar("Scanning service on Network finished");
                mActivity.actualNetwork.Services().addAll(listOfServiceFound);
                mActivity.actualNetwork.save();
            }
        });
    }

    void                            stopServiceDiscovery(NsdManager.DiscoveryListener listene) {
        this.mNsdManager.stopServiceDiscovery(listene);
    }
    void                            resolveService(NsdServiceInfo service) {
        this.mNsdManager.resolveService(service,  new ResolvListener(this, listClient));
    }

    public void                     bingo(String hostAddress, String serviceName, Service service) {
        listOfServiceFound.add(service);
        mActivity.showSnackbar("Service: " + serviceName + " on "+ hostAddress);
    }
}
