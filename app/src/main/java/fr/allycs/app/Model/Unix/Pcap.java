package fr.allycs.app.Model.Unix;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.allycs.app.Model.Target.Host;

@Table(name = "PcapFile", id = "_id")
public class                Pcap extends Model {
    private String          TAG = "TAG";
    public String           path;
    public List<Host>       sniffedDevice;
    public Date             date;

    public Pcap(String path, List<Host> sniffedDevice) {
        super();
        Log.d(TAG, "Creating new Path file:" + path);
        this.path = path;
        this.sniffedDevice = sniffedDevice;
        this.date = new Date();
    }
    public                  Pcap() {
        super();
    }

}
