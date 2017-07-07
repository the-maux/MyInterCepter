package su.sniff.cepter.Model;

/**
 * Created by the-maux on 07/07/17.
 */
public class            Port {
    public enum         Protocol {
        TCP, UDP
    }
    public enum         State   {
        CLOSED, OPEN, FILTERED
    }
    private int         port;
    private Protocol    protocol;
    private State       state;

}
