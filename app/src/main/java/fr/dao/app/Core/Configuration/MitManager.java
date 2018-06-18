package fr.dao.app.Core.Configuration;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Dnsmasq.DnsmasqControl;
import fr.dao.app.Core.Network.ArpSpoof;
import fr.dao.app.Core.Network.IPTables;
import fr.dao.app.Core.Tcpdump.DashboardSniff;
import fr.dao.app.Core.Tcpdump.Proxy;
import fr.dao.app.Core.Tcpdump.Tcpdump;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.View.Proxy.HTTPDispatcher;
import fr.dao.app.View.Sniff.SniffDispatcher;

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
    HTTPDispatcher                  mHttpTrameDispatcher;

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
                !(!MitManager.getInstance().isDnsmasqRunning() && !webSpoofedstarted);
    }
    public boolean                  isDnsmasqRunning() {
        return dnsSpoofed != null && dnsSpoofed.isRunning();
    }

    /**
     *
     */
    private void                    initMitmConnection() {
        if (!trafficRedirected) {
            Log.d(TAG, "Traffic redirection init");
            int a = IPTables.startForwardingStream();
            ArpSpoof.launchArpSpoof(targets);
            trafficRedirected = true;
        } else
            Log.i(TAG, "Traffic already redicreted");
    }
    public DashboardSniff           initTcpDump(SniffDispatcher mTrameDispatcher) {
        initMitmConnection();
        Log.d(TAG, "initTcpDump");
        Tcpdump.getTcpdump().initCmd(targets);
        return Tcpdump.getTcpdump().start(mTrameDispatcher);
    }
    public boolean                  initProxy(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        if (!isProxyRunning()) {
            initMitmConnection();
            Log.d(TAG, "initProxy");
            Proxy.getProxy().initCmd(targets);
            if (Proxy.getProxy() != null) {
                if (mHttpTrameDispatcher == null) {
                    mHttpTrameDispatcher = new HTTPDispatcher(recyclerView, adapter);
                } else {/* Clear shit its a restart*/
                    Log.e(TAG, "mHttpTrameDispatcher reseted");
                   // mHttpTrameDispatcher.reset();
                }
                Proxy.getProxy().start(mHttpTrameDispatcher);
                //initDNSSpoofing();

                return true;
            } else
                Log.e(TAG, "Proxy is null, can't do shit");
        } else
            Log.e(TAG, "Trying to start proxy but already launched");
        return false;
    }
    public DnsmasqControl           initDNSSpoofing() {
        if (!isDnsmasqRunning()) {
            Log.d(TAG, "initDNSSpoofing");
            if (!trafficRedirected)
                initMitmConnection();
            IPTables.startDnsPacketRedirect();
            isAttackRunning = true;
            dnsSpoofed = new DnsmasqControl();
        } else
            Log.e(TAG, "DNS already init");
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

    public void                     stopTcpdump(boolean isShutdown) {
        if (isTcpdumpRunning()) {
            if (!stopMITMBehavior()) {
                Tcpdump.getTcpdump().stop();
                //TODO: We need to close the Tcpdump current process, even if there is still MITM behavior
            }
            Log.d(TAG, "stopTcpdump");
            if (!isShutdown)
                updateRunningStatus();
        }
    }
    public void                     stopProxy() {
        Proxy.getProxy().stop();
        if (dnsSpoofed != null && dnsSpoofed.isRunning())
            stopDnsSpoofing(true);
        Log.d(TAG, "stopProxy");
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
        if (!isProxyRunning() && !isTcpdumpRunning() && !isDnsmasqRunning()) {
            stopEverything();
            return true;
        } else {
            Log.d(TAG, "Still some Mitm modules alive");
            return false;
        }

    }
    public void                     stopEverything() {
        Log.d(TAG, "stopEverything");
        if (webSpoofedstarted)
            stopWebserver(true);
        if (Tcpdump.isRunning() && Tcpdump.getTcpdump() != null) {
            stopTcpdump(true);
        }
        if (Proxy.isRunning() && Proxy.getProxy() != null) {
            stopProxy();
        }
        if (dnsSpoofed != null && dnsSpoofed.isRunning())
            stopDnsSpoofing(true);
        trafficRedirected = false;
        RootProcess.kill("tcpdump");
        ArpSpoof.stopArpSpoof();
        IPTables.stopIpTable();
        isAttackRunning = false;
    }

    public void                     loadHost(List<Host> targets) {
        Log.d(TAG, "loadHost::" +  targets.size() + " targets");
        if (isAttackRunning)
            stopEverything();
        this.targets.clear();
        this.targets.addAll(targets);
    }
    private void                    updateRunningStatus() {
     //   Log.d(TAG, "updateRunningStatus");
  //      stopMITMBehavior();
    }
    public DnsmasqControl           getDnsControler() {
        if (dnsSpoofed == null) {
            dnsSpoofed = initDNSSpoofing();
        }
        return dnsSpoofed;
    }

}
