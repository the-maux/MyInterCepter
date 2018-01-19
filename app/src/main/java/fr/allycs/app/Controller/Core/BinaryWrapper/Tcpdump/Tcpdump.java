package fr.allycs.app.Controller.Core.BinaryWrapper.Tcpdump;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.allycs.app.Controller.Core.BinaryWrapper.ArpSpoof;
import fr.allycs.app.Controller.Core.BinaryWrapper.RootProcess;
import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Database.DBHost;
import fr.allycs.app.Controller.Network.IPTables;
import fr.allycs.app.Model.Net.Trame;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Unix.Pcap;
import fr.allycs.app.View.WiresharkActivity;

public class                        Tcpdump {
    private String                  TAG = "Tcpdump";
    private static Tcpdump          mInstance = null;
    private Singleton               mSingleton = Singleton.getInstance();
    private LinkedHashMap<String, String> mCmds;
    private RootProcess             mTcpDumpProcess;
    private WiresharkActivity       mActivity;

    private boolean                 mAdvancedAnalyseTrame = false;
    public boolean                  isRunning = false, isDumpingInFile = false;
    public CopyOnWriteArrayList<Trame> listOfTrames;
    public String                   actualParam = "";
    public List<Host>               hosts;
    private String                  INTERFACE = "-i wlan0 ";    //  Focus interfacte;
    private String                  STDOUT_BUFF = "-l ";        //  Make stdOUT line buffered.  Useful if you want to see  the  data in live
    private String                  VERBOSE_v1 = "-v ";          //  Verbose mode 1
    private String                  VERBOSE_v2 = "-vv  ";        //  Even more verbose output.
    private String                  VERBOSE_v3 = "-vvvx  ";      //  Print trame in HEXA<->ASCII
    /*                           -x When parsing and printing, in addition to printing  the  headers
                                 of  each  packet,  print the data of each packet (minus its link
                                 level header) in hex.*/
    private String                  SNARF = "-s 0 ";             //  Snarf snaplen bytes of data from each  packet , no idea what this mean

    private                         Tcpdump(WiresharkActivity activity) {
        this.mActivity = activity;
        listOfTrames = new CopyOnWriteArrayList<>();
        initCmds();
    }

    public static synchronized Tcpdump getTcpdump(WiresharkActivity activity) {
        if (mInstance == null) {
            mInstance = new Tcpdump(activity);
        }
        return mInstance;
    }

    public static synchronized Tcpdump getTcpdump(Activity activity) {
        return mInstance;
    }

    private void                    initCmds() {
        mCmds = new LinkedHashMap<>();
        mCmds.put("No Filter", INTERFACE  + " \' ");
        mCmds.put("Custom Filter", INTERFACE + " \' ");
        mCmds.put("DNS Filter", INTERFACE + " \' dst port 53 ");
        mCmds.put("DNS Intercepter", INTERFACE + STDOUT_BUFF + VERBOSE_v2 + SNARF + VERBOSE_v3 + "\' dst port 53 ");
        mCmds.put("HTTP Filter",  INTERFACE + " \' (port 80 or port 443 or dst port 53) ");
        mCmds.put("Display Format", INTERFACE + STDOUT_BUFF + VERBOSE_v2 + SNARF + VERBOSE_v3 + "\'");
        mCmds.put("TCP Filter",  INTERFACE  + " \' tcp ");
        mCmds.put("UDP Filter", INTERFACE + " \' udp ");
        mCmds.put("Arp Filter",  INTERFACE + " \' arp ");
    }
    private String                  buildCmd(String actualParam, String hostFilter) {
        String date =  new SimpleDateFormat("MM_dd_HH_mm_ss", Locale.FRANCE).format(new Date());
        String nameFile = mSingleton.network.Ssid  + "_" + date;
        String pcapFile = ((isDumpingInFile) ?
                (" -w " + mSingleton.PcapPath + nameFile + ".pcap ") : "");
        Pcap pcap = new Pcap(mSingleton.PcapPath + nameFile + ".pcap ", hosts);
        pcap.sniffSession = mSingleton.getActualSniffSession();
        pcap.listDevicesSerialized = DBHost.SerializeListDevices(hosts);
        pcap.save();
        if (mSingleton.getActualSniffSession() != null)
            mSingleton.getActualSniffSession().listPcapRecorded().add(pcap);
        return mSingleton.FilesPath +
                        "tcpdump " +
                        pcapFile +
                        actualParam +
                        hostFilter;
    }

