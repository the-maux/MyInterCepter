package su.sniff.cepter.Controller.Network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import su.sniff.cepter.Controller.Core.Singleton;
import su.sniff.cepter.Controller.Core.BinaryWrapper.RootProcess;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.View.HostDiscovery.HostDiscoveryActivity;

/**
 * Created by root on 05/09/17.
 */

public class                         Fingerprint {

    /**
     * Scan with the cepter binary the hostList
     * @param scanActivity activity for callback
     * @return
     */
    public static void              guessHostFingerprint(final HostDiscoveryActivity scanActivity) {
        final List<Host> hosts = new ArrayList<>();
        final RootProcess process = new RootProcess("Cepter Scan host", Singleton.getInstance().FilesPath);
        final BufferedReader bufferedReader = new BufferedReader(process.getInputStreamReader());
        process.exec(Singleton.getInstance().FilesPath + "cepter scan " + Singleton.getInstance().nbrInteface);
        process.exec("exit");
        new Thread(new Runnable() {
            public void run() {
                try {
                    String read;
                    boolean alreadyIn;
                    while ((read = bufferedReader.readLine()) != null) {//sanityzeCheck: at least 3 '.' for x.x.x.x : Ip
                        if ((read.length() - read.replace(".", "").length()) >= 3 && !read.contains("wrong interface...")) { // wrong interface when no wifi
                            alreadyIn = false;
                            Host hostObj = new Host(read);//Format : IP\t(HOSTNAME) \n [MAC] [OS] : VENDOR \n
                            if (!hosts.contains(hostObj)) {

                                for (Host host : hosts) {
                                    if (host.equals(hostObj)) {
                                        alreadyIn = true;
                                        Log.d("Intercepter", host.getIp() + " is already in");
                                    }
                                }
                                if (!alreadyIn)
                                    hosts.add(hostObj);
                            }
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
