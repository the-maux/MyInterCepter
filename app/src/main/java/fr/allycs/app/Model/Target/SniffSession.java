package fr.allycs.app.Model.Target;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;
import java.util.List;

import fr.allycs.app.Model.Unix.DNSLog;
import fr.allycs.app.Model.Unix.Pcap;

//TODO: Create when wireshark is started
@Table(name = "SniffSession", id = "_id")
public class                SniffSession extends Model {
    @Column(name = "PcapRecorded")
    public List<Pcap>       listPcapRecorded;
    @Column(name = "ListDevices")
    public List<Host>       listDevices;
    @Column(name = "DnsLogs")
    public List<DNSLog>     logDnsSpoofed;
    @Column(name = "Date")
    public Date             date;

    public SniffSession() {
        super();
    }
}
