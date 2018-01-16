package fr.allycs.app.Controller.Network.Discovery;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.jmdns.JmDNS;

import fr.allycs.app.Controller.Core.Conf.Singleton;

public class                        MapNetwork {
    private String                  TAG = "MapNetwork";
    private ArrayList<String>       ipReachable;
    private String                  routerIp;
    private NetworkDiscoveryControler scannerControler;
    private static MapNetwork       instance = null;
    private String                  PATH_TO_PING_BINARY = Singleton.getInstance().FilesPath + "busybox ";

    private                         MapNetwork(NetworkDiscoveryControler scannerControler, String routerIp) {
        this.scannerControler = scannerControler;
        this.routerIp = routerIp;
        this.ipReachable = new ArrayList<>();
        DiscoverNetwork();
    }

    public static synchronized MapNetwork getInstance(NetworkDiscoveryControler scannerControler, String routerIp) {
        if (instance == null) {
            instance = new MapNetwork(scannerControler, routerIp);
        }
        return instance;
    }

    private void                    DiscoverNetwork() {
        Log.w(TAG, "Starting Discover Natif client in Network");

        final String ipBuild = routerIp.substring(0, routerIp.lastIndexOf('.'));
        for (int rcx = 1; rcx <= 255 ; rcx++) {
            final int copyFinalRcx = rcx;
            final String ip = ipBuild + "." + copyFinalRcx;
            new Thread (new Runnable() {
                @Override
                public void run() {
                    if (ping(ip)) {
                        Log.d(TAG, "Host : " + ip +  " is reachable");
                        getJmDNSName(ip);
                        ipReachable.add(ip);
                    } else {
                        Log.d(TAG, "Host : " + ip +  " is reachable");
                    }
                }}).start();
        }
        Log.w(TAG, "Stoping Discover Natif client in Network");
    }
    private void                    getJmDNSName(String ip) {
        try {
            InetAddress localHost = InetAddress.getByName(ip);
            JmDNS jmdns = JmDNS.create(localHost);
            Log.d("MapNetWork", "add Client [" + ip + "]" + jmdns.getHostName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.d(TAG, "UnknowHostException at JmDNS");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IO EXCEPTION AT JmDNS");
        }
    }
    public boolean                  ping(String host) {
        try {
//            RootProcess pingProces = new RootProcess("ICMP SCAN")
            Process  mIpAddrProcess = Runtime.getRuntime().exec(Singleton.getInstance().BinaryPath + "/busybox ping -c 1 " + host);
            if( mIpAddrProcess.waitFor() == 0){
                return true;
            } else {
                return false;
            }
        }
        catch (InterruptedException ignore)        {
            Log.w("MapNetwork", host + " not reachable interupted ");
            ignore.printStackTrace();
        }
        catch (IOException e)        {
            Log.w("MapNetwork", host + " not reachable IO ");
            e.printStackTrace();
        }
        return false;
    }

    private void                    purgeAndRestartService() {
        ipReachable = new ArrayList<>();
        DiscoverNetwork();
    }
    public ArrayList<String>        getStackclient() {
        return this.ipReachable;
    }
}