package su.sniff.cepter.Controller.CepterControl;

import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import su.sniff.cepter.Controller.NetUtils;
import su.sniff.cepter.Controller.RootProcess;
import su.sniff.cepter.Model.Host;
import su.sniff.cepter.View.ScanActivity;
import su.sniff.cepter.adapter.HostAdapter;
import su.sniff.cepter.globalVariable;

/**
 * Created by root on 01/08/17.
 */

public class                    Cepter {
    private static RootProcess  actualProcess = null;

    public static void          startCepter(String MAC) {
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
    public static void          fillHostAdapter(final ScanActivity scanActivity) {
        final List<Host> hosts = new ArrayList<>();
        final RootProcess process = new RootProcess("Cepter Scan host", globalVariable.path + "");
        final BufferedReader bufferedReader = new BufferedReader(process.getInputStreamReader());
        process.exec(globalVariable.path + "/cepter scan " + Integer.toString(globalVariable.adapt_num));
        process.exec("exit");
        actualProcess = process;
        new Thread(new Runnable() {
            public void run() {
                try {
                    String read;
                    while ((read = bufferedReader.readLine()) != null) {//sanityzeCheck: at least 3 '.' for x.x.x.x : Ip
                        if ((read.length() - read.replace(".", "").length()) >= 3) {
                            Host hostObj = new Host(read);//Format : IP\t(HOSTNAME) \n [MAC] [OS] : VENDOR \n
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

    public static void          waitForCepter() {
        actualProcess.waitFor();
    }
}
