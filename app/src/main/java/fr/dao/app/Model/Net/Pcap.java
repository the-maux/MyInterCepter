package fr.dao.app.Model.Net;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.SniffSession;

/**
 * TODO: Put how many long in time the sniff was
 */
@Table(name = "Pcap", id = "_id")
public class                Pcap extends Model {
    private String          TAG = "Pcap";
    private String          NAME_COLUMN = "Pcap";

    @Column(name ="lastScanDate")
    public Date             date;
    @Column(name ="NameFile")
    public String           nameFile;
    @Column(name = "SniffSession")
    public SniffSession     sniffSession;
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

    public                  Pcap() {
        super();
    }

    public                  Pcap(String nameFile, List<Host> sniffedDevice) {
        super();
        this.nameFile = nameFile;
        listDevicesSerialized = DBHost.SerializeListDevices(sniffedDevice);
        this.date = new Date();
        Log.d(TAG, "New PCAP FILE(" + nameFile + ") with " + sniffedDevice.size() + "devices");
    }

    public String           getFullNamePath() {
        return Singleton.getInstance().PcapPath + nameFile;
    }
    public String           getDate() {
        return new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE).format(date);
    }
    public File             getFile() {
        return new File(Singleton.getInstance().PcapPath + nameFile);
    }

    public String           toString() {
        return "Pcap: create the [" + new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE)
                .format(date)+ "] at [" + nameFile + "]";
    }
}
