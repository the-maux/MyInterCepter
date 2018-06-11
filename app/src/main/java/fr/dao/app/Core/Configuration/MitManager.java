package fr.dao.app.Core.Configuration;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Dnsmasq.DnsmasqControl;
import fr.dao.app.Core.Network.ArpSpoof;
import fr.dao.app.Core.Network.IPTables;
import fr.dao.app.Core.Tcpdump.Proxy;
import fr.dao.app.Core.Tcpdump.Tcpdump;
import fr.dao.app.Model.Target.Host;

//TODO: tu dois faire le lien en prenant le relai de ce qui étais dans Singleton
public class                        MitManager {
    private String                  TAG = "MitmManager";
    private static final MitManager ourInstance = new MitManager();
    public static MitManager        getInstance() {
        return ourInstance;
    }

    private List<Host>              targets = new ArrayList<>();
    private DnsmasqControl          dnsSpoofed = null;
    private boolean                 webSpoofedstarted = false;
    private boolean                 trafficRedirected = false;
    private boolean                 isAttackRunning = false;

    public boolean                  isProxyRunning() {
        return Proxy.isRunning();
    }
    public boolean                  isTcpdumpRunning() {
        return Tcpdump.isRunning();
    }
    public boolean                  isTrafficRedirected() {
        return trafficRedirected;
    }
    public boolean                  iswebSpoofed() {
        return webSpoofedstarted;
    }
    public boolean                  isSniffServiceActif(Activity activity) {
        return Tcpdump.getTcpdump(activity, false) != null && Tcpdump.isRunning() ||
                !(!MitManager.getInstance().isDnsControlstarted() && !webSpoofedstarted);
    }
    public boolean                  isDnsControlstarted() {
        return dnsSpoofed != null && dnsSpoofed.isRunning();
    }

    /**
     *
     */
    private void                    initMitmConnection() {
        Log.d(TAG, "initMitmConnection");
        IPTables.startForwardingStream();
        ArpSpoof.launchArpSpoof(targets);
        trafficRedirected = true;
    }
    public boolean                  initTcpDump() {
        Log.d(TAG, "initTcpDump");
        initMitmConnection();
        return true;
    }
    public DnsmasqControl           initDNSSpoofing() {
        Log.d(TAG, "initDNSSpoofing");
        if (!trafficRedirected)
            initMitmConnection();
        IPTables.startDnsPacketRedirect();
        isAttackRunning = true;
        return new DnsmasqControl();
    }
    public boolean                  initWebserver() {
        Log.d(TAG, "initWebserver");
        //TODO:
        webSpoofedstarted = true;
        isAttackRunning = true;
        return true;
    }
    public void                     setTrafficRedirected(boolean isRedirected) {
        trafficRedirected = isRedirected;
    }

    public void                     stopTcpdump(boolean isShutdown) {
        Log.d(TAG, "stopTcpdump");
        if (!stopMITMBehavior()) {
            //TODO: We need to close the Tcpdump current process, even if there is still MITM behavior
        }
        if (!isShutdown)
            updateRunningStatus();
    }
    public void                     stopProxy() {
        Log.d(TAG, "stopTcpdump");
        if (!stopMITMBehavior()) {
            //TODO: We need to close the Tcpdump current process, even if there is still MITM behavior
        }
        updateRunningStatus();
    }
    public boolean                  stopDnsSpoofing(boolean isShutdown) {
        Log.d(TAG, "stopDnsSpoofing");
        dnsSpoofed.stop();
        if (!isShutdown)
            updateRunningStatus();
        return true;
    }
    public void                     stopWebserver(boolean isShutdown) {
        Log.d(TAG, "stopWebserver");
        webSpoofedstarted = false;
        if (!isShutdown)
            updateRunningStatus();
    }
    public boolean                  stopMITMBehavior() {
        if (!isProxyRunning() && isTcpdumpRunning() && isDnsControlstarted()) {
            stopEverything();
            return true;
        } else {
            Log.d(TAG, "Still some Mitm modules alive");
            return false;
        }
    }
    public void                     stopEverything() {
        Log.d(TAG, "stopEverything");
        if (dnsSpoofed != null && dnsSpoofed.isRunning())
            stopDnsSpoofing(true);
        if (webSpoofedstarted)
            stopWebserver(true);
        if (Tcpdump.isRunning() && Tcpdump.getTcpdump() != null) {
            stopTcpdump(true);
        }
        trafficRedirected = false;
        RootProcess.kill("tcpdump");
        ArpSpoof.stopArpSpoof();
        IPTables.stopIpTable();
        isAttackRunning = false;
    }

    public void                     loadHost(List<Host> targets) {
        if (isAttackRunning)
            stopEverything();
        this.targets.clear();
        this.targets.addAll(targets);
    }
    private void                    updateRunningStatus() {
        Log.d(TAG, "updateRunningStatus");
        stopMITMBehavior();
    }
    public DnsmasqControl           getDnsControler() {
        if (dnsSpoofed == null) {
            dnsSpoofed = initDNSSpoofing();
        }
        return dnsSpoofed;
    }

}
