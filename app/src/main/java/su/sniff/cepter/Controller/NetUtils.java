package su.sniff.cepter.Controller;

import android.util.Log;
import su.sniff.cepter.globalVariable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by maxim on 29/06/2017.
 */
public class                NetUtils {
    private static String   TAG = "NetUtils";
    private static String   MAC = null;

    public static String    getMac() throws FileNotFoundException {
        if (MAC == null) {
            Log.d(TAG, "Reading /proc/net/arp");
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
            String mac = "";
            try {
                String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
                while (true) {
                    String read = bufferedReader.readLine();
                    if (read == null) {
                        break;
                    }
                    Matcher matcher = Pattern.compile(String.format(MAC_RE, read.substring(0, read.indexOf(" ")).replace(".", "\\."))).matcher(read);
                    if (matcher.matches()) {
                        mac = matcher.group(1);
                        if (globalVariable.own_ip.equals(globalVariable.gw_ip)) {
                            break;
                        }
                    }
                }
                bufferedReader.close();
                return mac;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            return MAC;
        return "MAC INTROUVABLE";
    }
}
