package su.sniff.cepter.Model.Pcap;

import android.util.Log;

import su.sniff.cepter.R;

/**
 * Created by root on 02/08/17.
 */

public class                Trame {
    public String          TAG = "Trame";
    public int             offsett, verbose;
    public String          time;
    public Protocol        protocol;
    public Ipv4            src, dest;
    public String          StringSrc, StringDest;
    public byte[]          bufferByte = null;
    public String          info;
    public int             backgroundColor;
    
    public                  Trame(String dump, int offsett, int verbose_lvl) {
        this.verbose = verbose_lvl;//0: Nothing; 1 => -v ; 2 -vv ; 3 -vvv ; 4 -X
        if (dump.contains("A?")) {
            dispatch(dump, Protocol.DNS);
        } else if (dump.toLowerCase().contains("arp")) {
            dispatch(dump, Protocol.ARP);
        } else if (dump.toLowerCase().contains("https")){
            dispatch(dump, Protocol.HTTPS);
        } else if (dump.toLowerCase().contains("http")){
            dispatch(dump, Protocol.HTTP);
        }  else if (dump.toLowerCase().contains("tcp")){
            dispatch(dump, Protocol.TCP);
        } else if (dump.toLowerCase().contains("udp")){
            dispatch(dump, Protocol.UDP);
        } else if (dump.toLowerCase().contains("icmp")){
            dispatch(dump, Protocol.ICMP);
        } else {
            dispatch(dump, Protocol.IP);
        }
        initColorBackground();
    }

    private  void           dispatch(String line, Protocol protocol) {
        switch (protocol) {
            case ARP:
                Log.d(TAG, "ARP trame: " + line);
                ArpParsing(line);
                break;
            case HTTP:
                Log.d(TAG, "HTTP trame: "  + line);
                HttpParsing(line);
                break;
            case HTTPS:
                Log.d(TAG, "HTTPS trame " + line);
                HttpsParsing(line);
                break;
            case TCP:
                Log.d(TAG, "TCP trame " + line);
                TcpParsing(line);
                break;
            case UDP:
                Log.d(TAG, "UDP trame " + line);
                UdpParsing(line);
                break;
            case DNS:
                Log.d(TAG, "DNS trame " + line);
                DnsParsing(line);
                break;
            case SMB:
                Log.d(TAG, "SMB trame " + line);
                SmbParsing(line);
                break;
            case NBNS:
                Log.d(TAG, "NBNS trame " + line);
                NBNSParsing(line);
                break;
            case ICMP:
                Log.d(TAG, "NBNS trame " + line);
                ICMPParsing(line);
                break;
            case IP:
                Log.d(TAG, "IP trame " + line);
                IParsing(line);
                break;
            default:
                Log.d(TAG, "Unknow trame " + line);
                IParsing(line);
                break;
        }
    }

    /**
     *
     * @param line
     * @throws StringIndexOutOfBoundsException
     */
    private  void          DnsParsing(String line)  throws StringIndexOutOfBoundsException {
        String[] lineSub = line.split(" ");
        protocol = Protocol.DNS;
        if (lineSub.length <= 9) {//if no verbose
            time = lineSub[0];
            StringSrc = lineSub[2];
            StringDest = lineSub[4].replace(".domain:", "");
            info = lineSub[7] + " " + lineSub[8];
        } else {// if -vvv
            time = lineSub[0].substring(0, lineSub[0].indexOf("."));
            StringSrc = lineSub[17];
            StringDest = lineSub[19].replace(".domain:", "");
            info = lineSub[25];
        }
    }
    private  void          ArpParsing(String line) {
        String[] splitted = line.substring(line.indexOf(" "), line.length()).split(" ");
        protocol = Protocol.ARP;
        time = splitted[1];
        StringSrc = splitted[3];
        StringDest = splitted[5];
        info = splitted[2].toUpperCase() + " " + StringSrc + " " + splitted[4].toUpperCase() + " " + splitted[5];
    }
    private  void          HttpParsing(String line) throws StringIndexOutOfBoundsException  {
        String[] splitted = line.split(" ");
        protocol = Protocol.HTTP;
        time = splitted[0];
        StringSrc = splitted[2];
        StringDest = splitted[4].replace("http", "");
        info = line.substring(line.indexOf(" ", 1), line.length()).replace("IP ", "");
    }

