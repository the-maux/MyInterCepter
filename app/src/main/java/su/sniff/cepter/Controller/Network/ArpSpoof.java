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
import android.content.Context;
import android.util.Log;

import su.sniff.cepter.Controller.Singleton;
import su.sniff.cepter.Controller.System.RootProcess;
import su.sniff.cepter.Model.Host;

/**
 * Wrapper of ArpSpoof tool
 */
public class                        ArpSpoof {
    private static final String     TAG = "ARPSPOOF";
    private Host                    target;
    private RootProcess             process;

    public                          ArpSpoof(Host target) {
        this.target = target;
    }
    public ArpSpoof                 start() {
        process = new RootProcess("ARPSPoof::" + target.getIp());
        process.exec(Singleton.BinaryPath + "arpspoof -i eth0 -t " + target.getIp() + " " + Singleton.network.gateway);
        Singleton.ArpSpoofProcessStack.add(this);
        return this;
    }
    public void                     stop() {
        Log.d(TAG, "Stoppinf Arp attack against::" + target.getName());
        new Thread(new Runnable() {
            @Override
            public void run() {
                process.closeProcess();
            }
        }).start();
    }
}