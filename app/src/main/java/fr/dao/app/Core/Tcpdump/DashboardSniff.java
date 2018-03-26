package fr.dao.app.Core.Tcpdump;

import android.util.Log;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.dao.app.Model.Net.Trame;
import fr.dao.app.R;

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
    private String          TAG = "DashboardSniff";
    private boolean         mIsRunning = false;
    private int             nbrPackets = 0;
    private int             UDP_packet = 0, TCP_packet = 0, FTP_packet = 0, ICMP_packet = 0;
    private int             HTTP_packet = 0, HTTPS_packet = 0, DNS_packet = 0, ARP_Packet = 0;
    private TextView        monitorPackets, nbrTargets, timer;
    private CircleImageView status;
    private boolean         isStatusUpdated = false;


    public                  DashboardSniff() {

    }

    public void             addTrame(Trame trame) {
        if (trame == null) {
            Log.d(TAG, "trame is null");
            return;
        }
        nbrPackets++;
        if (!mIsRunning) {
            mIsRunning = true;
        }
        if (!isStatusUpdated) {
            isStatusUpdated = true;
            status.setImageResource(R.color.online_color);
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
        monitorPackets.setText(nbrPackets + " packets");
    }

    public void             stop() {
        mIsRunning = false;
    }

    public void             setMonitorView(TextView packetsNumber, TextView nbrTargets, TextView timer, CircleImageView status) {
        this.monitorPackets = packetsNumber;
        this.nbrTargets = nbrTargets;
        this.timer = timer;
        this.status = status;
    }
}