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

/**
 * Created by maxim on 10/07/2017.
 */

public class                    IntercepterWrapper {
    private String              TAG = "IntercepterWrapper";
    private boolean             running = false;
    private ImageView           runIcon;
    private TextView            monitorIntercepter;
    private Activity            activity;
    private Singleton           singleton = Singleton.getInstance();

    public                      IntercepterWrapper(Activity activity, RootProcess sniff_process, ImageView runIcon, TextView monitorIntercepter)  {
        this.runIcon = runIcon;
        this.activity = activity;
        this.monitorIntercepter = monitorIntercepter;
    }

    private RootProcess         startCepter(String gateway) {
        RootProcess process = new RootProcess("Start ARP", Singleton.getInstance().FilesPath);
        process.exec(Singleton.getInstance().FilesPath + "/cepter " + Singleton.getInstance().nbrInteface
                + " " + "1" + " " + gateway);
        process.exec("exit").closeDontWait();
        return process;
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
        IPTables.stopIpTable();
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
