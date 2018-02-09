package fr.allycs.app.Core.Nmap;

/**
 Pre-scan script results:
 | broadcast-avahi-dos:
 |   Discovered hosts:
 |     192.168.0.25
 |     192.168.0.24
 |     192.168.0.2
 |   After NULL UDP avahi packet DoS (CVE-2011-1002).
 |_  Hosts are all up (not vulnerable).
 | broadcast-dhcp-discover:
 |   IP Offered: 192.168.0.26
 |   Subnet Mask: 255.255.255.0
 |   Router: 192.168.0.1
 |   Server Identifier: 192.168.0.1
 |_  Domain Name Server: 89.2.0.1, 8.8.8.8
 | broadcast-dns-service-discovery:
 |   192.168.0.25
 |     9/tcp workstation
 |       Address=192.168.0.25 fe80:0:0:0:a378:a928:ba7d:8937
 |     22/tcp udisks-ssh
 |       Address=192.168.0.25 fe80:0:0:0:a378:a928:ba7d:8937
 |   192.168.0.24
 |     47989/tcp nvstream_dbd
 |       Address=192.168.0.24 fe80:0:0:0:ed38:e9ec:d68e:b472
 |   192.168.0.2
 |     9/tcp workstation
 |_      Address=192.168.0.2
 | broadcast-eigrp-discovery:
 |_ ERROR: Couldn't get an A.S value.
 | broadcast-listener:
 |   udp
 |       MDNS
 |         Generic
 |           ip            ipv6  name
 |_          192.168.0.24        _nvstream_dbd._tcp.local
 | broadcast-netbios-master-browser:
 | ip           server  domain
 |_192.168.0.2  LABOX   WORKGROUP
 | broadcast-pppoe-discover:
 |_  ERROR: Failed to get source MAC address
 | broadcast-upnp-info:
 |   192.168.0.2
 |       Server: Linux/2.x.x, UPnP/1.0, pvConnect UPnP SDK/1.0, TwonkyMedia UPnP SDK/1.1
 |       Location: http://192.168.0.2:9000/TMSDeviceDescription.xml
 |   192.168.0.1
 |       Server: UPnP/1.0 UPnP/1.0 UPnP-Device-Host/1.0
 |_      Location: http://192.168.0.1:80/RootDevice.xml
 | broadcast-wpad-discover:
 |_  ERROR: Could not find WPAD using DNS/DHCP
 |_eap-info: please specify an interface with -e
 WARNING: No targets were specified, so 0 hosts scanned.
 Nmap done: 0 IP addresses (0 hosts up) scanned in 40.57 seconds

 */

public class NmapUpnpParser {
}
