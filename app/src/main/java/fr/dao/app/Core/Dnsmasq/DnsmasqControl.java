package fr.dao.app.Core.Dnsmasq;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

import fr.dao.app.Core.Configuration.MitManager;
import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Network.IPTables;
import fr.dao.app.Model.Target.DNSSpoofItem;
import fr.dao.app.Model.Target.SniffSession;
import fr.dao.app.Model.Unix.DNSLog;
import fr.dao.app.View.DnsSpoofing.DnsActivity;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Adapter.DnsLogsAdapter;

public class                    DnsmasqControl {
    private String              TAG = "DnsmasqControl";
    public  List<DNSLog>        mDnsLogs;
    private RootProcess         mProcess;
    private DnsLogsAdapter      mRV_Adapter = null;
    private DnsmasqConfig       mDnsConf;
    private MyActivity          mActivity;
    private SniffSession        sniffSession;
    private boolean             isRunning = false;
    private Thread              mThreadProcess = null;

    public DnsmasqControl() {
        mDnsConf = new DnsmasqConfig();
        if (Singleton.getInstance().getCurrentSniffSession() != null) {
            sniffSession = Singleton.getInstance().getCurrentSniffSession();
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

    public DnsmasqControl       start() {
        isRunning = true;
        initRVLink();
        if (!MitManager.getInstance().isDnsmasqRunning()) {
            mThreadProcess = runThread();
            mThreadProcess.start();
        } else
            Log.e(TAG, "DnsControl doesn't restart, already launched");
        return this;
    }

    private Thread              runThread() {
        return new Thread(new Runnable() {
            public void run() {
                mDnsLogs.clear();
                mProcess = new RootProcess("Dnsmasq::");
                mProcess.exec("dnsmasq --no-daemon --log-queries");
                BufferedReader reader = mProcess.getReader();
                try {
                    String buffer;
                    while ((buffer = reader.readLine()) != null) {
                        buffer = buffer.replace("dnsmasq: ", "");
                        if (buffer.contains("failed to create listening socket")){
                            stop();
                            onError(buffer);
                            break;
                        }
                        if (strip(buffer)) {
                            buildDNSLog(buffer);
                            Log.i(TAG, "DNSMASQ[" + buffer + ']');
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e.getMessage());
                }
                Log.d(TAG, "dnsmasq terminated");
            }
        });
    }

    public void                         onError(String error) {
        mActivity.showSnackbar(error);
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                new AlertDialog.Builder(mActivity)
                        .setTitle("Dns error detected")
                        .setMessage("Would you like to restart the dns process ?")
                        .setPositiveButton(mActivity.getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                stop();
                                start();
                            }
                        })
                        .setNegativeButton(mActivity.getResources().getString(android.R.string.no), null)
                        .show();
            }
        });
    }

    public void                 stop() {
        isRunning = false;
        mThreadProcess.interrupt();
        mProcess.closeDontWait();
        mThreadProcess = null;
        IPTables.stopDnsPacketRedirect();
        RootProcess.kill("dnsmasq");
        if (mRV_Adapter.getRecyclerview() != null)
            mRV_Adapter.getRecyclerview().post(new Runnable() {
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

    private void                buildDNSLog(String buffer) {
        DNSLog DomainlogTmp = new DNSLog();
        DomainlogTmp.init(buffer);
        boolean isAnewDomain;
        if ((isAnewDomain = isADomainConnu(DomainlogTmp))) {
            DomainlogTmp.sniffSession = sniffSession;
            DomainlogTmp.save();
            mDnsLogs.add(0, DomainlogTmp);
        }
        notifyAdapter(DomainlogTmp, isAnewDomain);
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
    private boolean             strip(String read) {
        return !read.isEmpty() && !read.contains("compile time options:") &&
                !read.contains("using nameserver 8.8.8.8#53") &&
                !read.contains("using nameserver") &&
                !read.contains("read /etc/dnsmasq.hosts") &&
                !read.contains("read /etc/hosts") &&
                !read.contains("reading ") &&
                !read.contains("started, version");
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
