package fr.allycs.app.Model.Target;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fr.allycs.app.Controller.Core.Database.DBHost;
import fr.allycs.app.Model.Net.Service;

@Table(name = "Session", id = "_id")
public class                Session extends Model {
    public static String    NAME_COLUMN = "Session";

    @Column(name = "Date")
    public java.util.Date   Date;

    @Column(name = "Gateway")
    public Host             Gateway;

    @Column(name = "OsNumber")
    public int              nbrOs;

    @Column(name = "typeScan")/* Arp, Icmp, Nmap*/
    public String           typeScan;

    @Column(name = "name")/* Arp, Icmp, Nmap*/
    public String           name;

    /**
     * Create the OneToMany relation
     * @return
     */
    public List<SniffSession>    SniffSessions() {
        return getMany(SniffSession.class, "Session");
    }

    @Column(name = "service")
    public List<Service>    services;

    @Column(name = "AccessPoint")
    public AccessPoint      Ap;

    @Column(name = "Devices")
    public String           listDevicesSerialized;

    private List<Host>      listDevices = null;
    /**
     * Create the ManyToMany relation
     */
    public List<Host>       listDevices() {
        if (listDevices == null) {
            listDevices = DBHost.getListFromSerialized(listDevicesSerialized);
            Log.d(NAME_COLUMN, "liste Session deserialized " + listDevices.size() + " devices");
        }
        return listDevices;
    }

    @Override
    public String           toString() {
        return new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE)
                .format(Date) +" " + listDevices().size() + " Devices Connected";
    }

    public String           getDateString() {
        return new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE).format(Date);
    }

    public                  Session() {
        super();
    }
}
