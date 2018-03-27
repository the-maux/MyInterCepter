package fr.dao.app.View.Widget.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.dao.app.Core.Tcpdump.DashboardSniff;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.R;
import fr.dao.app.View.Behavior.Activity.MyActivity;
import fr.dao.app.View.Behavior.MyGlideLoader;
import fr.dao.app.View.Widget.Adapter.Holder.PacketHolder;

public class                    WireshrakDashboardAdapter extends RecyclerView.Adapter<PacketHolder> {
    private String              TAG = "WireshrakDashboardAdapter";
    private MyActivity          mActivity;
    private DashboardSniff      wiresharkDashboard = new DashboardSniff();
    private TextView            packetsNumber, nbrTargets, timerMonitor;
    private CircleImageView     status;
    public final int            TCP = 0, UDP = 1, HTTP = 2, HTTPS = 3, DNS = 4, SPY = 5;

    public WireshrakDashboardAdapter(MyActivity activity, TextView packetsNumber, TextView nbrTargets,
                                     TextView timerMonitor, CircleImageView status) {
        this.mActivity = activity;
        this.packetsNumber = packetsNumber;
        this.nbrTargets = nbrTargets;
        this.timerMonitor = timerMonitor;
        this.status = status;
    }

    public PacketHolder         onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PacketHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_packets_type, parent, false));
    }

    public void                 onBindViewHolder(PacketHolder holder, final int position) {
        switch (position) {
            case TCP:
                initCard(holder, wiresharkDashboard.TCP_packet, "TCP");
                break;
            case UDP:
                initCard(holder, wiresharkDashboard.UDP_packet, "UDP");
                break;
            case HTTP:
                initCard(holder, wiresharkDashboard.HTTP_packet, "HTTP");
                break;
            case HTTPS:
                initCard(holder, wiresharkDashboard.HTTPS_packet, "HTTPS");
                break;
            case DNS:
                initCard(holder, wiresharkDashboard.DNS_packet, "DNS");
                break;
            case SPY:
                initCard(holder, 0, " credential");
                break;
            case 6:
                break;
        }
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mActivity.showSnackbar("CARD " + position + "CLICKED");
            }
        });
    }

    private void                initCard(PacketHolder holder, int nbrPackets, String protocol) {
        MyGlideLoader.loadDrawableInImageView(mActivity, R.mipmap.ic_https, holder.logo_protocol, false);
        String title = (nbrPackets == 0) ? "No " + protocol + " packets": nbrPackets + " packets";
        holder.nbr_packets_protocol.setText(title);
    }

    private View.OnClickListener onClick(final Network accessPoint) {
        return new View.OnClickListener() {
            public void onClick(View v) {

            }
        };
    }

    public int                  getItemCount() {
        return 6;
    }

    public void                 filtering(String query) {
/*
        mHosts.clear();
        for (Host title : mOriginalList) {
            if (title.getDumpInfo().toLowerCase().contains(query.toLowerCase()))
                mHosts.add(title);
        }
        notifyDataSetChanged();*/
    }

    public void                 setDashboard(DashboardSniff dashboard) {
        this.wiresharkDashboard = dashboard;
        dashboard.setMonitorView(packetsNumber, nbrTargets, timerMonitor, status);
    }
}
