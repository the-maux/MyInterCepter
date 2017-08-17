package su.sniff.cepter.View.TextView;

import android.util.Log;

import su.sniff.cepter.Model.Pcap.DNS;
import su.sniff.cepter.Model.Pcap.Protocol;


/**
 * This class parse the trame out of TcpDump
 * to translate it in Http for TextView printing
 */
public class                TrameToHtml {
    private String          TAG = getClass().getName();

    public String           Stdout(String line) {
        if (line.contains("A?")) {
            return dispatch(line, Protocol.DNS);
        } else if (line.toLowerCase().contains("arp")) {
            return dispatch(line, Protocol.ARP);
        } else if (line.toLowerCase().contains("https")){
            return dispatch(line, Protocol.HTTPS);
        } else if (line.toLowerCase().contains("http")){
            return dispatch(line, Protocol.HTTP);
        }  else if (line.toLowerCase().contains("tcp")){
            return dispatch(line, Protocol.TCP);
        } else if (line.toLowerCase().contains("udp")){
            return dispatch(line, Protocol.UDP);
        } else if (line.toLowerCase().contains("icmp")){
            return dispatch(line, Protocol.ICMP);
        } else if (line.contains("Quiting...")){
            return "<h5>Quiting...</h5>";
        } else {
            return dispatch(line, Protocol.UNKNOW);
        }
    }

    private String            dispatch(String line, Protocol protocol) {
        switch (protocol) {
            case ARP:
                Log.d(TAG, "ARP trame");
                return ArpParsing(line);
            case HTTP:
                Log.d(TAG, "HTTP trame");
                return HttpParsing(line);
            case HTTPS:
                Log.d(TAG, "HTTPS trame");
                return HttpsParsing(line);
            case TCP:
                Log.d(TAG, "TCP trame");
                return TcpParsing(line);
            case UDP:
                Log.d(TAG, "UDP trame");
                return UdpParsing(line);
            case DNS:
                Log.d(TAG, "DNS trame");
                return DnsParsing(line);
            case SMB:
                Log.d(TAG, "SMB trame");
                return SmbParsing(line);
            case NBNS:
                Log.d(TAG, "NBNS trame");
                return NBNSParsing(line);
            case ICMP:
                Log.d(TAG, "NBNS trame");
                return ICMPParsing(line);
            default:
                try {
                    Log.d(TAG, "Unknow trame");
                    return NBNSParsing(line);
                } catch (StringIndexOutOfBoundsException e) {
                    e.getStackTrace();
                    Log.e(TAG, "Trame:" + line);
                    return newHtmlPara(line, Protocol.UNKNOW);
                }
        }
    }



    private String          newHtmlPara(String line, Protocol protocol) throws StringIndexOutOfBoundsException  {
        switch (protocol) {
            case ARP:
                return "<h5 style='bgcolor='#d6e7ff'>" + "ARP  :" + line + "<br />" + "</h5>";
            case HTTP:
                return "<h5 bgcolor='#8cff7f'>" + "HTTP :" + line + "<br />" + "</h5>";
            case HTTPS:
                return "<h5 bgcolor='#8cff7f'>" + "HTTPS:" + line + "<br />" + "</h5>";
            case TCP:
                return "<h5 bgcolor='#cb0003'>" + "TCP  :" + line + "<br />" + "</h5>";
            case UDP:
                return "<h5 bgcolor='#70dfff'>" + "UDP  :" + line + "<br />" + "</h5>";
            case DNS:
                return "<h5 bgcolor='#0ecb00'>" + "DNS  :" + line + "<br />" + "</h5>";
            case SMB:
                return "<h5 bgcolor='#fff999'>" + "SMB  :" + line + "<br />" + "</h5>";
            case NBNS:
                return "<h5 bgcolor='#fff999'>" + "NBNS :" + line + "<br />" + "</h5>";
            case ICMP:
                return "<h5 bgcolor='#fff999'>" + "ICMP :" + line + "<br />" + "</h5>";
            default:
                return "<h5 bgcolor='#FFFFFF'>" + "IP   :" + line + "<br />" + "</h5>";
        }
    }

