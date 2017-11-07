package su.sniff.cepter.Controller.Core;

import android.app.Application;

import su.sniff.cepter.Controller.Core.BinaryWrapper.ArpSpoof;
import su.sniff.cepter.Controller.Core.BinaryWrapper.RootProcess;


/**
 * Created by root on 11/08/17.
 */

public class ApplicationControl extends Application {
    @Override
    public void onTerminate() {
        ArpSpoof.stopArpSpoof();
        RootProcess.kill("tcpdump");
        RootProcess.kill("cepter");
        RootProcess.kill("ping");
        RootProcess.kill("arpspoof");
        super.onTerminate();
    }
}
