package fr.allycs.app.Controller.Core.Nmap;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.RootProcess;
import fr.allycs.app.Controller.Network.Fingerprint;
import fr.allycs.app.Model.Net.Port;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.View.HostDiscovery.FragmentHostDiscoveryScan;
import jcifs.netbios.NbtAddress;

class NmapParser {
    private String                  TAG = "NmapParser";
    private Singleton               mSingleton = Singleton.getInstance();
    private ArrayList<Host>         hosts = new ArrayList<>();
    private NmapControler           mNmapControler;
    private FragmentHostDiscoveryScan mFragment;

    /**

*/
    NmapParser(NmapControler nmapControler, List<String> ips, FragmentHostDiscoveryScan fragment) {
        this.mNmapControler = nmapControler;
        this.mFragment = fragment;
        StringBuilder hostCmd = new StringBuilder("");
        StringBuilder hostsMAC = new StringBuilder("");
        Log.v(TAG, "buildHostFromDump::" + ips);
        for (String ip : ips) {
            String[] tmp = ip.split(":");
            Log.v(TAG, "buildHostFromDump::" + ip);
            /*if (tmp[0].contentEquals("10.16.187.6") || tmp[0].contentEquals("10.16.187.159") || tmp[0].contentEquals("10.16.187.223") ||
                    tmp[0].contentEquals("10.16.187.238") || tmp[0].contentEquals("10.16.187.21") ||
                    tmp[0].contentEquals("10.16.187.19") || tmp[0].contentEquals("10.16.187.25")) {*/
                hostCmd.append(" ").append(tmp[0]);
                hostsMAC.append(" ").append(ip);
            /*} else {
                //Log.e(TAG, "[" + ip + "]");
            }*/
        }
        String cmd = mNmapControler.PATH_NMAP + " -sT -O -Pn -T4 --max-os-tries 2 " + hostCmd.toString();
        Log.d(TAG, "CMD:["+ cmd + "]");
        startAsParse(cmd, hostsMAC.toString());
    }


    private Host                    buildHostFromDump(String nmapStdout, String ip, String mac) throws UnknownHostException {
        Host host = new Host();
        host.ip = ip;
        host.mac = mac;
        Log.v(TAG, "buildHostFromDump::" + host.toString());
        //host = DBHost.saveOrGetInDatabase(host, true);
        String[] nmapStdoutHost = nmapStdout.split("\n");
        List<Port> ports = new ArrayList<>();
        for (int i = 0; i < nmapStdoutHost.length; i++) {
            String line = nmapStdoutHost[i];
            if (line.contains("Device type: ")) {
                Log.v(TAG, line);
                host.deviceType = line.replace("Device type: ", "");
            } else if (line.contains("Running: ")) {
                Log.v(TAG, line);
                getOs(line.replace("Running: ", ""), host, nmapStdoutHost);
            } else if (line.contains("Too many fingerprints match this host to give specific OS details")) {
                Log.v(TAG, line);
                host.TooManyFingerprintMatchForOs = true;
            } else if (line.contains("Network Distance: ")) {
                Log.v(TAG, line);
                host.NetworkDistance = line.replace("Network Distance: ", "");
            } else if (line.contains("MAC Address: ")) {
                Log.v(TAG, line);
                host.mac = line.replace("MAC Address: ", "").split(" ")[0];
                host.vendor = line.replace("MAC Address: " + host.mac + " (", "").replace(")", "");
            } else if (line.contains("STATE SERVICE")) {
                Log.v(TAG, line);
                ports = getPortList(nmapStdoutHost, i);
            } else {
                Log.e(TAG, line);
            }
        }
        host.dumpInfo = nmapStdout;
        Log.d(TAG, "ports");
        //host.Ports().addAll(ports);/*CHECK DUPLICATA*/
        Fingerprint.initHost(host);
        Log.d(TAG, "getNetBiosName");
        getNetBiosName(host);
        host.mac = host.mac.toUpperCase();
        Log.d(TAG, "saving host");
        host.save();
        return host;
    }


    private void                    parseNmapStdout(String listMacs, String NmapDump) {
        String[] macs = listMacs.split(" ");
        for (int i = 1; i < NmapDump.split("Nmap scan report for ").length; i++) {/*First node is the nmap preambule*/
            try {
                String node = NmapDump.split("Nmap scan report for ")[i];
                String ip = node.split("\n")[0].split(" ")[0].replace(":", "");
                Log.d(TAG, "NODE:" + ip);
                Log.d(TAG, "\t\t" + node);
                Host host = buildHostFromDump(node, ip, getMACInTmp(macs, ip));
                host.dumpMe(mSingleton.selectedHostsList);
                Log.e(TAG, "new host builded: " + host.toString());
                hosts.add(host);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        for (Host host : hosts) {
            host.dumpMe(mSingleton.selectedHostsList);
            Log.d(TAG, "--------------------------------------------------");
        }
        Collections.sort(hosts, Host.getComparator());
        this.mFragment.onHostActualized(hosts);
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
                        Log.d(TAG, "\t\t" + tmp);
                    }
                    dumpOutputBuilder.append(tmp);
                    Log.d(TAG, "LIVENMAP : <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                    parseNmapStdout(listMacs, dumpOutputBuilder.toString().substring(1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void                    getNetBiosName(Host host) {
        try {
            if (host.osType.name().contains("Windows") ||
                    host.osType.name().contains("Android") ||
                    host.osType.name().contains("Unknow")) {//si Windows privilegié NetBios,
                String tmp;
                BufferedReader reader = new RootProcess("Nmap", mSingleton.FilesPath)
                        .exec(mNmapControler.PATH_NMAP + " -sU --script nbstat.nse -p137 " + host.ip)
                        .getReader();
                while ((tmp = reader.readLine()) != null && !tmp.contains("Nmap done")) {
                    Log.d(TAG, "NETBIOS::" + tmp);
                    if (tmp.contains("NetBIOS")) {
                        host.name = tmp.split(",")[0].replace("NetBIOS name: ", "")
                        .replace("|   ", "");
                    }
                }
                return;
            } else if (host.osType.name().contains("Unknow") && host.name != null)
                return;
            //else Jcifs name
                Log.d(TAG, "Jcifs::");
                InetAddress addr = InetAddress.getByName(host.ip);
                String hostname = (addr.getHostName().contentEquals(host.ip)) ? "-" : addr.getHostName();
                String jcifsName = ((NbtAddress.getByName(host.ip).nextCalledName() == null) ?
                        "-" : NbtAddress.getByName(host.ip).nextCalledName());
                if (!hostname.contentEquals(host.ip)) {
                    host.name = hostname;
                } else {
                    host.name = jcifsName;
                }


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<Port>             getPortList(String[] line, int i) {
        List<Port> ports = new ArrayList<>();
        for (; i < line.length; i++) {
            if (line[i] != null &&
                    (line[i].contains("STATE") || line[i].contains("open") || line[i].contains("close"))) {
                String[] tmp = line[i].replace("  ", " ").split(" ");
                Port port = new Port();
                port.port = tmp[0];
                port.state = tmp[1];
                port.protocol = tmp[2];
                ports.add(port);
            }
        }
        return ports;
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
