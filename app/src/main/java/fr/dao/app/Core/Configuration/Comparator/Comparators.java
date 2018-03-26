package fr.dao.app.Core.Configuration.Comparator;

import java.util.Comparator;
import java.util.Objects;

import fr.dao.app.Model.Net.Port;
import fr.dao.app.Model.Target.Host;

public class                            Comparators {

    public static Comparator<Host>      getHostComparator() {
        return new Comparator<Host>() {

            public int compare(Host o1, Host o2) {
                if (o1.state == o2.state) {
                    String ip1[] = o1.ip.replace(" ", "").replace(".", "::").split("::");
                    String ip2[] = o2.ip.replace(" ", "").replace(".", "::").split("::");
                    if (Integer.parseInt(ip1[2]) > Integer.parseInt(ip2[2]))
                        return 1;
                    else if (Integer.parseInt(ip1[2]) < Integer.parseInt(ip2[2]))
                        return -1;
                    else if (Integer.parseInt(ip1[3]) > Integer.parseInt(ip2[3]))
                        return 1;
                    else if (Integer.parseInt(ip1[3]) < Integer.parseInt(ip2[3]))
                        return -1;
                } else {
                    if (o1.state == Host.State.ONLINE || o2.state == Host.State.OFFLINE)
                        return -1;
                    else if (o2.state == Host.State.ONLINE || o1.state == Host.State.OFFLINE)
                        return 1;
                }
                return 0;
            }

            ;
        };
    }

    public static Comparator<Port>      getPortComparator() {
        return new Comparator<Port>() {

            public int compare(Port o1, Port o2) {
                if (Objects.equals(o1.protocol, o2.protocol)) {
                    if (o1.getPort() > o2.getPort())
                        return 1;
                    else
                        return -1;
                } else {
                    if (o1.protocol.contains("tcp"))
                        return 1;
                }
                return 0;
            }

            ;
        };
    }
}
