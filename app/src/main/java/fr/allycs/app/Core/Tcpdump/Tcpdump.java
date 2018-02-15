package fr.allycs.app.Core.Tcpdump;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import fr.allycs.app.Core.Configuration.RootProcess;
import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Core.Network.ArpSpoof;
import fr.allycs.app.Core.Network.IPTables;
import fr.allycs.app.Model.Net.Trame;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;
import fr.allycs.app.View.Activity.Wireshark.WiresharkActivity;
import fr.allycs.app.View.Behavior.WiresharkDispatcher;

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
    public String                   actualParam = "", actualCmd = "";
    private WiresharkDispatcher     mDispatcher = null;

    private                         Tcpdump(WiresharkActivity activity) {
        this.mActivity = activity;
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

    public Tcpdump                  initCmd(List<Host> hosts, String typeScan, final String actualParam) {
        actualCmd = mTcpdumpConf.buildCmd(actualParam, isDumpingInFile, typeScan, hosts);
        this.actualParam = actualParam;
        IPTables.InterceptWithoutSSL();
        return this;
    }

    public String                   start(final WiresharkDispatcher trameDispatcher) {
        mDispatcher = trameDispatcher;
        isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArpSpoof.launchArpSpoof();
                    Log.i(TAG, actualCmd);
                    mTcpDumpProcess = new RootProcess("Wireshark").exec(actualCmd);
                    readTcpdump(mTcpDumpProcess.getReader());
                    Log.i(TAG, "Tcpdump execution over");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Process Error: " + e.getMessage());
                    mActivity.showSnackbar(e.getMessage(), ContextCompat.getColor(mActivity, R.color.stop_color));
                    mActivity.setToolbarTitle("Execution stopped", e.getMessage());
                    onTcpDumpStop();
                    Log.d(TAG, "Restarting ?");
                    if (e.getMessage().contains("read failed"))
                        start(trameDispatcher);
                } finally {
                    if (mTcpDumpProcess != null)
                        mTcpDumpProcess.closeProcess();
                    onNewLine("Quiting...");
                }
                Log.i(TAG, "End of Tcpdump thread");
            }
        }).start();
        return actualCmd.replace("nmap/nmap", "nmap")
                .replace(mSingleton.FilesPath, "");//trimmed cmd
    }

    private void                    readTcpdump(BufferedReader reader) throws IOException {
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
    }

    private void                    onNewLine(String line) {
        if (line.contains("Quiting...")) {
            Log.d(TAG, "Finishing Adapter trame");
            Trame trame = new Trame("Processus over", 0);
            trame.connectionOver = true;
            mDispatcher.addToQueue(trame);
            onTcpDumpStop();
            return;
        }
        Trame trame = new Trame(line, 0);
        if (trame.initialised) {
            mDispatcher.addToQueue(trame);
        } else if (!trame.skipped) {
            Log.d(TAG, "trame created not initialized and not skipped, STOP TCPDUMP");
            mActivity.onError(/*trame*/);
            onTcpDumpStop();
        }//else skipped
    }
    public void                     onTcpDumpStop() {
        if (isRunning) {
            ArpSpoof.stopArpSpoof();
            RootProcess.kill("tcpdump");
            isRunning = false;
            IPTables.stopIpTable();
            mDispatcher.stop();
            if (isDumpingInFile) {
                new RootProcess("chmod Pcap files")
                        .exec("chmod 666 " + mSingleton.PcapPath + "/*")
                        .exec("chown sdcard_r:sdcard_r " + mSingleton.PcapPath + "/*")
                        .closeProcess();
                //TODO: faire un ok qui t'amene vers le dossier
                mActivity.showSnackbar("Pcap saved here : " + mSingleton.PcapPath, -1);
            }
        }
    }

}
