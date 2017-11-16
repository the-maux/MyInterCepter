package su.sniff.cepter.Model.Net;

import com.google.gson.annotations.SerializedName;

import java.util.StringTokenizer;

import su.sniff.cepter.Model.Target.MyObject;

/**
 * Created by root on 02/08/17.
 */

public class                Ipv4 extends MyObject {
    private String          TAG = "Ipv4";
    @SerializedName("ip_str")
    private String          ipString = "0.0.0.0";
    @SerializedName("ip_int")
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
