package fr.allycs.app.Model.Unix;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.SniffSession;

@Table(name = "Pcap", id = "_id")
public class                Pcap extends Model {
    private String          TAG = "Pcap";
    private String          NAME_COLUMN = "Pcap";


    @Column(name ="NameFile")
    public String           nameFile;

    @Column(name = "SniffSession")
    public SniffSession     sniffSession;

    private List<Host>      listDevices = null;

    public List<Host>       listDevices() {
        if (listDevices == null) {
            listDevices = sniffSession.listDevices();
            Log.d(NAME_COLUMN, "Liste Devices deserialized " + listDevices.size() + " devices");
        }
        return listDevices;
    }


    @Column(name ="Date")
    public Date             date;

    public                  Pcap(String nameFile, List<Host> sniffedDevice) {
        super();
        this.nameFile = nameFile;
        this.listDevices = sniffedDevice;
        this.date = new Date();
        Log.d(TAG, "New PCAP FILE(" + nameFile + ") with " + sniffedDevice.size() + "devices");
    }
    public                  Pcap() {
        super();
    }

    public String           getFullNamePath() {
        return Singleton.getInstance().PcapPath + nameFile;
    }

    @Override public String toString() {
        return "Pcap: create the [" + new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE)
                .format(date)+ "] at [" + nameFile + "]";
    }
}
