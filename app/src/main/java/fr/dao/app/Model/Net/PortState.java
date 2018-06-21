package fr.dao.app.Model.Net;

import java.util.HashMap;
import java.util.Map;

public enum                     PortState    {
    CLOSED(0), OPEN(1), FILTERED(2), UNFILTERED(3), OPEN_FILTERED(4), CLOSED_FILTERED(5), UNKNOWN(6);
    public int                  value;
    private static Map          map = new HashMap<>();

    PortState(int value) {
        this.value = value;
    }

    static {
        for (PortState pageType : PortState.values()) {
            map.put(pageType.value, pageType);
        }
    }
    public static PortState     valueOf(int pageType) {
        return (PortState) map.get(pageType);
    }

    public static PortState     valueOf(String pageType, int a, int b) {
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
            case "CLOSED":
                return CLOSED;
            case "OPEN":
                return OPEN;
            default:
                return UNKNOWN;
        }
    }

    public int                  getValue() {
        return value;
    }

    public String               toString() {
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
                return "CLOSED";
            case OPEN:
                return "OPEN";
            default:
                return "UNKNOWN";
        }
    }
}