    private String                  buildHostFilter(List<Host> hosts, String typeScan){
        StringBuilder hostFilterBuilder = new StringBuilder("\'" +
                ((typeScan.contains("No Filter") || typeScan.contains("Custom Filter")) ?
        " (" : " and ("));
        for (int i = 0; i < hosts.size(); i++) {
            if (i > 0)
                hostFilterBuilder.append(" or ");
            hostFilterBuilder.append(" host ").append(hosts.get(i).ip);
        }
        hostFilterBuilder.append(")\'");
        return hostFilterBuilder.toString();
    }

    /**
     * Start ARPSpoof
     * Bloque le port des trames DNS
     * Inspect/Alter DNS Query
     * Dispatch the DNS request on network
     */
    public String                   start(final String actualParam, List<Host> hosts, String typeScan) {
        Log.i(TAG, "start::" + actualParam);
        this.hosts = hosts;
        IPTables.InterceptWithoutSSL();
        this.actualParam = actualParam;
        final String cmd = buildCmd(actualParam, buildHostFilter(hosts, typeScan))
                .replace("//", "/").replace("  ", " ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                ArpSpoof.launchArpSpoof();
                try {
                    //Thread.sleep(3000);//Wait a sec for ARP Catched for target
                    if (actualParam.contains(STDOUT_BUFF) && actualParam.contains("dst port 53")) {
                        Log.i(TAG, "DNS REQUEST MITM");
                        new IPTables().discardForwardding2Port(53); //MITM DNS
                    }
                    listOfTrames.clear();
                    Log.d(TAG, cmd);
                    mTcpDumpProcess = new RootProcess("Wireshark").exec(cmd);
                    BufferedReader reader = mTcpDumpProcess.getReader();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String finalLine = line;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (isRunning)
                                    onNewLine(finalLine);
                            }
                        }).start();
                    }
                    Log.d(TAG, "./Tcpdump is finish");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Process Error: " + e.getMessage());
                } finally {
                    if (mTcpDumpProcess != null)
                        mTcpDumpProcess.closeProcess();
                    onNewLine("Quiting...");
                }
                Log.d(TAG, "onTcpDump start over");
            }
        }).start();
        return cmd;
    }
    private void                    onNewLine(String line) {
        if (line.contains("Quiting...")) {
            Trame trame = new Trame("Processus over", listOfTrames.size(), 0);
            trame.connectionOver = true;
            mActivity.onNewTrame(trame);
            onTcpDumpStop();
            return;
        }
        if (actualParam.contains(STDOUT_BUFF) && actualParam.contains("dst port 53")) {
            MITM_DNS(line);
        }
        Trame trame = new Trame(line, listOfTrames.size(), 0);
        if (trame.initialised) {
            listOfTrames.add(0, trame);
            trame.offsett = listOfTrames.size();
            mActivity.onNewTrame(trame);
        } else if (!trame.skipped) {
            mActivity.onNewTrame(trame);
            onTcpDumpStop();
        }
    }
    public void                     onTcpDumpStop() {
        if (isRunning) {
            ArpSpoof.stopArpSpoof();
            RootProcess.kill("tcpdump");
            isRunning = false;
            IPTables.stopIpTable();
            if (isDumpingInFile) {
                new RootProcess("chmod Pcap files")
                        .exec("chmod 666 " + mSingleton.PcapPath + "/*")
                        .exec("chown sdcard_r:sdcard_r " + mSingleton.PcapPath + "/*")
                        .closeProcess();
            }
        }
    }
    private void                    MITM_DNS(String line) {
        StringBuilder reqdata = new StringBuilder();
        String regex = "^.+length\\s+(\\d+)\\)\\s+([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})\\.[^\\s]+\\s+>\\s+([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})\\.[^\\:]+.*";
        Matcher matcher = Pattern.compile(regex).matcher(line);
        if (!matcher.find() && !line.contains("tcpdump")) {
            line = line.substring(line.indexOf(":") + 1).trim().replace(" ", "");
            reqdata.append(line);
        } else {
            if (reqdata.length() > 0) {

            }
            reqdata.delete(0, reqdata.length());
        }
        //new MyDNSMITM(reqdata.toString());
    }

    public LinkedHashMap<String, String> getCmdsWithArgsInMap() {
        return mCmds;
    }

    public boolean                  ismAdvancedAnalyseTrame() {
        return mAdvancedAnalyseTrame;
    }
    public void                     setmAdvancedAnalyseTrame(boolean mAdvancedAnalyseTrame) {
        /** TODO: Restart App with dump Mode**/
        this.mAdvancedAnalyseTrame = mAdvancedAnalyseTrame;
    }
}
