package fr.allycs.app.Controller.Core.Tools.Nmap;

import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Tools.RootProcess;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.View.Scan.NmapActivity;

public class                        NmapControler {
    private String                  TAG = "NmapControler";
    private NmapControler           mInstance = this;
    private Map<String, String>     mNmapParams =  new HashMap<>();
    private ArrayList<String>       mMenuCommand = new ArrayList<>();
    private Singleton               mSingleton = Singleton.getInstance();
    private List<Host>              mHost = null;
    private boolean                 mOneByOnExecution = false;
    private String                  actualItemMenu = "Ping scan";//Default
    private NmapParser              mNmapParser = null;

    public NmapControler() {
        initMenu();
    }

    private void                    initMenu() {
        mMenuCommand.add("Ping scan");
        mNmapParams.put(mMenuCommand.get(0), " -sn");
        mMenuCommand.add("Quick scan");
        mNmapParams.put(mMenuCommand.get(1), " -T4 -F");
        mMenuCommand.add("Quick scan plus");
        mNmapParams.put(mMenuCommand.get(2), " -sV -T4 -O -F --version-light");
        mMenuCommand.add("Quick traceroute");
        mNmapParams.put(mMenuCommand.get(3), " -sn --traceroute");
        mMenuCommand.add("Regular scan");
        mNmapParams.put(mMenuCommand.get(4), " ");
        mMenuCommand.add("Intrusive scan");
        mNmapParams.put(mMenuCommand.get(5), " -sS -sU -T4 -A -v -PE -PP -PS80,443 -PA3389 -PU40125 -PY -g 53 ");
        mMenuCommand.add("Intense Scan");
        mNmapParams.put(mMenuCommand.get(6), " -T4 -A -v");
        mMenuCommand.add("Intense scan plus UDP");
        mNmapParams.put(mMenuCommand.get(7), " -sS -sU -T4 -A -v");
        mMenuCommand.add("Intense scan, all TCP ports");
        mNmapParams.put(mMenuCommand.get(8), " -p 1-65535 -T4 -A -v");
        mMenuCommand.add("Intense scan, no ping");
        mNmapParams.put(mMenuCommand.get(9), " -T4 -A -v -Pn");
    }

    public ArrayList<String>        getMenuCommmands() {
        return mMenuCommand;
    }


    public String                   getNmapParamFromMenuItem(String itemMenu) {
        return mNmapParams.get(itemMenu);
    }

    public void                     setActualItemMenu(String itemMenu) {
        this.actualItemMenu = itemMenu;
    }

    private String                  buildHostFilterCommand() {
        StringBuilder res = new StringBuilder("");
        for (Host host : mHost) {
            res.append(host.ip).append("\n");
        }
        return res.toString();
    }

    private String                  buildCommand() {
        String Binary = mSingleton.FilesPath + "nmap/nmap ";
        String hostFilter = buildHostFilterCommand();
        String parameter = getNmapParamFromMenuItem(actualItemMenu);
        Log.d(TAG, Binary + hostFilter + " " + parameter + " ");
        return Binary + hostFilter + " " + parameter + " ";
    }

    public void                     start(final TextView Output, final NmapActivity activity) {
        if (mHost != null) {
            final String cmd = buildCommand();
            String trimmed_cmd = "root$> " + cmd
                    .replace("nmap/nmap", "nmap").replace("\n", "")
                    .replace(mSingleton.FilesPath, "");
            Output.setText(trimmed_cmd);
            stdoutToBuffer(cmd, activity);
        } else {
            if (activity != null)
                activity.showSnackbar("Can\'t exec nmap without target selected");
            Log.e(TAG, "No client selected when launched");
        }
    }

    private void                    stdoutToBuffer(final String cmd, final NmapActivity activity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    BufferedReader reader = new RootProcess("Nmap", mSingleton.FilesPath)
                            .exec(cmd).getReader();
                    String tmp;
                    StringBuilder dumpOutputBuilder = new StringBuilder();
                    while ((tmp = reader.readLine()) != null && !tmp.contains("Nmap done")) {
                        dumpOutputBuilder.append(tmp).append('\n');
                    }
                    dumpOutputBuilder.append(tmp);
                    if (activity != null) {
                        Log.d(TAG, "Nmap STDOUT LIVE MODE");
                        activity.flushOutput(dumpOutputBuilder.toString());
                    }
                    else {
                        Log.d(TAG, "Nmap STDOUT PARSING MODE");
                        new NmapParser(mInstance).parseStdout(dumpOutputBuilder.toString());
                    }
                    Log.d(TAG, "Nmap final stdouT" + dumpOutputBuilder.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



    public void                     setHosts(List<Host> hosts) {
        this.mHost = hosts;
    }

    public void                     setExecAllHostInOnceExecution(boolean execAllHostInOnceExecution) {
        this.mOneByOnExecution = execAllHostInOnceExecution;
    }

}
