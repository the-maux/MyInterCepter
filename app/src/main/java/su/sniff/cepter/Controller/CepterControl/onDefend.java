package su.sniff.cepter.Controller.CepterControl;

import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Controller.System.Wrapper.RootProcess;
import su.sniff.cepter.globalVariable;

/**
 * Created by maxim on 10/07/2017.
 */

public class                onDefend {
    private String          TAG = "onDefend";
    private String[]        maclist = new String[MotionEventCompat.ACTION_MASK];
    private String[]        iplist = new String[MotionEventCompat.ACTION_MASK];

    /**
     * Si deux Host dans la List Tab ont la meme mac
     * Alors il y a une attaque Referele chmillblick plus simplement quand tout aura était printé
     * @param monitorIntercepter
     */
    public                  onDefend(TextView monitorIntercepter){
        try {
            boolean found = false;
            int nbrHostToDefend = fillListHost();
            int rcx = 0;
            while (rcx < nbrHostToDefend) {
                int rax = 0;
                while (rax < nbrHostToDefend) {
                    if (maclist[rax].equals(maclist[rcx]) && rax != rcx && Singleton.getInstance().network.gateway.equals(iplist[rcx])) {
                        monitorIntercepter.append("Warning! Gateway poisoned by " + iplist[rax] + " - " + maclist[rax] + "\n");
                        found = true;
                        RootProcess process = new RootProcess("onDefend", Singleton.getInstance().FilesPath);
                        process.exec(Singleton.getInstance().FilesPath + "/cepter " + globalVariable.adapt_num + " -r " + Singleton.getInstance().network.gateway);
                        process.exec("exit");
                        BufferedReader bufferedReader = process.getReader();
                        String read = bufferedReader.readLine();
                        String mac = read.substring(read.indexOf(58) + 1, read.length());
                        bufferedReader.close();
                        process.waitFor();
                        mac = mac.replaceAll("-", ":");
                        monitorIntercepter.append("Restoring original mac - " + mac + "\n");
                        process = new RootProcess("BUSYBOX", "/system/bin");
                        process.exec("LD_LIBRARY_PATH=" + Singleton.getInstance().FilesPath + " " + Singleton.getInstance().FilesPath + "/busybox arp -s " + Singleton.getInstance().network.gateway + " " + mac);
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

    /**
     * Reading ARP Table to guess host
     * @return
     * @throws IOException
     */
    private int        fillListHost() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
        int offsetHostToDefend = 0;
        String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
        String read;
        while ((read = bufferedReader.readLine()) != null ) {
            Log.d(TAG, "OnDefend::" + read);
            String ip = read.substring(0, read.indexOf(" "));
            Matcher matcher = Pattern.compile(String.format(MAC_RE, ip.replace(".", "\\."))).matcher(read);
            if (matcher.matches()) {
                String mac = matcher.group(1);
                maclist[offsetHostToDefend] = mac;
                iplist[offsetHostToDefend] = ip;
                offsetHostToDefend++;
            }
        }
        bufferedReader.close();
        return offsetHostToDefend;
    }
}
