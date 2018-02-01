package fr.allycs.app.Controller.Core.Nmap;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.RootProcess;
import fr.allycs.app.Controller.Database.DBHost;
import fr.allycs.app.Model.Net.Port;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Unix.Os;
import fr.allycs.app.View.HostDiscovery.FragmentHostDiscoveryScan;

class NmapParser {
    private String                  TAG = "NmapParser";
    private Singleton               mSingleton = Singleton.getInstance();
    private ArrayList<Host>         hosts = new ArrayList<>();
    private NmapControler           mNmapControler;
    private FragmentHostDiscoveryScan mFragment;
    private String                  NMAP_ARG_SCAN = " -PN -T4 -sS -sU --script nbstat.nse,dns-service-discovery --min-parallelism 100 -p T:21,T:22,T:23,T:25,T:80,T:110,T:135,T:139,T:3128,T:443,T:445,U:53,U:3031,U:5353  ";
    /**

*/
    NmapParser(NmapControler nmapControler, List<String> ips, FragmentHostDiscoveryScan fragment) {
        this.mNmapControler = nmapControler;
        this.mFragment = fragment;
        StringBuilder hostCmd = new StringBuilder("");
        StringBuilder hostsMAC = new StringBuilder("");
        for (String ip : ips) {
            String[] tmp = ip.split(":");
            /*if (tmp[0].contentEquals("10.16.187.6") || tmp[0].contentEquals("10.16.187.159") || tmp[0].contentEquals("10.16.187.223") ||
                    tmp[0].contentEquals("10.16.187.238") || tmp[0].contentEquals("10.16.187.21") ||
                    tmp[0].contentEquals("10.16.187.19") || tmp[0].contentEquals("10.16.187.25")) {*/
                hostCmd.append(" ").append(tmp[0]);
                hostsMAC.append(" ").append(ip);
            /*} else {
                //Log.e(TAG, "[" + ip + "]");
            }*/
        }
        String cmd = mNmapControler.PATH_NMAP + NMAP_ARG_SCAN + hostCmd.toString();
        Log.d(TAG, "CMD:["+ cmd + "]");
        startAsParse(cmd, hostsMAC.toString());
    }

    private Host                    buildHostFromNmapDump(String nmapStdout, Host host) throws UnknownHostException {
        String[] nmapStdoutHost = nmapStdout.split("\n");
        List<Port> ports = new ArrayList<>();
        for (int i = 0; i < nmapStdoutHost.length; i++) {
            String line = nmapStdoutHost[i];
            if (line.contains("Device type: ")) {
                //Log.v(TAG,  "buildHostFromNmapDump::" + line);
                host.deviceType = line.replace("Device type: ", "");
            } else if (line.contains("Running: ")) {
                //Log.v(TAG, "buildHostFromNmapDump::" + line);
                getOs(line.replace("Running: ", ""), host, nmapStdoutHost);
            } else if (line.contains("Too many fingerprints match this host to give specific OS details")) {
                //Log.v(TAG, "buildHostFromNmapDump::" + line);
                host.TooManyFingerprintMatchForOs = true;
            } else if (line.contains("Network Distance: ")) {
                //Log.v(TAG, "buildHostFromNmapDump::" + line);
                host.NetworkDistance = line.replace("Network Distance: ", "");
            } else if (line.contains("NetBIOS name:")) {
                host.name = line.split(",")[0].replace("NetBIOS name: ", "")
                        .replace("|   ", "");
            } else if (line.contains("MAC Address: ")) {
                //              Log.v(TAG, "buildHostFromNmapDump::" + line);
                host.mac = line.replace("MAC Address: ", "").split(" ")[0];
                host.vendor = line.replace("MAC Address: " + host.mac + " (", "").replace(")", "");
            } else if (line.contains("PORT ")) {
  //              Log.v(TAG, "buildHostFromNmapDump::" + line);
                i = getPortList(nmapStdoutHost, i +1, host);
            } else {
  //              Log.e(TAG,"buildHostFromNmapDump::" +  line);
            }
        }
        host.dumpInfo = nmapStdout;
        Fingerprint.initHost(host);
//        Log.d(TAG, "getNetBiosName");
        //getNetBiosName(host);
        host.mac = host.mac.toUpperCase();
        Log.d(TAG, "Saving " + host.ip +" builded from dump");
        host.save();
        return host;
    }


