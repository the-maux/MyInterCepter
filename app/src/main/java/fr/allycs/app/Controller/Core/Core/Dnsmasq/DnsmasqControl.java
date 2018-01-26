package fr.allycs.app.Controller.Core.Core.Dnsmasq;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.Core.RootProcess;
import fr.allycs.app.Model.Target.DNSSpoofItem;
import fr.allycs.app.Model.Target.SniffSession;
import fr.allycs.app.Model.Unix.DNSLog;
import fr.allycs.app.View.DnsSpoofing.DnsActivity;
import fr.allycs.app.View.Widget.Adapter.DnsLogsAdapter;

public class DnsmasqControl {
    private String              TAG = "DnsmasqControl";
    public  List<DNSLog>        mDnsLogs;
    private RootProcess         mProcess;
    private DnsLogsAdapter      mRV_Adapter = null;
    private DnsmasqConfig       mDnsConf;
    private DnsActivity         mActivity;
    private SniffSession        sniffSession;

    public DnsmasqControl() {
        mDnsConf = new DnsmasqConfig();
        if (Singleton.getInstance().getActualSniffSession() != null) {
            sniffSession = Singleton.getInstance().getActualSniffSession();
            mDnsLogs = sniffSession.logDnsSpoofed();
        }

    }

    private void                initRVLink() {
        if (mRV_Adapter.getRecyclerview() != null)
            mRV_Adapter.getRecyclerview().post(new Runnable() {
                @Override
                public void run() {
                    mDnsLogs.clear();
                    if (mRV_Adapter != null)
                        mRV_Adapter.notifyDataSetChanged();
                }
            });
    }

    private boolean             isItALog(String read) {
        return !read.isEmpty() && !read.contains("compile time options: no-IPv6 GNU-getopt no-DBus no-I18N DHCP no-scripts no-TFTP") &&
                !read.contains("using nameserver 8.8.8.8#53") &&
                !read.contains("using nameserver") &&
                !read.contains("read /etc/dnsmasq.hosts") &&
                !read.contains("read /etc/hosts") &&
                !read.contains("reading ") &&
                !read.contains("started, version 2.51 cachesize 150");
    }
    private boolean             isADomainConnu(DNSLog dnsLog) {
        for (DNSLog domainLog : mDnsLogs) {
            if (domainLog.isSameDomain(dnsLog)) {
                domainLog.addLog(dnsLog);
                return false;
            }
        }
        return true;
    }

    public DnsmasqControl       start() {
        initRVLink();
        new Thread(new Runnable() {
            @Override
            public void run() {
                DNSLog Domainlog;
                mDnsLogs.clear();
                mProcess = new RootProcess("Dnsmasq::");
                mProcess.exec("dnsmasq --no-daemon --log-queries");
                BufferedReader reader = mProcess.getReader();
                final boolean[] deprecatedStart = {false};
                try {
                    String read;
                    while (!deprecatedStart[0] && ((read = reader.readLine()) != null)) {
                        Log.d(TAG, "DNS_STDOUT::(" +read + ')');
                        read = read.replace("dnsmasq: ", "");
                        if (isItALog(read)) {
                            DNSLog DomainlogTmp = new DNSLog();
                            DomainlogTmp.init(read);
                            boolean isAnewDomain;
                            if ((isAnewDomain = isADomainConnu(DomainlogTmp))) {
                                DomainlogTmp.sniffSession = sniffSession;
                                DomainlogTmp.save();
                                mDnsLogs.add(0, DomainlogTmp);
                            }
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
    public void                 stop() {
        RootProcess.kill("dnsmasq");
        if (mRV_Adapter.getRecyclerview() != null)
            mRV_Adapter.getRecyclerview().post(new Runnable() {
                @Override
                public void run() {
                    //mDnsLogs.clear();
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
                        mRV_Adapter.notifyItemInserted(mDnsLogs.indexOf(log));
                    else if (mRV_Adapter != null && !isAnewDomain)
                        mRV_Adapter.notifyDataSetChanged();
                }
            });
        if (mActivity != null)
            mActivity.setToolbarTitle(null, mDnsLogs.size() + " dns request catched");
    }

    /**
     * DnsmasqConfig
     */
    public void                 saveDnsConf(String nameOfFile) {
        mDnsConf.saveConf();
    }
    public void                 removeDomain(DNSSpoofItem domainAsked) {
        mDnsConf.listDomainSpoofable.remove(mDnsConf.listDomainSpoofable.indexOf(domainAsked));
    }
    public void                 clear() {
        mDnsConf.clear();
    }
    public DnsmasqConfig getDnsConf() {
        return mDnsConf;
    }
    public void                 setToolbar(DnsActivity activity) {
        this.mActivity = activity;
    }
}
