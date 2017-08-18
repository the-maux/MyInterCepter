package su.sniff.cepter.View.TextView;

import android.util.Log;

import su.sniff.cepter.Model.Pcap.DNS;
import su.sniff.cepter.Model.Pcap.Protocol;
import su.sniff.cepter.Model.Pcap.Trame;


/**
 * This class parse the trame out of TcpDump
 * to translate it in Http for TextView printing
 */
public class                TrameToHtml {
    private String          TAG = getClass().getName();

    public String           toHtml(Trame trame) {
        switch (trame.protocol) {
            case ARP:
                Log.d(TAG, "ARP trame");
                return ArpParsing(trame);
            case HTTP:
                Log.d(TAG, "HTTP trame");
                return HttpParsing(trame);
            case HTTPS:
                Log.d(TAG, "HTTPS trame");
                return HttpsParsing(trame);
            case TCP:
                Log.d(TAG, "TCP trame");
                return TcpParsing(trame);
            case UDP:
                Log.d(TAG, "UDP trame");
                return UdpParsing(trame);
            case DNS:
                Log.d(TAG, "DNS trame");
                return DnsParsing(trame);
            case SMB:
                Log.d(TAG, "SMB trame");
                return SmbParsing(trame);
            case NBNS:
                Log.d(TAG, "NBNS trame");
                return NBNSParsing(trame);
            case ICMP:
                Log.d(TAG, "ICMP trame");
                return ICMPParsing(trame);
            default:
                Log.d(TAG, "Unknow trame");
                return newHtmlPara(trame.info, Protocol.UNKNOW);
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
    private String          DnsParsing(Trame trame)  throws StringIndexOutOfBoundsException {
        return newHtmlPara(
                        "<font color='green'>" + trame.time + "</font>" +
                        "<font color='red'>" + "   " + trame.StringSrc + " > " + trame.StringDest + "</font>" +
                        "<font color='white'>" + " : " + trame.info + "</font>", Protocol.DNS);
    }
    private String          ArpParsing(Trame trame) {
        String line = "<font color='green'>" + trame.info + " </font>";
        if (line.contains("WHO-HAS")) {
            line = line + " ?";
        }
        return newHtmlPara(line, Protocol.ARP);
    }
    private String          HttpParsing(Trame trame) throws StringIndexOutOfBoundsException  {
        String line = "<font color='blue'>" + trame.info + "</font>";
        return newHtmlPara(line, Protocol.HTTP);
    }

    private String          HttpsParsing(Trame trame) throws StringIndexOutOfBoundsException  {
        String line = "<font color='blue'>" + trame.info + "</font>";
        return newHtmlPara(line, Protocol.HTTPS);
    }
    private String          TcpParsing(Trame trame) throws StringIndexOutOfBoundsException  {
        return newHtmlPara(trame.info, Protocol.TCP);
    }
    private String          UdpParsing(Trame trame) throws StringIndexOutOfBoundsException  {
        return newHtmlPara(trame.info, Protocol.UDP);
    }
    private String          SmbParsing(Trame trame) throws StringIndexOutOfBoundsException  {
        return newHtmlPara(trame.info, Protocol.SMB);
    }
    private String          NBNSParsing(Trame trame) throws StringIndexOutOfBoundsException  {
        return newHtmlPara(trame.info, Protocol.UNKNOW);
    }
    private String          ICMPParsing(Trame trame) {
        return newHtmlPara(trame.info, Protocol.ICMP);
    }
}
