package fr.dao.app.Core.Tcpdump;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Config.Action;
import fr.dao.app.Model.Net.HttpTrame;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.View.Sniff.HTTPDispatcher;
import fr.dao.app.View.Proxy.ProxyReaderFrgmnt;
import fr.dao.app.View.ZViewController.Activity.MyActivity;


public class                        Proxy {
    private static String           TAG = "Proxy";
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
    private ArrayList<HttpTrame>    mBufferOfTrame = new ArrayList<>();

    private                         Proxy(ProxyReaderFrgmnt activity) {
        Log.d(TAG, "Constructor");
        this.mActivity = (MyActivity) activity.getActivity();
        mProxyConf.initCmds();
    }

    public static synchronized Proxy getProxy(ProxyReaderFrgmnt fragment, boolean isProxyActivity) {
        if (isProxyActivity && mInstance == null) {
            mInstance = new Proxy(fragment);
            mInstance.mFragment = fragment;
        }
        Log.d(TAG, "getProxy");
        return mInstance;
    }

    public static synchronized Proxy getProxy() {
        return mInstance;
    }

    public static synchronized boolean  isRunning() {
        Log.d(TAG, "isRunning:" + ((mInstance != null) && mInstance.isRunning));
        return mInstance != null && mInstance.isRunning;
    }

    public String                   initCmd(List<Host> hosts) {
        //sites.google.com/site/jimmyxu101/testing/use-tcpdump-to-monitor-http-traffic
        String actualParam = "-A -s 0 'tcp port 80 and (((ip[2:2] - ((ip[0]&0xf)<<2)) - ((tcp[12]&0xf0)>>2)) != 0) and ";
        actualCmd = mProxyConf.buildProxyCmd(actualParam, isDumpingInFile, "No Filter", hosts);
        return actualCmd.replace("nmap/nmap", "nmap")
                .replace(mSingleton.Settings.FilesPath, "");
    }

    public void                     start(final HTTPDispatcher trameDispatcher) {
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
                    Log.i(TAG, "End of Proxy thread");
                }
            }
        }).start();
    }

    private void                    run(final BufferedReader reader) throws IOException {
        String tmp;
        final ArrayList<String> buffer = new ArrayList<>();
        while ((tmp = reader.readLine()) != null && isRunning) {
            if (!tmp.contains("tcpdump: verbose ") && !tmp.contains("listening on ")) {
                if (!tmp.isEmpty())
                    buffer.add(tmp);
                else if (!buffer.isEmpty()) {
                        try {
                            Log.e(TAG, "Dump de la trame:");
                            for (String s : buffer) {
                                Log.e(TAG, "[" + s + "]");
                            }
                            //Not in thread cause .clear() is too fast for the thread
                            final HttpTrame trame = new HttpTrame(buffer);
                            new Thread(new Runnable() {
                                public void run() {
                                    readAndAnalyse(trame);
                                }
                            }).start();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.d(TAG, "Trame parsing error");
                        }
                        buffer.clear();
                    }
            } else
                Log.i(TAG, "Skipped[" + tmp + "]");
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
            if (mFragment != null)
                mFragment.onProxyStopped();
        }
    }

    public ArrayList<HttpTrame>     getActualTrameStack() {
        if (mDispatcher != null)
            return mDispatcher.getActualTrameStack();
        return new ArrayList<>();
    }
}


