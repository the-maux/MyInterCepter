package su.sniff.cepter.Controller.CepterControl;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import su.sniff.cepter.Controller.Network.IPTables;
import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Controller.System.Wrapper.RootProcess;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

/**
 * Created by maxim on 10/07/2017.
 */

public class                    IntercepterWrapper {
    private String              TAG = "IntercepterWrapper";
    private boolean             running = false;
    private ImageView           runIcon;
    private TextView            monitorIntercepter;
    private Activity activity;

    public                      IntercepterWrapper(Activity activity, RootProcess sniff_process, ImageView runIcon, TextView monitorIntercepter)  {
        this.runIcon = runIcon;
        this.activity = activity;
        this.monitorIntercepter = monitorIntercepter;
    }

    private RootProcess         startCepter(String gateway) {
        RootProcess process = new RootProcess("Start ARP", Singleton.getInstance().FilesPath);
        process.exec(Singleton.getInstance().FilesPath + "/cepter " + Integer.toString(globalVariable.adapt_num) + " " + "1" + " " + gateway);
        process.exec("exit").closeDontWait();
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
        new RootProcess("/cepter getv", Singleton.getInstance().FilesPath) //Start Cepter Binary
                .exec(Singleton.getInstance().FilesPath + "/cepter getv " + MAC)
                .exec("exit")
                .closeProcess();
    }


}
