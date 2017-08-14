package su.sniff.cepter.Model.Pcap;

/**
 * Created by root on 02/08/17.
 */

public class                Trame {
    private int             offsetInStream;
    private String          time;
    private Protocol        protocol;
    private Ipv4            src, dest;
    private byte[]          bufferByte;
    private String          dataBuffer;

    public                  Trame(String dump) {

    }
}
