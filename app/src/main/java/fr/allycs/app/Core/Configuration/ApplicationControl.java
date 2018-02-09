package fr.allycs.app.Core.Configuration;

import com.activeandroid.ActiveAndroid;

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
