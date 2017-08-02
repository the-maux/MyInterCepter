package su.sniff.cepter.Controller.CepterControl;

import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import su.sniff.cepter.Controller.System.RootProcess;
import su.sniff.cepter.globalVariable;

/**
 * Created by maxim on 10/07/2017.
 */

public class                onDefend {
    private String          TAG = "onDefend";
    private String[]        maclist = new String[MotionEventCompat.ACTION_MASK];
    private String[]        iplist = new String[MotionEventCompat.ACTION_MASK];

    public                  onDefend(TextView monitorIntercepter){
        try {
            boolean found = false;
            int nbrHostToDefend = fillListHost();
            int rcx = 0;
            while (rcx < nbrHostToDefend) {
                int rax = 0;
                while (rax < nbrHostToDefend) {
                    if (maclist[rax].equals(maclist[rcx]) && rax != rcx && globalVariable.gw_ip.equals(iplist[rcx])) {
                        monitorIntercepter.append("Warning! Gateway poisoned by " + iplist[rax] + " - " + maclist[rax] + "\n");
                        found = true;
                        RootProcess process = new RootProcess("onDefend", globalVariable.path + "");
                        process.exec(globalVariable.path + "/cepter " + globalVariable.adapt_num + " -r " + globalVariable.gw_ip);
                        process.exec("exit");
                        BufferedReader bufferedReader = process.getReader();
                        String read = bufferedReader.readLine();
                        String mac = read.substring(read.indexOf(58) + 1, read.length());
                        bufferedReader.close();
                        process.waitFor();
                        mac = mac.replaceAll("-", ":");
                        monitorIntercepter.append("Restoring original mac - " + mac + "\n");
                        process = new RootProcess("BUSYBOX", "/system/bin");
                        process.exec("LD_LIBRARY_PATH=" + globalVariable.path + " " + globalVariable.path + "/busybox arp -s " + globalVariable.gw_ip + " " + mac);
                        process.closeProcess();
                        break;
                    }
                    rax++;
                }
                rcx++;
            }
            if (!found) {
                monitorIntercepter.append("ARP Cache is clean. No attacks detected.\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int        fillListHost() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
        int nbrHostToDefend = 0;
        String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
        while (true) {
            String read = bufferedReader.readLine();
            if (read == null) {
                break;
            }
            Log.d(TAG, "OnDefend::" + read);
            String ip = read.substring(0, read.indexOf(" "));
            Matcher matcher = Pattern.compile(String.format(MAC_RE, ip.replace(".", "\\."))).matcher(read);
            if (matcher.matches()) {
                String mac = matcher.group(1);
                maclist[nbrHostToDefend] = mac;
                iplist[nbrHostToDefend] = ip;
                nbrHostToDefend++;
            }
        }
        bufferedReader.close();
        return nbrHostToDefend;
    }
}
