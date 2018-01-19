package fr.allycs.app.Model.Unix;

import com.google.gson.annotations.SerializedName;

public class                Preferences {
    @SerializedName("AutoScanOnStartup")
    public boolean          autoScanOnStartup = true;
    @SerializedName("DumpInPcap")
    public boolean          dumpInPcap;
    @SerializedName("SendFeedBackToServer")
    public boolean          feedBackToServer;
    @SerializedName("DebugMode")
    public boolean          debugMode;
    @SerializedName("UltraDebugMode")
    public boolean          ultraDebugMode;
    @SerializedName("SearchServiceOnHostDiscovery")
    public boolean          searchServiceOnHostDiscovery;
    @SerializedName("MaxSizeOfDbSpace")
    public int              maxSizeOfDbSpace;
    @SerializedName("Lockscreen")
    public boolean          Lockscreen;
    @SerializedName("SslstripMode")
    public boolean          sslstripMode;
    @SerializedName("AutoSaveSession")
    public boolean          autoSaveSession;
    @SerializedName("AutoSaveSniffSession")
    public boolean          autoSaveSniffSession;
    @SerializedName("AutoSaveDnsLogs")
    public boolean          autoSaveDnsLogs;
    @SerializedName("SearchForUpdateOnStartup")
    public boolean          searchForUpdateOnStartup;

    @SerializedName("PATH_TO_PCAP")
    public String          PATH_TO_PCAP;
    @SerializedName("PATH_TO_FILES")
    public String          PATH_TO_FILES;


    
}
