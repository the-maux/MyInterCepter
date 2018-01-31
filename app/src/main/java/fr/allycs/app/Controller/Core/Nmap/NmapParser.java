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
import fr.allycs.app.Controller.Database.DBHost;
import fr.allycs.app.Model.Net.Port;
import fr.allycs.app.Model.Net.listPorts;
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
        String cmd = mNmapControler.PATH_NMAP + " -Pn -T4 -sS --script nbstat.nse -p 21,22,23,25,80,110,135,3128,443,445  " + hostCmd.toString();
        Log.d(TAG, "CMD:["+ cmd + "]");
        startAsParse(cmd, hostsMAC.toString());
    }


    private Host                    buildHostFromDump(String nmapStdout, Host host) throws UnknownHostException {


        String[] nmapStdoutHost = nmapStdout.split("\n");
        List<Port> ports = new ArrayList<>();
        for (int i = 0; i < nmapStdoutHost.length; i++) {
            String line = nmapStdoutHost[i];
            if (line.contains("Device type: ")) {
                Log.v(TAG,  "buildHostFromDump::" + line);
                host.deviceType = line.replace("Device type: ", "");
            } else if (line.contains("Running: ")) {
                Log.v(TAG, "buildHostFromDump::" + line);
                getOs(line.replace("Running: ", ""), host, nmapStdoutHost);
            } else if (line.contains("Too many fingerprints match this host to give specific OS details")) {
                Log.v(TAG, "buildHostFromDump::" + line);
                host.TooManyFingerprintMatchForOs = true;
            } else if (line.contains("Network Distance: ")) {
                Log.v(TAG, "buildHostFromDump::" + line);
                host.NetworkDistance = line.replace("Network Distance: ", "");
            } else if (line.contains("NetBIOS name:")) {
                host.name = line.split(",")[0].replace("NetBIOS name: ", "")
                        .replace("|   ", "");
            } else if (line.contains("MAC Address: ")) {
                Log.v(TAG, "buildHostFromDump::" + line);
                host.mac = line.replace("MAC Address: ", "").split(" ")[0];
                host.vendor = line.replace("MAC Address: " + host.mac + " (", "").replace(")", "");
            } else if (line.contains("PORT ")) {
                Log.v(TAG, "buildHostFromDump::" + line);
                i = getPortList(nmapStdoutHost, i +1, host);
            } else {
                Log.e(TAG,"buildHostFromDump::" +  line);
            }
        }
        host.dumpInfo = nmapStdout;
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
                Host host = new Host();
                host.ip = ip;
                host.mac = getMACInTmp(macs, ip);

                if (!Fingerprint.isItMyDevice(host)) {
                    host = DBHost.saveOrGetInDatabase(host, true);
                    host = buildHostFromDump(node, host);
                    host.dumpMe(mSingleton.selectedHostsList);
                    Log.e(TAG, "new host builded: " + host.toString());
                    hosts.add(host);
                } else {

                }
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
                    Log.d(TAG, "\t\t" + tmp);
                    Log.d(TAG, "LIVENMAP : <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
                    parseNmapStdout(listMacs, dumpOutputBuilder.toString().substring(1));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * TODO: Regarder comment avoir le nom des servers comme on avait eu a L.M
     * @param host
     */
    private void                    getNetBiosName(Host host) {
        try {
                Log.d(TAG, "Jcifs::");
                InetAddress addr = InetAddress.getByName(host.ip);
                String hostname = (addr.getHostName().contentEquals(host.ip)) ? "-" : addr.getHostName();
                String jcifsName = ((NbtAddress.getByName(host.ip).nextCalledName() == null) ?
                        "" : NbtAddress.getByName(host.ip).nextCalledName());
                if (!hostname.contentEquals(host.ip)) {
                    host.name = host.name + hostname;
                } else {
                    host.name = host.name + jcifsName;
                }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int               getPortList(String[] line, int i, Host host) {
        Log.d(TAG, "getPortList::");
        ArrayList<String> ports = new ArrayList<>();
        for (; i < line.length; i++) {
            if (!(line[i].contains("open") || line[i].contains("close") || line[i].contains("filtered"))) {
                host.Ports(ports);/*TODO:CHECK DUPLICATA*/
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
