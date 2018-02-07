package fr.allycs.app.Model.Target;


import android.util.Log;
import android.util.SparseIntArray;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

import fr.allycs.app.Model.Net.Port;
import fr.allycs.app.Model.Unix.Os;

public class                    Ports {
    private String              TAG = "Ports";
    private ArrayList<Port>     mPorts = new ArrayList<>();
    private SparseIntArray      primitivePortsLits = new SparseIntArray();
    public String               dump;

    public Ports(ArrayList<String> lines, Host host) {
        dump = StringUtils.join(lines, "\n");
        for (String line : lines) {
            line = line.replaceAll("  ", " ");
            if (line.contains("|"))
                initService(host, line);
            else {
                initPort(line, false);
            }
        }
    }

    /*
    ** 5353/udp closed zeroconf
    */
    private void                initPort(String line, boolean open) {
        try {
            Port port = (open) ? new Port(line) : add(line);
            primitivePortsLits.append(port.getPort(), port.state.getValue());
            mPorts.add(port);
        } catch (Exception e) {
            Log.e(TAG, "Error building Port[" + line + "]");
            e.getStackTrace();
        }
    }

    private void                initService(Host host, String line) {
        if (line.contains("/tcp") || line.contains("/udp")) {
            initPort(line.replaceAll("  ", " ").replace("|", ""), true);
        } else if (line.contains("model=") && !line.contains("rmodel=")) {
            host.vendor = line.replace("model=", "").split(",")[0];
            host.vendor = host.vendor.replace("|", "").trim();
            host.osType = Os.Apple;
        } else {
            //DEBUG
            //Log.e(TAG, "initService::[" + line + "]");
        }
    }

    public Port                 add(String dumpPortLine) {
        String tmp =  dumpPortLine.trim().replaceAll(" +", " ");
        String[] dumpssplited = tmp.split(" ");
        return new Port(dumpssplited[0], dumpssplited[1], dumpssplited[2]);
    }

    public Ports                add(Port port) {
        mPorts.add(port);
        return this;
    }

    public boolean              isPortOpen(int portNumber) {
        return Port.State.valueOf(primitivePortsLits.get(portNumber)) == Port.State.OPEN;
    }

    public void                 dump() {
        Log.e(TAG, "Ports dumps:" + dump);
    }

    public String               getDump() {
        return dump;
    }

}
