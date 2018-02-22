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

    public int                  valueOfPort(int port) {
        return primitivePortsLits.get(port);
    }

    public boolean              isPortOpen(int portNumber) {
       // Log.d(TAG, "isPort(" + portNumber + ")Open:" + Port.State.valueOf(primitivePortsLits.get(portNumber)) + " => " + (Port.State.valueOf(primitivePortsLits.get(portNumber)) == Port.State.OPEN));
        return primitivePortsLits.get(portNumber) == Port.State.OPEN.getValue();
    }

    public void                 dump() {
        Log.i(TAG, "22/tcp       ssh          " + Port.State.valueOf(primitivePortsLits.get(22)));
        Log.i(TAG, "23/tcp       telnet       " + Port.State.valueOf(primitivePortsLits.get(23)));
        Log.i(TAG, "25/tcp       smtp         " + Port.State.valueOf(primitivePortsLits.get(25)));
        Log.i(TAG, "53/udp       domain       " + Port.State.valueOf(primitivePortsLits.get(53)));
        Log.i(TAG, "80/tcp       http         " + Port.State.valueOf(primitivePortsLits.get(80)));
        Log.i(TAG, "110/tcp      pop3         " + Port.State.valueOf(primitivePortsLits.get(110)));
        Log.i(TAG, "135/tcp      msrpx        " + Port.State.valueOf(primitivePortsLits.get(135)));
        Log.i(TAG, "139/tcp      netbios-ssn  " + Port.State.valueOf(primitivePortsLits.get(139)));
        Log.i(TAG, "443/tcp      https        " + Port.State.valueOf(primitivePortsLits.get(443)));
        Log.i(TAG, "445/tcp      microsoft-ds " + Port.State.valueOf(primitivePortsLits.get(445)));
        Log.i(TAG, "3031/udp     unknow       " + Port.State.valueOf(primitivePortsLits.get(3031)));
        Log.i(TAG, "3128/tcp     squid-http   " + Port.State.valueOf(primitivePortsLits.get(3128)));
        Log.i(TAG, "5353/udp     zeroconf     " + Port.State.valueOf(primitivePortsLits.get(5353)));
    }

    public String               getDump() {
        return dump;
    }

}
