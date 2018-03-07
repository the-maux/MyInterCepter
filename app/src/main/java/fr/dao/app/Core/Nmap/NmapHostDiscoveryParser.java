package fr.dao.app.Core.Nmap;

import android.util.Log;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.Model.Unix.Os;

class NmapHostDiscoveryParser {
    private String                  TAG = "NmapHostDiscoveryParser";
    private Singleton               mSingleton = Singleton.getInstance();
 //   private ArrayList<Host>         hosts = new ArrayList<>();
    private Network                 mNetwork;
    private NmapControler           mNmapControler;
    private int                     LENGTH_NODE, NBR_PARSED_NODE = 0;

    NmapHostDiscoveryParser(NmapControler nmapControler, String listMacs, String NmapDump, Network ap) {
        this.mNmapControler = nmapControler;
        //Log.d(TAG, "dump list macs[" + listMacs + "]");
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
                        //Log.d(TAG, "buildHostFromNmapDump on " + host.toString());
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
                host.deviceType = line.replace("Device type: ", "");
            } else if (line.contains("Running: ")) {
                dump.append(line).append("\n");
//                Log.v(TAG, "buildHostFromNmapDump::" + line);
                getOs(line.replace("Running: ", ""), host, nmapStdoutHost);
            } else if (line.contains("Too many fingerprints match this host to give specific OS details")) {
                dump.append(line).append("\n");
                host.TooManyFingerprintMatchForOs = true;
            } else if (line.contains("Network Distance: ")) {
                dump.append(line).append("\n");
                host.NetworkDistance = line.replace("Network Distance: ", "");
            } else if (line.contains("NetBIOS name:")) {
                dump.append(line).append("\n");
                host.name = line.split(",")[0].replace("NetBIOS name: ", "")
                        .replace("|   ", "");
            } else if (line.contains("MAC Address: ")) {
                dump.append(line).append("\n");
                host.mac = line.replace("MAC Address: ", "").split(" ")[0];
                host.vendor = line.replace("MAC Address: " + host.mac + " (", "").replace(")", "");
            } else if (line.contains("PORT ")) {
                int z = getPortList(nmapStdoutHost, i +1, host, dump);
                for (; i < z; i++) {
                    dump.append(nmapStdoutHost[i]).append("\n");
                }
            } else if (line.contains("Host script results:")) {
                int z = analyseHostScriptResult(nmapStdoutHost, i + 1, host, dump);
                for (; i < z; i++) {
                    dump.append(nmapStdoutHost[i]).append("\n");
                }
            }
        }
        host.state = Host.State.ONLINE;
        saveHost(host, dump, nmapStdout);
    }


    private void                    saveHost(Host host, StringBuilder dump, String nmapStdout) {
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
            mNetwork.Gateway = host;
        }
        host.Notes = nmapStdout;/* host.dumpInfo + '\n' +
                "---------------------------------\n Analyse is:" +
                    ((host.Ports() == null) ? " No Port detected ? " : host.Ports().getDump());*/
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

    /**
     * Host script results:
     | nbstat:
     |   NetBIOS name: BCOUSIN-LATITUD, NetBIOS user: <unknown>, NetBIOS MAC: <unknown>
     |   Names
     |     BCOUSIN-LATITUD<00>  Flags: <unique><active>
     |     BCOUSIN-LATITUD<03>  Flags: <unique><active>
     |     BCOUSIN-LATITUD<20>  Flags: <unique><active>
     |     WORKGROUP<00>        Flags: <group><active>
     |_    WORKGROUP<1e>        Flags: <group><active>
     * @param nmapStdoutHost
     * @param i
     * @param host
     * @param dump
     * @return
     */
    private int                     analyseHostScriptResult(String[] nmapStdoutHost, int i, Host host, StringBuilder dump) {
        ArrayList<String> dumpHostScript = new ArrayList<>();
        while (i < nmapStdoutHost.length && !nmapStdoutHost[i].startsWith("|_")){
            dumpHostScript.add(nmapStdoutHost[i++]);
        }
        Log.d(TAG, "HOSTSCRIPT:[" + dumpHostScript+ "]");
        for (String line : dumpHostScript) {
            if (line.contains("NetBIOS name")) {
                String[] splitted = line.split(",");
                Log.d(TAG, "NetBIOS name:[" + splitted[0].replace("NetBIOS name: ", "").replaceAll("  ", " ").replace("|", "") + "]");
                Log.d(TAG, "NetBIOS user:[" + splitted[1].replace("NetBIOS user: ", "").replaceAll("  ", " ") + "]");
                //TODO: get le groupe ->  Flags: <group>
            }
        }
        return i;
    }

    /**
     * 5353/udp open   zeroconf
     | dns-service-discovery:
     |   49600/tcp http
     |     Address=10.16.186.167 fe80:0:0:0:8c1:dc67:c4cc:4b15
     |   59544/tcp companion-link
     |     rpBA=86:4D:AB:5D:21:2C
     |     rpVr=120.51
     |     rpHI=233259abbbba
     |     rpHN=9348e33de016
     |     rpHA=bf1e06edf5f7
     |     model=MacBookPro11,2
     |     osxvers=17
     |_    Address=10.16.186.167 fe80:0:0:0:8c1:dc67:c4cc:4b15
     */
    private int                     getPortList(String[] line, int i, Host host, StringBuilder dump) {
        ArrayList<String> ports = new ArrayList<>();
        for (; i < line.length; i++) {
            if (!(line[i].contains("open") || line[i].contains("close") || line[i].contains("filtered"))) {
                if (line[i].startsWith("| ") && line[i].endsWith(": ")) {
                    while (i < line.length && !line[i].startsWith("|_")) {
                        ports.add(line[i++].replaceAll("  ", " "));
                    }
                } else {
                    host.Ports(ports);
                    return i-1;
                }
            } else {
                ports.add(line[i].replaceAll("  ", " "));
            }
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
        throw new UnknownHostException("getMACInTmp::no subtitle[" + ip +"] in list of macts");
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
       // Collections.sort(mNetwork.listDevices(), Fingerprint.getComparator());
        Iterator<Host> iter = mNetwork.listDevices().iterator();
        while (iter.hasNext()) {//ConcurrentModificationException
            Host host = iter.next();
            if (host.osType == Os.Unknow) {
                host.dumpMe(mSingleton.hostList);
                Log.d(TAG, "-------------");
            }
        }
        mNmapControler.onHostActualized(mNetwork.listDevices());
    }

    private void                    onAllNodeParsed() {
        try {
            Log.d(TAG, "AllNode parsed, inintializing..");
            Collections.sort(mNetwork.listDevices(), Fingerprint.getComparator());
            Iterator<Host> iter = mNetwork.listDevices().iterator();
            while (iter.hasNext()) {
                Host host = iter.next();
                if (host.osType == Os.Unknow) {
                    host.dumpMe(mSingleton.hostList);
                    Log.d(TAG, "-------------");
                }
            }
            mNmapControler.onHostActualized(mNetwork.listDevices());
        } catch (ConcurrentModificationException ex) {
            ex.getStackTrace();
            Log.e(TAG, "Thread error detected, restarting analyse");
            onNodeParsed();
        }
    }

}
