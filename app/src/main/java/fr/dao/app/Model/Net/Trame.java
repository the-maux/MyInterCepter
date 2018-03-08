package fr.dao.app.Model.Net;

import android.util.Log;

import fr.dao.app.R;

public class               Trame  {
    public String          TAG = "Trame";
    public int             offsett = 0, verbose;
    public String          time;
    public Protocol        protocol;
    public String          StringSrc, StringDest, Errno = null;
    public byte[]          bufferByte = null;
    public String          info;
    public int             backgroundColor;
    public boolean         initialised = false, skipped = false;
    public boolean         connectionOver = false;

    public                  Trame(String dump) {
        if (skipUnnecessary(dump)) {
            Log.e(TAG, "Skipped:" + dump);
            return;
        }
        if (dump.contains("A?")) {
            dispatch(dump, Protocol.DNS);
        } else if (dump.toLowerCase().contains("arp")) {
            dispatch(dump, Protocol.ARP);
        } else if (dump.toLowerCase().contains("https")){
            dispatch(dump, Protocol.HTTPS);
        } else if (dump.toLowerCase().contains("http")){
            dispatch(dump, Protocol.HTTP);
        } else if (dump.toLowerCase().contains("icmp")){
            dispatch(dump, Protocol.ICMP);
        } else if (dump.toLowerCase().contains("tcp")){
            dispatch(dump, Protocol.TCP);
        } else if (dump.toLowerCase().contains("udp")){
            dispatch(dump, Protocol.UDP);
        } else {
            dispatch(dump, Protocol.IP);
        }
    }

    private boolean        skipUnnecessary(String line) {
        if (line.contains("for full protocol decode") ||
            line.contains("listening on ") ||
                line.contains("packets captured") ||
                line.contains("packets received by filter") ||
                line.contains("packets dropped by kernel") ||
                line.contains("Processus over") ||
                line.contains("reading from file")) {
            skipped = true;
            return true;
        }
        return false;
    }

    private  void          dispatch(String line, Protocol protocol) {
        try {
            switch (protocol) {
                case ARP:
                    //Log.d(TAG, "ARP trame: " + line);
                    ArpParsing(line);
                    break;
                case HTTPS:
                    //Log.d(TAG, "HTTPS trame " + line);
                    HttpsParsing(line);
                    break;
                case HTTP:
                    //Log.d(TAG, "HTTP trame: " + line);
                    HttpParsing(line);
                    break;
                case TCP:
                    //Log.d(TAG, "TCP trame " + line);
                    TcpParsing(line);
                    break;
                case UDP:
                    //Log.d(TAG, "UDP trame " + line);
                    UdpParsing(line);
                    break;
                case DNS:
                    //Log.d(TAG, "DNS trame " + line);
                    DnsParsing(line);
                    break;
                case SMB:
                    //Log.d(TAG, "SMB trame " + line);
                    SmbParsing(line);
                    break;
                case ICMP:
                    //Log.d(TAG, "ICMP trame " + line);
                    ICMPParsing(line);
                    break;
                case NBNS:
                    //Log.d(TAG, "NBNS trame " + line);
                    NBNSParsing(line);
                    break;
                case IP:
                    //Log.e(TAG, "UNKNOW IP trame " + line);
                    IParsing(line);
                    break;
                default:
                    Log.e(TAG, "Unknow trame " + line);
                    IParsing(line);
                    break;
            }
            initColorBackground();
            initialised = true;
        } catch (StringIndexOutOfBoundsException e) {
            Log.e(TAG, "Error in trame : " + line);
            Errno = line.replace("tcpdump:", "");
            e.getStackTrace();
        }
    }

    /**
     *
     * @param line
     * @throws StringIndexOutOfBoundsException
     */
    private  void          DnsParsing(String line)  throws StringIndexOutOfBoundsException {
        String[] lineSub = line.split(" ");
        //Log.d(TAG, "DnsParsing::->"+Arrays.toString(lineSub));
        protocol = Protocol.DNS;
        if (lineSub.length <= 9) {//if no verbose
            time = lineSub[0];
            String[] src = extractThePortOrProto(lineSub[2]);
            String[] dest = extractThePortOrProto(lineSub[4]);
            StringSrc = src[0];
            StringDest = dest[0];
            info = lineSub[7] + " " + lineSub[8];
        } else {// if -vvv
            time = lineSub[0].substring(0, lineSub[0].indexOf("."));
            StringSrc = lineSub[17];
            StringDest = lineSub[19].replace(".title:", "").replace(".DOMAIN:", "");
            info = lineSub[25];
        }
    }
    private  void          ArpParsing(String line) {
        String[] splitted = line.split(" ");
        protocol = Protocol.ARP;
        time = splitted[0].substring(0, splitted[0].indexOf("."));
        StringSrc = splitted[3];
        StringDest = splitted[5];
        info = splitted[2].toUpperCase() + " " + StringSrc + " " + splitted[4].toUpperCase() + " " + splitted[5];
    }
    private  void          HttpParsing(String line) throws StringIndexOutOfBoundsException  {
        String[] splitted = line.split(" ");
        //Log.d(TAG, "HttpParsing::->"+Arrays.toString(splitted));
        protocol = Protocol.HTTP;
        time = splitted[0].substring(0, splitted[0].indexOf("."));
        String[] src = extractThePortOrProto(splitted[2]);
        String[] dest = extractThePortOrProto(splitted[4]);
        StringSrc = src[0];
        StringDest = dest[0];
        info = line.substring(line.indexOf(StringDest)+StringDest.length(), line.length()).replace("IP ", "");
    }