    private  void          HttpsParsing(String line) throws StringIndexOutOfBoundsException  {
        String[] splitted = line.split(" ");
        protocol = Protocol.HTTPS;
        time = splitted[0];
        StringSrc = splitted[2];
        StringDest = splitted[4].replace("https", "");
        info = line.substring(line.indexOf(" ", 1), line.length()).replace("IP ", "");
    }

    /**
     * TODO:
     * @param line
     * @throws StringIndexOutOfBoundsException
     */
    private  void          TcpParsing(String line) throws StringIndexOutOfBoundsException  {
        String[] splitted = line.split(" ");
        protocol = Protocol.TCP;
        time = splitted[0];
        StringSrc = splitted[2];
        StringDest = splitted[4];
        info = line.substring(line.indexOf(" ", 1), line.length()).replace("IP ", "");
    }

    /**
     * TODO:
     * @param line
     * @throws StringIndexOutOfBoundsException
     */
    private  void          UdpParsing(String line) throws StringIndexOutOfBoundsException  {
        String[] splitted = line.split(" ");
        protocol = Protocol.UDP;
        time = splitted[0];
        StringSrc = splitted[2];
        StringDest = splitted[4];
        info = line.substring(line.indexOf(" ", 1), line.length()).replace("IP ", "");
    }

    /**
     * TODO:
     * @param line
     * @throws StringIndexOutOfBoundsException
     */
    private  void          SmbParsing(String line) throws StringIndexOutOfBoundsException  {
        String[] splitted = line.split(" ");
        protocol = Protocol.SMB;
        time = splitted[0];
        StringSrc = splitted[2];
        StringDest = splitted[4];
        info = line.substring(line.indexOf(" ", 1), line.length()).replace("IP ", "");
    }
    private  void          NBNSParsing(String line) throws StringIndexOutOfBoundsException  {
        String[] splitted = line.split(" ");
        protocol = Protocol.NBNS;
        time = splitted[0];
        StringSrc = splitted[2];
        StringDest = splitted[4];
        info = line.substring(line.indexOf(" ", 1), line.length()).replace("IP ", "");
    }
    private  void          ICMPParsing(String line) {
        String[] splitted = line.split(" ");
        protocol = Protocol.ICMP;
        time = splitted[0];
        StringSrc = splitted[2];
        StringDest = splitted[4];
        info = line.substring(line.indexOf("ICMP"), line.length()).replace("ICMP ", "");
    }

    private void           IParsing(String line) {
        String[] splitted = line.split(" ");
        protocol = Protocol.IP;
        if (splitted.length >= 4) {
            time = splitted[0];
            StringSrc = splitted[2];
            StringDest = splitted[4];
        } else {
            time = "Parsing";
            StringSrc = "Parsing";
            StringDest = "Parsing";
        }
        try {
            info = line;
        } catch (StringIndexOutOfBoundsException e) {
            e.getStackTrace();
            info = line;
        }
    }

    public  void            initColorBackground() {
        switch (protocol) {
            case ARP:
                backgroundColor = R.color.arp;
                break;
            case HTTP:
                backgroundColor = R.color.http;
                break;
            case HTTPS:
                backgroundColor = R.color.http;
                break;
            case TCP:
                backgroundColor = R.color.http;
                break;
            case UDP:
                backgroundColor = R.color.udp;
                break;
            case DNS:
                backgroundColor = R.color.dns;
                break;
            case SMB:
                backgroundColor = R.color.smb;
                break;
            case NBNS:
                backgroundColor = R.color.material_light_white;
                break;
            case ICMP:
                backgroundColor = R.color.icmp;
                break;
            case IP:
                backgroundColor = R.color.material_blue_grey_100;
                break;
            default:
                backgroundColor = R.color.material_light_white;
                break;
        }
    }


}
