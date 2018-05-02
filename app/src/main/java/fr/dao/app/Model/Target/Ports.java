package fr.dao.app.Model.Target;


import android.util.Log;
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.Collections;

import fr.dao.app.Core.Configuration.Comparator.Comparators;
import fr.dao.app.Model.Net.Port;
import fr.dao.app.Model.Net.PortState;
import fr.dao.app.Model.Unix.Os;

public class                    Ports {
    private String              TAG = "getPorts";
    private ArrayList<Port>     mPorts = new ArrayList<>();
    private SparseIntArray      primitivePortsLits = new SparseIntArray();

    /**
     * Parsing from Host.getPorts()
     * @param host
     * @throws Exception
     */
    public                      Ports(Host host) {
        if (host.dumpPort == null) {
            Log.e(TAG, "No dump to analyze");
        }
        init(host.dumpPort, host);
    }

    public void                 init(String dump, Host host) {
        for (String line : dump.split("\n")) {
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
            if (Character.isDigit(host.vendor.charAt(host.vendor.length()-1))) {
                if (Character.isDigit(host.vendor.charAt(host.vendor.length()-2))) {
                    host.vendor = host.vendor.substring(0, host.vendor.length()-2) +
                            " " + host.vendor.substring(host.vendor.length()-2, host.vendor.length());
                } else {
                    host.vendor = host.vendor.substring(0, host.vendor.length()-1) +
                            " " + host.vendor.substring(host.vendor.length()-1, host.vendor.length());
                }
            }
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
        // Log.d(TAG, "isPort(" + portNumber + ")Open:" + Port.PortState.valueOf(primitivePortsLits.get(portNumber)) + " => " + (Port.PortState.valueOf(primitivePortsLits.get(portNumber)) == Port.PortState.OPEN));
        return primitivePortsLits.get(portNumber) == PortState.OPEN.value;
    }

    public void                 dump() {
        Log.i(TAG, "22/tcp       ssh          " + PortState.valueOf(primitivePortsLits.get(22)));
        Log.i(TAG, "23/tcp       telnet       " + PortState.valueOf(primitivePortsLits.get(23)));
        Log.i(TAG, "25/tcp       smtp         " + PortState.valueOf(primitivePortsLits.get(25)));
        Log.i(TAG, "53/udp       dns          " +  PortState.valueOf(primitivePortsLits.get(53)));
        Log.i(TAG, "80/tcp       http         " + PortState.valueOf(primitivePortsLits.get(80)));
        Log.i(TAG, "110/tcp      pop3         " + PortState.valueOf(primitivePortsLits.get(110)));
        Log.i(TAG, "135/tcp      msrpx        " + PortState.valueOf(primitivePortsLits.get(135)));
        Log.i(TAG, "139/tcp      netbios-ssn  " + PortState.valueOf(primitivePortsLits.get(139)));
        Log.i(TAG, "443/tcp      https        " + PortState.valueOf(primitivePortsLits.get(443)));
        Log.i(TAG, "445/tcp      microsoft-ds " + PortState.valueOf(primitivePortsLits.get(445)));
        Log.i(TAG, "2869/tcp     upnp Server  " + PortState.valueOf(primitivePortsLits.get(2869)));
        Log.i(TAG, "3031/udp     unknow       " + PortState.valueOf(primitivePortsLits.get(3031)));
        Log.i(TAG, "3128/tcp     squid-http   " + PortState.valueOf(primitivePortsLits.get(3128)));
        Log.i(TAG, "5353/udp     zeroconf     " + PortState.valueOf(primitivePortsLits.get(5353)));
    }

    public ArrayList<Port>      portArrayList() {
        Collections.sort(mPorts, Comparators.getPortComparator());
        return mPorts;
    }

    public Port                 getPortForAdapter(int position) {
        Log.d(TAG, "getPortForAdapter(" + position + ") returning Port =>" + mPorts.get(position).toString());
        return mPorts.get(position);
    }
}
