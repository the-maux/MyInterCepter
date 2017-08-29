package su.sniff.cepter.View.Adapter;

import android.app.Activity;
import android.icu.text.StringSearch;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import su.sniff.cepter.Model.Pcap.DoraProcess;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.Holder.DoraHolder;
import su.sniff.cepter.View.Adapter.Holder.HostCheckBoxHolder;


public class                    DoraAdapter extends RecyclerView.Adapter<DoraHolder> {
    private String              TAG = this.getClass().getName();
    private Activity            activity;
    private List<DoraProcess>   hosts;

    public                      DoraAdapter(Activity activity, List<DoraProcess> hostsSelected) {
        this.hosts = hostsSelected;
        this.activity = activity;
    }

    @Override
    public DoraHolder           onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DoraHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dora, parent, false));
    }

    @Override
    public void                 onBindViewHolder(DoraHolder holder, int position) {
        final DoraProcess host = hosts.get(position);
        holder.diagnose.setText(new String(new char[(host.sent - host.rcv)]).replace("\0", "*"));
        holder.IP.setText(host.host.getIp());
        holder.uptime.setText("Uptime:" + host.getUptime());
        holder.stat.setText("sent: " + host.sent + " / rcv: " + host.rcv);
        int pourc = host.getPourcentage();
        if (pourc == 0) {
            holder.diagnosPourcentage.setTextColor(ContextCompat.getColor(activity, R.color.material_light_white));
        } else if (pourc <= 60) {
            holder.diagnosPourcentage.setTextColor(ContextCompat.getColor(activity, R.color.material_red_500));
        } else if (pourc <= 90) {
            holder.diagnosPourcentage.setTextColor(ContextCompat.getColor(activity, R.color.material_deep_orange_500));
        } else {
            holder.diagnosPourcentage.setTextColor(ContextCompat.getColor(activity, R.color.material_green_600));
        }
        holder.diagnosPourcentage.setText(pourc + "%");
    }

    @Override
    public int                  getItemCount() {
        return hosts.size();
    }
}
