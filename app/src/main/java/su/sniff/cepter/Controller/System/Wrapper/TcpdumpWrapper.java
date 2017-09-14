package su.sniff.cepter.Controller.System.Wrapper;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import su.sniff.cepter.Controller.Network.IPTables;
import su.sniff.cepter.Controller.Network.MyDNSMITM;
import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Model.Pcap.Trame;
import su.sniff.cepter.View.WiresharkActivity;

public class                TcpdumpWrapper {
    private LinkedHashMap<String, String> cmds;
    private RootProcess     tcpDumpProcess;
    private String          TAG = "TcpdumpWrapper";
    private WiresharkActivity activity;
    public boolean          isRunning = false, isDumpingInFile = false;
    private boolean         deepAnalyseTrame = false;
    public ArrayList<Trame> listOfTrames = new ArrayList<>();
    public String           actualParam = "";

    private String          INTERFACE = "-i wlan0 ";    //  Focus interfacte;
    private String          STDOUT_BUFF = "-l ";        //  Make stdOUT line buffered.  Useful if you want to see  the  data in live
    private String          VERBOSE_v1 = "-v ";          //  Verbose mode 1
    private String          VERBOSE_v2 = "-vv  ";        //  Even more verbose output.
    private String          VERBOSE_v3 = "-vvvx  ";      //  Print trame in HEXA<->ASCII
    /*                           -x When parsing and printing, in addition to printing  the  headers
                                 of  each  packet,  print the data of each packet (minus its link
                                 level header) in hex.*/
    private String          SNARF = "-s 0 ";             //  Snarf snaplen bytes of data from each  packet , no idea what this mean

    public                  TcpdumpWrapper(WiresharkActivity activity) {
        this.activity = activity;
        initCmds();
    }
    private void            initCmds() {
        cmds = new LinkedHashMap<>();
        cmds.put("No Filter", INTERFACE  + " \' ");
        cmds.put("Custom Filter", INTERFACE + " \' ");
        cmds.put("DNS Filter", INTERFACE + " \' dst port 53 ");
        cmds.put("DNS Intercepter", INTERFACE + STDOUT_BUFF + VERBOSE_v2 + SNARF + VERBOSE_v3 + "\' dst port 53 ");
        cmds.put("HTTP Filter",  INTERFACE + " \' (port 80 or port 443 or dst port 53) ");
        cmds.put("Display Format", INTERFACE + STDOUT_BUFF + VERBOSE_v2 + SNARF + VERBOSE_v3 + "\'");
        cmds.put("TCP Filter",  INTERFACE  + " \' tcp ");
        cmds.put("UDP Filter", INTERFACE + " \' udp ");
        cmds.put("Arp Filter",  INTERFACE + " \' arp ");
    }
    private String          buildCmd(String actualParam, String hostFilter) {
        String date =  new SimpleDateFormat("MM_dd_HH_mm_ss", Locale.FRANCE).format(new Date());
        String pcapFile = ((isDumpingInFile) ?
                (" -w " + Singleton.getInstance().PcapPath + date + ".pcap ") : "");
        return
                Singleton.getInstance().FilesPath +
                        "tcpdump " +
                        pcapFile +
                        actualParam +
                        hostFilter;

    }

    /**
     * Start ARPSpoof
     * Bloque le port des trames DNS
     * Inspect/Alter DNS Query
     * Dispatch the DNS request on network
     */
    public void             start(final String actualParam, String hostFilter) {
        Log.i(TAG, "start");
        IPTables.InterceptWithoutSSL();
        this.actualParam = actualParam;
        final String cmd = buildCmd(actualParam, hostFilter).replace("//", "/").replace("  ", " ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                ArpSpoof.launchArpSpoof();
                try {
                    Thread.sleep(3000);//Wait a sec for ARP Catched for target
                    if (actualParam.contains(STDOUT_BUFF) && actualParam.contains("dst port 53")) {
                        Log.i(TAG, "DNS REQUEST MITM");
                        new IPTables().discardForwardding2Port(53); //MITM DNS
                    }
                    listOfTrames.clear();
                    tcpDumpProcess = new RootProcess("Wireshark").exec(cmd);
                    BufferedReader reader = tcpDumpProcess.getReader();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String finalLine = line;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                onNewLine(finalLine);
                            }
                        }).start();
                    }
                    Log.d(TAG, "./Tcpdump finish");
                    onNewLine("Quiting...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.d(TAG, "InterruptedException");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Process Error");
                } finally {
                    if (tcpDumpProcess != null)
                        tcpDumpProcess.closeProcess();
                }
                Log.d(TAG, "onTcpDump start over");
            }
        }).start();
    }
    private void            onNewLine(String line) {
        if (line.contains("Quiting...")) {
            Trame trame = new Trame("Processus over", listOfTrames.size(), 0);
            trame.connectionOver = true;
            activity.onNewTrame(trame);
            return;
        }
        if (actualParam.contains(STDOUT_BUFF) && actualParam.contains("dst port 53")) {
            MITM_DNS(line);
        }
        Trame trame = new Trame(line, listOfTrames.size(), 0);
        if (trame.initialised) {
            listOfTrames.add(0, trame);
            trame.offsett = listOfTrames.size();
            activity.onNewTrame(trame);
        } else if (!trame.skipped) {
            activity.onNewTrame(trame);
            onTcpDumpStop();
        }
    }
    public void             onTcpDumpStop() {
        ArpSpoof.stopArpSpoof();
        RootProcess.kill("tcpdump");
        isRunning = false;
        IPTables.stopIpTable();
    }
    /**
     * Renvoie la trame mais peut altérer la réponse
     * @param line
     */
    private void            MITM_DNS(String line) {
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
        new MyDNSMITM(reqdata.toString());
    }

    public LinkedHashMap<String, String> getCmdsWithArgsInMap() {
        return cmds;
    }

    public boolean          isDeepAnalyseTrame() {
        return deepAnalyseTrame;
    }
    public void             setDeepAnalyseTrame(boolean deepAnalyseTrame) {
        /** TODO: Restart App with dump Mode**/
        this.deepAnalyseTrame = deepAnalyseTrame;
    }
}
