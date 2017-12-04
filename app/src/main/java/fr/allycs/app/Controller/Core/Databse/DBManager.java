package fr.allycs.app.Controller.Core.Databse;

import java.util.List;

import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.Model.Target.Host;

public class                    DBManager {
    public static AccessPoint   saveSession(String ssid, String gateway, List<Host> hosts) {
        AccessPoint ap = DBAccessPoint.getAccessPoint(ssid);
        DBAccessPoint.saveSession(ap, gateway, hosts);
        return ap;
    }
}