    private void                    parseNmapMultipleTarget(String listMacs, String NmapDump) {
        String[] macs = listMacs.split(" ");
        for (int i = 1; i < NmapDump.split("Nmap scan report for ").length; i++) {/*First node is the nmap preambule*/
            try {
                String node = NmapDump.split("Nmap scan report for ")[i];
                Host host = new Host();
                getIpHOSTNAME(node.split("\n")[0], host);
                //Log.d(TAG, "NODE:" + host.ip);
                //Log.d(TAG, "\t\t" + node);
                host.mac = getMACInTmp(macs, host.ip);
                if (!Fingerprint.isItMyDevice(host)) {
                    host = DBHost.saveOrGetInDatabase(host, true);
                    host = buildHostFromNmapDump(node, host);
                    //host.dumpMe(mSingleton.selectedHostsList);
                    //Log.e(TAG, "new host builded: " + host.toString());
                } else {
                    host.mac = mSingleton.network.mac;
                    host.name = "My Device";
                    host.ip = mSingleton.network.myIp;
                    host.os = "Android";
                    host.osType = Os.Android;
                    host.isItMyDevice = true;
                }
                hosts.add(host);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        //Log.d(TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        for (Host host : hosts) {
            //host.dumpMe(mSingleton.selectedHostsList);
            //Log.d(TAG, "--------------------------------------------------");
        }
        Collections.sort(hosts, Host.getComparator());
        this.mFragment.onHostActualized(hosts);
    }

    private void                    getIpHOSTNAME(String line, Host host) {
        /* nbl037421.hq.fr.corp.leroymerlin.com (10.16.187.230) */
        if (line.contains("(")) {
            host.ip = line.split(" ")[1].replace("(", "").replace(")", "");
            host.name = line.split(" ")[0];
        } else {
            host.ip = line.split(" ")[0];
        }
    }

    private void                    startAsParse(final String cmd, final String listMacs) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String tmp;
                    StringBuilder dumpOutputBuilder = new StringBuilder();
                    BufferedReader reader = new RootProcess("Nmap", mSingleton.FilesPath)
                            .exec(cmd).getReader();
                    Log.d(TAG, "LIVENMAP : >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                    while ((tmp = reader.readLine()) != null && !tmp.contains("Nmap done")) {
                        dumpOutputBuilder.append(tmp).append('\n');
                    }
                    dumpOutputBuilder.append(tmp);
                    Log.d(TAG, "\t\t" + dumpOutputBuilder.toString().substring(1));
                    Log.d(TAG, "LIVENMAP : <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                    parseNmapMultipleTarget(listMacs, dumpOutputBuilder.toString().substring(1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     *                                   PORT     STATE  SERVICE
     21/tcp   closed ftp
     22/tcp   closed ssh
     23/tcp   closed telnet
     25/tcp   closed smtp
     80/tcp   closed http
     110/tcp  closed pop3
     135/tcp  closed msrpc
     139/tcp  closed netbios-ssn
     443/tcp  closed https
     445/tcp  closed microsoft-ds
     3128/tcp closed squid-http
     53/udp   closed domain
     3031/udp closed unknown
     5353/udp open   zeroconf
     | dns-service-discovery:
     |   49804/tcp companion-link
     |     rpBA=BB:AF:8F:77:DC:AD
     |     rpVr=120.51
     |     rpHI=9bfff01882c6
     |     rpHN=5b3e412e991f
     |     rpHA=2c264c268b6a
     |     model=MacBookPro11,4
     |     osxvers=17
     |_    Address=10.16.187.114 fe80:0:0:0:c38:76b8:7a27:48f3
     MAC Address: 6C:96:CF:DB:51:6F (Apple, Inc.)
     * @param line
     * @param i
     * @param host
     * @return
     */

    private int               getPortList(String[] line, int i, Host host) {
        ArrayList<String> ports = new ArrayList<>();
        for (; i < line.length; i++) {
            if (!(line[i].contains("open") || line[i].contains("close") || line[i].contains("filtered"))) {
                host.Ports(ports);/*TODO:CHECK DUPLICATA*/
                if (line[i].contains("dns-service-discovery: ")) {
                    while (i < line.length && !line[i].contains("|_ ")) {
                        i++;
                    }
                }
                return i-1;
            } else
                ports.add(line[i].replaceAll("  ", " "));
        }
        return i;
    }

    private void                    getOs(String line, Host host, String[] nmapStdoutHost) {
        String OsDetail = getLineContaining("OS details", nmapStdoutHost);
        if (OsDetail != null)
            host.osDetail = OsDetail;
        host.os = line;
    }

    private String                  getLineContaining(String substring, String[] nmapStdoutHost) {
        for (String line : nmapStdoutHost) {
            if (line.contains(substring))
                return line;
        }
        return null;
    }

    private String                  getMACInTmp(String[] macs, String ip) {
        for (String mac : macs) {
            if (mac.contains(ip))
                return mac.replace(ip + ':', "").toUpperCase();
        }
        return null;
    }

}
