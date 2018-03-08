package fr.dao.app.Model.Net;

import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.net.InetAddress;
import java.util.List;

import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.Network;

@Table(name = "ServiceDiscovery", id = "_id")
public class            Service extends Model {
    private String      TAG = "Service";
    @Column(name ="Host")
    public Host         host;
    @Column(name ="Ports")
    public String       Port;
    @Column(name ="ServiceName")
    public String       ServiceName;
    @Column(name ="ServiceType")
    public String       ServiceType;
    @Column(name = "Network")
    public Network      network;

    private NsdServiceInfo service;

    public              Service() {
        super();
    }

    public              Service(String hostAddr,
                                String CanonicalHostname,
                                byte[] Address,
                                String Hostname,
                                String Port,
                                String ServiceName,
                                String ServiceType,
                                InetAddress addr,
                                NsdServiceInfo service, List<Host> hostList) {
        super();
        this.Port = Port;
        this.ServiceName = ServiceName;
        this.ServiceType = ServiceType;
        this.service = service;
        for (Host hostTmp : hostList) {
            Log.d(TAG, hostTmp.mac + " compared to " + hostAddr);
            if (hostTmp.mac.equals(hostAddr)) {
                Log.d(TAG, "Service[" + getServiceName() + "] FOUND HIS HOST:" + hostTmp.toString());
                host = hostTmp;
                break;
            }
        }
    }

    public Host         getHost() { return host; }
    public String       getPort() {
        return Port;
    }
    public String       getServiceName() {
        return ServiceName;
    }
    public String       getServiceType() {
        return ServiceType;
    }
}
