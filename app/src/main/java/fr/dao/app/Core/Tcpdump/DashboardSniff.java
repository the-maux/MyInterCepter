package fr.dao.app.Core.Tcpdump;

import fr.dao.app.Model.Net.Trame;
/**
 * TODO:
 *          + Packets/s
 *          + UDP Packets/s
 *          + TCP/s
 *          + HTTP/s
 *          + HTTPs/s
 *          + DNS/s
 *          + /s
 *          + Packets/s
 *          + Size (Packets.length/s) by seconds
 ---------------------------------
 * DNS SERVER :
 * WEBSERVER VISITED ?
 *
 */

public class                DashboardSniff {
    private boolean         mIsRunning = false;
    private int             nbrPackets = 0;
    private int             UDP_packet = 0, TCP_packet = 0, FTP_packet = 0, ICMP_packet = 0;
    private int             HTTP_packet = 0, HTTPS_packet = 0, DNS_packet = 0, ARP_Packet = 0;

    public                  DashboardSniff() {

    }

    public void             addTrame(Trame trame) {
        nbrPackets++;
        if (!mIsRunning) {
            mIsRunning = true;
        }
        switch (trame.protocol) {
            case IP:
                HTTP_packet++;
                break;
            case UDP:
                UDP_packet++;
                break;
            case TCP:
                TCP_packet++;
                break;
            case HTTP:
                HTTP_packet++;
                break;
            case HTTPS:
                HTTPS_packet++;
                break;
            case DNS:
                DNS_packet++;
                break;
            case ARP:
                ARP_Packet++;
                break;
        }
    }

    public void             stop() {
        mIsRunning = false;
    }
}
