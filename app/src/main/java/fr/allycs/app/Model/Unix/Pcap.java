package fr.allycs.app.Model.Unix;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;
import java.util.List;

import fr.allycs.app.Controller.Core.Database.DBHost;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.SniffSession;

@Table(name = "Pcap", id = "_id")
public class                Pcap extends Model {
    private String          TAG = "Pcap";
    private String          NAME_COLUMN = "Pcap";

    @Column(name ="Path")
    public String           path;

    @Column(name = "SniffSession")
    public SniffSession     sniffSession;

    @Column(name = "Devices")
    public String           listDevicesSerialized;
    private List<Host>      listDevices = null;

    /**
     * Create the ManyToMany relation
     */
    public List<Host>       listDevices() {
        if (listDevices == null) {
            listDevices = DBHost.getListFromSerialized(listDevicesSerialized);
            Log.d(NAME_COLUMN, "Liste Devices deserialized " + listDevices.size() + " devices");
        }
        return listDevices;
    }


    @Column(name ="Date")
    public Date             date;

    public                  Pcap(String path, List<Host> sniffedDevice) {
        super();
        this.path = path;
        this.listDevicesSerialized = DBHost.SerializeListDevices(sniffedDevice);
        this.listDevices = sniffedDevice;
        this.date = new Date();
        Log.d(TAG, "New PCAP FILE(" + path + ") with " + sniffedDevice.size() + "devices");
    }
    public                  Pcap() {
        super();
    }

}
