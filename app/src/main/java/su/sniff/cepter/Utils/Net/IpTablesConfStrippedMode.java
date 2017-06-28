package su.sniff.cepter.Utils.Net;

import su.sniff.cepter.Controller.RootProcess;
import su.sniff.cepter.globalVariable;

public class        IpTablesConfStrippedMode implements  Runnable {
    public          IpTablesConfStrippedMode() {
        new Thread(this).start();
    }

    public void     run() {
        RootProcess process = new RootProcess(this.getClass().getName());
        process.exec("iptables -F;iptables -X;iptables -t nat -F;iptables -t nat -X;iptables -t mangle -F;iptables -t mangle -X;iptables -P INPUT ACCEPT;iptables -P FORWARD ACCEPT;iptables -P OUTPUT ACCEPT")
                .exec("echo '1' > /proc/sys/net/ipv4/ip_forward")
                .exec("iptables -t nat -A PREROUTING -p tcp --destination-port 80 -j REDIRECT --to-port 8081");
        if (globalVariable.dnss == 1)
            process.exec("iptables -t nat -A PREROUTING -p udp --destination-port 53 -j REDIRECT --to-port 8053");
        process.closeProcess();
    }
}
