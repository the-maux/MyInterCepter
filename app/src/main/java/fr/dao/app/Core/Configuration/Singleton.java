package fr.dao.app.Core.Configuration;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import java.util.ArrayList;

import fr.dao.app.Core.Database.DBNetwork;
import fr.dao.app.Core.Database.DBSniffSession;
import fr.dao.app.Core.Dnsmasq.DnsmasqControl;
import fr.dao.app.Core.Network.ArpSpoof;
import fr.dao.app.Core.Network.IPTables;
import fr.dao.app.Core.Network.NetDiscovering;
import fr.dao.app.Core.Network.NetworkInformation;
import fr.dao.app.Core.Tcpdump.Tcpdump;
import fr.dao.app.Model.Config.Session;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.Model.Target.SniffSession;

public class                            Singleton {
    private static Singleton            mInstance = null;
    public  SettingsControler           Settings = null;
    public  Session                     Session;
    public  NetworkInformation          NetworkInformation = null;
    public  Network                     CurrentNetwork = null;
    public  ArrayList<Host>             hostList = null, savedHostList = null;
    private SniffSession                CurrentSniffSession = null;
    private DnsmasqControl              dnsSpoofed = null;
    private boolean                     webSpoofedstarted = false;
    String                              VERSION = "0xDEADBEEF";

    private                             Singleton() {
    }

    public static synchronized Singleton getInstance() {
        if(mInstance == null) {
            mInstance = new Singleton();
        }
        return mInstance;
    }

    public SniffSession getCurrentSniffSession() {
        if (CurrentSniffSession == null) {
            CurrentSniffSession = DBSniffSession.buildSniffSession();
        }
       return CurrentSniffSession;
    }
    public DnsmasqControl               getDnsControler() {
        if (dnsSpoofed == null) {
            dnsSpoofed = new DnsmasqControl();
        }
        return dnsSpoofed;
    }

    public boolean                      isSslstripMode() {
        return Settings.getUserPreferences().sslstripMode;
    }
    public void                         setSslstripMode(boolean sslstripMode) {
        Settings.getUserPreferences().sslstripMode = sslstripMode;
        IPTables.sslConf();
    }
    public boolean                      isDnsControlstarted() {
        return dnsSpoofed != null && dnsSpoofed.isRunning();
    }
    public boolean                      isLockScreen() {
        return Settings.getUserPreferences().Lockscreen;
    }
    public void                         setLockScreen(boolean lockScreen) {
        Settings.getUserPreferences().Lockscreen = lockScreen;
        //TODO: LockScreen
        Log.i("setockScreenActived", "Not implemented");
    }

    public boolean                      iswebSpoofed() {
        return webSpoofedstarted;
    }
    public void                         setwebSpoofed(boolean webSpoofed) {
        webSpoofedstarted = webSpoofed;
    }

    public void                         resetActualSniffSession() {
        CurrentSniffSession = null;
    }

    public void                         closeEverySniffService(Activity activity) {
        Tcpdump tcpdump = Tcpdump.getTcpdump(activity, false);
        if (Tcpdump.isRunning()) {
            Log.e("Singleton", "Stopping tcpdump not implemented");
        }
        if (!ArpSpoof.ArpSpoofProcessStack.isEmpty()) {
            Log.e("Singleton", "Stopping ArpSpoof not implemented");
        }
        if (dnsSpoofed != null) {
            Log.e("Singleton", "Stopping dnsSpoofnot implemented");
            dnsSpoofed.stop();
        }
        if (webSpoofedstarted) {
            Log.e("Singleton", "Stopping webspoof not implemented");
        }
    }

    public boolean                      isSniffServiceActif(Activity activity) {
        return Tcpdump.getTcpdump(activity, false) != null && Tcpdump.isRunning() ||
                !(!Settings.getUserPreferences().sslstripMode && !isDnsControlstarted() && !webSpoofedstarted);
    }

    public void                         init(Activity activity) {
        if (Settings == null) {
            Log.d("Singleton", "Singleton"+"::initialisation");
            Settings = new SettingsControler(activity.getFilesDir().getPath() + '/');
            Settings.PcapPath = Environment.getExternalStorageDirectory().getPath() + "/Dao/Pcap/";
            Settings.DumpsPath = Environment.getExternalStorageDirectory().getPath() + "/Dao/Nmap/";
            Settings.BinaryPath = Settings.FilesPath;
            if (NetDiscovering.initNetworkInfo(activity))
                CurrentNetwork = DBNetwork.getAPFromSSID(NetworkInformation.ssid);
        }
    }
}
