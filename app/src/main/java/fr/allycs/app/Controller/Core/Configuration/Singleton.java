package fr.allycs.app.Controller.Core.Configuration;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.Core.ArpSpoof;
import fr.allycs.app.Controller.Core.Dnsmasq.DnsmasqControl;
import fr.allycs.app.Controller.Database.DBSniffSession;
import fr.allycs.app.Controller.Network.IPTables;
import fr.allycs.app.Controller.Network.NetworkInformation;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.Model.Target.SniffSession;

public class                            Singleton {
    private static Singleton            mInstance = null;
    public static synchronized Singleton getInstance() {
        if(mInstance == null)
            mInstance = new Singleton();
        return mInstance;
    }
    private                             Singleton() {

    }
    PreferenceControler                 userPreference;
    public  int                         nbrInteface = 1;
    public boolean                      DebugMode = true, UltraDebugMode = false;
    String                              VERSION = "0xDEADBEEF";
    public String                       PcapPath;
    public String                       BinaryPath = null;
    public String                       FilesPath = null;

    public ArrayList<Host>              selectedHostsList = null;
    public List<ArpSpoof>               ArpSpoofProcessStack = new ArrayList<>();
    public NetworkInformation           network = null;
    public Session                      actualSession = null;
    private DnsmasqControl              dnsSpoofed = null;
    private SniffSession                actualSniffSession = null;
    private boolean                     sslstripMode = false, LockScreen = false;
    private boolean                     DnsControlstarted = false;


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
        return DnsControlstarted;
    }
    public void                         setDnsControlstarted(boolean dnsControlstarted) {
        DnsControlstarted = dnsControlstarted;
        //TODO: CHECK IF TCPDUMP IS STARTED, IF YES RESTART IT
        IPTables.sslConf();

    }
    public boolean                      isLockScreen() {
        return LockScreen;
    }
    public void                         setLockScreen(boolean lockScreen) {
        LockScreen = lockScreen;
        //TODO: LockScreen
        Log.i("setockScreenActived", "Not implemented");
    }

    public void                         resetActualSniffSession() {
        actualSniffSession = null;
    }
}
