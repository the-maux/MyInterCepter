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
    private NmapParam               mParams = NmapParam.getInstance();
    private NetworkDiscoveryControler mNnetworkDiscoveryControler;
    private Date                    startParsing;
    private boolean                 mIsLiveDump;
    private List<Host>              mHost = null;
    private RootProcess             process = new RootProcess("Nmap", mSingleton.Settings.FilesPath);
    private String                  mActualScan = "Ping scan", mActualScript = "Heartbleed check";//Default


    public                          NmapControler(Host host, int port, String script) {/*Script Vulnerability*/
        mSingleton.actualNetwork.offensifAction = mSingleton.actualNetwork.offensifAction + 1;
        mSingleton.actualNetwork.save();

    }

    public                          NmapControler(final VulnScanner scanner, final Host host) {  /*Analyze before Scan vulns*/
        new Thread(new Runnable() {
            public void run() {
                try {
                    String tmp;
                    String cmd = PATH_NMAP + mParams.getHostQuickDiscoverArgs() + host.ip;
                    StringBuilder outputBuilder = new StringBuilder();
                    BufferedReader reader = process.exec(cmd).getReader();
                    while ((tmp = reader.readLine()) != null && !tmp.startsWith("Nmap done")) {
                        outputBuilder.append(tmp).append('\n');
                    }
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
                    //TODO: Need to update the host.Port() !!

                    NmapHostDiscoveryParser.getPortList(FullDUMP.split("\n"), 0)


                    scanner.onHostPortsActualized(FullDUMP);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*
        **   NmapActivity
    */
    public                          NmapControler() {/*Live mode*/
        //boolean execureAllCommandTogether This need to be in Settings
        mSingleton.actualNetwork.offensifAction = mSingleton.actualNetwork.offensifAction + 1;
        mSingleton.actualNetwork.save();
        mIsLiveDump = true;
    }

    /*
     **   HostDiscoveryActivity
     */
    public                          NmapControler(Network ap, NetworkDiscoveryControler discoveryControler,
                                                  Context context) {/* Parsing mode */
        mSingleton.actualNetwork.offensifAction = mSingleton.actualNetwork.offensifAction + 1;
        mSingleton.actualNetwork.save();
        mIsLiveDump = false;
        mNnetworkDiscoveryControler = discoveryControler;
        String hostCmd = NmapParam.buildHostCmdArgs(ap.listDevices());
        Log.d(TAG, "CMD:["+ PATH_NMAP + mParams.getHostQuickDiscoverArgs() + hostCmd + "]");
        setTitleToolbar(null, "Scanning " + hostCmd.split(" ").length + " devices");
        hostDiscoveryFromNmap( context, PATH_NMAP + mParams.getHostQuickDiscoverArgs() + hostCmd, ap);
    }

    private void                    hostDiscoveryFromNmap(final Context context, final String cmd, final Network ap) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    mSingleton.actualNetwork.defensifAction = mSingleton.actualNetwork.defensifAction + 1;
                    mSingleton.actualNetwork.save();
                    String tmp;
                    StringBuilder outputBuilder = new StringBuilder();
                    BufferedReader reader = process.exec(cmd).getReader();
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
                    new NmapHostDiscoveryParser(mInstance, FullDUMP, ap, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private String                  build(NmapOutputView nmapOutputFragment) {
        StringBuilder res = new StringBuilder("");
        if (mIsLiveDump) {
            for (Host host : mHost) {
                res.append(host.ip).append(" ");
            }
        }
        String hostFilter = res.toString();
        String parameter = getParamOfScan(mActualScan);
        String cmd = PATH_NMAP + parameter + " " + hostFilter + " -d";
        cmd = cmd.replace("  ", " ").replace("\n", "");
        Log.i(TAG, cmd);
        nmapOutputFragment.printCmdInTerminal(cmd
                .replace("nmap/nmap", "nmap")
                .replace(mSingleton.Settings.FilesPath, ""));
        return cmd;
    }

    public boolean                  startScan(final NmapOutputView nmapOutputFragment, final ProgressBar progressBar) {
        if (mHost != null)
            new Thread(new Runnable() {
                public void run() {
                    try {
                        String tmp;
                        StringBuilder dumpOutputBuilder = new StringBuilder();
                        BufferedReader reader = process.exec(build(nmapOutputFragment)).getReader();
                        while ((tmp = reader.readLine()) != null && !tmp.contains("Nmap done")) {
                            if (!tmp.isEmpty()) {
                                if (tmp.charAt(0) == '\n')
                                    tmp = tmp.substring(1);
                                nmapOutputFragment.flushOutput(tmp + '\n', null);
                                dumpOutputBuilder.append(tmp).append('\n');
                            }
                        }
                        Log.d(TAG, "Nmap final dump:" + dumpOutputBuilder.toString());
                        nmapOutputFragment.flushOutput(dumpOutputBuilder.toString(), progressBar);
                    } catch (IOException e) {
                        e.printStackTrace();
                        nmapOutputFragment.flushOutput(null, progressBar);
                    }
                }
            }).start();
        else {
            Log.e(TAG, "No client selected when launched");
            return false;
        }
        return true;
    }

    public void                     onHostActualized(ArrayList<Host> hosts) {
        Log.d(TAG, "All node was parsed in :" + Utils.TimeDifference(startParsing));
        if (mNnetworkDiscoveryControler != null)
            mNnetworkDiscoveryControler.onScanFinished(hosts);
        else
            Log.e(TAG, "onHostActualized but networkDiscoveryControler is null ");
    }

    public void                     setHosts(List<Host> hosts) {
        this.mHost = hosts;
    }

    public void                     setTitleToolbar(String title, String subtitle) {
        if (mNnetworkDiscoveryControler != null)
            mNnetworkDiscoveryControler.setToolbarTitle(title, subtitle);
        else
            Log.e(TAG, "setting title toolbar but networkDiscoveryControler is null ");
    }

    public void                     setmActualScan(String itemMenu) {
        this.mActualScan = itemMenu;
    }
    public String                   getActualCmd() {
        return mActualScan;
    }
    public ArrayList<String>        getMenuCommmands() {
        return mParams.getmMenuCmd();
    }
    public ArrayList<String>        getMenuScripts() {
        return mParams.getmMenuCmd();
    }
    public String                   getNameOfScan(int offset) {
        return mParams.getNameTypeScan(offset);
    }
    public String                   getParamOfScan(String itemMenu) {
        return mParams.getParamTypeScan(itemMenu);
    }
    public String                   getActualScript() {
        return mActualScript;
    }
}
