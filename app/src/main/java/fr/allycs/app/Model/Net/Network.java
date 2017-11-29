package fr.allycs.app.Model.Net;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.HostDiscoverySession;
import fr.allycs.app.Model.Target.SniffSession;

public class                                Network {
    @SerializedName("ssid")
    public String                           mSSID;
    @SerializedName("sniffingSessions")
    private List<SniffSession>              mSniffingSessions;
    @SerializedName("discoverySessions")
    private ArrayList<HostDiscoverySession> mDiscoverySessions;

    public Network(String ssid, ArrayList<SniffSession> sniffSessions, ArrayList<HostDiscoverySession> discoverySessions) {
        this.mSSID = ssid;
        this.mSniffingSessions = sniffSessions;
        this.mDiscoverySessions = discoverySessions;
    }

    public SniffSession                     getLastSession() {
        return mSniffingSessions.get(0);
    }

    public List<SniffSession>               getSniffSessionsWithDeviceIn(Host device) {
        List<SniffSession>                  sessions = new ArrayList<>();
        for (SniffSession sniffingSession : mSniffingSessions) {
            for (Host deviceInSniff : sniffingSession.getListDevices()) {
                if (deviceInSniff.getGenericId().contains(device.getGenericId())) {
                    sessions.add(sniffingSession);
                }
            }
        }
        return sessions;
    }
}