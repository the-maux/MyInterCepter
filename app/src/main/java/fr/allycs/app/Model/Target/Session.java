package fr.allycs.app.Model.Target;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Table(name = "HostDiscoverySession", id = "_id")
public class                Session extends Model {
    @Column(name = "Date")
    public java.util.Date   Date;

    @Column(name = "Gateway")
    public Host            Gateway;

    @Column(name = "Devices")
    public List<Host>       listDevices;

    @Column(name = "typeScan")/* Arp, Icmp, Nmap*/
    public String          typeScan;

    @Column(name = "sniffedSession")
    public List<SniffSession> sniffedSession;

    public Session() {
        super();
    }

    @Override
    public String           toString() {
        return new SimpleDateFormat("dd/MM/yyyy-hh:mm:ss", Locale.FRANCE)
                .format(Date) + " Devices Connected : " + listDevices.size();
    }
}
