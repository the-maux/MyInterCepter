package fr.allycs.app.Controller.Core.Nmap;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.RootProcess;
import fr.allycs.app.Controller.Database.DBHost;
import fr.allycs.app.Controller.Network.Fingerprint;
import fr.allycs.app.Model.Net.Port;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.View.HostDiscovery.FragmentHostDiscoveryScan;

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
            hostCmd.append(" ").append(tmp[0]);
            hostsMAC.append(" ").append(ip);
        }
        String cmd = mSingleton.FilesPath + "nmap/nmap " + " -O -v " + hostCmd.toString();
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
                    while ((tmp = reader.readLine()) != null && !tmp.contains("Nmap done")) {
                        dumpOutputBuilder.append(tmp).append('\n');
                    }
                    dumpOutputBuilder.append(tmp);
                    parseNmapStdout(listMacs, dumpOutputBuilder.toString().substring(1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    private void                    parseNmapStdout(String listMacs, String NmapDump) {
        String[] macs = listMacs.split(" ");
        for (String node : NmapDump.split("Nmap scan report for ")) {
            String ip = node.split("\n")[0];
            Log.d(TAG, "NODE:" + ip);
            Log.d(TAG, "\t\t"+ node);
            Host host = buildHostFromDump(node, ip, getMACInTmp(macs, ip));
            Log.e(TAG, "new host builded: " + host.toString());
            hosts.add(host);
        }
        Log.d(TAG, "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        for (Host host : hosts) {
            host.dumpMe();
            Log.d(TAG, "--------------------------------------------------");
        }
        this.mFragment.onHostActualized(hosts);
    }

    private Host                    buildHostFromDump(String nmapStdout, String ip, String mac) {
        Host host = new Host();
        host.ip = ip;
        host.mac = mac;
        host = DBHost.saveOrGetInDatabase(host, true);
        String[] nmapStdoutHost = nmapStdout.split("\n");
        List<Port> ports = new ArrayList<>();
        for (int i = 0; i < nmapStdoutHost.length; i++) {
            String line = nmapStdoutHost[i];
            if (line.contains("Device type:")) {
                Log.v(TAG, line);
                host.deviceType = line.replace("Device type: ", "");
            } else if (line.contains("Running")) {
                Log.v(TAG, line);
                parseOs(line.replace("Running: ", ""), host, nmapStdoutHost);
            } else if (line.contains("Too many fingerprints match this host to give specific OS details")) {
                Log.v(TAG, line);
                host.TooManyFingerprintMatchForOs = true;
            } else if (line.contains("Network Distance:")) {
                Log.v(TAG, line);
                host.NetworkDistance = line.replace("Network Distance:", "");
            } else if (line.contains("STATE SERVICE")) {
                Log.v(TAG, line);
                ports = parsePortList(nmapStdoutHost, i, host);
            } else {
                Log.e(TAG, line);
            }
        }
        host.dumpInfo = nmapStdout;
        if (!ports.isEmpty()){
            host.Ports().addAll(ports);
        }
        host.save();
        return host;
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
