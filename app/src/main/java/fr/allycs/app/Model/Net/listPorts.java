package fr.allycs.app.Model.Net;


import android.util.Log;
import android.util.SparseIntArray;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import fr.allycs.app.Model.Target.Host;

public class                    listPorts {
    private String              TAG = "listPorts";
    private ArrayList<Port>     mPorts = new ArrayList<>();
    private SparseIntArray      primitivePortsLits = new SparseIntArray();
    private String              dump;
    private Host                host;

    public                      listPorts(ArrayList<String> lines, Host host) {
        dump = StringUtils.join(lines, "\n");
        for (String line : lines) {
            line = line.replaceAll("  ", " ");
            if (line.contains("|"))
                initService(host, line);
            else {
                initPort(host, line, false);
            }
        }
        dump();
    }

    /*
    * 5353/udp closed zeroconf
    */
    private void                initPort(Host host, String line, boolean open) {
        String DumpedLine = line.trim().replaceAll(" +", " ");
        Port port = (open) ? new Port(line) : add(line);
        try {
            primitivePortsLits.append(port.getPort(), port.state.getValue());
        } catch (Exception e) {
            Log.e(TAG, "Error building Port[" + line + "]");
            e.getStackTrace();
        }
        mPorts.add(port);
    }

    /**
     | dns-service-discovery:
     |   49804/tcp companion-link
     |     rpBA=BB:AF:8F:77:DC:AD
     |     rpVr=120.51
     |     rpHI=9bfff01882c6
     |     rpHN=5b3e412e991f
     |     rpHA=2c264c268b6a
     |     model=MacBookPro11,4
     |     osxvers=17
     |_    Address=10.16.187.114 fe80:0:0:0:c38:76b8:7a27:48f3
     */
    private void                initService(Host host, String line) {
        if (line.contains("/tcp") || line.contains("/udp")) {
            initPort(host, line.replaceAll("  ", " ").replace("|", ""), true);
        } else if (line.contains("model=") && !line.contains("rmodel=")) {
            host.vendor = line.replace("model=", "").split(",")[0];
            host.vendor = host.vendor.replace("|", "").trim();
        } else {
            //DEBUG
            Log.e(TAG, "initService::[" + line + "]");
        }
    }

    public Port                 add(String dumpPortLine) {
        String tmp =  dumpPortLine.trim().replaceAll(" +", " ");
        String[] dumpssplited = tmp.split(" ");
        return new Port(dumpssplited[0], dumpssplited[1], dumpssplited[2]);
    }

    public listPorts            add(Port port) {
        mPorts.add(port);
        return this;
    }


    public boolean              isPortOpen(int portNumber) {
        return Port.State.valueOf(primitivePortsLits.get(portNumber)) == Port.State.OPEN;
    }

    public void                 dump() {
//        Log.e(TAG, "Ports dumps:" + dump);

    }
}
