package fr.allycs.app.Controller.Core.Nmap;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.RootProcess;
import fr.allycs.app.Controller.Database.DBHost;
import fr.allycs.app.Model.Net.Port;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.View.HostDiscovery.FragmentHostDiscoveryScan;
import jcifs.netbios.NbtAddress;

class NmapParser {
    private String                  TAG = "NmapParser";
    private Singleton               mSingleton = Singleton.getInstance();
    private ArrayList<Host>         hosts = new ArrayList<>();
    private FragmentHostDiscoveryScan mFragment;

    NmapParser(List<String> ips, FragmentHostDiscoveryScan fragment) {
        this.mFragment = fragment;
        StringBuilder hostCmd = new StringBuilder("");
        StringBuilder hostsMAC = new StringBuilder("");
        for (String ip : ips) {
            String[] tmp = ip.split(":");
            if (tmp[0].contentEquals("10.16.187.6") || tmp[0].contentEquals("10.16.187.159") || tmp[0].contentEquals("10.16.187.223") ||
                    tmp[0].contentEquals("10.16.187.238") || tmp[0].contentEquals("10.16.187.21") ||
                    tmp[0].contentEquals("10.16.187.19") || tmp[0].contentEquals("10.16.187.25")) {
                hostCmd.append(" ").append(tmp[0]);
                hostsMAC.append(" ").append(ip);
            } else {
                //Log.e(TAG, "[" + ip + "]");
            }
        }
        String cmd = mSingleton.FilesPath + "nmap/nmap " + " -sT -O -Pn -T4 --max-os-tries 2 " + hostCmd.toString();
        Log.d(TAG, "CMD:["+ cmd + "]");
        startAsParse(cmd, hostsMAC.toString());
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

    private Host                    buildHostFromDump(String nmapStdout, String ip, String mac) throws UnknownHostException {
        Host host = new Host();
        host.ip = ip;
        host.mac = mac;
        host = DBHost.saveOrGetInDatabase(host, true);
        String[] nmapStdoutHost = nmapStdout.split("\n");
        List<Port> ports = new ArrayList<>();
        for (int i = 0; i < nmapStdoutHost.length; i++) {
            String line = nmapStdoutHost[i];
            if (line.contains("Device type: ")) {
                Log.v(TAG, line);
                host.deviceType = line.replace("Device type: ", "");
            } else if (line.contains("Running: ")) {
                Log.v(TAG, line);
                parseOs(line.replace("Running: ", ""), host, nmapStdoutHost);
            } else if (line.contains("Too many fingerprints match this host to give specific OS details")) {
                Log.v(TAG, line);
                host.TooManyFingerprintMatchForOs = true;
            } else if (line.contains("Network Distance: ")) {
                Log.v(TAG, line);
                host.NetworkDistance = line.replace("Network Distance: ", "");
            } else if (line.contains("STATE SERVICE")) {
                Log.v(TAG, line);
                ports = parsePortList(nmapStdoutHost, i, host);
            } else {
                Log.e(TAG, line);
            }
        }
        try {
            InetAddress addr = InetAddress.getByName(host.ip);
            String hostname = addr.getHostName();
            String jcifsName = ((NbtAddress.getByName(host.ip).nextCalledName() == null) ? "(-)" : NbtAddress.getByName(host.ip).nextCalledName());
            if (!hostname.contentEquals(host.ip)) {
                host.name = hostname;
            } else {
                host.name = jcifsName;
            }
            Log.d(TAG, ip + ":[" + host.name + "]");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (host.name == null) {
            host.name = "(-)";
        }
        host.dumpInfo = nmapStdout;
        if (!ports.isEmpty()){
            host.Ports().addAll(ports);
        }
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
                Log.e(TAG, "new host builded: " + host.toString());
                hosts.add(host);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        for (Host host : hosts) {
            host.dumpMe();
            Log.d(TAG, "--------------------------------------------------");
        }
        this.mFragment.onHostActualized(hosts);
    }



    private List<Port>              parsePortList(String[] line, int i, Host host) {
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

    private void                    parseMacAddress(String[] line, Host host) {
        host.mac = line[0];
        host.vendor = line[1];
    }

    private void                    parseOs(String line, Host host, String[] nmapStdoutHost) {
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
                return mac.replace(ip + ':', "");
        }
        return null;
    }

}
