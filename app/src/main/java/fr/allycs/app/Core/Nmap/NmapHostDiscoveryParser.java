package fr.allycs.app.Core.Nmap;

import android.util.Log;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Core.Database.DBHost;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Network;
import fr.allycs.app.Model.Unix.Os;

class NmapHostDiscoveryParser {
    private String                  TAG = "NmapHostDiscoveryParser";
    private Singleton               mSingleton = Singleton.getInstance();
 //   private ArrayList<Host>         hosts = new ArrayList<>();
    private Network                 mNetwork;
    private NmapControler           mNmapControler;
    private int                     LENGTH_NODE, NBR_PARSED_NODE = 0;

    NmapHostDiscoveryParser(NmapControler nmapControler, String listMacs, String NmapDump, Network ap) {
        this.mNmapControler = nmapControler;
        Log.d(TAG, "dump list macs[" + listMacs + "]");
        String[] macs = listMacs.split(" ");
        String[] HostNmapDump = NmapDump.split("Nmap scan report for ");
        LENGTH_NODE = HostNmapDump.length-1;
        ExecutorService service = Executors.newCachedThreadPool();
        mNetwork = ap;
        Log.i(TAG, "{{{{{{{{{{{{{" + HostNmapDump[0] + "}}}}}}}}}}}}}}}}}");
        for (int i = 1; i < HostNmapDump.length; i++) {/*First node is the nmap preambule*/
            service.execute(dispatcher(HostNmapDump[i], macs, ap));
            Log.i(TAG, "{{{{{{{{{{{{{" + HostNmapDump[i] + "}}}}}}}}}}}}}}}}}");
        }
        service.shutdown();
        try {
            service.awaitTermination(210, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            nmapIsTooLong();
        }
    }

    private Runnable                    dispatcher(final String node, final String[] macs, final Network ap) {
       return new Runnable() {
            public void run() {
                try {
                    Host host = new Host();
                    getIpHOSTNAME(node.split("\n")[0], host);
                    host.mac = getMACInTmp(macs, host.ip);
                    Host hostInList = ap.getHostFromMac(host.mac);
//                    Log.d(TAG, "host from mac OK");
                    if (hostInList.name.isEmpty() || hostInList.name.contains("Unknow"))
                        hostInList.name = host.name;
                    if (!Fingerprint.isItMyDevice(host)) {
                        Log.d(TAG, "buildHostFromNmapDump on " + host.toString());
                        buildHostFromNmapDump(node, hostInList);
                    } else {
//                        Log.d(TAG, "myDevice on " + host.toString());
                        initIfItsMyDevice(hostInList);
                    }
                } catch (UnknownHostException e) {
                    Log.e(TAG, "UnknowHost");
                    e.printStackTrace();
                } catch (Exception e) {
                    Log.e(TAG, "Error detected in dispatcher");
                } finally {
                    onNodeParsed();
                }
            }
        };
    }

    private void                    buildHostFromNmapDump(String nmapStdout, Host host) throws UnknownHostException {
        String[] nmapStdoutHost = nmapStdout.split("\n");
        StringBuilder dump = new StringBuilder("");
        for (int i = 0; i < nmapStdoutHost.length; i++) {
            String line = nmapStdoutHost[i];
            if (line.contains("Device type: ")) {
                dump.append(line).append("\n");
//                Log.v(TAG,  "buildHostFromNmapDump::" + line);
                host.deviceType = line.replace("Device type: ", "");
            } else if (line.contains("Running: ")) {
                dump.append(line);
//                Log.v(TAG, "buildHostFromNmapDump::" + line);
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
            }
        }
        host.state = Host.State.ONLINE;
        saveHost(host, dump);
    }

    private void                    saveHost(Host host, StringBuilder dump) {
        host.dumpInfo = dump.toString();
        Fingerprint.initHost(host);
        host.mac = host.mac.toUpperCase();
        if (host.Notes == null)
            host.Notes = "";
        if (Fingerprint.isItWindows(host)) {
            host.osType = Os.Windows;
        }
        if (Fingerprint.isItMyGateway(host)) {
            host.osType = Os.Gateway;
        }
        host.Notes = host.dumpInfo + '\n' +
                    ((host.Ports() == null) ? " No Port detected ? " : host.Ports().getDump());
        host.save();
    }

    private void                    initIfItsMyDevice(Host host) {
        host.mac = mSingleton.network.mac;
        host.ip = mSingleton.network.myIp;
        host = DBHost.saveOrGetInDatabase(host);
        host.name = "My Device";
        host.os = "Android/(AOSP)";
        host.osType = Os.Android;
        host.isItMyDevice = true;
        host.save();
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
 //       Log.d(TAG, "portList");
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

    private String                  getMACInTmp(String[] macsAndIp, String ip) throws UnknownHostException {
        for (String mac : macsAndIp) {
            if (mac.contains(ip + ':'))
                return mac.replace(ip + ':', "").toUpperCase();
        }
        throw new UnknownHostException("getMACInTmp::no ip[" + ip +"] in list of macts");
    }

    private void                    onNodeParsed() {
        NBR_PARSED_NODE = NBR_PARSED_NODE + 1;
        mNmapControler.setTitleToolbar("Analyzing",
                "(" + NBR_PARSED_NODE + "/" + LENGTH_NODE + ") devices scanned");
        if (NBR_PARSED_NODE >= LENGTH_NODE)
            onAllNodeParsed();
    }

    private void                    nmapIsTooLong() {
        Log.d(TAG, "Some node wasn't parsed, inintializing..");
        Log.d(TAG, "Analyzing (" + NBR_PARSED_NODE + "/" + LENGTH_NODE + ") devices scanned");
        Collections.sort(mNetwork.listDevices(), Fingerprint.getComparator());
        Iterator<Host> iter = mNetwork.listDevices().iterator();
        while (iter.hasNext()) {//ConcurrentModificationException
            Host host = iter.next();
            if (host.osType == Os.Unknow) {
                host.dumpMe(mSingleton.selectedHostsList);
                Log.d(TAG, "-------------");
            }
        }
        mNmapControler.onHostActualized(mNetwork.listDevices());
    }

    private void                    onAllNodeParsed() {
        Log.d(TAG, "AllNode parsed, inintializing..");
        Collections.sort(mNetwork.listDevices(), Fingerprint.getComparator());
        //TODO: add offline devices
        Iterator<Host> iter = mNetwork.listDevices().iterator();
        while (iter.hasNext()) {//ConcurrentModificationException
            Host host = iter.next();
            if (host.osType == Os.Unknow) {
                host.dumpMe(mSingleton.selectedHostsList);
                Log.d(TAG, "-------------");
            }
        }
        mNmapControler.onHostActualized(mNetwork.listDevices());
    }

}
