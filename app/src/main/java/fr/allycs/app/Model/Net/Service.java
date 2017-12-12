package fr.allycs.app.Model.Net;

import android.net.nsd.NsdServiceInfo;

import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.net.InetAddress;

@Table(name = "ServiceDiscovery", id = "_id")
public class            Service {
    @Column(name ="HostAddress")
    public String       HostAddress;
    @Column(name ="CanonicalHostName")
    public String       CanonicalHostName;
    @Column(name ="Address")
    public byte[]       Address;
    @Column(name ="HostName")
    public String       HostName;
    @Column(name ="Port")
    public String       Port;
    @Column(name ="ServiceName")
    public String       ServiceName;
    @Column(name ="ServiceType")
    public String       ServiceType;
    public InetAddress  addr;

    private NsdServiceInfo service;

    public              Service(String hostAddr,
                                String CanonicalHostname,
                                byte[] Address,
                                String Hostname,
                                String Port,
                                String ServiceName,
                                String ServiceType,
                                InetAddress addr,
                                NsdServiceInfo service) {
        this.HostAddress = hostAddr;
        this.CanonicalHostName = CanonicalHostname;
        this.Address = Address;
        this.HostName = Hostname;
        this.Port = Port;
        this.ServiceName = ServiceName;
        this.ServiceType = ServiceType;
        this.addr = addr;
        this.service = service;
    }

    public String       getHostAddress() {
        return HostAddress;
    }
    public String       getCanonicalHostName() {
        return CanonicalHostName;
    }
    public byte[]       getAddress() {
        return Address;
    }
    public String       getHostName() {
        return HostName;
    }
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
