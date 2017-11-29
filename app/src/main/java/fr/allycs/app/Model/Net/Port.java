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
    private int         port;
    @SerializedName("protocol")
    private Protocol    protocol;
    @SerializedName("state")
    private State       state;

}
