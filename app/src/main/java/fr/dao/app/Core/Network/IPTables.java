package fr.dao.app.Core.Network;

import android.util.Log;

import fr.dao.app.Core.Configuration.MitManager;
import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Singleton;

public class                    IPTables {
    private static final String TAG = "IPTABLES";

    private static  void        startForwardingStreamWithHTTPS() {
        startForwardingStream();
        new RootProcess("IpTable::startForwardingStreamWithHTTPS")
                .noDebugOutput()
                .exec("iptables -t nat -A PREROUTING -p tcp --destination-port 80 -j REDIRECT --to-port 8081");
    }

    /**
     * Classic :
     * iptables -F; iptables -X; iptables -t nat -F; iptables -t nat -X; iptables -t mangle -F; iptables -t mangle -X;
     * iptables -P INPUT ACCEPT; iptables -P FORWARD ACCEPT; iptables -P OUTPUT ACCEPT ; echo '1' > /proc/sys/net/ipv4/ip_forward
     * @return
     */
    public static int           startForwardingStream() {
        Log.d(TAG, "IPTable configuration for MITM");
        RootProcess process = new RootProcess("IpTable::InitWithoutSSL");
        process.exec("iptables -F; " +
                "iptables -X; " +
                "iptables -t nat -F; " +
                "iptables -t nat -X; " +
                "iptables -t mangle -F; " +
                "iptables -t mangle -X; " +
                "iptables -P INPUT ACCEPT; " +
                "iptables -P FORWARD ACCEPT; " +
                "iptables -P OUTPUT ACCEPT ; " +
                "echo '1' > /proc/sys/net/ipv4/ip_forward");
        MitManager.getInstance().setTrafficRedirected(true);
        return process.closeProcess();
    }

    public static void          startDnsPacketRedirect() {
        Log.d("DNSREDIRECT", "iptables -t nat -A PREROUTING -p udp --destination-port 53 -j REDIRECT --to-port 8053");
        new RootProcess("IpTable::DNSREDIRECT")
                .exec("iptables -t nat -A PREROUTING -p udp --destination-port 53 -j REDIRECT --to-port 8053")
                .closeProcess();
    }
    public static void          stopDnsPacketRedirect() {
        Log.d("DNSREDIRECT", "iptables -t nat -A PREROUTING -p udp --destination-port 53 -j REDIRECT --to-port 8053");
        new RootProcess("IpTable::DNSREDIRECT")
                .exec("iptables -t nat -D PREROUTING -p udp --destination-port 53 -j REDIRECT --to-port 8053")
                .closeProcess();
    }

    public static void          stopIpTable() {
        Log.d(TAG, "Stopping IPTable configuration for MITM");
        new RootProcess("IpTable stop")
                .exec("iptables -F;")
                .exec("iptables -X;")
                .exec("iptables -t nat -F;")
                .exec("iptables -t nat -X;")
                .exec("iptables -t mangle -F;")
                .exec("iptables -t mangle -X;")
                .exec("iptables -P INPUT ACCEPT;")
                .exec("iptables -P FORWARD ACCEPT;")
                .exec("iptables -P OUTPUT ACCEPT;")
                .closeProcess();
        MitManager.getInstance().setTrafficRedirected(false);
    }

    public static void          sslConf() {
        if (Singleton.getInstance().isSslstripMode()) {
            Log.d(TAG, "Conf with SslStrip");
            IPTables.startForwardingStreamWithHTTPS();
        } else {
            Log.d(TAG, "Conf without SslStrip");
            IPTables.startForwardingStream();
        }
    }

    public void                 trafficRedirect(String to) {
        Log.d(TAG, "Redirecting traffic to " + to);

        new RootProcess("IPTable::")
                .noDebugOutput()
                .exec("iptables " + "-t nat -A PREROUTING -j DNAT -p tcp --to " + to);
    }

    public void                 changeSource(String to) {
        new RootProcess("IPTable::")
                .noDebugOutput()
                .exec("iptables " + "-t nat -A POSTROUTING -j SNAT -p udp --dport 53 --to " + to);
    }

    public void                 flushNAT() {
        new RootProcess("IPTable::")
                .noDebugOutput()
                .exec("iptables " + "-t nat -F");
    }
    
    public void                 discardForwardding2Port(int port) {
        new RootProcess("IPTable::")
                .noDebugOutput()
                .exec("iptables " + "-A FORWARD --proto udp --dport " + String.valueOf(port) + " -j DROP");
    }

    public void                 allowForwardding2Port(int port) {
        new RootProcess("IPTable::allowForwardding2Port")
                .noDebugOutput()
                .exec("iptables " + "-A FORWARD --proto udp --dport " + String.valueOf(port) + " -j ACCEPT");

    }

    public void                 undoTrafficRedirect(String to) {
        Log.d(TAG, "Undoing traffic redirection");
        new RootProcess("IPTable::undoTrafficRedirect")
                .noDebugOutput()
                .exec("iptables " +"-t nat -D PREROUTING -j DNAT -p tcp --to " + to);
    }

    public void                 portRedirect(int from, int to) {
        Log.d(TAG, "Redirecting traffic from port " + from + " to port " + to);
        new RootProcess("IPTable::portRedirect")
                .noDebugOutput()
                .exec("iptables " + "-t nat -F")// reset nat
                .exec("iptables " + "-F")// reset
                .exec("iptables " + "-t nat -I POSTROUTING -s 0/0 -j MASQUERADE")// post route
                .exec("iptables " + "-P FORWARD ACCEPT")// accept all
                .exec("iptables " + "-t nat -A PREROUTING -j DNAT -p tcp --dport " + from + " --to " + Singleton.getInstance().NetworkInformation.myIp + ":" + to);// add rule;
    }

    public void                 undoPortRedirect(int from, int to) {
        Log.d(TAG, "Undoing port redirection");
        new RootProcess("IPTable::undoPortRedirect")
                .noDebugOutput()
                .exec("iptables " + "-t nat -F") // reset nat
                .exec("iptables " + "-F")// reset
                .exec("iptables " + "-t nat -D POSTROUTING -s 0/0 -j MASQUERADE")  // remove post route
                .exec("iptables " + "-t nat -D PREROUTING -j DNAT -p tcp --dport " + from + " --to " + Singleton.getInstance().NetworkInformation.myIp + ":" + to);// remove rule
    }
}