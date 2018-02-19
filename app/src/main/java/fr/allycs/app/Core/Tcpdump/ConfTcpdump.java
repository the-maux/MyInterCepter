package fr.allycs.app.Core.Tcpdump;

import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Model.Net.Pcap;
import fr.allycs.app.Model.Target.Host;

class                           ConfTcpdump {
    private String              TAG = "ConfTcpdump";
    String                      INTERFACE = "-i wlan0 ";    //  Focus interfacte;
    String                      STDOUT_BUFF = "-l ";        //  Make stdOUT line buffered.  Useful if you want to see  the  data in live
    String                      VERBOSE_v1 = "-v ";          //  Verbose mode 1
    String                      VERBOSE_v2 = "-vv  ";        //  Even more verbose output.
    String                      VERBOSE_v3 = "-vvvx  ";      //  Print trame in HEXA<->ASCII
    /*                           -x When parsing and printing, in addition to printing  the  headers
                                 of  each  packet,  print the data of each packet (minus its link
                                 level header) in hex.*/
    String                      SNARF = "-s 0 ";             //  Snarf snaplen bytes of data from each  packet , no idea what this mean
    Singleton                   mSingleton = Singleton.getInstance();
    
    ConfTcpdump() {

    }
    
    LinkedHashMap<String, String> initCmds() {
        LinkedHashMap<String, String> cmds = new LinkedHashMap<>();
        cmds.put("No Filter", INTERFACE  + " \' ");
        cmds.put("Custom Filter", INTERFACE + " \' ");
        cmds.put("DNS Filter", INTERFACE + " \' dst port 53 ");
        cmds.put("DNS Intercepter", INTERFACE + STDOUT_BUFF + VERBOSE_v2 + SNARF + VERBOSE_v3 + "\' dst port 53 ");
        cmds.put("HTTP Filter",  INTERFACE + " \' (port 80 or port 443 or dst port 53) ");
        cmds.put("Display Format", INTERFACE + STDOUT_BUFF + VERBOSE_v2 + SNARF + VERBOSE_v3 + "\'");
        cmds.put("TCP Filter",  INTERFACE  + " \' tcp ");
        cmds.put("UDP Filter", INTERFACE + " \' udp ");
        cmds.put("Arp Filter",  INTERFACE + " \' arp ");
        return cmds;
    }

    private String              buildHostFilterCommand(List<Host> hosts, String typeScan){
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

    private String              buildForDumpingPcap(String nameFile, List<Host> hosts) {
        String pcapFile = mSingleton.PcapPath + nameFile + ".pcap ";
        Pcap pcap = new Pcap(nameFile + ".pcap ", hosts);
        pcap.sniffSession = mSingleton.getActualSniffSession();

        pcap.save();
        if (mSingleton.getActualSniffSession() != null) {
            mSingleton.getActualSniffSession().listPcapRecorded().add(pcap);
            Log.d(TAG, "Pcap added to Sniff Session");
        } else {
            Log.d(TAG, "Pcap not added to Sniff Session");
        }
        pcapFile =  " -w " + pcapFile;
        Log.d(TAG, pcap.toString());
        return pcapFile;
    }

    String                      buildCmd(String actualParam, boolean isDumpingInFile,
                                            String typeScan, List<Host> hosts) {
        String hostFilter = buildHostFilterCommand(hosts, typeScan);
        String date =  new SimpleDateFormat("dd_MMMM_HH#mm-ss", Locale.FRANCE).format(new Date())
                .replace("#", "h").replace("-", "m").replace("é", "e");
        String nameFile = mSingleton.network.Ssid  + "_" + date;
        String pcapFile = (isDumpingInFile) ? buildForDumpingPcap(nameFile, hosts) : "";
        String cmd = (mSingleton.FilesPath + "tcpdump " +
                pcapFile + actualParam + hostFilter)
                .replace("//", "/").replace("  ", " ");
        Log.d(TAG, cmd);
        return cmd ;
    }

    public String               buildCmd(File mPcapFile) {
        Log.d(TAG, "buildCmd::" + mSingleton.FilesPath + "tcpdump " + "-r " + mPcapFile.getPath());
        return mSingleton.FilesPath + "tcpdump " + "-r " + mPcapFile.getPath();
    }
}