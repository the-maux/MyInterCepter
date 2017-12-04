package fr.allycs.app.Model.Target;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;
import java.util.List;

@Table(name = "Session", id = "_id")
public class Session extends Model {
    @Column(name = "Date")
    public java.util.Date   Date;

    @Column(name = "Gateway")
    public Host            Gateway;

    @Column(name = "Devices")
    public List<Host>       listDevices;

    @Column(name = "typeScan")/* Arp, Icmp, Nmap*/
    public String          typeScan;

    @Column(name = "ListPcapRecorded")
    public List<String>     listPcapRecorded;

    public Session() {
        super();
    }

}
