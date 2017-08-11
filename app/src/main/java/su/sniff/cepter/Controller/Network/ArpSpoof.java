/*
 * This file is part of the dSploit.
 *
 * Copyleft of Simone Margaritelli aka evilsocket <evilsocket@gmail.com>
 *
 * dSploit is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * dSploit is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with dSploit.  If not, see <http://www.gnu.org/licenses/>.
 */
package su.sniff.cepter.Controller.Network;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;

import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Controller.System.RootProcess;
import su.sniff.cepter.Model.Host;

/**
 * Wrapper of ArpSpoof tool
 */
public class                        ArpSpoof {
    private static final String     TAG = "ARPSPOOF";
    private ArpSpoof                mInstance = this;
    private Host                    target;
    private RootProcess             process;

    public                          ArpSpoof(Host target) {
        this.target = target;
    }
    public ArpSpoof                 start() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                process = new RootProcess("ARPSPoof::" + target.getIp());
                process.exec(Singleton.BinaryPath + "arpspoof -i wlan0 -t " + target.getIp() + " " + Singleton.network.gateway);
                Singleton.ArpSpoofProcessStack.add(mInstance);
                if (Singleton.DebugMode) {
                    BufferedReader reader = process.getReader();
                    String read;
                    try {
                        while ((read = reader.readLine()) != null) {
                            Log.d(TAG, target.getIp() + "::" + read);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return this;
    }


    public static void             stopArpSpoof() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RootProcess process = new RootProcess("ARPSpoof");
                process.exec("ps | grep arpspoof");
                BufferedReader reader = new BufferedReader(process.getReader());
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        if (line.contains("arp"))
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
        for (Host host : Singleton.hostsList) {
            if (host.isSelected()) {
                new ArpSpoof(host).start();
            }
        }
    }
}