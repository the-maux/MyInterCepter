package fr.dao.app.Core.Configuration;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import java.util.ArrayList;

import fr.dao.app.Core.Database.DBNetwork;
import fr.dao.app.Core.Database.DBSniffSession;
import fr.dao.app.Core.Network.IPTables;
import fr.dao.app.Core.Network.NetDiscovering;
import fr.dao.app.Core.Network.NetworkInformation;
import fr.dao.app.Model.Config.Session;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.Model.Target.SniffSession;

public class                            Singleton {
    private static Singleton            mInstance = null;
    public  NetworkInformation          NetworkInformation = null;
    public  Network                     CurrentNetwork = null;
    private SniffSession                CurrentSniffSession = null;
    private boolean                     webSpoofedstarted = false;
    public  Session                     Session;
    public  SettingsControler           Settings = null;
    public  ArrayList<Host>             hostList = null, savedHostList = null;
    String                              VERSION = "0xDEADBEEF";
    public ArrayList<Host>              alreadyExtracted = new ArrayList<>();


    private                             Singleton() { }

    public static synchronized Singleton getInstance() {
        if(mInstance == null) {
            mInstance = new Singleton();
        }
        return mInstance;
    }

    public SniffSession                 getCurrentSniffSession() {
        if (CurrentSniffSession == null) {
            CurrentSniffSession = DBSniffSession.buildSniffSession();
        }
       return CurrentSniffSession;
    }

    public boolean                      isSslstripMode() {
        return Settings.getUserPreferences().sslstripMode;
    }
    public void                         setSslstripMode(boolean sslstripMode) {
        Settings.getUserPreferences().sslstripMode = sslstripMode;
        IPTables.sslConf();
    }
    public boolean                      isLockScreen() {
        return Settings.getUserPreferences().Lockscreen;
    }
    public void                         setLockScreen(boolean lockScreen) {
        Settings.getUserPreferences().Lockscreen = lockScreen;
        //TODO: LockScreen
        Log.i("setockScreenActived", "Not implemented");
    }

    public void                         resetActualSniffSession() {
        CurrentSniffSession = null;
    }

    public void                         init(Activity activity) {
        if (Settings == null) {
            Log.d("Singleton", "Singleton"+"::initialisation");
            Settings = new SettingsControler(activity.getFilesDir().getPath() + '/');
            Settings.PcapPath = Environment.getExternalStorageDirectory().getPath() + "/Dao/Pcap/";
            Settings.DumpsPath = Environment.getExternalStorageDirectory().getPath() + "/Dao/Nmap/";
            Settings.BinaryPath = Settings.FilesPath;
            if (NetDiscovering.initNetworkInfo(activity))
                CurrentNetwork = DBNetwork.getAPFromSSID(NetworkInformation.ssid);
        }
    }

}
