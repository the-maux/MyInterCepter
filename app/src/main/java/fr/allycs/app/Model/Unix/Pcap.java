package fr.allycs.app.Model.Unix;

import com.activeandroid.Model;
import com.activeandroid.annotation.Table;

import java.util.Date;
import java.util.List;

import fr.allycs.app.Model.Target.Host;

@Table(name = "PcapFile", id = "_id")
public class                Pcap extends Model {
    public String           path;
    public List<Host>       sniffedDevice;
    public Date             date;

    public Pcap(String path, List<Host> sniffedDevice, Date date) {
        super();
        this.path = path;
        this.sniffedDevice = sniffedDevice;
        this.date = date;
    }
    public Pcap() {
        super();
    }

}
