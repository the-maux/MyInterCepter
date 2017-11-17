package su.sniff.cepter.Controller.Network.Discovery;

import java.util.ArrayList;

import su.sniff.cepter.Controller.Core.Conf.Singleton;
import su.sniff.cepter.Controller.Network.IPv4CIDR;
import su.sniff.cepter.Controller.Network.NetUtils;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.View.HostDiscovery.HostDiscoveryActivity;

public class                ArpScan {
    private ArpScan         mInstance = this;
    private Singleton       mSingleton = Singleton.getInstance();
    private HostDiscoveryActivity mActivity;

    public ArpScan(final HostDiscoveryActivity activity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new ScanNetmask(new IPv4CIDR(mSingleton.network.myIp, mSingleton.network.netmask), mInstance);
            }
        }).start();
        this.mActivity = activity;
    }

    public void                     onReachableScanOver(ArrayList<String> ipReachable) {
        mActivity.monitor("Target Identification");
        NetUtils.dumpListHostFromARPTableInFile(mActivity, ipReachable);
        mActivity.setProgressState(1500);
        mActivity.monitor("Fingerprint scan");
        Fingerprint.guessHostFingerprint(mActivity);
        mActivity.setProgressState(2000);
    }

}

