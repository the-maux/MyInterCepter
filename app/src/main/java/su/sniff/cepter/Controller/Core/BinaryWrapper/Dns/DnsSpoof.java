package su.sniff.cepter.Controller.Core.BinaryWrapper.Dns;

import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import su.sniff.cepter.Controller.Core.BinaryWrapper.RootProcess;
import su.sniff.cepter.Model.Target.DNSSpoofItem;
import su.sniff.cepter.Model.Unix.DNSLog;
import su.sniff.cepter.View.Adapter.DnsLogsAdapter;
import su.sniff.cepter.View.DNSSpoofingActivity;

public class                    DnsSpoof {
    private String              TAG = "DnsSpoof";
    public ArrayList<DNSLog>    mDomainLogs = new ArrayList<>();
    private RootProcess         mProcess;
    private DnsLogsAdapter      mRV_Adapter = null;
    private DnsConf             dnsConf;
    private DNSSpoofingActivity mActivity;

    public                      DnsSpoof() {
        dnsConf = new DnsConf();
    }

    public DnsSpoof             start() {
        initRVLink();
        new Thread(new Runnable() {
            @Override
            public void run() {
                DNSLog Domainlog;
                mDomainLogs.clear();
                mProcess = new RootProcess("Dnsmasq::");
                mProcess.exec("dnsmasq --no-daemon --log-queries");
                BufferedReader reader = mProcess.getReader();
                final boolean[] deprecatedStart = {false};
                try {
                    String read;
                    while (!deprecatedStart[0] && ((read = reader.readLine()) != null)) {
                        Log.d(TAG, read);
                        read = read.replace("dnsmasq: ", "");
                        if (!read.isEmpty() && !read.contains("compile time options: no-IPv6 GNU-getopt no-DBus no-I18N DHCP no-scripts no-TFTP") &&
                                !read.contains("using nameserver 8.8.8.8#53") && !read.contains("using nameserver") && !read.contains("read /etc/dnsmasq.hosts") &&
                                !read.contains("reading ") && !read.contains("started, version 2.51 cachesize 150")) {
                            DNSLog DomainlogTmp = new DNSLog(read);
                            boolean isAnewDomain;
                            if ((isAnewDomain = isADomainConnu(DomainlogTmp)))
                                mDomainLogs.add(0, DomainlogTmp);
                            notifyAdapter(DomainlogTmp, deprecatedStart, isAnewDomain);
                        }
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

    private void                initRVLink() {
        if (mRV_Adapter.getRecyclerview() != null)
            mRV_Adapter.getRecyclerview().post(new Runnable() {
                @Override
                public void run() {
                    mDomainLogs.clear();
                    if (mRV_Adapter != null)
                        mRV_Adapter.notifyDataSetChanged();
                }
            });
    }

    private boolean             isADomainConnu(DNSLog dnsLog) {
        for (DNSLog domainLog : mDomainLogs) {
            if (domainLog.isSameDomain(dnsLog)) {
                domainLog.addLog(dnsLog);
                return false;
            }
        }
        return true;
    }

    public void                 stop() {
        RootProcess.kill("dnsmasq");
        if (mRV_Adapter.getRecyclerview() != null)
            mRV_Adapter.getRecyclerview().post(new Runnable() {
                @Override
                public void run() {
                    //mDomainLogs.clear();
                    if (mRV_Adapter != null)
                        mRV_Adapter.notifyDataSetChanged();
                }
            });
    }

    /**
     * RecyclerView Conf
     */

    public void                 setRV_Adapter(DnsLogsAdapter mRV_Adapter) {
        this.mRV_Adapter = mRV_Adapter;
    }

    private void                notifyAdapter(final DNSLog log, final boolean[] deprecatedStart, final boolean isAnewDomain) {
        if (mRV_Adapter.getRecyclerview() != null)
            mRV_Adapter.getRecyclerview().post(new Runnable() {
                @Override
                public void run() {
                    if (log.data.contains("failed to create listening socket: Address already in use")) {
                        deprecatedStart[0] = true;
                    } else if (mRV_Adapter != null && isAnewDomain)
                        mRV_Adapter.notifyItemInserted(mDomainLogs.indexOf(log));
                    else if (mRV_Adapter != null && !isAnewDomain)
                        mRV_Adapter.notifyDataSetChanged();
                }
            });
        if (mActivity != null)
            mActivity.titleToolbar(mDomainLogs.size() + " dns request");
    }

    /**
     * DnsConf
     */

    public void                 saveDnsConf(String nameOfFile) {
        dnsConf.saveConf(nameOfFile);
    }

    public void                 removeDomain(DNSSpoofItem domainAsked) {
        Log.d(TAG, "removing DNS spoofed nbr:" + dnsConf.listDomainSpoofed.indexOf(domainAsked));
        dnsConf.listDomainSpoofed.remove(dnsConf.listDomainSpoofed.indexOf(domainAsked));
    }

    public void                 clear() {
        dnsConf.clear();
    }

    public DnsConf              getDnsConf() {
        return dnsConf;
    }

    public void                 setToolbar(DNSSpoofingActivity activity) {
        this.mActivity = activity;
    }
}
