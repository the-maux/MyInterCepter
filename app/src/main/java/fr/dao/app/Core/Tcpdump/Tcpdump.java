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
import fr.dao.app.Model.Config.Action;
import fr.dao.app.Model.Net.Trame;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.Sniff.SniffActivity;
import fr.dao.app.View.Sniff.SniffDispatcher;
import fr.dao.app.View.Sniff.SniffReaderFrgmnt;

public class                        Tcpdump {
    private String                  TAG = "Tcpdump";
    private static Tcpdump          mInstance = null;
    private RootProcess             mTcpDumpProcess;
    private Singleton               mSingleton = Singleton.getInstance();
    private SniffActivity           mActivity;
    private ConfTcpdump             mTcpdumpConf = new ConfTcpdump();
    private boolean                 isRunning = false;
    public  boolean                 isDumpingInFile = true, isPcapReading = false;
    private String                  actualCmd = "";
    private SniffDispatcher         mDispatcher = null;
    private SniffReaderFrgmnt       mFragment = null;
    private ArrayList<Trame>        mBufferOfTrame = new ArrayList<>();

    private                         Tcpdump(SniffActivity activity) {
        this.mActivity = activity;
        LinkedHashMap<String, String> mCmds = mTcpdumpConf.initCmds();
    }

    public static synchronized Tcpdump getTcpdump(Activity activity, boolean isWiresharkActivity) {
        if (isWiresharkActivity) {
            if (mInstance == null) {
                mInstance = new Tcpdump((SniffActivity) activity);
            }
        }
        return mInstance;
    }

    public static synchronized Tcpdump getTcpdump() {
        return mInstance;
    }

    public static synchronized boolean  isRunning() {
        return mInstance != null && mInstance.isRunning;
    }

    public String                   initCmd(List<Host> hosts) {
        int a = IPTables.startForwardingStream();
        Log.d(TAG, "IPtable returned: " + a);
        ArpSpoof.launchArpSpoof(hosts);
        String actualParam = "";
        actualCmd = mTcpdumpConf.buildCmd(actualParam, isDumpingInFile, "No Filter", hosts);
        return actualCmd.replace("nmap/nmap", "nmap")
                .replace(mSingleton.Settings.FilesPath, "");
    }

    public DashboardSniff           start(final SniffDispatcher trameDispatcher) {
        isPcapReading = false;
        mDispatcher = trameDispatcher;
        isRunning = true;
        final DashboardSniff dashboardSniff = new DashboardSniff();
        mDispatcher.setDashboard(dashboardSniff);
        Singleton.getInstance().Session.addAction(Action.ActionType.SNIFF, true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.i(TAG, actualCmd);
                    mTcpDumpProcess = new RootProcess("Wireshark").exec(actualCmd);
                    Tcpdump.this.run(mTcpDumpProcess.getReader(), dashboardSniff);
                    Log.i(TAG, "Tcpdump execution over");
                    stop();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Process Error: " + e.getMessage());
                    mActivity.showSnackbar(e.getMessage(), ContextCompat.getColor(mActivity, R.color.stop_color));
                    mActivity.setToolbarTitle("Execution stopped", e.getMessage());
                    stop();
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

    public void                     readPcap(File pcapFile, SniffReaderFrgmnt fragment) {
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
                    mInstance.run(mTcpDumpProcess.getReader(), null);
                    Log.i(TAG, "Tcpdump execution over");
                    stop();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Process Error: " + e.getMessage());
                    mActivity.showSnackbar(e.getMessage(), ContextCompat.getColor(mActivity, R.color.stop_color));
                    mActivity.setToolbarTitle("Execution stopped", e.getMessage());
                    stop();
                    Log.d(TAG, "Restarting ?");
                } finally {
                    if (mTcpDumpProcess != null)
                        mTcpDumpProcess.closeProcess();
                }
                Log.i(TAG, "End of Tcpdump thread");
            }
        }).start();
    }

    private void                    run(final BufferedReader reader, final DashboardSniff dashboardSniff) throws IOException {
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
            stop();
            return;
        }
        Trame trame = new Trame(line);
        if (trame.initialised && !trame.skipped) {
            mBufferOfTrame.add(trame);
        } else if (!trame.skipped) {
            Log.d(TAG, "trame created not initialized and not skipped, STOP TCPDUMP");
            mActivity.onError(/*trame*/);
            stop();
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
            stop();
            return;
        }
        Trame trame = new Trame(line);
        if (trame.initialised && !trame.skipped) {
            mDispatcher.addToQueue(trame);
        } else if (!trame.skipped) {
            Log.d(TAG, "trame created not initialized and not skipped, STOP TCPDUMP");
            mActivity.onError(/*trame*/);
            stop();
        }//else skipped
    }

    public void                     stop() {
        Log.d(TAG, "stop");
        if (isRunning) {
            ArpSpoof.stopArpSpoof();
            mActivity.onTcpdumpstopped();
            RootProcess.kill("tcpdump");
            isRunning = false;
            IPTables.stopIpTable();
            if (isDumpingInFile && !isPcapReading) {
                Log.d(TAG, "allow right on pcap's dump directory");
                new RootProcess("chmod Pcap files")
                        .exec("chmod 666 " + mSingleton.Settings.PcapPath + "/*")
                        .exec("chown sdcard_r:sdcard_r " + mSingleton.Settings.PcapPath + "/*")
                        .closeProcess();
                //TODO: faire un ok qui t'amene vers le dossier
                mActivity.showSnackbar("Pcap saved here : " + mSingleton.Settings.PcapPath, -1);
            }
            if (isPcapReading) {
                Log.d(TAG, "mFragment.onPcapAnalysed(mBufferOfTrame), fragment => " + mFragment.getClass().getName());
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
