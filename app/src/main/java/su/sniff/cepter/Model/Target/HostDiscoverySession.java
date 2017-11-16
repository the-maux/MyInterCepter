package su.sniff.cepter.Model.Target;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class                HostDiscoverySession {
    @SerializedName("listDevices")
    private List<Host>      mListDevices;
    @SerializedName("date")
    private Date            mDate;

    public  HostDiscoverySession(Date date, List<Host> devices) {
        this.mListDevices = devices;
        this.mDate = date;
    }
}
