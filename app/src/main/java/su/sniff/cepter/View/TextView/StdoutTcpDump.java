package su.sniff.cepter.View.TextView;

import su.sniff.cepter.Model.Pcap.DNS;
import su.sniff.cepter.Model.Pcap.Protocol;

/**
 * Created by maxim on 14/08/2017.
 */

public class StdoutTcpDump {
    public String stdout(String line, Protocol protocol) {
        switch (protocol) {
            case ARP:
                break;
            case HTTP:
                break;
            case HTTPS:
                break;
            case TCP:
                break;
            case UDP:
                break;
            case DNS:
                DNS trame = new DNS(line);
                return  "<p>" +
                        "<font color='green'>" + trame.time + "</font>" +
                        "<font color='red'>" + "   " + trame.ipSrc + " > " + trame.ipDst + "</font>" +
                        "<font color='white'>" + " : " + trame.domain + "</font>" +
                        "</p>";
            case SMB:
                break;
            case NBNS:
                break;
        }
        return line;
    }
}
