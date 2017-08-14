package su.sniff.cepter.Model.Pcap;

import java.util.StringTokenizer;

/**
 * Created by root on 02/08/17.
 */

public class                Ipv4 {
    private String          TAG = "Ipv4";
    private String          ipString = "0.0.0.0";
    private int             ipInt = 0;

    public                  Ipv4(String ip) {
        this.ipString = ip;
        StringTokenizer st = new StringTokenizer(ip, ".");
        for (int i = 3; i >= 0; i--) {
            ipInt |= Integer.parseInt(st.nextToken()) << (i * 8);
        }
    }

    public int              getIpInt() {
        return ipInt;
    }

    public String           getIpString() {
        return ipString;
    }
}
