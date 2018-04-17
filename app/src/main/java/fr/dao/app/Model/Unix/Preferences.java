package fr.dao.app.Model.Unix;

import android.os.Environment;

import com.google.gson.annotations.SerializedName;

public class                Preferences {
    /*
     ** Global settings
     */
    @SerializedName("DebugMode")
    public boolean          debugMode = true;
    @SerializedName("UltraDebugMode")
    public boolean          ultraDebugMode = true;
    @SerializedName("SendFeedBackToServer")
    public boolean          feedBackToServer = true;
    @SerializedName("MaxSizeOfDbSpaceInMB")
    public int              maxSizeOfDbSpace = 200;

    @SerializedName("PATH_TO_FILES")
    public String           PATH_TO_FILES = Environment.getExternalStorageDirectory().getPath();
    @SerializedName("PATH_TO_PCAP")
    public String           PATH_TO_PCAP = PATH_TO_FILES + "/Pcap";
    @SerializedName("SearchForUpdateOnStartup")
    public boolean          searchForUpdateOnStartup = true;
    @SerializedName("Lockscreen")
    public boolean          Lockscreen = true;

    /*
     ** MITM Settings
     */
    @SerializedName("DumpInPcap")
    public boolean          dumpInPcap = true;
    @SerializedName("SslstripMode")
    public boolean          sslstripMode = false;
    @SerializedName("AutoSaveSniffSession")
    public boolean          autoSaveSniffSession = true;
    @SerializedName("AutoSaveDnsLogs")
    public boolean          autoSaveDnsLogs = false;

    /*
     ** Scan/Nmap Settings
     */
    @SerializedName("AutoSaveSession")
    public boolean          autoSaveSession = true;
    @SerializedName("AutoScanOnStartup")
    public boolean          autoScanOnStartup = true;
    @SerializedName("SearchServiceOnHostDiscovery")
    public boolean          searchServiceOnHostDiscovery = false;
    @SerializedName("AutoSaveNmapSession")
    public boolean          autoSaveNmapSession = true;
    /**
     * 1 - Basic No nmap
     * 2 - Nmap 5 Port
     * 3 - Nmap Script + -T4
     * 4 - Nmap All Script -T1
     */
    @SerializedName("NmapMode")
    public int              NmapMode = 1;
    @SerializedName("MaxThread")
    public int              MaxThread = 100;
    @SerializedName("CleverScan")
    public boolean          CleverScan = true;
    @SerializedName("VendorOnline")
    public boolean           VendorOnline = false;

    /*
     ** Dora Settings
     */
    @SerializedName("timeBeetweenRequest")
    public float            timeBeetweenRequest = 0.2f;


}
