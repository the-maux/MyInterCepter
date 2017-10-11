package su.sniff.cepter.Controller.System.BinaryWrapper;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import su.sniff.cepter.Model.Target.DNSSpoofItem;
import su.sniff.cepter.Model.Unix.DNSLog;
import su.sniff.cepter.View.Adapter.ConsoleLogAdapter;
import su.sniff.cepter.View.Adapter.DnsLogsAdapter;

/**
 * Created by the-maux on 02/10/17.
 */

public class                    DnsSpoof {
    private String              TAG = "DnsSpoof";
    public List<DNSSpoofItem>   listDomainSpoofed;
    public List<DNSLog>         mDomainLogs = new ArrayList<>();
    private String              PATH_HOST_FILE = "/etc/dnsmasq.hosts";
    private RootProcess         mProcess;
    private DnsLogsAdapter      mRV_Adapter = null;

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
        //TODO: DNS readDomainList
    }


    public DnsSpoof             start() {
        if (mRV_Adapter.getRecyclerview() != null)
            mRV_Adapter.getRecyclerview().post(new Runnable() {
                @Override
                public void run() {
                    mDomainLogs.clear();
                    if (mRV_Adapter != null)
                        mRV_Adapter.notifyDataSetChanged();            }
            });
        new Thread(new Runnable() {
            @Override
            public void run() {
                DNSLog Domainlog;
                mProcess = new RootProcess("Dnsmasq::");
                mProcess.exec("dnsmasq --no-daemon --log-queries");
                BufferedReader reader = mProcess.getReader();
                final boolean[] deprecatedStart = {false};
                try {
                    String read;
                    while (!deprecatedStart[0] && ((read = reader.readLine()) != null)) {
                        Log.d(TAG, read);
                        read = read.replace("dnsmasq:", "");
                        if ((Domainlog = buildLogs(read)) != null)
                            mDomainLogs.add(Domainlog);
                        putToFront(Domainlog, deprecatedStart);
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

    private DNSLog              buildLogs(String line) {
        DNSLog Domainlog = new DNSLog(line);
        for (DNSLog domainLog : mDomainLogs) {
            if (domainLog.isSameDomain(Domainlog)) {
                domainLog.addLog(Domainlog);
                return null;
            }
        }
        return Domainlog;
    }

    private void                putToFront(final DNSLog log, final boolean[] deprecatedStart) {
        if (mRV_Adapter.getRecyclerview() != null)
            mRV_Adapter.getRecyclerview().post(new Runnable() {
                @Override
                public void run() {
                    if (log.data.contains("failed to create listening socket: Address already in use")) {
                        deprecatedStart[0] = true;
                    }
                    if (mRV_Adapter != null)
                        mRV_Adapter.notifyItemInserted(mDomainLogs.indexOf(log));
                }
            });
    }

    public void                 stop() {
        RootProcess.kill("dnsmasq");
        if (mRV_Adapter.getRecyclerview() != null)
            mRV_Adapter.getRecyclerview().post(new Runnable() {
                @Override
                public void run() {
                    mDomainLogs.clear();
                    if (mRV_Adapter != null)
                        mRV_Adapter.notifyDataSetChanged();          }
            });
    }

    public void                 setRV_Adapter(DnsLogsAdapter mRV_Adapter) {
        this.mRV_Adapter = mRV_Adapter;
    }
}
