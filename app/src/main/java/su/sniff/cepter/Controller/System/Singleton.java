package su.sniff.cepter.Controller.System;

import java.util.ArrayList;
import java.util.List;

import su.sniff.cepter.Controller.Network.ArpSpoof;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.Model.Target.NetworkInformation;

/**
 * Created by root on 03/08/17.
 */

public class                            Singleton {
    public static String                BinaryPath = Singleton.FilesPath;
    public static String                FilesPath;
    public static ArrayList<Host>       hostsList;
    public static List<ArpSpoof>        ArpSpoofProcessStack = new ArrayList<>();
    public static NetworkInformation    network = null;
    public static boolean               DebugMode = true, UltraDebugMode = false;


}
