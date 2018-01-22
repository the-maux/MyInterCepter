package fr.allycs.app.Model.Target;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.allycs.app.Controller.Core.Database.DBHost;
import fr.allycs.app.Model.Unix.DNSLog;
import fr.allycs.app.Model.Unix.Pcap;

//TODO: Create when wireshark is started
@Table(name = "SniffSession", id = "_id")
public class                SniffSession extends Model {
    public String           TAG = "SniffSession";
    public String           NAME_COLUMN = "SniffSession";

    @Column(name = "Date")
    public Date             date;

    @Column(name = "Session")
    public Session          session;

    @Column(name = "Devices")
    public String           listDevicesSerialized;
    private List<Host>      listDevices = null;

    public List<Host>       listDevices() {
        if (listDevices == null) {
            listDevices = DBHost.getListFromSerialized(listDevicesSerialized);
            Log.d(NAME_COLUMN, "Liste devices deserialized " + listDevices.size() + " devices");
        }
        return listDevices;
    }

    public List<Pcap>       listPcapRecorded() {
        return getMany(Pcap.class, "SniffSession");
    }

    public List<DNSLog>     logDnsSpoofed() {
        return getMany(DNSLog.class, "SniffSession");
    }

    public                  SniffSession() {
        super();
    }

    public String           toString() {
        List<Pcap> pcaps = listPcapRecorded();
        List<DNSLog> dnsLogs = logDnsSpoofed();
        return new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE).format(date) +
                " " + listDevices().size() + " Devices sniffed  with "  +
                ((pcaps != null) ?  pcaps.size() : "0") + " pcap file and " +
                ((dnsLogs != null) ?  dnsLogs.size() : "0") + " dnsRecord";
    }
}
