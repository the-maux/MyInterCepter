package fr.dao.app.Core.Configuration;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Dnsmasq.DnsmasqControl;
import fr.dao.app.Core.Network.ArpSpoof;
import fr.dao.app.Core.Network.IPTables;
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


    private void                    initMitmConnection() {
        Log.d(TAG, "initMitmConnection");
        IPTables.startForwardingStream();
        ArpSpoof.launchArpSpoof(targets);
        trafficRedirected = true;
    }
    public boolean                  initTcpDump() {
        Log.d(TAG, "initTcpDump");
        //TODO: IF IPTables startForwardingStream TODO2: SSLSTRIP
        if (!trafficRedirected)
            initMitmConnection();
        IPTables.startForwardingStream();
        ArpSpoof.launchArpSpoof(targets);
        isAttackRunning = true;
        return true;
    }
    public DnsmasqControl           initDNSSpoofing() {
        Log.d(TAG, "initDNSSpoofing");
        if (!trafficRedirected)//TODO: Start the MITM connection
            initMitmConnection();
        IPTables.startDnsPacketRedirect();
        isAttackRunning = true;
        return dnsSpoofed;
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

    private void                    stopMitmConnection() {
        Log.d(TAG, "stopMitmConnection");
        IPTables.stopIpTable();
        ArpSpoof.stopArpSpoof();
        trafficRedirected = false;
    }
    public void                     stopTcpdump(boolean isShutdown) {
        Log.d(TAG, "stopTcpdump");
        Tcpdump.getTcpdump().stop();
        if (!isShutdown)
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
        if (Tcpdump.isRunning() ||
            (dnsSpoofed != null && dnsSpoofed.isRunning()) ||
                (webSpoofedstarted)) {
            Log.d(TAG, "Every modules closed");
            isAttackRunning = false;
            stopMitmConnection();
        } else {
            Log.d(TAG, "Still some Mitm modules alive");
        }
    }
    public DnsmasqControl           getDnsControler() {
        if (dnsSpoofed == null) {
            dnsSpoofed = new DnsmasqControl();
        }
        return dnsSpoofed;
    }

}
