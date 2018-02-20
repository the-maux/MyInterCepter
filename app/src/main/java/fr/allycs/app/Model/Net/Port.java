package fr.allycs.app.Model.Net;

import java.util.HashMap;
import java.util.Map;

import fr.allycs.app.Model.Target.Host;

import static fr.allycs.app.Model.Net.Port.State.OPEN;

public class            Port {
    public String       TAG = "Port";
    public String       port;
    public String       protocol;
    public State        state;
    public Host         host;


    public enum         State   {
        CLOSED(0), OPEN(1), FILTERED(2), UNFILTERED(3), OPEN_FILTERED(4), CLOSED_FILTERED(5), UNKNOW(6);


        private int value;
        private static Map map = new HashMap<>();

        State(int value) {
            this.value = value;
        }

        static {
            for (State pageType : State.values()) {
                map.put(pageType.value, pageType);
            }
        }
        public static State valueOf(int pageType) {
            return (State) State.map.get(pageType);
        }

        public static State valueOf(String pageType, int a) {
            pageType = pageType.toUpperCase().replace("|", "_");
            switch (pageType) {
                case "OPEN_FILTERED":
                    return OPEN_FILTERED;
                case "CLOSED_FILTERED":
                    return CLOSED_FILTERED;
                case "FILTERED":
                    return FILTERED;
                case "UNFILTERED":
                    return UNFILTERED;
                case "OFFLINE":
                    return CLOSED;
                case "ONLINE":
                    return OPEN;
                default:
                    return UNKNOW;
            }
        }

        public int getValue() {
            return value;
        }

        @Override
        public String           toString() {
            switch (valueOf(value)) {
                case OPEN_FILTERED:
                    return "OPEN_FILTERED";
                case CLOSED_FILTERED:
                    return "CLOSED_FILTERED";
                case FILTERED:
                    return "FILTERED";
                case UNFILTERED:
                    return "UNFILTERED";
                case CLOSED:
                    return "OFFLINE";
                case OPEN:
                    return "ONLINE";
                default:
                    return "UNKNOW";
            }
        }
    }
    public Port(String line) {
        String[] dumpSplitetd = line.trim().split(" ");
        this.state = OPEN;
        this.port = dumpSplitetd[0];
        this.protocol = dumpSplitetd[1];
    }

    /*  Exemple port: 22/tcp   closed ssh */
    public Port(String port_proto, String state, String protocolName) {
        super();
        //Log.i(TAG, "Building:\t" + port_proto + "  " + state + " -> " + protocolName);
        this.port = port_proto;
        this.state = State.valueOf(state, 0);
        this.protocol = protocolName;
    }

    public int          getPort() {
        return Integer.valueOf(port.split("/")[0]);
    }

    public String       toString() {
        return port + "  " + state.name() + " " + protocol;
    }
}
