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
        StringBuilder dump = new StringBuilder("");
        for (int i = 0; i < nmapStdoutHost.length; i++) {
            String line = nmapStdoutHost[i];
            if (line.contains("Device type: ")) {
                dump.append(line);
                //Log.v(TAG,  "buildHostFromNmapDump::" + line);
                host.deviceType = line.replace("Device type: ", "");
            } else if (line.contains("Running: ")) {
                dump.append(line);
                //Log.v(TAG, "buildHostFromNmapDump::" + line);
                getOs(line.replace("Running: ", ""), host, nmapStdoutHost);
            } else if (line.contains("Too many fingerprints match this host to give specific OS details")) {
                dump.append(line);
                host.TooManyFingerprintMatchForOs = true;
            } else if (line.contains("Network Distance: ")) {
                dump.append(line);
                host.NetworkDistance = line.replace("Network Distance: ", "");
            } else if (line.contains("NetBIOS name:")) {
                dump.append(line);
                host.name = line.split(",")[0].replace("NetBIOS name: ", "")
                        .replace("|   ", "");
            } else if (line.contains("MAC Address: ")) {
                dump.append(line);
                host.mac = line.replace("MAC Address: ", "").split(" ")[0];
                if (host.vendor.contains("Unknown"))
                    host.vendor = line.replace("MAC Address: " + host.mac + " (", "").replace(")", "");
            } else if (line.contains("PORT ")) {
                i = getPortList(nmapStdoutHost, i +1, host);
            } else {

            }
        }
        host.dumpInfo = dump.toString();
        Fingerprint.initHost(host);
//        Log.d(TAG, "getNetBiosName");
        //getNetBiosName(host);
        host.mac = host.mac.toUpperCase();
        //Log.d(TAG, "Saving " + host.ip +" builded from dump");
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
                host.mac = getMACInTmp(macs, host.ip);
                if (!Fingerprint.isItMyDevice(host)) {
                    host = DBHost.saveOrGetInDatabase(host);
                    host = buildHostFromNmapDump(node, host);
                } else {
                    host.mac = mSingleton.network.mac;
                    host.ip = mSingleton.network.myIp;
                    if ((host = DBHost.saveOrGetInDatabase(host)) == null) {
                        host.name = "My Device";
                        host.os = "Android/(AOSP)";
                        host.osType = Os.Android;
                        host.isItMyDevice = true;
                        host.save();
                    }
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
                    mFragment.setTitleToolbar("Network scan", "Scanning devices");
                    String lastLine = "";
                    while ((tmp = reader.readLine()) != null && !tmp.startsWith("Nmap done")) {
                        dumpOutputBuilder.append(tmp).append('\n');
                    }
                    if (tmp == null || !tmp.startsWith("Nmap done")) {
                        Log.d(TAG, "Error in nmap execution, Nmap didn't end");
                        mFragment.setTitleToolbar("Network scan", "Nmap Error");
                        return;
                    }
                    dumpOutputBuilder.append(tmp);
                    String FullDUMP = dumpOutputBuilder.toString().substring(1);
                    Log.d(TAG, "\t\t LastLine[" + tmp + "]");
                    mFragment.setTitleToolbar("Fingerprint", tmp.replace("Nmap done: ", ""));
                    parseNmapMultipleTarget(listMacs, FullDUMP);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private int                     getPortList(String[] line, int i, Host host) {
        ArrayList<String> ports = new ArrayList<>();
        for (; i < line.length; i++) {
            if (!(line[i].contains("open") || line[i].contains("close") || line[i].contains("filtered"))) {
                /*TODO:CHECK DUPLICATA*/
                if (line[i].startsWith("| ") && line[i].endsWith(": ")) {
                    while (i < line.length && !line[i].startsWith("|_")) {
                        ports.add(line[i++].replaceAll("  ", " "));
                    }
                } else {
                    host.Ports(ports);
                    return i-1;
                }
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
