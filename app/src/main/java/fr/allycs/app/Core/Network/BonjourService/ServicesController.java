package fr.allycs.app.Core.Network.BonjourService;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Model.Net.Service;
import fr.allycs.app.Model.Target.Host;

public class                    ServicesController {

    private  static boolean     isMyDeviceInTheList(List<Host> hosts, Host hostFocused) {
        for (Host host : hosts) {
            if (host.equals(hostFocused))
                return true;
        }
        return false;
    }

    public static  int          howManyHostTheServices(List<Service> services) {
        List<Host> hostsRecorded = new ArrayList<>();
        for (Service service : services) {
            if (!isMyDeviceInTheList(hostsRecorded, service.host))
                hostsRecorded.add(service.host);
        }
        return hostsRecorded.size();
    }
}
