package fr.dao.app.Core.Scan;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import fr.dao.app.Model.Target.Host;

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
 |   HTTPProxy Identifier: 192.168.0.1
 |_  Domain Name HTTPProxy: 89.2.0.1, 8.8.8.8
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
 |           subtitle            ipv6  name
 |_          192.168.0.24        _nvstream_dbd._tcp.local
 | broadcast-netbios-master-browser:
 | subtitle           server  title
 |_192.168.0.2  LABOX   WORKGROUP
 | broadcast-pppoe-discover:
 |_  ERROR: Failed to get source MAC address
 | broadcast-upnp-info:
 |   192.168.0.2
 |       HTTPProxy: Linux/2.x.x, UPnP/1.0, pvConnect UPnP SDK/1.0, TwonkyMedia UPnP SDK/1.1
 |       Location: http://192.168.0.2:9000/TMSDeviceDescription.xml
 |   192.168.0.1
 |       HTTPProxy: UPnP/1.0 UPnP/1.0 UPnP-Device-Host/1.0
 |_      Location: http://192.168.0.1:80/RootDevice.xml
 | broadcast-wpad-discover:
 |_  ERROR: Could not find WPAD using DNS/DHCP
 |_eap-info: please specify an interface with -e
 WARNING: No targets were specified, so 0 hosts scanned.
 Nmap done: 0 IP addresses (0 hosts up) scanned in 40.57 seconds

 */

public class                    NmapUpnpParser {
    private String              TAG = "NmapUpnpParser";
    private RequestQueue        listRequestApi;
    private ArrayList<String>   items = new ArrayList<>();

    public                      NmapUpnpParser(Context context) {
        listRequestApi = Volley.newRequestQueue(context);
    }

    /**
     * 1900/udp open          upnp
     | upnp-info:
     | 192.168.0.12
     |     server: microsoft-windows/10.0 upnp/1.0 upnp-device-host/1.0
     |_    location: http://192.168.0.12:2869/upnphost/udhisapi.dll?content=uuid:b0a22e22-1541-424f-bd84-cfda678aaa4d
     */
    public int                  analyseUPnPtResult(String[] nmapStdoutHost, int i, final Host host) {
        ArrayList<String> dumpHostScript = new ArrayList<>();
        String urlUPnP = "";
        while (i < nmapStdoutHost.length && !nmapStdoutHost[i].startsWith("|_")) {
            dumpHostScript.add(nmapStdoutHost[i++].toLowerCase().replace("|", "").trim());
        }
        dumpHostScript.add(nmapStdoutHost[i++].toLowerCase().replace("|_", "").trim());
        Log.d(TAG, "UPnP:[" + dumpHostScript+ "]");
        for (String line : dumpHostScript) {
            if (line.contains("server:")) {
                String[] splitted = line.replace("server: ","").split(" ");
                host.os = splitted[0].replace("microsoft-", "").replace("|", "").trim();
                host.UPnP_Device = splitted[0].replace("microsoft-", "").replace("|", "").trim();
                host.vendor = host.UPnP_Device;
            } else if (line.contains("location: ")) {
                urlUPnP = line.replace("location: ", "");
                host.UPnP_Infos = urlUPnP;
            }
        }
        if (!urlUPnP.isEmpty()) {//GET HTTP XML UPnP
            Log.i(TAG, "GET /upnp-info [" + urlUPnP + "]");
            getHTTPUpnp(host, urlUPnP);
            for (String item : items) {
                Log.d(TAG, "UPnP[" + item + "]");
            }
        }
        return i;
    }

    private void                getHTTPUpnp(final Host host, String urlUPnP) {
        try {//YOU HAVE TO LET THE ALL NODE WAIT THE HTTP RESPONSE
            StringRequest request = new StringRequest(Request.Method.GET, urlUPnP,
                    new Response.Listener<String>() {
                        public void onResponse(String response) {
                            parseHttpUpnp(host, response);
                        }
                    },
                    new Response.ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            Log.w(TAG, "getModules::VolleyError:"  + error.getMessage());
                            error.getStackTrace();
                        }
            });
            listRequestApi.add(request);
        } catch (Throwable t) {
            Log.e(TAG, "error in getting UpnP XML");
        }
    }

    private void                parseHttpUpnp(Host host, String response) {
        host.Notes = host.Notes.concat("OxBABOBAB").concat(response);
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(response));
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {
                } else if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().contains("serialNumber")) {
                        xpp.next();
                        host.Brand_and_Model = xpp.getText();
                        Log.d(TAG, "serialNumber " + xpp.getText());
                    } else if (xpp.getName().contains("friendlyName")) {
                        xpp.next();
                        if (!xpp.getText().contains("http"))
                            host.UPnP_Name = xpp.getText();
                        Log.d(TAG, "friendlyName " + xpp.getText());
                    } else if (xpp.getName().contains("deviceType")) {
                        xpp.next();
                        host.osDetail = xpp.getText();
                        Log.d(TAG, "deviceType::" + xpp.getText());
                    } else if (xpp.getName().contains("manufacturer")) {
                        xpp.next();
                        host.UPnP_Device = xpp.getText();
                        Log.d(TAG, "manufacturer::" + xpp.getText());
                    } else if (xpp.getName().contains("modelName")) {
                        xpp.next();
                        host.UPnP_Services = xpp.getText();
                        Log.d(TAG, "ModelName::" + xpp.getText());
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    // Log.d(TAG, "End tag " + xpp.getName());
                } else if (eventType == XmlPullParser.TEXT) {
                    // Log.d(TAG, "Text " + xpp.getText()); // here you get the text from xml
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
