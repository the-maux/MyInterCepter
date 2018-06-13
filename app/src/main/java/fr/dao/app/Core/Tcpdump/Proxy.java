package fr.dao.app.Core.Tcpdump;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Pattern;

import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Config.Action;
import fr.dao.app.Model.Net.HttpTrame;
import fr.dao.app.Model.Net.Trame;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.View.Sniff.HTTPDispatcher;
import fr.dao.app.View.Sniff.ProxyReaderFrgmnt;
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
    private HTTPDispatcher          mDispatcher = null;
    private ProxyReaderFrgmnt       mFragment = null;
    private ArrayList<Trame>        mBufferOfTrame = new ArrayList<>();

    private                         Proxy(ProxyReaderFrgmnt activity) {
        this.mActivity = (MyActivity) activity.getActivity();
        LinkedHashMap<String, String> mCmds = mProxyConf.initCmds();
    }

    public static synchronized Proxy getProxy(ProxyReaderFrgmnt fragment, boolean isProxyActivity) {
        if (isProxyActivity && mInstance == null) {
            mInstance = new Proxy(fragment);
            mInstance.mFragment = fragment;
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

    public void           start(final HTTPDispatcher trameDispatcher) {
        Log.i(TAG, "Proxy execution started");
        isPcapReading = false;
        mDispatcher = trameDispatcher;
        isRunning = true;
        Singleton.getInstance().Session.addAction(Action.ActionType.PROXY, true);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Log.i(TAG, actualCmd);
                    mTcpDumpProcess = new RootProcess("Proxy").exec(actualCmd);
                    mInstance.run(mTcpDumpProcess.getReader());
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
    }

    private void                    run(final BufferedReader reader) throws IOException {
        String tmp;
        final ArrayList<String> buffer = new ArrayList<>();
        while ((tmp = reader.readLine()) != null && isRunning) {
            final String line = tmp;
            buffer.add(tmp);
            if (Pattern.compile("/([0-9])\\w+:([0-9])\\w+:([0-9])\\w+.+([0-9])\\w+ +IP/g").matcher(line).find()) {
                new Thread(new Runnable() {
                    public void run() {
                        readAndAnalyse(new HttpTrame(buffer));
                    }
                }).start();
                buffer.clear();
            }
        }
        stop();
        Log.i(TAG, "Proxy execution over");
    }

    /**
     * Trame sended to mBufferOfTrame
     * TODO: big analyse with dump of each packets
     * @param trame
     */
    private void                    readAndAnalyse(HttpTrame trame) {
        Log.d(TAG, "readAndAnalyse[" + trame.getDump().length() + " charactere ]");
        mDispatcher.addToQueue(trame);
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
}
