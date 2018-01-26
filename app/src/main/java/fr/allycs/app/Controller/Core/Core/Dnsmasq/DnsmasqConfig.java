package fr.allycs.app.Controller.Core.Core.Dnsmasq;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.AndroidUtils.Utils;
import fr.allycs.app.Controller.Core.Core.RootProcess;
import fr.allycs.app.Model.Target.DNSSpoofItem;

public class                    DnsmasqConfig {
    private String              TAG = "DnsmasqConfig";
    public static String        PATH_CONF_FILE = "/etc/dnsmasq.conf ";
    public static String        PATH_HOST_FILE = "/etc/dnsmasq.hosts";
    public static String        PATH_RESOLV_FILE = "/etc/resolv.conf";
    public List<DNSSpoofItem>   listDomainSpoofable;

    DnsmasqConfig() {
        listDomainSpoofable = new ArrayList<>();
        readDnsFromFile();
    }

    private void                readDnsFromFile() {
        FileReader fileReader;
        File file = new File(PATH_HOST_FILE);
        clear();
        try {
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.d("DNS CONF::readDnsconf", line);
                listDomainSpoofable.add(new DNSSpoofItem(line.split(" ")[0], line.split(" ")[1]));
            }
        } catch (IOException e) {
            try {
                file = new File(PATH_HOST_FILE);
                fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Log.d("DNS CONF", line);
                    listDomainSpoofable.add(new DNSSpoofItem(line.split(" ")[0], line.split(" ")[1]));
                }
            } catch (IOException e2) {
                e2.getStackTrace();
            }
        }
    }
    public void                 flushFromFile() {
        readDnsFromFile();
    }
    public void                 clear() {
        listDomainSpoofable.clear();
    }
    public void                 saveConf() {
        if (Utils.ReadOnlyFileSystemOFF() != -1) {
            int rax = 0;
            StringBuilder tmp = new StringBuilder("");
            for (DNSSpoofItem dnsSpoofItem : listDomainSpoofable) {
                if (rax == 0) {
                    tmp.append("echo \"")
                            .append(dnsSpoofItem.ip)
                            .append(" www.")
                            .append(dnsSpoofItem.domain)
                            .append(" ")
                            .append(dnsSpoofItem.domain)
                            .append("\" > ")
                            .append(DnsmasqConfig.PATH_HOST_FILE);
                } else {
                    tmp.append(" && echo \"")
                            .append(dnsSpoofItem.ip)
                            .append(" www.")
                            .append(dnsSpoofItem.domain)
                            .append(" ")
                            .append(dnsSpoofItem.domain)
                            .append("\" >> ")
                            .append(DnsmasqConfig.PATH_HOST_FILE);
                }
                rax++;
            }
            Log.d(TAG, "saveConf:" + tmp.toString().replace("www.www.", "www."));
            new RootProcess("initialisation ")
                    .exec(tmp.toString().replace("www.www.", "www."))
                    .closeProcess();
        }
    }
    public void                 addHost(String ip, String domain) {
        listDomainSpoofable.add(0, new DNSSpoofItem(ip, domain));
    }
}
