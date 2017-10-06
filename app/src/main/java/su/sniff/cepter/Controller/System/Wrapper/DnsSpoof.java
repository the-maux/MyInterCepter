package su.sniff.cepter.Controller.System.Wrapper;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Model.Pcap.DNSSpoofItem;
import su.sniff.cepter.Model.Wrap.ConsoleLog;
import su.sniff.cepter.View.Adapter.ConsoleLogAdapter;

/**
 * Created by the-maux on 02/10/17.
 */

public class                    DnsSpoof {
    private String              TAG = "DnsSpoof";
    public List<DNSSpoofItem>   listDomainSpoofed;
    public List<ConsoleLog>     consoleLogList = new ArrayList<>();
    private String              PATH_HOST_FILE = "/etc/dnsmasq.hosts";
    private RootProcess         process;
    private ConsoleLogAdapter   consoleAdapter = null;

    public                      DnsSpoof() {
        listDomainSpoofed = new ArrayList<>();
        readDnsFromFile(new File(PATH_HOST_FILE));
    }

    public void                 readDnsFromFile(File file) {
        FileReader fileReader;
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

    public void                 dumpDomainList(String nameOfFile) {
        BufferedWriter output;
        try {
            File file = new File(nameOfFile);
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

    public void                 readDomainList(String nameOfFile) {
    }


    public DnsSpoof             start() {
        if (consoleAdapter.getRecyclerview() != null)
            consoleAdapter.getRecyclerview().post(new Runnable() {
                @Override
                public void run() {
                    consoleLogList.clear();
                    if (consoleAdapter != null)
                        consoleAdapter.notifyDataSetChanged();            }
            });
        new Thread(new Runnable() {
            @Override
            public void run() {
                process = new RootProcess("Dnsmasq::");
                process.exec("dnsmasq --no-daemon --log-queries");
                BufferedReader reader = process.getReader();
                final boolean[] deprecatedStart = {false};
                try {
                    String read;
                    while (!deprecatedStart[0] && ((read = reader.readLine()) != null)) {
                        Log.d(TAG, read);
                        final ConsoleLog log = new ConsoleLog(read.replace("dnsmasq:", ""));
                        consoleLogList.add(log);
                        if (consoleAdapter.getRecyclerview() != null)
                            consoleAdapter.getRecyclerview().post(new Runnable() {
                                @Override
                                public void run() {
                                    if (log.line.contains("failed to create listening socket: Address already in use")) {
                                        deprecatedStart[0] = true;
                                        consoleAdapter.notifyItemInserted(consoleLogList.indexOf(log));
                                        final ConsoleLog log2 = new ConsoleLog("Restarting");
                                        consoleLogList.add(log2);
                                        consoleAdapter.notifyItemInserted(consoleLogList.indexOf(log2));
                                    } else if (consoleAdapter != null)
                                        consoleAdapter.notifyItemInserted(consoleLogList.indexOf(log));
                                }
                            });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (deprecatedStart[0]) {
                        stop();
                        start();
                    }
                }
                Log.d(TAG, "dnsmasq terminated");
            }
        }).start();
        return this;
    }

    public void                 stop() {
        RootProcess.kill("dnsmasq");
        if (consoleAdapter.getRecyclerview() != null)
            consoleAdapter.getRecyclerview().post(new Runnable() {
                @Override
                public void run() {
                    consoleLogList.clear();
                    if (consoleAdapter != null)
                        consoleAdapter.notifyDataSetChanged();          }
            });
    }

    public void                 setConsoleAdapter(ConsoleLogAdapter consoleAdapter) {
        this.consoleAdapter = consoleAdapter;
    }
}
