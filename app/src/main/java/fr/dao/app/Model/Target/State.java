package fr.dao.app.Model.Target;

import java.util.HashMap;
import java.util.Map;

public enum                 State   {
    OFFLINE(0), ONLINE(1), FILTERED(2), UNKNOW(3);

    private int             value;
    private static Map      map = new HashMap<>();

    State(int value) {
        this.value = value;
    }

    static {
        for (State pageType : State.values()) {
            map.put(pageType.value, pageType);
        }
    }
    public static State     valueOf(int pageType) {
        return (State) State.map.get(pageType);
    }

    public static State     valueOf(String pageType, int a) {
        pageType = pageType.toUpperCase().replace("|", "_");
        switch (pageType) {
            case "FILTERED":
                return FILTERED;
            case "OFFLINE":
                return OFFLINE;
            case "ONLINE":
                return ONLINE;
            default:
                return FILTERED;
        }
    }

    public int              getValue() {
        return value;
    }

    public String           toString() {
        switch (valueOf(value)) {
            case FILTERED:
                return "FILTERED";
            case OFFLINE:
                return "OFFLINE";
            case ONLINE:
                return "ONLINE";
            default:
                return "UNKNOWN";
        }
    }
}
