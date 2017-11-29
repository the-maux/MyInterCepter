package fr.allycs.app.Controller.Core.Conf;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.Core.BinaryWrapper.Dns.DnsControl;
import fr.allycs.app.Controller.Network.IPTables;
import fr.allycs.app.Controller.Core.BinaryWrapper.ArpSpoof;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Net.NetworkInformation;

public class                            Singleton {
    private static Singleton            mInstance = null;
    private                             Singleton() {}
    public static synchronized Singleton getInstance() {
        if(mInstance == null)
            mInstance = new Singleton();
        return mInstance;
    }

    private DnsControl                  dnsSpoofed = null;
    public ArrayList<Host>              hostsList = null;
    public List<ArpSpoof>               ArpSpoofProcessStack = new ArrayList<>();
    public NetworkInformation           network = null;

    private boolean                     sslstripMode = false, LockScreen = false;
    private boolean                     DnsControlstarted = false, isTcpdumpStarted = false;

    public  int                         nbrInteface = 1;
    public boolean                      DebugMode = true, UltraDebugMode = false;
    public String                       VERSION = "0xDEADBEEF";
    public String                       PcapPath;
    public String                       BinaryPath = null;
    public String                       FilesPath = null;


    public DnsControl                   getDnsControler() {
        if (dnsSpoofed == null) {
            dnsSpoofed = new DnsControl();
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
        //TODO: DNSSoof
        Log.i("setDnsControlstarted", "Not implemented");
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

}
