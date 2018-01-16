package fr.allycs.app.Model.Target;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;
import java.util.List;

import fr.allycs.app.Controller.Core.Database.DBHost;
import fr.allycs.app.Model.Unix.DNSLog;
import fr.allycs.app.Model.Unix.Pcap;

//TODO: Create when wireshark is started
@Table(name = "SniffSession", id = "_id")
public class                SniffSession extends Model {
    public String           TAG = "SniffSession";
    public String           NAME_COLUMN = "SniffSession";

    @Column(name = "Session")
    public Session          session;

    @Column(name = "Devices")
    public String           listDevicesSerialized;
    private List<Host>      listDevices = null;

    /**
     * Create the ManyToMany relation
     */
    public List<Host>       listDevices() {
        if (listDevices == null) {
            listDevices = DBHost.getListFromSerialized(listDevicesSerialized);
            Log.d(NAME_COLUMN, "Liste devices deserialized " + listDevices.size() + " devices");
        }
        return listDevices;
    }

    /**
     * Create the OneToMany relation
     */
    public List<Pcap>       listPcapRecorded() {
        return getMany(Pcap.class, "SniffSession");
    }

    /**
     * Create the OneToMany relation
     */
    public List<DNSLog>     logDnsSpoofed() {
        return getMany(DNSLog.class, "SniffSession");
    }

    @Column(name = "Date")
    public Date             date;

    public                  SniffSession() {
        super();
    }
}
