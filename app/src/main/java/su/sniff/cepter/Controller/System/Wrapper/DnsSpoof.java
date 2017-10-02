package su.sniff.cepter.Controller.System.Wrapper;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import su.sniff.cepter.Model.Pcap.DNSSpoofItem;

/**
 * Created by the-maux on 02/10/17.
 */

public class                    DnsSpoof {
    private String              TAG = "DnsSpoof";
    public List<DNSSpoofItem>   listDomainSpoofed;
    private String              PATH_HOST_FILE = "/etc/dnsmasq.hosts";
    public                      DnsSpoof() {
        File file;
        FileReader fileReader;
        try {
            listDomainSpoofed = new ArrayList<>();
            file = new File(PATH_HOST_FILE);
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
                file = new File(PATH_HOST_FILE);
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

    public void                 dumpDomainList() {
        BufferedWriter output;
        try {
            File file = new File(PATH_HOST_FILE);
            output = new BufferedWriter(new FileWriter(file));
            for (DNSSpoofItem dnsSpoofItem : listDomainSpoofed) {
                output.write(dnsSpoofItem.domainAsked + " " + dnsSpoofItem.domainSpoofed + '\n');
            }
            output.close();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void                 removeDomain(DNSSpoofItem domainAsked) {
        Log.d(TAG, "removing DNS spoofed nbr:" + listDomainSpoofed.indexOf(domainAsked));
        listDomainSpoofed.remove(listDomainSpoofed.indexOf(domainAsked));
    }

    public void                 clear() {
        listDomainSpoofed.clear();
    }
}
