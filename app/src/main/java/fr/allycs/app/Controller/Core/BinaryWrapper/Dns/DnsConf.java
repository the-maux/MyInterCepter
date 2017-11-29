package fr.allycs.app.Controller.Core.BinaryWrapper.Dns;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.Core.BinaryWrapper.RootProcess;
import fr.allycs.app.Controller.Misc.Utils;
import fr.allycs.app.Model.Target.DNSSpoofItem;

public class                    DnsConf {
    private String              TAG = "DnsConf";
    public static String        PATH_CONF_FILE = "/etc/dnsmasq.hosts";
    public static String        PATH_RESOLV_FILE = "/etc/resolv.conf";
    public List<DNSSpoofItem>   listDomainSpoofed;

    DnsConf() {
        listDomainSpoofed = new ArrayList<>();
        readDnsFromFile();
    }

    private void                readDnsFromFile() {
        FileReader fileReader;
        File file = new File(PATH_CONF_FILE);
        clear();
        try {
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.d("DNS CONF::readDnsconf", line);
                listDomainSpoofed.add(new DNSSpoofItem(line.split(" ")[0], line.split(" ")[1]));
            }
        } catch (IOException e) {
            try {
                file = new File(PATH_CONF_FILE);
                fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Log.d("DNS CONF", line);
                    listDomainSpoofed.add(new DNSSpoofItem(line.split(" ")[0], line.split(" ")[1]));
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
        listDomainSpoofed.clear();
    }
    public void                 saveConf() {
        if (Utils.ReadOnlyFileSystemOFF() != -1) {
            int rax = 0;
            StringBuilder tmp = new StringBuilder("");
            for (DNSSpoofItem dnsSpoofItem : listDomainSpoofed) {
                if (rax == 0) {
                    tmp.append("echo \"")
                            .append(dnsSpoofItem.ip)
                            .append(" www.")
                            .append(dnsSpoofItem.domain)
                            .append(" ")
                            .append(dnsSpoofItem.domain)
                            .append("\" > ")
                            .append(DnsConf.PATH_CONF_FILE);
                } else {
                    tmp.append(" && echo \"")
                            .append(dnsSpoofItem.ip)
                            .append(" www.")
                            .append(dnsSpoofItem.domain)
                            .append(" ")
                            .append(dnsSpoofItem.domain)
                            .append("\" >> ")
                            .append(DnsConf.PATH_CONF_FILE);
                }
                rax++;
            }
            Log.d(TAG, "saveConf:" + tmp.toString().replace("www.www.", "www."));
            new RootProcess("initialisation ")
                    .exec(tmp.toString().replace("www.www.", "www.")).
                    closeProcess();
        }
    }
    public void                 addHost(String ip, String domain) {
        listDomainSpoofed.add(0, new DNSSpoofItem(ip, domain));
    }
}
