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
    public static String        PATH_CONF_FILE = "/etc/dnsmasq.hosts";
    public static String        PATH_RESOLV_FILE = "/etc/resolv.conf";
    public List<DNSSpoofItem>   listDomainSpoofed;

    DnsConf() {
        listDomainSpoofed = new ArrayList<>();
        readDnsFromFile();
    }

    private void                 readDnsFromFile() {

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
        BufferedWriter output;
        try {
            File file = new File(PATH_CONF_FILE);
            output = new BufferedWriter(new FileWriter(file));
            for (DNSSpoofItem dnsSpoofItem : listDomainSpoofed) {
                output.write(dnsSpoofItem.domainAsked + " " + dnsSpoofItem.domainSpoofed + '\n');
            }
            output.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void                 addHost(String ip, String domain) {
        listDomainSpoofed.add(0, new DNSSpoofItem(ip, domain));
    }
}
