package fr.allycs.app.Controller.Network.Discovery;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.BinaryWrapper.RootProcess;
import fr.allycs.app.Controller.Core.Databse.DBHost;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.View.HostDiscovery.HostDiscoveryActivity;

public class                         Fingerprint {
    private static String            TAG = "Fingerprint";
    /**
     * Scan with the cepter binary the hostList
     * @param scanActivity activity for callback
     */
    public static void              guessHostFingerprint(final HostDiscoveryActivity scanActivity) {
        final List<Host> hosts = new ArrayList<>();
        final RootProcess process = new RootProcess("Cepter Scan host", Singleton.getInstance().FilesPath);
        //final BufferedReader bufferedReader = new BufferedReader(process.getInputStreamReader());
        final BufferedReader bufferedReader = process.getReader();
        process.exec(Singleton.getInstance().FilesPath + "cepter scan " + Singleton.getInstance().nbrInteface);
        process.exec("exit");
        new Thread(new Runnable() {
            public void run() {
//                try { qAdmin: Cannot perform this operation on a closed dataset
                    String buffer = null;
                    boolean alreadyIn, over = false;
                    while (!over) {
                        try {
                            buffer = bufferedReader.readLine();
                        } catch (IOException e) {
                            Log.d(TAG, "ERROR INTRANET");
                            e.printStackTrace();
                            scanActivity.onHostActualized(hosts);
                        }
                        if (buffer == null) {
                            Log.d(TAG, "BUFFER IS NULL");
                            over = true;
                        } else if ((buffer.length() - buffer.replace(".", "").length()) >= 3 &&
                                !buffer.contains("wrong interface...")) {
                            alreadyIn = false;
                            Host newDevice = new Host(buffer);
                            if (!hosts.contains(newDevice)) {
                                for (Host host : hosts) {
                                    if (host.getMac().equals(newDevice.getMac())) {
                                        alreadyIn = true;
                                    }
                                }
                                if (!alreadyIn) {
                                    hosts.add(DBHost.saveOrGetInDatabase(newDevice));
                                }
                            }
                        }
                    }

                    Log.d(TAG, "Nbr Host discovered:" + hosts.size());
                    Collections.sort(hosts, Host.comparator);
                    scanActivity.onHostActualized(hosts);
//                } catch (IOException e) {
//                    Log.d(TAG, "CATCHED ERROR");
//                    e.printStackTrace();
//                }
            }
        }).start();
    }
}
