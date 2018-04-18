package fr.dao.app.View.ZViewController.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Tcpdump.DashboardSniff;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Adapter.Holder.PacketHolder;

public class                    WiresharkDashboardAdapter extends RecyclerView.Adapter<PacketHolder> {
    private String              TAG = "WiresharkDashboardAdapter";
    private MyActivity          mActivity;
    private DashboardSniff      wiresharkDashboard = new DashboardSniff();
    private TextView            packetsNumber, nbrTargets, timerMonitor;
    private CircleImageView     status;
    private Timer               timer = new Timer();
    public final int            TCP = 0, UDP = 1, DNS = 2, HTTP = 3, HTTPS = 4, SPY = 5;

    public WiresharkDashboardAdapter(MyActivity activity, TextView packetsNumber, TextView nbrTargets,
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

    /**
     * lOGO
     * Thamoa
     * 20% padding
     * DDDDDD
     * 414141
     */
    public void                 onBindViewHolder(final PacketHolder holder, int position) {
        switch (holder.getAdapterPosition()) {
            case TCP:
                initCard(holder, wiresharkDashboard.TCP_packet, mActivity.getString(R.string.tcp), R.mipmap.ic_tcp);
                break;
            case UDP:
                initCard(holder, wiresharkDashboard.UDP_packet, "UDP", R.mipmap.ic_udp);
                break;
            case HTTP:
                initCard(holder, wiresharkDashboard.HTTP_packet, "HTTP", R.mipmap.ic_http);
                break;
            case HTTPS:
                initCard(holder, wiresharkDashboard.HTTPS_packet, "HTTPS", R.mipmap.ic_https);
                break;
            case DNS:
                initCard(holder, wiresharkDashboard.DNS_packet, "DNS", R.mipmap.ic_dns);
                break;
            case SPY:
                initCard(holder, 0, " credential", R.mipmap.ic_spy);
                break;
            case 6:
                break;
        }
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mActivity.showSnackbar("CARD " + holder.getAdapterPosition() + "CLICKED");
            }
        });
    }

    private void                initCard(PacketHolder holder, int nbrPackets, String protocol, int res) {
        MyGlideLoader.loadDrawableInImageView(mActivity, res, holder.logo_protocol, false);
        String title = (nbrPackets == 0) ? "No " + protocol + " packets": nbrPackets + " packets";
        holder.nbr_packets_protocol.setText(title);
    }

    public void                 setDashboard(DashboardSniff dashboard) {
        this.wiresharkDashboard = dashboard;
        dashboard.setMonitorView(packetsNumber, nbrTargets, timerMonitor, status);
    }

    private View.OnClickListener onClick(final Network accessPoint) {
        return new View.OnClickListener() {
            public void onClick(View v) {

            }
        };
    }

    public void                 startTimer() {
        class UpdateTimer extends TimerTask {
            private Date start = Calendar.getInstance().getTime();
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        timerMonitor.setText(Utils.TimeDifference(start));
                    }
                });
            }
        }
        if (timer == null)
            timer = new Timer();
        timer.scheduleAtFixedRate(new UpdateTimer(), 0, 1000);
    }

    public void                 stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void                 reset() {
        wiresharkDashboard.reset();
        notifyDataSetChanged();
        timerMonitor.setText("");

    }

    public int                  getItemCount() {
        return 6;
    }

}