    private  void          HttpsParsing(String line) throws StringIndexOutOfBoundsException  {
        String[] splitted = line.split(" ");
        //Log.d(TAG, "HttpsParsing::->"+Arrays.toString(splitted));
        protocol = Protocol.HTTPS;
        time = splitted[0].substring(0, splitted[0].indexOf("."));
        String[] src = extractThePortOrProto(splitted[2]);
        String[] dest = extractThePortOrProto(splitted[4]);
        StringSrc = src[0];
        StringDest = dest[0];
        info = line.substring(line.indexOf(StringDest)+StringDest.length()+dest[1].length()+1, line.length()).replace("IP ", "");
    }

    /**
     * TODO:
     * @param line
     * @throws StringIndexOutOfBoundsException
     */
    private  void          TcpParsing(String line) throws StringIndexOutOfBoundsException  {
        String[] splitted = line.split(" ");
        //Log.d(TAG, "TcpParsing::->"+Arrays.toString(splitted));
        protocol = Protocol.TCP;
        time = splitted[0].substring(0, splitted[0].indexOf("."));
        String[] src = extractThePortOrProto(splitted[2]);
        String[] dest = extractThePortOrProto(splitted[4]);
        StringSrc = src[0];
        StringDest = dest[0];
        info = line.substring(line.indexOf(StringDest)+StringDest.length(), line.length()).replace("IP ", "");
    }

    /**
     * TODO:
     * @param line
     * @throws StringIndexOutOfBoundsException
     */
    private  void          UdpParsing(String line) throws StringIndexOutOfBoundsException  {
        String[] splitted = line.split(" ");
        //Log.d(TAG, "UdpParsing::->"+Arrays.toString(splitted));
        protocol = Protocol.UDP;
        time = splitted[0].substring(0, splitted[0].indexOf("."));
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
    //    Log.d(TAG, "SmbParsing::->"+Arrays.toString(splitted));
        protocol = Protocol.SMB;
        time = splitted[0].substring(0, splitted[0].indexOf("."));
        StringSrc = splitted[2];
        StringDest = splitted[4];
        info = line.substring(line.indexOf(" ", 1), line.length()).replace("IP ", "");
    }
    private  void          NBNSParsing(String line) throws StringIndexOutOfBoundsException  {
        String[] splitted = line.split(" ");
        protocol = Protocol.NBNS;
     //   Log.d(TAG, "NBNSParsing::->"+Arrays.toString(splitted));
        time = splitted[0].substring(0, splitted[0].indexOf("."));
        StringSrc = splitted[2];
        StringDest = splitted[4];
        info = line.substring(line.indexOf(" ", 1), line.length()).replace("IP ", "");
    }
    private  void          ICMPParsing(String line) {
        String[] splitted = line.split(" ");
        protocol = Protocol.ICMP;
     //   Log.d(TAG, "ICMPParsing::->"+Arrays.toString(splitted));
        time = splitted[0].substring(0, splitted[0].indexOf("."));
        String[] src = extractThePortOrProto(splitted[2]);
        String[] dest = extractThePortOrProto(splitted[4]);
        StringSrc = src[0];
        StringDest = dest[0];
        info = line.substring(line.indexOf(StringDest)+StringDest.length(), line.length()).replace("ICMP ", "");
    }

    private void           IParsing(String line) {
        String[] splitted = line.split(" ");
        String type = "";
        protocol = Protocol.TCP;
        //Log.d(TAG, "IParsing::->"+ Arrays.toString(splitted));
        if (splitted.length >= 4) {
            time = splitted[0].substring(0, splitted[0].indexOf("."));
            String[] src = extractThePortOrProto(splitted[2]);
            String[] dest = extractThePortOrProto(splitted[4]);
            StringSrc = src[0];
            StringDest = dest[0];
            type = dest[1];
        } else {
            time = "Quiting...";
            StringSrc = "  ";
            StringDest = "  ";
        }
        try {
            info = type + line.substring(line.indexOf(StringDest)+StringDest.length()+type.length()+1, line.length());
        } catch (StringIndexOutOfBoundsException e) {
            e.getStackTrace();
            info = line;
        }
    }

    private void           initColorBackground() {
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
                backgroundColor = R.color.ftp;
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
    private String[]       extractThePortOrProto(String ip) {
        String[] extracted = new String[2];
        extracted[0] = ip;
        extracted[1] = "";
        if (ip.length() - ip.replace(".", "").length() != 3) {//ex:10.10.10.10.8080
            int rcx = 0, rax = 0;
            while (rcx <= ip.length()) {
                rcx = ip.indexOf(".", rcx+1);
                if (++rax == 4) {
                    extracted[0] = ip.substring(0, rcx);
                    extracted[1] = ip.substring(rcx+1, ip.length()).toUpperCase();
                    break;
                }
            }
        }
        return extracted;
    }

    @Override
    public String          toString() {
        return  "Trame n°" + offsett + " " +
                ((protocol != null) ? protocol.name() : "") + ":" +
                ((StringSrc != null) ? StringSrc : "") + ">" +
                ((StringDest != null) ? StringDest : "") + ": " +
                ((info != null) ? info : "");
    }
}
