package fr.allycs.app.Controller.Misc;

import fr.allycs.app.Controller.Core.BinaryWrapper.ArpSpoof;
import fr.allycs.app.Controller.Core.BinaryWrapper.RootProcess;

public class ApplicationControl extends com.activeandroid.app.Application {
    @Override
    public void onTerminate() {
        RootProcess.kill("tcpdump");
        RootProcess.kill("cepter");
        RootProcess.kill("ping");
        RootProcess.kill("arpspoof");
        super.onTerminate();
    }
}
