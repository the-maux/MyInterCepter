package fr.allycs.app.Model.Net;

import com.google.gson.annotations.SerializedName;

public class            Port {
    public enum         Protocol {
        TCP, UDP
    }
    public enum         State   {
        CLOSED, OPEN, FILTERED
    }
    @SerializedName("port")
    public int         port;
    @SerializedName("protocol")
    public Protocol    protocol;
    @SerializedName("state")
    public State       state;

}
