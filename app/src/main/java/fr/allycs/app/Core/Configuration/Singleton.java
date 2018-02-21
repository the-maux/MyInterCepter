package fr.allycs.app.Core.Configuration;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Core.Database.DBSniffSession;
import fr.allycs.app.Core.Dnsmasq.DnsmasqControl;
import fr.allycs.app.Core.Network.ArpSpoof;
import fr.allycs.app.Core.Network.IPTables;
import fr.allycs.app.Core.Network.NetworkInformation;
import fr.allycs.app.Core.Tcpdump.Tcpdump;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Network;
import fr.allycs.app.Model.Target.SniffSession;

public class                            Singleton {
    PreferenceControler                 userPreference;
    public boolean                      DebugMode = true, UltraDebugMode = false;
    public String                       PcapPath;
    public String                       BinaryPath = null;
    public String                       FilesPath = null;

    public ArrayList<Host>              selectedHostsList = null;
    public List<ArpSpoof>               ArpSpoofProcessStack = new ArrayList<>();
    public NetworkInformation           network = null;
    public Network actualSession = null;
    private DnsmasqControl              dnsSpoofed = null;
    private SniffSession                actualSniffSession = null;
    private boolean                     sslstripMode = false, LockScreen = false;
    private boolean                     webSpoofedstarted = false;
    public boolean                      isNmapRunning = false;

    private static Singleton            mInstance = null;
    String                              VERSION = "0xDEADBEEF";
    public static synchronized Singleton getInstance() {
        if(mInstance == null)
            mInstance = new Singleton();
        return mInstance;
    }
    private                             Singleton() {}

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
        return sslstripMode;
    }
    public void                         setSslstripMode(boolean sslstripMode) {
        this.sslstripMode = sslstripMode;
        IPTables.sslConf();
    }
    public boolean                      isDnsControlstarted() {
        return dnsSpoofed != null && dnsSpoofed.isRunning();
    }
    public boolean                      isLockScreen() {
        return LockScreen;
    }
    public void                         setLockScreen(boolean lockScreen) {
        LockScreen = lockScreen;
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
        if (tcpdump.isRunning) {
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
        return tcpdump != null && tcpdump.isRunning ||
                !(!sslstripMode && !isDnsControlstarted() && !webSpoofedstarted);
    }
}
