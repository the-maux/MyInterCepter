package fr.dao.app.Core.Dnsmasq;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Network.IPTables;
import fr.dao.app.Model.Target.DNSSpoofItem;
import fr.dao.app.Model.Target.SniffSession;
import fr.dao.app.Model.Unix.DNSLog;
import fr.dao.app.View.DnsSpoofing.DnsActivity;
import fr.dao.app.View.ZViewController.Adapter.DnsLogsAdapter;

public class                    DnsmasqControl {
    private String              TAG = "DnsmasqControl";
    public  List<DNSLog>        mDnsLogs;
    private RootProcess         mProcess;
    private DnsLogsAdapter      mRV_Adapter = null;
    private DnsmasqConfig       mDnsConf;
    private DnsActivity         mActivity;
    private SniffSession        sniffSession;
    private boolean             isRunning = false;

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
        return !read.isEmpty() && !read.contains("compile time options:") &&
                !read.contains("using nameserver 8.8.8.8#53") &&
                !read.contains("using nameserver") &&
                !read.contains("read /etc/dnsmasq.hosts") &&
                !read.contains("read /etc/hosts") &&
                !read.contains("reading ") &&
                !read.contains("started, version");
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
        isRunning = true;
        initRVLink();
        IPTables.redirectDnsForSpoofing();
        new Thread(new Runnable() {
            @Override
            public void run() {
                mDnsLogs.clear();
                mProcess = new RootProcess("Dnsmasq::");
                mProcess.exec("dnsmasq --no-daemon --log-queries");
                BufferedReader reader = mProcess.getReader();
                try {
                    String read;
                    while ((read = reader.readLine()) != null) {
                        Log.d(TAG, "DNS_STDOUT::(" +read + ')');
                        read = read.replace("dnsmasq: ", "");
                        if (read.contains("failed to create listening socket")){
                            stop();
                            mActivity.onError(read);
                            break;
                        }
                        if (isItALog(read)) {
                            DNSLog DomainlogTmp = new DNSLog();
                            DomainlogTmp.init(read);
                            boolean isAnewDomain;
                            if ((isAnewDomain = isADomainConnu(DomainlogTmp))) {
                                DomainlogTmp.sniffSession = sniffSession;
                                DomainlogTmp.save();
                                mDnsLogs.add(0, DomainlogTmp);
                            }
                            notifyAdapter(DomainlogTmp, isAnewDomain);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    mActivity.onError(e.getMessage());
                }
                Log.d(TAG, "dnsmasq terminated");
            }
        }).start();
        return this;
    }
    public void                 stop() {
        isRunning = false;
        IPTables.stopRedirectDnsForSpoofing();
        RootProcess.kill("dnsmasq");
        if (mRV_Adapter.getRecyclerview() != null)
            mRV_Adapter.getRecyclerview().post(new Runnable() {
                @Override
                public void run() {
                    //mDnsLogs.reset();
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
    private void                notifyAdapter(final DNSLog log, final boolean isAnewDomain) {
        if (mRV_Adapter.getRecyclerview() != null)
            mRV_Adapter.getRecyclerview().post(new Runnable() {
                @Override
                public void run() {
                    if (mRV_Adapter != null && isAnewDomain)
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
    public DnsmasqConfig        getDnsConf() {
        return mDnsConf;
    }
    public void                 setActivity(DnsActivity activity) {
        this.mActivity = activity;
    }

    public boolean              isRunning() {
        return isRunning;
    }
}
