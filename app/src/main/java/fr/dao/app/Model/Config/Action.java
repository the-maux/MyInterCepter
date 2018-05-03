package fr.dao.app.Model.Config;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.dao.app.Model.Target.Network;

@Table(name = "Action", id = "_id")
public class                Action extends Model {
    @Column(name = "Session")
    public Session          session;

    @Column(name = "Network")
    public Network          network;
    @Column(name = "Type")
    public actionType       type;
    @Column(name = "Date")
    public Date             date;

    @Column(name = "offensifAction")
    public int              offensifAction;
    @Column(name = "defensifAction")
    public int              defensifAction;

    public enum             actionType {
        SPY, SNIFF, DNSSPPOOF, WEBSERVER, SCAN, VULNSCAN, DORA, CRYPTCHECK,
    }

    public String           getStringDate() {
        return new SimpleDateFormat("dd/MMMM-k:mm:ss", Locale.FRANCE).format(date);
    }

    Action() {
        super();
    }
}
