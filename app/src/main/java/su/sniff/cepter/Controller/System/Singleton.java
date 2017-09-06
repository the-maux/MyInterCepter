package su.sniff.cepter.Controller.System;

import java.util.ArrayList;
import java.util.List;

import su.sniff.cepter.Controller.System.Wrapper.ArpSpoof;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.Model.Target.NetworkInformation;

/**
 * Created by root on 03/08/17.
 */

public class                            Singleton {
    private static Singleton            mInstance = null;

    private                             Singleton() {}
    public static synchronized Singleton getInstance() {
        if(mInstance == null)
            mInstance = new Singleton();
        return mInstance;
    }

    public  String                      BinaryPath = null;
    public  String                      FilesPath = null;
    public  ArrayList<Host>             hostsList = null;
    public  List<ArpSpoof>              ArpSpoofProcessStack = new ArrayList<>();
    public  NetworkInformation          network = null;
    public  boolean                     DebugMode = true, UltraDebugMode = false;
    public  boolean                     SslStripModeActived = false, DnsSpoofActived = false;

    public  int                         lock = 0, nbrInteface = 1;
}
