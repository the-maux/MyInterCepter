package fr.allycs.app.Model.Unix;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.Date;
import java.util.List;

import fr.allycs.app.Model.Target.Host;

@Table(name = "PcapFile", id = "_id")
public class                Pcap extends Model {
    private String          TAG = "TAG";
    @Column(name ="Path")
    public String           path;
    @Column(name ="SniffedDevices")
    public List<Host>       sniffedDevice;
    @Column(name ="Date")
    public Date             date;

    public Pcap(String path, List<Host> sniffedDevice) {
        super();
        this.path = path;
        this.sniffedDevice = sniffedDevice;
        this.date = new Date();
    }
    public                  Pcap() {
        super();
    }

}
