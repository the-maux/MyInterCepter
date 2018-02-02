package fr.allycs.app.Controller.Core.Nmap;

import android.util.Log;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;

import fr.allycs.app.Controller.Core.Configuration.Singleton;
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
    private int                     LENGTH_NODE, NBR_PARSED_NODE = 0;

    NmapParser(NmapControler nmapControler,FragmentHostDiscoveryScan fragment, String listMacs, String NmapDump) {
        this.mNmapControler = nmapControler;
        this.mFragment = fragment;
        String[] macs = listMacs.split(" ");
        String[] HostNmapDump = NmapDump.split("Nmap scan report for ");
        LENGTH_NODE = HostNmapDump.length -1;
        for (int i = 1; i < HostNmapDump.length; i++) {/*First node is the nmap preambule*/
            dispatcher(HostNmapDump[i], macs);
        }
    }

    private void                    dispatcher(final String node, final String[] macs) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Host host = new Host();
                    getIpHOSTNAME(node.split("\n")[0], host);
                    host.mac = getMACInTmp(macs, host.ip);
                    if (!Fingerprint.isItMyDevice(host)) {
                        host = DBHost.saveOrGetInDatabase(host);
                        buildHostFromNmapDump(node, host, hosts);
                    } else {
                        initIfItsMyDevice(host, hosts);
                    }
                    onNodeParsed();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void                    buildHostFromNmapDump(String nmapStdout, Host host, ArrayList<Host> hosts) throws UnknownHostException {
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
        hosts.add(host);
    }

    private void                    initIfItsMyDevice(Host host, ArrayList<Host> hosts) {
        host.mac = mSingleton.network.mac;
        host.ip = mSingleton.network.myIp;
        host = DBHost.saveOrGetInDatabase(host);
        host.name = "My Device";
        host.os = "Android/(AOSP)";
        host.osType = Os.Android;
        host.isItMyDevice = true;
        host.save();
        hosts.add(host);
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

    private void                    onNodeParsed() {
        NBR_PARSED_NODE = NBR_PARSED_NODE + 1;
        if (NBR_PARSED_NODE >= LENGTH_NODE)
            onAllNodeParsed();
    }

    private void                    onAllNodeParsed() {
        Log.d(TAG, "All node was parsed in :" + mNmapControler.getTimeSpend());
        Collections.sort(hosts, Host.getComparator());
        this.mFragment.onHostActualized(hosts);
    }

}
