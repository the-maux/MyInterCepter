package fr.allycs.app.Model.Net;

import java.util.HashMap;
import java.util.Map;

import fr.allycs.app.Model.Target.Host;

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
            switch (pageType) {
                case "CLOSED":
                    return CLOSED;
                case "OPEN":
                    return OPEN;
                case "FILTERED":
                    return FILTERED;
                case "UNFILTERED":
                    return UNFILTERED;
                case "OPEN_FILTERED":
                    return OPEN_FILTERED;
                case "OPEN_UNFILTERED":
                    return CLOSED_FILTERED;
                default:
                    return UNKNOW;
            }
        }

        public int getValue() {
            return value;
        }

        }

    /*  Exemple port: 22/tcp   closed ssh */
    public Port(String port_proto, String state, String protocolName) {
        super();
        this.port = port_proto;
        this.state = State.valueOf(state.toUpperCase().replace("|", "_"), 0);
        protocol = protocolName;
    }

    public int          getPort() {
        return Integer.valueOf(port.split("/")[0]);
    }

    public String       toString() {
        return port + "  " + state.name() + " " + protocol;
    }
}
