package fr.allycs.app.Controller.Core.Tcpdump;

import android.app.Activity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import fr.allycs.app.Controller.Core.ArpSpoof;
import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.RootProcess;
import fr.allycs.app.Controller.Network.IPTables;
import fr.allycs.app.Model.Net.Trame;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.View.Activity.Tcpdump.WiresharkActivity;

public class                        Tcpdump {
    private String                  TAG = "Tcpdump";
    private static Tcpdump          mInstance = null;
    private Singleton               mSingleton = Singleton.getInstance();
    private LinkedHashMap<String, String> mCmds;
    private RootProcess mTcpDumpProcess;
    private WiresharkActivity       mActivity;
    private ConfTcpdump             mTcpdumpConf = new ConfTcpdump();
    private boolean                 mAdvancedAnalyseTrame = false;
    public boolean                  isRunning = false, isDumpingInFile = true;
    public CopyOnWriteArrayList<Trame> listOfTrames;
    public String                   actualParam = "";
    public List<Host>               hosts;

    private                         Tcpdump(WiresharkActivity activity) {
        this.mActivity = activity;
        listOfTrames = new CopyOnWriteArrayList<>();
        initPromptCmds();
    }

    public static synchronized Tcpdump getTcpdump(Activity activity, boolean isWiresharkActivity) {
        if (isWiresharkActivity) {
            if (mInstance == null) {
                mInstance = new Tcpdump((WiresharkActivity) activity);
            }
        }
        return mInstance;
    }

    public static synchronized boolean  isRunning() {
        if (mInstance == null)
            return false;
        return mInstance.isRunning;
    }

    private void                    initPromptCmds() {
        mCmds = mTcpdumpConf.initCmds();
    }


    public String                   start(final String actualParam, List<Host> hosts, String typeScan) {
        this.hosts = hosts;
        this.actualParam = actualParam;
        IPTables.InterceptWithoutSSL();
        final String cmd = mTcpdumpConf.buildCmd(actualParam, isDumpingInFile, typeScan, hosts);
        new Thread(new Runnable() {
            @Override
            public void run() {
                isRunning = true;
                ArpSpoof.launchArpSpoof();
                try {
                    mTcpdumpConf.evilSocketDnsParsing.init_mitm_dns_behavior(actualParam);
                    listOfTrames.clear();
                    Log.i(TAG, cmd);
                    mTcpDumpProcess = new RootProcess("Wireshark").exec(cmd);
                    BufferedReader reader = mTcpDumpProcess.getReader();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String finalLine = line;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (isRunning) onNewLine(finalLine);
                            }
                        }).start();
                    }
                    Log.i(TAG, "Tcpdump execution over");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Process Error: " + e.getMessage());
                } finally {
                    if (mTcpDumpProcess != null)
                        mTcpDumpProcess.closeProcess();
                    onNewLine("Quiting...");
                }
                Log.d(TAG, "End of Tcpdump thread");
            }
        }).start();
        return cmd.replace("nmap/nmap", "nmap")
                .replace(mSingleton.FilesPath, "");//trimmed cmd
    }
    private void                    onNewLine(String line) {
        if (line.contains("Quiting...")) {
            Trame trame = new Trame("Processus over", 0);
            trame.connectionOver = true;
            mActivity.onNewTrame(trame);
            onTcpDumpStop();
            return;
        }
        mTcpdumpConf.evilSocketDnsParsing.mitm_dns_behavior(actualParam, line);
        Trame trame = new Trame(line, 0);
        if (trame.initialised) {
            listOfTrames.add(0, trame);
            trame.offsett = listOfTrames.size();
            mActivity.onNewTrame(trame);
        } else if (!trame.skipped) {
            mActivity.onNewTrame(trame);
            Log.d(TAG, "trame created not initialized and not skipped, STOP TCPDUMP");
            mActivity.setToolbarTitle(null, "Error in processing");
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
                //TODO: faire un ok qui t'amene vers le dossier
                mActivity.showSnackbar("Pcap saved here : " + mSingleton.PcapPath);
            }
        }
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
