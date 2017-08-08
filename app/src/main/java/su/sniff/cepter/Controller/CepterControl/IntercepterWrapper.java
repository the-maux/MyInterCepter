package su.sniff.cepter.Controller.CepterControl;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import su.sniff.cepter.Controller.Network.IPTables;
import su.sniff.cepter.Controller.System.RootProcess;
import su.sniff.cepter.Model.Host;
import su.sniff.cepter.R;
import su.sniff.cepter.View.MainActivity;
import su.sniff.cepter.View.ScanActivity;
import su.sniff.cepter.globalVariable;

/**
 * Created by maxim on 10/07/2017.
 */

public class                    IntercepterWrapper {
    private String              TAG = "IntercepterWrapper";
    private boolean             running = false;
    private ImageView           runIcon;
    private TextView            monitorIntercepter;
    private MainActivity        activity;

    public                      IntercepterWrapper(MainActivity activity, RootProcess sniff_process, ImageView runIcon, TextView monitorIntercepter)  {
        this.runIcon = runIcon;
        this.activity = activity;
        this.monitorIntercepter = monitorIntercepter;
    }

    public RootProcess          run(String gateway) throws IOException {
        if (running) {
            stopIntercept();
        }
        running = true;
        runIcon.setImageResource(R.drawable.stop);
        monitorIntercepter.setTextSize(2, (float) globalVariable.raw_textsize);
        ((TextView) activity.findViewById(R.id.monitor)).setTextSize(2, (float) globalVariable.raw_textsize);
        sslConf();
        return startCepter(gateway);
    }

    private RootProcess         startCepter(String gateway) {
        RootProcess process = new RootProcess("Start ARP", globalVariable.path + "");
        process.exec(globalVariable.path + "/cepter " + Integer.toString(globalVariable.adapt_num) + " " + "1" + " " + gateway);
        process.exec("exit").closeDontWait();
        new IntercepterReader(activity, monitorIntercepter, process);
        return process;
    }

    private void                sslConf() {
        if (globalVariable.strip == 1) {
            Log.d(TAG, "iptables Conf as full striped");
            IPTables.InterceptWithSSlStrip();
        } else {
            Log.d(TAG, "iptables Conf as partially striped");
            IPTables.InterceptWithoutSSL();
        }
    }

    /**
     * Créer le fichier exits.id
     * Configure les iptable pour fermer mais on dirait que le mec c'est pas foulé
     * et a juste mis pareil
     * @throws IOException
     */
    private void                stopIntercept() throws IOException {
        RootProcess process;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        running = false;
        activity.openFileOutput("exits.id", 0).close();
        runIcon.setImageResource(R.drawable.start);
        if (globalVariable.strip == 1) {
            process = new RootProcess("IpTable conf");
            process.exec("iptables -F;")
                    .exec("iptables -X;")
                    .exec("iptables -t nat -F;")
                    .exec("iptables -t nat -X;")
                    .exec("iptables -t mangle -F;")
                    .exec("iptables -t mangle -X;")
                    .exec("iptables -P INPUT ACCEPT;")
                    .exec("iptables -P FORWARD ACCEPT;")
                    .exec("iptables -P OUTPUT ACCEPT");
            process.closeProcess();
        }
        Log.d(TAG, "onInterceptorRunClick::typical over with strip");
    }

    /**
     * Init Cepter en lui envoyant notre MAC ?
     * @param MAC
     */
    public static void          initCepter(String MAC) {
        new RootProcess("/cepter getv", globalVariable.path) //Start Cepter Binary
                .exec(globalVariable.path + "/cepter getv " + MAC)
                .exec("exit")
                .closeProcess();
    }

    /**
     * Scan with cepter the hostList
     * @param scanActivity activity for callback
     * @return
     */
    public static void          fillHostListWithCepterScan(final ScanActivity scanActivity) {
        final List<Host> hosts = new ArrayList<>();
        final RootProcess process = new RootProcess("Cepter Scan host", globalVariable.path + "");
        final BufferedReader bufferedReader = new BufferedReader(process.getInputStreamReader());
        process.exec(globalVariable.path + "/cepter scan " + Integer.toString(globalVariable.adapt_num));
        process.exec("exit");
        new Thread(new Runnable() {
            public void run() {
                try {
                    String read;
                    while ((read = bufferedReader.readLine()) != null) {//sanityzeCheck: at least 3 '.' for x.x.x.x : Ip
                        if ((read.length() - read.replace(".", "").length()) >= 3) {
                            Host hostObj = new Host(read);//Format : IP\t(HOSTNAME) \n [MAC] [OS] : VENDOR \n
                            if (!hosts.contains(hostObj))
                                hosts.add(hostObj);
                        }
                    }
                    Collections.sort(hosts, Host.comparator);
                    scanActivity.onHostActualized(hosts);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
