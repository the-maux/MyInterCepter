package fr.allycs.app.Controller.Network.BonjourService;

import android.net.nsd.NsdServiceInfo;

import java.net.InetAddress;

public class Service {
    private String      HostAddress;
    private String      CanonicalHostName;
    private byte[]      Address;
    private String      HostName;
    private String      Port;
    private String      ServiceName;
    private String      ServiceType;
    private InetAddress addr;
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
