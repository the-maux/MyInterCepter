package fr.dao.app.Core.Configuration;

import com.activeandroid.ActiveAndroid;
import com.squareup.leakcanary.LeakCanary;

public class                ApplicationControl extends com.activeandroid.app.Application {

    public void             onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...
    }

    public void             onTerminate() {
        RootProcess.kill("tcpdump");
        RootProcess.kill("cepter");
        RootProcess.kill("ping");
        RootProcess.kill("arpspoof");
        ActiveAndroid.endTransaction();
        super.onTerminate();
    }

}
