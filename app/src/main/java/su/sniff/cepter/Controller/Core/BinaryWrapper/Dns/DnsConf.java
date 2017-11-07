package su.sniff.cepter.Controller.Core.BinaryWrapper.Dns;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import su.sniff.cepter.Controller.Core.BinaryWrapper.RootProcess;
import su.sniff.cepter.Model.Target.DNSSpoofItem;

public class                    DnsConf {
    public String               PATH_CONF_FILE = "/etc/dnsmasq.hosts";
    public List<DNSSpoofItem>   listDomainSpoofed;

    DnsConf() {
        listDomainSpoofed = new ArrayList<>();
    }

    public void                 readDnsFromFile() {
        FileReader fileReader;
        File file = new File(PATH_CONF_FILE);
        try {
            fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.d("DNS CONF", line);
                listDomainSpoofed.add(new DNSSpoofItem(line.split(" ")[0], line.split(" ")[1]));
            }
        } catch (IOException e) {
            try {
                new RootProcess("Install").exec("chmod 644 /etc/dnsmasq.hosts").closeProcess();
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

    public void                 clear() {
        listDomainSpoofed.clear();
    }

    public void                 saveConf(String nameOfFile) {
        BufferedWriter output;
        try {
            File file = new File(PATH_CONF_FILE);
            output = new BufferedWriter(new FileWriter(file));
            for (DNSSpoofItem dnsSpoofItem : listDomainSpoofed) {
                output.write(dnsSpoofItem.domainAsked + " " + dnsSpoofItem.domainSpoofed + '\n');
            }
            output.close();
            PATH_CONF_FILE = nameOfFile;
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void                 addHost(String ip, String domain) {
        listDomainSpoofed.add(0, new DNSSpoofItem(ip, domain));
    }
}
