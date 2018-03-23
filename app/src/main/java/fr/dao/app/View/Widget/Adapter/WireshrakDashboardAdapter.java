package fr.dao.app.View.Widget.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import fr.dao.app.Core.Tcpdump.DashboardSniff;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.R;
import fr.dao.app.View.Behavior.Activity.MyActivity;
import fr.dao.app.View.Behavior.MyGlideLoader;
import fr.dao.app.View.Widget.Adapter.Holder.PacketHolder;

public class                    WireshrakDashboardAdapter extends RecyclerView.Adapter<PacketHolder> {
    private String              TAG = "NetworksAdapter";
    private MyActivity          mActivity;
    private DashboardSniff      wiresharkDashboard;
    private TextView            subtitle_sniffer;

    public final int            TCP = 0, UDP = 1, HTTP = 2, HTTPS = 3, DNS = 4, SPY = 5;

    public WireshrakDashboardAdapter(MyActivity activity, TextView subtitle_sniffer) {
        this.mActivity = activity;
        this.subtitle_sniffer = subtitle_sniffer;
    }

    public PacketHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PacketHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_packets_type, parent, false));
    }

    public void                 onBindViewHolder(PacketHolder holder, final int position) {
        switch (position) {
            case TCP:
                initTcpCard(holder);
                break;
            case UDP:
                initUdpCard(holder);
                break;
            case HTTP:
                initHTTPCard(holder);
                break;
            case HTTPS:
                initHTTPSCard(holder);
                break;
            case DNS:
                initDNSCard(holder);
                break;
            case SPY:
                initSpyCard(holder);
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

    private void                initSpyCard(PacketHolder holder) {
        MyGlideLoader.loadDrawableInImageView(mActivity, R.drawable.spy, holder.logo_protocol, false);
        holder.nbr_packets_protocol.setText("Creds");
    }

    private void                initDNSCard(PacketHolder holder) {
        MyGlideLoader.loadDrawableInImageView(mActivity, R.mipmap.ic_dns, holder.logo_protocol, false);
        holder.nbr_packets_protocol.setText("DNS");
    }

    private void                initHTTPSCard(PacketHolder holder) {
        MyGlideLoader.loadDrawableInImageView(mActivity, R.mipmap.ic_https, holder.logo_protocol, false);
        holder.nbr_packets_protocol.setText("HTTPS");
    }

    private void                initHTTPCard(PacketHolder holder) {
        MyGlideLoader.loadDrawableInImageView(mActivity, R.mipmap.ic_http, holder.logo_protocol, false);
        holder.nbr_packets_protocol.setText("HTTP");
    }

    private void                initUdpCard(PacketHolder holder) {
        MyGlideLoader.loadDrawableInImageView(mActivity, R.mipmap.ic_udp, holder.logo_protocol, false);
        holder.nbr_packets_protocol.setText("UDP");
    }

    private void                initTcpCard(PacketHolder holder) {
        MyGlideLoader.loadDrawableInImageView(mActivity, R.mipmap.ic_tcp, holder.logo_protocol, false);
        holder.nbr_packets_protocol.setText("TCP");
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
        dashboard.setMonitorPackets(subtitle_sniffer);
    }
}
