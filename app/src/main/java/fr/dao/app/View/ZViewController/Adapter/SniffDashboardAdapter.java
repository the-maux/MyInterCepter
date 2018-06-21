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
import fr.dao.app.View.Sniff.SniffActivity;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Adapter.Holder.PacketHolder;

public class                    SniffDashboardAdapter extends RecyclerView.Adapter<PacketHolder> {
    private String              TAG = "SniffDashboardAdapter";
    private MyActivity          mActivity;
    private DashboardSniff      wiresharkDashboard;
    private TextView            packetsNumber, nbrTargetsNameFile, timerMonitor;
    private CircleImageView     status;
    private Timer               timer;
    public final int            TCP = 0, UDP = 1, DNS = 2, HTTP = 3, HTTPS = 4, SPY = 5;

    public SniffDashboardAdapter(MyActivity activity, TextView packetsNumber, TextView nbrTargets,
                                 TextView timerMonitor, CircleImageView status) {
        updateFragmentWidget(activity, packetsNumber, nbrTargets, timerMonitor, status);
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
                initCard(holder, wiresharkDashboard != null ? wiresharkDashboard.TCP_packet : 0, mActivity.getString(R.string.tcp), R.mipmap.ic_tcp);
                break;
            case UDP:
                initCard(holder, wiresharkDashboard != null ? wiresharkDashboard.UDP_packet : 0, "UDP", R.mipmap.ic_udp);
                break;
            case HTTP:
                initCard(holder, wiresharkDashboard != null ? wiresharkDashboard.HTTP_packet : 0, "HTTP", R.mipmap.ic_http);
                break;
            case HTTPS:
                initCard(holder, wiresharkDashboard != null ? wiresharkDashboard.HTTPS_packet : 0, "HTTPS", R.mipmap.ic_https);
                break;
            case DNS:
                initCard(holder, wiresharkDashboard != null ? wiresharkDashboard.DNS_packet : 0, "DNS", R.mipmap.ic_dns);
                break;
            case SPY:
                initCard(holder, 0, " credential", R.mipmap.ic_spy);
                break;
            case 6:
                break;
        }
        holder.card_view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.vibrateDevice(mActivity, 100);
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
        dashboard.setMonitorView(mActivity, packetsNumber, nbrTargetsNameFile, status);
    }

    private View.OnClickListener onClick(final Network accessPoint) {
        return new View.OnClickListener() {
            public void onClick(View v) {

            }
        };
    }
    UpdateTimer mytime;

    public void updateFragmentWidget(MyActivity activity, TextView nbrPacket, TextView timer, TextView nameFile, CircleImageView statusIconSniffing) {
        this.mActivity = activity;
        this.packetsNumber = nbrPacket;
        this.nbrTargetsNameFile = nameFile;
        this.timerMonitor = timer;
        this.status = statusIconSniffing;
        if (wiresharkDashboard != null)
            wiresharkDashboard.setAdapter(this);
    }

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
    public void                 startTimer() {
        if (timer != null) {
            timer = new Timer();
            mytime = new UpdateTimer();
            timer.scheduleAtFixedRate(mytime, 0, 1000);
        }
    }

    public void                 stopTimer() {
        if (timer != null) {
            mytime.cancel();
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