    /**
     *     private String          DnsParsing(String line)  throws StringIndexOutOfBoundsException {
     DNS trame = new DNS(line);
     return newHtmlPara(
     "<font color='green'>" + trame.time + "</font>" +
     "<font color='red'>" + "   " + trame.ipSrc + " > " + trame.ipDst + "</font>" +
     "<font color='white'>" + " : " + trame.domain + "</font>", Protocol.DNS);
     }
     private String          ArpParsing(String line) {
     String[] splitted = line.substring(line.indexOf(" "), line.length()).split(" ");
     line = "<font color='green'>" + splitted[2].toUpperCase() + " </font>" + // 'REPLY' OR 'WHO-HAS'
     "<font color='red'>" + splitted[3] + " </font>" + //Ip1
     "<font color='green'>" + splitted[4].toUpperCase() + " </font>" + //'is-at' OR 'tell'
     "<font color='white'>" + splitted[5] + " </font>"; //Ip2 OR MAC
     if (line.contains("WHO-HAS")) {
     line = line + " ?";
     }
     return newHtmlPara(line, Protocol.ARP);
     }
     * @param line
     * @return
     * @throws StringIndexOutOfBoundsException
     */
    private String          DnsParsing(String line)  throws StringIndexOutOfBoundsException {
        DNS trame = new DNS(line);
        return newHtmlPara(
                        "<font color='green'>" + trame.time + "</font>" +
                        "<font color='red'>" + "   " + trame.ipSrc + " > " + trame.ipDst + "</font>" +
                        "<font color='white'>" + " : " + trame.domain + "</font>", Protocol.DNS);
    }
    private String          ArpParsing(String line) {
        String[] splitted = line.substring(line.indexOf(" "), line.length()).split(" ");
        line = "<font color='green'>" + splitted[2].toUpperCase() + " </font>" + // 'REPLY' OR 'WHO-HAS'
                "<font color='red'>" + splitted[3] + " </font>" + //Ip1
                "<font color='green'>" + splitted[4].toUpperCase() + " </font>" + //'is-at' OR 'tell'
                "<font color='white'>" + splitted[5] + " </font>"; //Ip2 OR MAC
        if (line.contains("WHO-HAS")) {
            line = line + " ?";
        }
        return newHtmlPara(line, Protocol.ARP);
    }
    private String          HttpParsing(String line) throws StringIndexOutOfBoundsException  {
        String lineTmp = line.substring(line.indexOf(" "), line.length()).replace("IP ", "");
        lineTmp = "<font color='blue'>" + lineTmp + "</font>";
        return newHtmlPara(lineTmp, Protocol.HTTP);
    }

    private String          HttpsParsing(String line) throws StringIndexOutOfBoundsException  {
        String lineTmp = line.substring(line.indexOf(" ", 1), line.length()).replace("IP ", "");
        lineTmp = "<font color='blue'>" + lineTmp + "</font>";
        return newHtmlPara(lineTmp, Protocol.HTTPS);
    }
    private String          TcpParsing(String line) throws StringIndexOutOfBoundsException  {
        String lineTmp = line.substring(line.indexOf(" ", 1), line.length()).replace("IP ", "");
        lineTmp = "" + lineTmp + "";
        return newHtmlPara(lineTmp, Protocol.TCP);
    }
    private String          UdpParsing(String line) throws StringIndexOutOfBoundsException  {
        String lineTmp = line.substring(line.indexOf(" ", 1), line.length()).replace("IP ", "");
        lineTmp = "" + lineTmp + "";
        return newHtmlPara(lineTmp, Protocol.UDP);
    }
    private String          SmbParsing(String line) throws StringIndexOutOfBoundsException  {
        String lineTmp = line.substring(line.indexOf(" ", 1), line.length()).replace("IP ", "");
        lineTmp = "" + lineTmp + "";
        return newHtmlPara(lineTmp, Protocol.SMB);
    }
    private String          NBNSParsing(String line) throws StringIndexOutOfBoundsException  {
        String lineTmp = line.substring(line.indexOf(" ", 1), line.length()).replace("IP ", "");
        lineTmp = "" + lineTmp + "";
        return newHtmlPara(lineTmp, Protocol.UNKNOW);
    }
    private String          ICMPParsing(String line) {
        String lineTmp = line.substring(line.indexOf(" ", 1), line.length()).replace("IP ", "");
        lineTmp = "<font color='yellow'>" + lineTmp + "</font>";
        return newHtmlPara(lineTmp, Protocol.ICMP);
    }
}
