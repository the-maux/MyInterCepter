package su.sniff.cepter.Controller.System;

import android.app.Application;

import su.sniff.cepter.Controller.Network.ArpSpoof;


/**
 * Created by root on 11/08/17.
 */

public class ApplicationControl extends Application {
    @Override
    public void onTerminate() {
        ArpSpoof.stopArpSpoof();
        RootProcess.kill("tcpdump");
        RootProcess.kill("cepter");
        super.onTerminate();
    }
}