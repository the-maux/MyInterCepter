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
import fr.dao.app.View.Widget.Adapter.Holder.SessionHolder;

public class                    WireshrakDashboardAdapter extends RecyclerView.Adapter<SessionHolder> {
    private String              TAG = "NetworksAdapter";
    private MyActivity          mActivity;
    private DashboardSniff      wiresharkDashboard;
    private TextView            subtitle_sniffer;

    public final int            TCP = 0, UDP = 1, HTTP = 2, HTTPS = 3, DNS = 4, SPY = 5;

    public WireshrakDashboardAdapter(MyActivity activity, TextView subtitle_sniffer) {
        this.mActivity = activity;
        this.subtitle_sniffer = subtitle_sniffer;
    }

    public SessionHolder        onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SessionHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dora, parent, false));
    }

    public void                 onBindViewHolder(SessionHolder holder, final int position) {
        switch (position) {
            case TCP:
                initTcpCard();
                break;
            case UDP:
                initUdpCard();
                break;
            case HTTP:
                initHTTPCard();
                break;
            case HTTPS:
                initHTTPSCard();
                break;
            case DNS:
                initDNSCard();
                break;
            case SPY:
                initSpyCard();
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

    private void                initSpyCard() {

    }

    private void                initDNSCard() {
    }

    private void                initHTTPSCard() {

    }

    private void                initHTTPCard() {

    }

    private void                initUdpCard() {

    }

    private void                initTcpCard() {
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
