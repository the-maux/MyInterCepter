package fr.dao.app.Core.Scan;

import java.util.ArrayList;

import fr.dao.app.Model.Target.Host;

public class                        PortParser {
    /**
     * 5353/udp open   zeroconf
     | dns-service-discovery:
     |   49600/tcp http
     |     Address=10.16.186.167 fe80:0:0:0:8c1:dc67:c4cc:4b15
     |   59544/tcp companion-link
     |     rpBA=86:4D:AB:5D:21:2C
     |     rpVr=120.51
     |     rpHI=233259abbbba
     |     rpHN=9348e33de016
     |     rpHA=bf1e06edf5f7
     |     model=MacBookPro11,2
     |     osxvers=17
     |_    Address=10.16.186.167 fe80:0:0:0:8c1:dc67:c4cc:4b15
     */
    public static int               getPortList(String[] line, int i, Host host) throws Exception {
        ArrayList<String> ports = new ArrayList<>();
        for (; i < line.length; i++) {
            if (!(line[i].contains("open") || line[i].contains("close") || line[i].contains("filtered"))) {
                if (line[i].startsWith("| ") && line[i].endsWith(": ")) {//Entering script detail
                    while (i < line.length && !line[i].startsWith("|_")) {//eating script detail on port
                        ports.add(line[i++].replaceAll("  ", " "));
                    }
                } else {//ADD the LINE of port, ex: '443/tcp      https        FILTERED'
                    host.buildPorts(ports);
                    return i-1;
                }
            } else {
                ports.add(line[i].replaceAll("  ", " "));
            }
        }
        return i;
    }
    /**
     * 5353/udp open   zeroconf
     | dns-service-discovery:
     |   49600/tcp http
     |     Address=10.16.186.167 fe80:0:0:0:8c1:dc67:c4cc:4b15
     |   59544/tcp companion-link
     |     rpBA=86:4D:AB:5D:21:2C
     |     rpVr=120.51
     |     rpHI=233259abbbba
     |     rpHN=9348e33de016
     |     rpHA=bf1e06edf5f7
     |     model=MacBookPro11,2
     |     osxvers=17
     |_    Address=10.16.186.167 fe80:0:0:0:8c1:dc67:c4cc:4b15
     */
    public static void               parsePorts4Vulns(String[] line, Host host, ExploitScanner scanner) {
        ArrayList<String> ports = new ArrayList<>();
        for (int i=0; i < line.length; i++) {
            if (line[i].contains("open") || line[i].contains("close") || line[i].contains("filtered"))       {
                ports.add(line[i]);
            }
        }
        host.buildPorts(ports);
        scanner.hostActualized();
    }
}
