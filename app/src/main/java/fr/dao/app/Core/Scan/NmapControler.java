package fr.dao.app.Core.Scan;

import android.content.Context;
import android.util.Log;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Network.Discovery.NetworkDiscoveryControler;
import fr.dao.app.Model.Config.NmapParam;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.View.Scan.NmapOutputView;


public class                        NmapControler {
    private String                  TAG = "NmapControler";
    private NmapControler           mInstance = this;
    private Singleton               mSingleton = Singleton.getInstance();
    private String                  PATH_NMAP = mSingleton.Settings.FilesPath + "nmap/nmap ";
    private boolean                 mIsLiveDump, isRunning;
    private NetworkDiscoveryControler mNnetworkDiscoveryControler;
    private String                  mActualItemMenu = "Ping scan";//Default
    private List<Host>              mHost = null;
    private Date                    startParsing;
    private NmapParam               mParams = NmapParam.getInstance();

    /*
        **   HostDiscoveryActivity
    * --Script =
     *              nbstat => U:137
     *              dns-service-discovery => U:5353
     *              upnp-info => U:1900
     *              Windows check => T:135 https://nmap.org/nsedoc/scripts/msrpc-enum.html msrpc
     *                            => T:445 microsoft-ds
    *
    */
    public                          NmapControler(ArrayList<Host> hosts, NetworkDiscoveryControler networkDiscoveryControler,
                                                  Network ap, Context context) {/* Parsing mode */
        String NMAP_ARG_SCAN = " -PN -sS -T3 -sU " +
                "--script nbstat.nse,dns-service-discovery,upnp-info " +
                "--min-parallelism 100 " +
                "-p T:21,T:22,T:23,T:25,T:80,T:110,T:111,T:135,T:139,T:3128,T:443,T:445,T:2049,T:2869," +
                "U:53,U:1900,U:3031,U:5353  ";
        mIsLiveDump = false;
        mNnetworkDiscoveryControler = networkDiscoveryControler;
        mActualItemMenu = "Basic Host discovery";
        StringBuilder hostCmd = new StringBuilder("");
        for (Host host : hosts) {
            if (mSingleton.Settings.getUserPreferences().NmapMode > host.Deepest_Scan)//To not scan again automaticaly already scanned host
                hostCmd.append(" ").append(host.ip);
        }
        String cmd = PATH_NMAP + NMAP_ARG_SCAN + hostCmd.toString();
        Log.d(TAG, "CMD:["+ cmd + "]");
        setTitleToolbar(null, "Scanning " + hostCmd.toString().split(" ").length + " devices");
        hostDiscoveryFromNmap(cmd, hosts, ap, context);
        mSingleton.actualNetwork.offensifAction = mSingleton.actualNetwork.offensifAction + 1;
        mSingleton.actualNetwork.save();
    }

    /*
        **   NmapActivity
    */
    public                          NmapControler(boolean execureAllCommandTogether) {/*Live mode*/
        mIsLiveDump = true;
    }

    private void                    hostDiscoveryFromNmap(final String cmd, final ArrayList<Host> hosts, final Network ap, final Context context) {
        mSingleton.actualNetwork.defensifAction = mSingleton.actualNetwork.defensifAction + 1;
        mSingleton.actualNetwork.save();
        new Thread(new Runnable() {
            public void run() {
                try {
                    String tmp;
                    StringBuilder outputBuilder = new StringBuilder();
                    BufferedReader reader = new RootProcess("Nmap", mSingleton.Settings.FilesPath)
                            .exec(cmd).getReader();
                    while ((tmp = reader.readLine()) != null && !tmp.startsWith("Nmap done")) {
                        outputBuilder.append(tmp).append('\n');
                    }
                    /*
                     * Hello dear, If you're here,
                     * Trying to understand why the condition is
                     * outputBuilder.toString().isEmpty()
                     * I love you, thank you, for existing
                     * You're not alone, we are connected
                     */
                    if (outputBuilder.toString().isEmpty() || tmp.isEmpty() || !tmp.startsWith("Nmap done")) {
                        Log.d(TAG, "Error in nmap execution, Nmap didn't end");
                        outputBuilder.append(tmp);
                        Log.e(TAG, outputBuilder.toString());
                        setTitleToolbar("Network scan", "Nmap Error");
                        return;
                    }
                    outputBuilder.append(tmp);
                    String FullDUMP = outputBuilder.toString().substring(1);
                    Log.d(TAG, "\t\t LastLine[" + tmp + "]");
                    startParsing = Calendar.getInstance().getTime();
                    new NmapHostDiscoveryParser(mInstance, hosts, FullDUMP, ap, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String                  buildCommand() {
        StringBuilder res = new StringBuilder("");
        if (mIsLiveDump) {
            for (Host host : mHost) {
                res.append(host.ip).append(" ");
            }
        }
        String hostFilter = res.toString();
        String parameter = getParamOfScan(mActualItemMenu);
        String cmd = PATH_NMAP + parameter + " " + hostFilter + " -d";
        return cmd.replace("  ", " ").replace("\n", "");
    }

    private String                  build(NmapOutputView nmapOutputFragment) {
        String cmd = buildCommand();
        Log.i(TAG, cmd);
        nmapOutputFragment.printCmdInTerminal(cmd
                .replace("nmap/nmap", "nmap")
                .replace(mSingleton.Settings.FilesPath, ""));
        return cmd;
    }

    public void                     start(final NmapOutputView nmapOutputFragment, final ProgressBar progressBar) {
        if (mHost == null) {
            Log.e(TAG, "No client selected when launched");
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String tmp;
                        StringBuilder dumpOutputBuilder = new StringBuilder();
                        BufferedReader reader = new RootProcess("Nmap", mSingleton.Settings.FilesPath)
                                .exec(build(nmapOutputFragment)).getReader();
                        while ((tmp = reader.readLine()) != null && !tmp.contains("Nmap done")) {
                            if (!tmp.isEmpty()) {
                                if (tmp.charAt(0) == '\n')
                                    tmp = tmp.substring(1);
                                nmapOutputFragment.flushOutput(tmp + '\n', null);
                            }
                        }
                        dumpOutputBuilder.append(tmp);
                        Log.d(TAG, "Nmap final dump:" + dumpOutputBuilder.toString());
                        nmapOutputFragment.flushOutput(tmp + '\n', progressBar);
                    } catch (IOException e) {
                        e.printStackTrace();
                        nmapOutputFragment.flushOutput(null, progressBar);
                    }
                }
            }).start();
        }
    }

    public void                     onHostActualized(ArrayList<Host> hosts) {
        Log.d(TAG, "All node was parsed in :" + Utils.TimeDifference(startParsing));
        mNnetworkDiscoveryControler.onScanFinished(hosts);
    }

    public void                     setmActualItemMenu(String itemMenu) {
        this.mActualItemMenu = itemMenu;
    }

    public void                     setHosts(List<Host> hosts) {
        this.mHost = hosts;
    }

    public void                     setTitleToolbar(String title, String subtitle) {
        mNnetworkDiscoveryControler.setToolbarTitle(title, subtitle);
    }

    public String                   getActualCmd() {
        return mActualItemMenu;
    }

    public ArrayList<String>        getMenuCommmands() {
        return mParams.getmMenuCmd();
    }

    public String                   getNameOfScan(int offset) {
        return mParams.getNameTypeScan(offset);
    }

    public String                   getParamOfScan(String itemMenu) {
        return mParams.getParamTypeScan(itemMenu);
    }
}
