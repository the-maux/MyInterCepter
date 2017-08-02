package su.sniff.cepter.Controller.CepterControl;

import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

import su.sniff.cepter.Controller.System.RootProcess;
import su.sniff.cepter.R;
import su.sniff.cepter.Controller.Network.IpTablesConfStrippedMode;
import su.sniff.cepter.View.MainActivity;
import su.sniff.cepter.globalVariable;

/**
 * Created by maxim on 10/07/2017.
 */

public class                    onInterceptRun {
    private String              TAG = "onInterceptRun";
    private RootProcess         sniff_process;
    private ImageView           runIcon;
    private TextView            monitorIntercepter;
    private MainActivity        activity;

    public                      onInterceptRun(MainActivity activity, RootProcess sniff_process, ImageView runIcon, TextView monitorIntercepter)  {
        this.runIcon = runIcon;
        this.sniff_process = sniff_process;
        this.activity = activity;
        this.monitorIntercepter = monitorIntercepter;
    }

    public RootProcess             run(String cmd) throws IOException {
        RootProcess process;
        if (sniff_process != null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runIcon.setImageResource(R.drawable.start);
            activity.openFileOutput("exits.id", 0).close();
            if (globalVariable.strip == 1) {
                process = new RootProcess("IpTable conf");
                process.exec("iptables -F;iptables -X;iptables -t nat -F;iptables -t nat -X;iptables -t mangle -F;iptables -t mangle -X;iptables -P INPUT ACCEPT;iptables -P FORWARD ACCEPT;iptables -P OUTPUT ACCEPT");
                process.closeProcess();
            }
            Log.d(TAG, "onInterceptorRunClick::typical over with strip");
            return null;
        }
        String sc;
        runIcon.setImageResource(R.drawable.stop);
        if (globalVariable.savepcap == 1) {
            sc = " w ";
        } else {
            sc = " ";
        }
        monitorIntercepter.setTextSize(2, (float) globalVariable.raw_textsize);
        ((TextView) activity.findViewById(R.id.monitor)).setTextSize(2, (float) globalVariable.raw_textsize);
        File fDroidSheep = new File(globalVariable.path + "/exits.id");
        if (fDroidSheep.exists()) {
            fDroidSheep.delete();
        }
        if (globalVariable.strip == 1) {
            Log.d(TAG, "iptables Conf as full striped");
            new IpTablesConfStrippedMode();
        } else {
            Log.d(TAG, "iptables Conf as partially striped");
            process = new RootProcess("IpTableStriped");
            process.exec("iptables -F;iptables -X;iptables -t nat -F;iptables -t nat -X;iptables -t mangle -F;iptables -t mangle -X;iptables -P INPUT ACCEPT;iptables -P FORWARD ACCEPT;iptables -P OUTPUT ACCEPT");
            process.exec("echo '1' > /proc/sys/net/ipv4/ip_forward");
            if (globalVariable.dnss == 1) {
                process.exec("iptables -t nat -A PREROUTING -p udp --destination-port 53 -j REDIRECT --to-port 8053");
            }
            process.closeProcess();
        }
        process = new RootProcess("Start ARP", globalVariable.path + "");
        process.exec(globalVariable.path + "/cepter " + Integer.toString(globalVariable.adapt_num) + " " + Integer.toString(globalVariable.resurrection) + sc + cmd);
        process.exec("exit").closeDontWait();
        this.sniff_process = process;
        new IntercepterReader(activity, monitorIntercepter, process);
        return process;
    }
}
