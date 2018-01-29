package fr.allycs.app.Controller.Core.Configuration;

import com.activeandroid.ActiveAndroid;

import fr.allycs.app.Controller.Core.RootProcess;

public class                ApplicationControl extends com.activeandroid.app.Application {

    @Override public void   onTerminate() {
        RootProcess.kill("tcpdump");
        RootProcess.kill("cepter");
        RootProcess.kill("ping");
        RootProcess.kill("arpspoof");
        ActiveAndroid.endTransaction();
        super.onTerminate();
    }

}
