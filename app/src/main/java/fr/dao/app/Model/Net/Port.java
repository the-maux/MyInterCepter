package fr.dao.app.Model.Net;

import android.util.Log;

import fr.dao.app.Model.Target.Host;

public class            Port {
    public String       TAG = "Port";
    public String       TAG2 = "Porc";
    public String       port;
    public String       protocol;
    public String       service;
    public Host         host;
    public PortState    state;

    public Port(String line) {
        String[] dumpSplitetd = line.trim().split(" ");
        this.state = PortState.CLOSED;
        this.port = dumpSplitetd[0];
        this.protocol = dumpSplitetd[1];
        if (line.contains("filtered"))
            this.state = PortState.FILTERED;
        else if (line.contains("open"))
            this.state = PortState.OPEN;
        this.service = dumpSplitetd[dumpSplitetd.length-1];
    }

    /*  Exemple port: 22/tcp   closed ssh */
    public Port(String port_proto, String state, String protocolName) {
        super();
       // Log.w(TAG, "Building:\t" + port_proto + "  " + state + " -> " + protocolName);
        this.port = port_proto;
        this.state = PortState.valueOf(state, 0, 0);
        this.protocol = protocolName;
    }

    public int          getPort() {
        return Integer.valueOf(port.split("/")[0]);
    }

    public String       toString() {
        return port + "  " + state.name() + " " + protocol;
    }

    public boolean      isOpen() {
        return state == PortState.OPEN || state == PortState.OPEN_FILTERED;
    }
}
