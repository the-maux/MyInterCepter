package fr.dao.app.Core.Scan;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import fr.dao.app.Core.Configuration.Comparator.Comparators;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Words;
import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.Model.Target.State;
import fr.dao.app.Model.Unix.Os;

class                               NmapHostDiscoveryParser {
    private String                  TAG = "NmapHostDiscoveryParser";
    private Singleton               mSingleton = Singleton.getInstance();
    private Network                 mNetwork;
    private NmapControler           mNmapControler;
    private int                     LENGTH_NODE, NBR_PARSED_NODE = 0;
    private NmapUpnpParser          UpnParser;

    NmapHostDiscoveryParser(NmapControler nmapControler, String NmapDump, Network ap, Context context) {
        this.mNmapControler = nmapControler;
        UpnParser = new NmapUpnpParser(context);
        if (mSingleton.Settings.getUserPreferences().autoSaveNmapSession)
            dumpToFile(NmapDump);
        String[] HostNmapDump = NmapDump.split("Nmap scan report for ");
        LENGTH_NODE = HostNmapDump.length-1;
        ExecutorService service = Executors.newCachedThreadPool();
        mNetwork = ap;
        for (int i = 1; i < HostNmapDump.length; i++) {/*First node is the nmap preambule*/
            service.execute(dispatcher(HostNmapDump[i], ap));
        }
        service.shutdown();
        try {
            service.awaitTermination(210, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            nmapIsTooLong();
        }
    }

    private void                    dumpToFile(String nmapDump) {
        final File file = new File(mSingleton.Settings.DumpsPath, mSingleton.NetworkInformation.ssid + Words.getGenericDateFormat(new Date()));
        try {
            file.createNewFile();
            file.setReadable(true, false);
            file.setWritable(true, false);
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(nmapDump);
            myOutWriter.close();
            fOut.flush();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "ERROR IN DUMPING NMAP SCAN");
        }
    }

    private Runnable                dispatcher(final String node, final Network ap) {
       return new Runnable() {
            public void run() {
                try {
                    Host hostInList = getIpHOSTNAME(node.split("\n")[0], ap.listDevices());
                    if (!hostInList.ip.contains(Singleton.getInstance().NetworkInformation.myIp))/* Its my device*/
                        buildHostFromNmapDump(node, hostInList);
                    else
                        initIfItsMyDevice(hostInList);
                } catch (Exception e) {
                    Log.e(TAG, "Error detected in dispatcher");
                } finally {
                    onNodeParsed();
                }
            }
        };
    }

