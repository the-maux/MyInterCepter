package fr.dao.app.Core.Tcpdump;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Network.ArpSpoof;
import fr.dao.app.Core.Network.IPTables;
import fr.dao.app.Model.Net.Trame;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.Activity.Wireshark.WiresharkActivity;
import fr.dao.app.View.Activity.Wireshark.WiresharkReaderFragment;
import fr.dao.app.View.Behavior.WiresharkDispatcher;

public class                        Tcpdump {
    private String                  TAG = "Tcpdump";
    private static Tcpdump          mInstance = null;
    private RootProcess             mTcpDumpProcess;
    private Singleton               mSingleton = Singleton.getInstance();
    private WiresharkActivity       mActivity;
    private ConfTcpdump             mTcpdumpConf = new ConfTcpdump();
    private boolean                 isRunning = false;
    public  boolean                 isDumpingInFile = true, isPcapReading;
    private String                  actualCmd = "";
    private WiresharkDispatcher     mDispatcher = null;
    private WiresharkReaderFragment mFragment = null;
    private ArrayList<Trame>        mBufferOfTrame = new ArrayList<>();

    private                         Tcpdump(WiresharkActivity activity) {
        this.mActivity = activity;
        LinkedHashMap<String, String> mCmds = mTcpdumpConf.initCmds();
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

    public String                   initCmd(List<Host> hosts) {
        int a = IPTables.InterceptWithoutSSL();
        Log.d(TAG, "IPtable returned: " + a);
        ArpSpoof.launchArpSpoof(hosts);
        String actualParam = "";
        actualCmd = mTcpdumpConf.buildCmd(actualParam, isDumpingInFile, "No Filter", hosts);
        return actualCmd.replace("nmap/nmap", "nmap")
                .replace(mSingleton.FilesPath, "");
    }

    public DashboardSniff           start(final WiresharkDispatcher trameDispatcher) {
        isPcapReading = false;
        mDispatcher = trameDispatcher;
        isRunning = true;
        final DashboardSniff dashboardSniff = new DashboardSniff();
        mDispatcher.setDashboard(dashboardSniff);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, actualCmd);
                    mTcpDumpProcess = new RootProcess("Wireshark").exec(actualCmd);
                    execTcpDump(mTcpDumpProcess.getReader(), dashboardSniff);
                    Log.i(TAG, "Tcpdump execution over");
                    onTcpDumpStop();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Process Error: " + e.getMessage());
                    mActivity.showSnackbar(e.getMessage(), ContextCompat.getColor(mActivity, R.color.stop_color));
                    mActivity.setToolbarTitle("Execution stopped", e.getMessage());
                    onTcpDumpStop();
                    Log.d(TAG, "Restarting ?");
                } finally {
                    if (mTcpDumpProcess != null)
                        mTcpDumpProcess.closeProcess();
                    readLineForLivePrint("Quiting...", dashboardSniff);
                }
                Log.i(TAG, "End of Tcpdump thread");
            }
        }).start();
        return dashboardSniff;
    }

    public void                     readPcap(File pcapFile, WiresharkReaderFragment fragment) {
        isPcapReading = true;
        isRunning = true;
        mFragment = fragment;
        Log.d(TAG, "reading Pcap:" + pcapFile.getPath());
        actualCmd = mTcpdumpConf.buildCmd(pcapFile);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mTcpDumpProcess = new RootProcess("Wireshark")
                            .exec(actualCmd);
                    execTcpDump(mTcpDumpProcess.getReader(), null);
                    Log.i(TAG, "Tcpdump execution over");
                    onTcpDumpStop();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Process Error: " + e.getMessage());
                    mActivity.showSnackbar(e.getMessage(), ContextCompat.getColor(mActivity, R.color.stop_color));
                    mActivity.setToolbarTitle("Execution stopped", e.getMessage());
                    onTcpDumpStop();
                    Log.d(TAG, "Restarting ?");
                } finally {
                    if (mTcpDumpProcess != null)
                        mTcpDumpProcess.closeProcess();
                }
                Log.i(TAG, "End of Tcpdump thread");
            }
        }).start();
    }

    private void                    execTcpDump(final BufferedReader reader, final DashboardSniff dashboardSniff) throws IOException {
        String buffer;
        while ((buffer = reader.readLine()) != null) {
            final String line = buffer;
            new Thread(new Runnable() {
                public void run() {
                    if (isRunning) {
                        if (isPcapReading) {
                            readAndAnalyse(line);
                            mFragment.loadingMonitor();
                        } else
                            readLineForLivePrint(line, dashboardSniff);
                    }
                }
            }).start();
        }
    }

    /**
     * Trame sended to mBufferOfTrame
     * TODO: big analyse with dump of each packets
     */
    private void                    readAndAnalyse(String line) {
        Log.d(TAG, "readAndAnalyse[" + line + "]");
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

    /**
     * Trame sended to trame dispatcher
     */
    private void                    readLineForLivePrint(String line, DashboardSniff dashboardSniff) {
        Log.d(TAG, "readLineForLivePrint::" + line);
        if (line.contains("Quiting...")) {
            Log.d(TAG, "Finishing Adapter trame");
            Trame trame = new Trame("Processus over");
            trame.connectionOver = true;
            mDispatcher.addToQueue(trame);
            dashboardSniff.stop();
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
            } else
                mDispatcher.stop();
        }
    }

    public void                     flushToAdapter() {
        Log.d(TAG, "flushToAdapter");
        if (mDispatcher != null)
            mDispatcher.flush();
    }

    public void                     switchOutputType(boolean isDashboard) {
        if (mDispatcher != null) {
            mDispatcher.switchOutputType(isDashboard);
        }
    }
}
