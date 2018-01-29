package fr.allycs.app.Model.Net;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import fr.allycs.app.Model.Target.Host;

@Table(name = "Ports", id = "_id")
public class            Port extends Model {
    public static String NAME_COLUMN = "Ports";
    @Column(name = "port")
    public String       port;
    @Column(name = "protocol")
    public String       protocol;
    @Column(name = "state")
    public String       state;
    @Column(name = "Host")
    public Host         host;

    public              Port() {
        super();
    }


    public enum         State   {
        CLOSED, OPEN, FILTERED
    }
}