    private void                    buildHostFromNmapDump(String nmapStdout, Host host) {
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
            } else if (line.contains("NetworkInformation Distance: ")) {
                dump.append(line).append("\n");
                host.NetworkDistance = line.replace("NetworkInformation Distance: ", "");
            } else if (line.contains("NetBIOS name:")) {
                dump.append(line).append("\n");
                host.name = line.split(",")[0].replace("NetBIOS name: ", "")
                        .replace("|   ", "");
            } else if (line.contains("MAC Address: ")) {
                dump.append(line).append("\n");
              //  Log.i(TAG, "MAC:[" + line + "]");
                host.mac = line.replace("MAC Address: ", "").split(" ")[0];
                host.vendor = line.replace("MAC Address: " + host.mac + " (", "").replace(")", "");
                host.vendor = host.vendor.substring(0, 1).toUpperCase() + host.vendor.substring(1);
            } else if (line.contains("PORT ")) {
                try {
                    int z = PortParser.getPortList(nmapStdoutHost, i +1, host);
                    for (; i < z; i++) {
                        if (nmapStdoutHost[i].contains("upnp-info:"))
                            UpnParser.analyseUPnPtResult(nmapStdoutHost, i + 1, host);
                        dump.append(nmapStdoutHost[i]).append("\n");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in analyzing nmap dump");
                    e.printStackTrace();
                }
            } else if (line.contains("Host script results:")) {
                int z = analyseHostScriptResult(nmapStdoutHost, i + 1, host);
                for (; i < z; i++) {
                    dump.append(nmapStdoutHost[i]).append("\n");
                }
            }
        }
        host.state = State.ONLINE;
        saveHost(host, dump, nmapStdout);
    }

    private void                    saveHost(Host host, StringBuilder dump, String nmapStdout) {
        host.mac = host.mac.toUpperCase();
        host.dumpInfo = dump.toString();
        Fingerprint.initHost(host);
        if (host.Notes == null)
            host.Notes = "";
        host.Notes = host.Notes.concat("OxBABOBAB").concat(nmapStdout);
        if (host.osType == Os.Unknow || (host.osType == Os.Android && !host.isItMyDevice)) {
            Log.i(TAG, "HOST[" + host.ip + "] STILL UNKNOWN !");
            InetAddress inetAddress = null;
            try {
                inetAddress = InetAddress.getByName(host.ip);
                String name = inetAddress.getCanonicalHostName();
                Log.i(TAG, "HOST[" + host.ip + "] DNS NAME[" + name + "] !");
            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.e(TAG, "HOST NO DNS FOUND");
            }
        }
        if (host.Deepest_Scan > mSingleton.Settings.getUserPreferences().NmapMode) {
            Log.d(TAG, "Host[" + host.ip + "] was already Nmap scanned at this level[" + host.Deepest_Scan + "]");
        } else {
            host.Deepest_Scan = mSingleton.Settings.getUserPreferences().NmapMode;
            host.save();
        }
    }

    private void                    initIfItsMyDevice(Host host) {
        Log.i(TAG, "THIS IS MY DEVICE DETECTED");
        host.mac = mSingleton.NetworkInformation.mac;
        host.ip = mSingleton.NetworkInformation.myIp;
        host = DBHost.saveOrGetInDatabase(host);
        host.name = "My Device";
        host.os = "Android API " + Build.VERSION.SDK_INT;
        host.osType = Os.Android;
        host.osDetail = Build.BRAND.substring(0, 1).toUpperCase() + Build.BRAND.substring(1) + " " + Build.DEVICE;
        host.vendor = host.osDetail;
        host.isItMyDevice = true;
        host.save();
    }

    private Host                    getIpHOSTNAME(String line, ArrayList<Host> hosts) {
        /* nbl037421.hq.fr.corp.leroymerlin.com (10.16.187.230) */
        String ip = null, hostname = null;
        if (line.contains("(")) {
            ip = line.split(" ")[1].replace("(", "").replace(")", "");
            hostname = line.split(" ")[0];
        } else {
            ip = line.split(" ")[0];
        }
        for (Host host : hosts) {
            if (host.ip.contentEquals(ip)) {
                if (hostname != null)
                    host.name = hostname;
                return host;
            }

        }
        Log.e(TAG, "ERROR FOR " + line);
        return null;
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
     * @return;
     */
    private int                     analyseHostScriptResult(String[] nmapStdoutHost, int i, Host host) {
        ArrayList<String> dumpHostScript = new ArrayList<>();
        while (i < nmapStdoutHost.length && !nmapStdoutHost[i].startsWith("|_")){
            dumpHostScript.add(nmapStdoutHost[i++]);
        }
        Log.d(TAG, "HOSTSCRIPT:[" + dumpHostScript+ "]");
        for (String line : dumpHostScript) {
            if (line.contains("NetBIOS name")) {
                String[] splitted = line.split(",");
                host.NetBIOS_Name = splitted[0].replace("NetBIOS name: ", "").replace("|", "").trim();
                Log.d(TAG, "NetBIOS name:[" + host.NetBIOS_Name + "]");
                host.NetBIOS_Role = splitted[1].replace("NetBIOS user: ", "").trim();
                Log.d(TAG, "NetBIOS user:[" + host.NetBIOS_Role + "]");
                //TODO: get le groupe ->  Flags: <group>
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

    private String                  getMACInTmp(ArrayList<Host> hosts, String ip) throws UnknownHostException {
        for (Host host : hosts) {
            if (host.mac.contains(ip + ':'))
                return host.mac.replace(ip + ':', "").toUpperCase();
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
        Iterator<Host> iter = mNetwork.listDevices().iterator();
        mNmapControler.onHostActualized(mNetwork.listDevices());
    }

    private void                    onAllNodeParsed() {
        try {
            Log.d(TAG, "AllNode parsed, inintializing..");
            Collections.sort(mNetwork.listDevices(), Comparators.getHostComparator());
            Iterator<Host> iter = mNetwork.listDevices().iterator();
            while (iter.hasNext()) {
                Host host = iter.next();
                /*if (host.osType == Os.Unknow) {
                    host.dumpMe();
                    Log.d(TAG, "-------------");
                }*/
            }
            mNmapControler.onHostActualized(mNetwork.listDevices());
        } catch (ConcurrentModificationException ex) {
            ex.getStackTrace();
            Log.e(TAG, "Thread error detected, restarting analyse");
            onNodeParsed();
        }
    }

}