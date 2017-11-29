package fr.allycs.app.Controller.Misc;

import android.app.Application;

import fr.allycs.app.Controller.Core.BinaryWrapper.ArpSpoof;
import fr.allycs.app.Controller.Core.BinaryWrapper.RootProcess;


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
