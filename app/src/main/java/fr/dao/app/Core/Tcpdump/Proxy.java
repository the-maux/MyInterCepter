package fr.dao.app.Core.Tcpdump;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import fr.dao.app.Core.Configuration.MitManager;
import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Config.Action;
import fr.dao.app.Model.Net.Trame;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.Proxy.ProxyActivity;
import fr.dao.app.View.Sniff.ProxyReaderFrgmnt;
import fr.dao.app.View.Sniff.SniffDispatcher;
import fr.dao.app.View.ZViewController.Activity.MyActivity;


public class                        Proxy {
    private String                  TAG = "Proxy";
    private static Proxy            mInstance = null;
    private RootProcess             mTcpDumpProcess;
    private Singleton               mSingleton = Singleton.getInstance();
    private MyActivity              mActivity;
    private ConfTcpdump             mProxyConf = new ConfTcpdump();
    private boolean                 isRunning = false;
    public  boolean                 isDumpingInFile = true, isPcapReading = false;
    private String                  actualCmd = "";
    private SniffDispatcher         mDispatcher = null;
    private ProxyReaderFrgmnt       mFragment = null;
    private ArrayList<Trame>        mBufferOfTrame = new ArrayList<>();

    private                         Proxy(ProxyReaderFrgmnt activity) {
        this.mActivity = (MyActivity) activity.getActivity();
        LinkedHashMap<String, String> mCmds = mProxyConf.initCmds();
    }

    public static synchronized Proxy getProxy(ProxyReaderFrgmnt fragment, boolean isProxyActivity) {
        if (isProxyActivity && mInstance == null) {
            mInstance = new Proxy(fragment);
        }
        return mInstance;
    }

    public static synchronized Proxy getProxy() {
        return mInstance;
    }

    public static synchronized boolean  isRunning() {
        if (mInstance == null)
            return false;
        return mInstance.isRunning;
    }

    public String                   initCmd(List<Host> hosts) {
        //sites.google.com/site/jimmyxu101/testing/use-tcpdump-to-monitor-http-traffic
        String actualParam = "-A -s 0 'tcp port 80 and (((ip[2:2] - ((ip[0]&0xf)<<2)) - ((tcp[12]&0xf0)>>2)) != 0) and ";
        actualCmd = mProxyConf.buildProxyCmd(actualParam, isDumpingInFile, "No Filter", hosts);
        return actualCmd.replace("nmap/nmap", "nmap")
                .replace(mSingleton.Settings.FilesPath, "");
    }

    public DashboardSniff           start(final SniffDispatcher trameDispatcher) {
        Log.i(TAG, "Proxy execution started");
        isPcapReading = false;
        mDispatcher = trameDispatcher;
        isRunning = true;
        final DashboardSniff dashboardSniff = new DashboardSniff();
        mDispatcher.setDashboard(dashboardSniff);
        Singleton.getInstance().Session.addAction(Action.ActionType.PROXY, true);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Log.i(TAG, actualCmd);
                    mTcpDumpProcess = new RootProcess("Proxy").exec(actualCmd);
                    mInstance.run(mTcpDumpProcess.getReader(), dashboardSniff);
                    Log.i(TAG, "Proxy execution over");
                    stop();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Process Error: " + e.getMessage());
                    mActivity.showSnackbar(e.getMessage()/*, ContextCompat.getColor(mActivity, R.color.stop_color)*/);
                    mActivity.setToolbarTitle("Execution stopped", e.getMessage());
                    stop();
                } finally {
                    if (mTcpDumpProcess != null)
                        mTcpDumpProcess.closeProcess();
                }
                Log.i(TAG, "End of Proxy thread");
            }
        }).start();
        return dashboardSniff;
    }

    private void                    run(final BufferedReader reader, final DashboardSniff dashboardSniff) throws IOException {
        String buffer;
        while ((buffer = reader.readLine()) != null) {
            final String line = buffer;
            new Thread(new Runnable() {
                public void run() {
                    if (isRunning) {
                        readAndAnalyse(line);
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
            mFragment.onError(/*trame*/);
            stop();
        }//else skipped
    }

    public void                     stop() {
        Log.d(TAG, "stop");
        if (isRunning) {
            isRunning = false;
            if (mActivity != null)
                mFragment.onProxystopped();
            if (mFragment != null)
                mFragment.onSniffingOver(mBufferOfTrame);
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
