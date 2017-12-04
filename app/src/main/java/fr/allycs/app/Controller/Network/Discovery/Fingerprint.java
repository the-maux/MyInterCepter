package fr.allycs.app.Controller.Network.Discovery;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.BinaryWrapper.RootProcess;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.View.HostDiscovery.HostDiscoveryActivity;

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
                    String buffer;
                    boolean alreadyIn;
                    while ((buffer = bufferedReader.readLine()) != null) {//sanityzeCheck: at least 3 '.' for x.x.x.x : Ip
                        if ((buffer.length() - buffer.replace(".", "").length()) >= 3 &&
                                !buffer.contains("wrong interface...")) { // wrong interface when no wifi
                            alreadyIn = false;
                            Host newDevice = new Host(buffer);//Format : IP\t(HOSTNAME) \n [MAC] [OS] : VENDOR \n
                            if (!hosts.contains(newDevice)) {
                                for (Host host : hosts) {
                                    if (host.equals(newDevice)) {
                                        alreadyIn = true;
                                        Log.d("Intercepter", host.getIp() + " is already in");
                                    }
                                }
                                if (!alreadyIn) {
                                    hosts.add(newDevice);
                                    newDevice.saveInDatabase();
                                }
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