/**
 * listening on wlan0, link-type EN10MB (Ethernet), capture size 65535 bytes


22:12:25.147615 IP 192.168.0.24.33048 > 52.1.35.184.www: . 504:505(1) ack 1653 win 256
E..)..@.........4.#....P.....$DiP...B.........
22:12:25.147790 IP 192.168.0.24.33048 > 52.1.35.184.www: . 504:505(1) ack 1653 win 256
E..)..@.........4.#....P.....$DiP...B....
22:12:35.239490 IP 192.168.0.24.33048 > 52.1.35.184.www: . 504:505(1) ack 1653 win 256
E..)..@.........4.#....P.....$DiP...B.........
22:12:35.239861 IP 192.168.0.24.33048 > 52.1.35.184.www: . 504:505(1) ack 1653 win 256
E..)..@.........4.#....P.....$DiP...B....
22:12:45.422886 IP 192.168.0.24.33048 > 52.1.35.184.www: . 504:505(1) ack 1653 win 256
E..)..@.........4.#....P.....$DiP...B.........
22:12:45.423333 IP 192.168.0.24.33048 > 52.1.35.184.www: . 504:505(1) ack 1653 win 256
E..)..@.........4.#....P.....$DiP...B....
22:12:45.615902 IP 192.168.0.24.33048 > 52.1.35.184.www: P 505:931(426) ack 1653 win 256
E.....@....(....4.#....P.....$DiP...L...GET /bearer HTTP/1.1
Host: httpbin.org
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0
Accept: application/json
Accept-Language: en-US,en;q=0.5
Accept-Encoding: gzip, deflate
Referer: http://httpbin.org/
origin: http://httpbin.org
Cookie: _gauges_unique_day=1; _gauges_unique_month=1; _gauges_unique_year=1; _gauges_unique=1; _gauges_unique_hour=1
Connection: keep-alive


22:12:45.616113 IP 192.168.0.24.33048 > 52.1.35.184.www: P 505:931(426) ack 1653 win 256
E.....@....(....4.#....P.....$DiP...L...GET /bearer HTTP/1.1
Host: httpbin.org
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0
Accept: application/json
Accept-Language: en-US,en;q=0.5
Accept-Encoding: gzip, deflate
Referer: http://httpbin.org/
origin: http://httpbin.org
Cookie: _gauges_unique_day=1; _gauges_unique_month=1; _gauges_unique_year=1; _gauges_unique=1; _gauges_unique_hour=1
Connection: keep-alive


22:12:46.959007 IP 192.168.0.24.33048 > 52.1.35.184.www: P 931:1357(426) ack 1958 win 255
E.....@....&....4.#....P.....$E.P...I...GET /bearer HTTP/1.1
Host: httpbin.org
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0
Accept: application/json
Accept-Language: en-US,en;q=0.5
Accept-Encoding: gzip, deflate
Referer: http://httpbin.org/
origin: http://httpbin.org
Cookie: _gauges_unique_day=1; _gauges_unique_month=1; _gauges_unique_year=1; _gauges_unique=1; _gauges_unique_hour=1
Connection: keep-alive


22:12:46.959172 IP 192.168.0.24.33048 > 52.1.35.184.www: P 931:1357(426) ack 1958 win 255
E.....@....&....4.#....P.....$E.P...I...GET /bearer HTTP/1.1
Host: httpbin.org
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0
Accept: application/json
Accept-Language: en-US,en;q=0.5
Accept-Encoding: gzip, deflate
Referer: http://httpbin.org/
origin: http://httpbin.org
Cookie: _gauges_unique_day=1; _gauges_unique_month=1; _gauges_unique_year=1; _gauges_unique=1; _gauges_unique_hour=1
Connection: keep-alive


22:12:57.096499 IP 192.168.0.24.33048 > 52.1.35.184.www: . 1356:1357(1) ack 2263 win 254
E..)..@.........4.#....P...6.$F.P...<.........
22:12:57.096751 IP 192.168.0.24.33048 > 52.1.35.184.www: . 1356:1357(1) ack 2263 win 254
E..)..@.........4.#....P...6.$F.P...<....
22:13:07.235509 IP 192.168.0.24.33048 > 52.1.35.184.www: . 1356:1357(1) ack 2263 win 254
E..)..@.........4.#....P...6.$F.P...<.........
22:13:07.236173 IP 192.168.0.24.33048 > 52.1.35.184.www: . 1356:1357(1) ack 2263 win 254
E..)..@.........4.#....P...6.$F.P...<....
22:13:13.275944 IP 192.168.0.24.33048 > 52.1.35.184.www: P 1357:1810(453) ack 2263 win 254
E.....@.........4.#....P...7.$F.P...p...GET /basic-auth/LOGIN_ICI/PASSWORD_ICI HTTP/1.1
Host: httpbin.org
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0
Accept: application/json
Accept-Language: en-US,en;q=0.5
Accept-Encoding: gzip, deflate
Referer: http://httpbin.org/
origin: http://httpbin.org
Cookie: _gauges_unique_day=1; _gauges_unique_month=1; _gauges_unique_year=1; _gauges_unique=1; _gauges_unique_hour=1
Connection: keep-alive


22:13:13.276161 IP 192.168.0.24.33048 > 52.1.35.184.www: P 1357:1810(453) ack 2263 win 254
E.....@.........4.#....P...7.$F.P...p...GET /basic-auth/LOGIN_ICI/PASSWORD_ICI HTTP/1.1
Host: httpbin.org
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0
Accept: application/json
Accept-Language: en-US,en;q=0.5
Accept-Encoding: gzip, deflate
Referer: http://httpbin.org/
origin: http://httpbin.org
Cookie: _gauges_unique_day=1; _gauges_unique_month=1; _gauges_unique_year=1; _gauges_unique=1; _gauges_unique_hour=1
Connection: keep-alive


22:13:23.414540 IP 192.168.0.24.33048 > 52.1.35.184.www: . 1809:1810(1) ack 2546 win 253
E..)..@.........4.#....P.....$G.P...:.........
22:13:23.415130 IP 192.168.0.24.33048 > 52.1.35.184.www: . 1809:1810(1) ack 2546 win 253
E..)..@.........4.#....P.....$G.P...:....
22:13:24.744980 IP 192.168.0.24.33048 > 52.1.35.184.www: P 1810:2326(516) ack 2546 win 253
E..,..@.........4.#....P.....$G.P....}..GET /basic-auth/LOGIN_ICI/PASSWORD_ICI HTTP/1.1
Host: httpbin.org
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0
Accept: application/json
Accept-Language: en-US,en;q=0.5
Accept-Encoding: gzip, deflate
Referer: http://httpbin.org/
origin: http://httpbin.org
Cookie: _gauges_unique_day=1; _gauges_unique_month=1; _gauges_unique_year=1; _gauges_unique=1; _gauges_unique_hour=1
Connection: keep-alive
Authorization: Basic TU9OIExPR0lOIElDSTpNT04gUEFTU1dPUkQgSUNJ


22:13:24.745416 IP 192.168.0.24.33048 > 52.1.35.184.www: P 1810:2326(516) ack 2546 win 253
E..,..@.........4.#....P.....$G.P....}..GET /basic-auth/LOGIN_ICI/PASSWORD_ICI HTTP/1.1
Host: httpbin.org
User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:60.0) Gecko/20100101 Firefox/60.0
Accept: application/json
Accept-Language: en-US,en;q=0.5
Accept-Encoding: gzip, deflate
Referer: http://httpbin.org/
origin: http://httpbin.org
Cookie: _gauges_unique_day=1; _gauges_unique_month=1; _gauges_unique_year=1; _gauges_unique=1; _gauges_unique_hour=1
Connection: keep-alive
Authorization: Basic TU9OIExPR0lOIElDSTpNT04gUEFTU1dPUkQgSUNJ


^C
22 packets captured
22 packets received by filter
0 packets dropped by kernel
 * 
 **/