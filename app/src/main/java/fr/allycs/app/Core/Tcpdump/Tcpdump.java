package fr.allycs.app.Core.Tcpdump;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import fr.allycs.app.View.Activity.Wireshark.WiresharkReaderFragment;
import fr.allycs.app.View.Behavior.WiresharkDispatcher;

public class                        Tcpdump {
    private String                  TAG = "Tcpdump";
    private static Tcpdump          mInstance = null;
    private Singleton               mSingleton = Singleton.getInstance();
    private LinkedHashMap<String, String> mCmds;
    private RootProcess mTcpDumpProcess;
    private WiresharkActivity       mActivity;
    private ConfTcpdump             mTcpdumpConf = new ConfTcpdump();
    public  boolean                 isRunning = false, isDumpingInFile = true, isPcapReading;
    private String                  actualParam = "", actualCmd = "";
    private WiresharkDispatcher     mDispatcher = null;
    private WiresharkReaderFragment mFragment = null;
    private ArrayList<Trame>        mBufferOfTrame = new ArrayList<>();

    private                         Tcpdump(WiresharkActivity activity) {
        this.mActivity = activity;
        mCmds = mTcpdumpConf.initCmds();
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
        return mInstance != null && mInstance.isRunning;
    }

    public Tcpdump                  initCmd(List<Host> hosts) {
        actualCmd = mTcpdumpConf.buildCmd(actualParam, isDumpingInFile, "No Filter", hosts);
        IPTables.InterceptWithoutSSL();
        return this;
    }

    public String                   start(final WiresharkDispatcher trameDispatcher) {
        isPcapReading = false;
        mDispatcher = trameDispatcher;
        isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ArpSpoof.launchArpSpoof();
                    Log.i(TAG, actualCmd);
                    mTcpDumpProcess = new RootProcess("Wireshark").exec(actualCmd);
                    execTcpDump(mTcpDumpProcess.getReader());
                    Log.i(TAG, "Tcpdump execution over");
                    onTcpDumpStop();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Process Error: " + e.getMessage());
                    //TODO: ask if he wants to retry sniff
                    mActivity.showSnackbar(e.getMessage(), ContextCompat.getColor(mActivity, R.color.stop_color));
                    mActivity.setToolbarTitle("Execution stopped", e.getMessage());
                    onTcpDumpStop();
                    Log.d(TAG, "Restarting ?");
                } finally {
                    if (mTcpDumpProcess != null)
                        mTcpDumpProcess.closeProcess();
                    readLineForLivePrint("Quiting...");
                }
                Log.i(TAG, "End of Tcpdump thread");
            }
        }).start();
        return actualCmd.replace("nmap/nmap", "nmap")
                .replace(mSingleton.FilesPath, "");//trimmed cmd
    }

    public void                     readPcap(File mPcapFile, WiresharkReaderFragment fragment) {
        isPcapReading = true;
        isRunning = true;
        actualCmd = mTcpdumpConf.buildCmd(mPcapFile);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mTcpDumpProcess = new RootProcess("Wireshark")
                            .exec(actualCmd);
                    execTcpDump(mTcpDumpProcess.getReader());
                    Log.i(TAG, "Tcpdump execution over");
                    onTcpDumpStop();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Process Error: " + e.getMessage());
                    //TODO: ask if he wants to retry sniff
                    mActivity.showSnackbar(e.getMessage(), ContextCompat.getColor(mActivity, R.color.stop_color));
                    mActivity.setToolbarTitle("Execution stopped", e.getMessage());
                    onTcpDumpStop();
                    Log.d(TAG, "Restarting ?");
                } finally {
                    if (mTcpDumpProcess != null)
                        mTcpDumpProcess.closeProcess();
                    readLineForLivePrint("Quiting...");
                }
                Log.i(TAG, "End of Tcpdump thread");
            }
        }).start();
    }

    private void                    execTcpDump(BufferedReader reader) throws IOException {
        String buffer;
        while ((buffer = reader.readLine()) != null) {
            final String line = buffer;
            new Thread(new Runnable() {
                public void run() {
                    if (isRunning) {
                        if (isPcapReading)
                            readAndAnalyse(line);
                        else
                            readLineForLivePrint(line);
                    }
                }
            }).start();
        }
    }

    private void                    readAndAnalyse(String line) {
        if (line.contains("Quiting...")) {
            Log.d(TAG, "Finishing Adapter trame");
            Trame trame = new Trame("Processus over");
            trame.connectionOver = true;
            mBufferOfTrame.add(trame);
            onTcpDumpStop();
            return;
        }
        Trame trame = new Trame(line);
        if (trame.initialised && !trame.skipped) {
            mBufferOfTrame.add(trame);
        } else if (!trame.skipped) {
            Log.d(TAG, "trame created not initialized and not skipped, STOP TCPDUMP");
            mActivity.onError(/*trame*/);
            onTcpDumpStop();
        }//else skipped

    }

    private void                    readLineForLivePrint(String line) {
        if (line.contains("Quiting...")) {
            Log.d(TAG, "Finishing Adapter trame");
            Trame trame = new Trame("Processus over");
            trame.connectionOver = true;
            mDispatcher.addToQueue(trame);
            onTcpDumpStop();
            return;
        }
        Trame trame = new Trame(line);
        if (trame.initialised && !trame.skipped) {
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
            mActivity.onTcpdumpstopped();
            RootProcess.kill("tcpdump");
            isRunning = false;
            IPTables.stopIpTable();
            mDispatcher.stop();
            if (isDumpingInFile && !isPcapReading) {
                new RootProcess("chmod Pcap files")
                        .exec("chmod 666 " + mSingleton.PcapPath + "/*")
                        .exec("chown sdcard_r:sdcard_r " + mSingleton.PcapPath + "/*")
                        .closeProcess();
                //TODO: faire un ok qui t'amene vers le dossier
                mActivity.showSnackbar("Pcap saved here : " + mSingleton.PcapPath, -1);
            }
            if (isPcapReading) {
                mFragment.onPcapAnalysed(mBufferOfTrame);
            }
        }
    }

}
