package fr.dao.app.Model.Unix;

import android.os.Environment;

import com.google.gson.annotations.SerializedName;

public class                Preferences {
    @SerializedName("AutoScanOnStartup")
    public boolean          autoScanOnStartup = true;
    @SerializedName("DumpInPcap")
    public boolean          dumpInPcap = true;
    @SerializedName("SendFeedBackToServer")
    public boolean          feedBackToServer = true;
    @SerializedName("DebugMode")
    public boolean          debugMode = true;
    @SerializedName("UltraDebugMode")
    public boolean          ultraDebugMode = true;
    @SerializedName("SearchServiceOnHostDiscovery")
    public boolean          searchServiceOnHostDiscovery = false;
    @SerializedName("MaxSizeOfDbSpaceInMB")
    public int              maxSizeOfDbSpace = 200;
    @SerializedName("Lockscreen")
    public boolean          Lockscreen = true;
    @SerializedName("SslstripMode")
    public boolean          sslstripMode = false;
    @SerializedName("AutoSaveSession")
    public boolean          autoSaveSession = true;
    @SerializedName("AutoSaveSniffSession")
    public boolean          autoSaveSniffSession = true;
    @SerializedName("AutoSaveDnsLogs")
    public boolean          autoSaveDnsLogs = false;
    @SerializedName("SearchForUpdateOnStartup")
    public boolean          searchForUpdateOnStartup = true;
    @SerializedName("PATH_TO_FILES")
    public String          PATH_TO_FILES = Environment.getExternalStorageDirectory().getPath();
    @SerializedName("PATH_TO_PCAP")
    public String          PATH_TO_PCAP = PATH_TO_FILES + "/Pcap";

}
