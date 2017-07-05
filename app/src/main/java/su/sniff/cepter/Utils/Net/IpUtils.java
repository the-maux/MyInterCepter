package su.sniff.cepter.Utils.Net;

import java.util.StringTokenizer;

/**
 * Created by maxim on 06/07/2017.
 */
public class                        IpUtils {
    public static Integer          getIPAsInteger(String ip) throws Exception {
        if (ip == null) {
            return null;
        }
        int result = 0;
        StringTokenizer st = new StringTokenizer(ip, ".");
        for (int i = 3; i >= 0; i--) {
            result |= Integer.parseInt(st.nextToken()) << (i * 8);
        }
        return result;
    }
}
