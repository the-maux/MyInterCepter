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

    public String           Stdout(String line, Protocol protocol) {
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
                default:
                    try {
                        Log.d(TAG, "Unknow trame");
                        return newHtmlPara(line);
                    } catch (StringIndexOutOfBoundsException e) {
                        e.getStackTrace();
                        Log.e(TAG, "Trame:" + line);
                        return newHtmlPara(line);
                   }
            }

    }

    private String          newHtmlPara(String line) throws StringIndexOutOfBoundsException  {
        return "<p>" + line + "</p>";
    }
    private String          DnsParsing(String line)  throws StringIndexOutOfBoundsException {
        DNS trame = new DNS(line);
        return newHtmlPara(
                        "<font color='green'>" + trame.time + "</font>" +
                        "<font color='red'>" + "   " + trame.ipSrc + " > " + trame.ipDst + "</font>" +
                        "<font color='white'>" + " : " + trame.domain + "</font>");
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
        return newHtmlPara(line);
    }
    private String          HttpParsing(String line) throws StringIndexOutOfBoundsException  {
        return newHtmlPara(line);
    }
    private String          HttpsParsing(String line) throws StringIndexOutOfBoundsException  {
        return newHtmlPara(line);
    }
    private String          TcpParsing(String line) throws StringIndexOutOfBoundsException  {
        return newHtmlPara(line);
    }
    private String          UdpParsing(String line) throws StringIndexOutOfBoundsException  {
        return newHtmlPara(line);
    }
    private String          SmbParsing(String line) throws StringIndexOutOfBoundsException  {
        return newHtmlPara(line);
    }
    private String          NBNSParsing(String line) throws StringIndexOutOfBoundsException  {
        return newHtmlPara(line);
    }
}
