package fr.dao.app.Core.Configuration;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Database.DBSniffSession;
import fr.dao.app.Core.Dnsmasq.DnsmasqControl;
import fr.dao.app.Core.Network.ArpSpoof;
import fr.dao.app.Core.Network.IPTables;
import fr.dao.app.Core.Network.NetworkInformation;
import fr.dao.app.Core.Tcpdump.Tcpdump;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.Model.Target.SniffSession;

public class                            Singleton {
    public  SettingsControler           Settings = null;
    public  NetworkInformation          network = null;
    public  Network                     actualNetwork = null;
    public  ArrayList<Host>             hostList = null, savedHostList = null;
    public  List<ArpSpoof>              ArpSpoofProcessStack = new ArrayList<>();
    private DnsmasqControl              dnsSpoofed = null;
    private SniffSession                actualSniffSession = null;
    private boolean                     webSpoofedstarted = false, isNmapRunning = false;
    private static Singleton            mInstance = null;
    String                              VERSION = "0xDEADBEEF";

    private                             Singleton() {}

    public static synchronized Singleton getInstance() {
        if(mInstance == null)
            mInstance = new Singleton();
        return mInstance;
    }

    public SniffSession                 getActualSniffSession() {
        if (actualSniffSession == null) {
            actualSniffSession = DBSniffSession.buildSniffSession();
        }
       return actualSniffSession;
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
        actualSniffSession = null;
    }

    public void                         closeEverySniffService(Activity activity) {
        Tcpdump tcpdump = Tcpdump.getTcpdump(activity, false);
        if (Tcpdump.isRunning()) {
            Log.e("Singleton", "Stopping tcpdump not implemented");
        }
        if (!ArpSpoofProcessStack.isEmpty()) {
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
        Tcpdump tcpdump = Tcpdump.getTcpdump(activity, false);
        return tcpdump != null && Tcpdump.isRunning() ||
                !(!Settings.getUserPreferences().sslstripMode && !isDnsControlstarted() && !webSpoofedstarted);
    }
}
