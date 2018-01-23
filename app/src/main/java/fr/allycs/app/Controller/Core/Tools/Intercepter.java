package fr.allycs.app.Controller.Core.Tools;

import android.app.Activity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Network.IPTables;
import fr.allycs.app.R;

public class                    Intercepter {
    private String              TAG = "Intercepter";
    private boolean             mRunning = false;
    private ImageView           mRunIcon;
    private TextView            mMonitor;
    private Activity            mActivity;
    private Singleton           mSingleton = Singleton.getInstance();

    public Intercepter(Activity activity, RootProcess sniff_process, ImageView runIcon, TextView monitorIntercepter)  {
        this.mRunIcon = runIcon;
        this.mActivity = activity;
        this.mMonitor = monitorIntercepter;
    }

    private RootProcess         startCepter(String gateway) {
        RootProcess process = new RootProcess("Start ARP", Singleton.getInstance().FilesPath);
        process.exec(Singleton.getInstance().FilesPath + "cepter " + Singleton.getInstance().nbrInteface
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
        mRunning = false;
        mRunIcon.setImageResource(R.drawable.start);
        IPTables.stopIpTable();
        Log.d(TAG, "onInterceptorRunClick::typical over with strip");
    }

    /**
     * Init Cepter en lui envoyant notre MAC ?
     * @param MAC
     */
    public static void          initCepter(String MAC) {
        Log.e("Intercepter", Singleton.getInstance().FilesPath + "cepter getv " + MAC);
        new RootProcess("cepter getv", Singleton.getInstance().FilesPath)
                .exec(Singleton.getInstance().FilesPath + "cepter getv " + MAC)
                .exec("exit")
                .closeProcess();
    }

    public static RootProcess   getNetworkInfoByCept() throws IOException, InterruptedException {
        Log.d("Intercepter", "su " + Singleton.getInstance().FilesPath + "cepter list; exit");
        RootProcess process = new RootProcess("getNetworkInfoByCept", Singleton.getInstance().FilesPath)
                .exec(Singleton.getInstance().FilesPath + "cepter list")
                .exec("exit");
        return process;
    }
}
