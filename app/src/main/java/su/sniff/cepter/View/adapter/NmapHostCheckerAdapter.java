package su.sniff.cepter.View.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.bumptech.glide.Glide;

import java.util.List;

import su.sniff.cepter.Model.Host;
import su.sniff.cepter.R;
import su.sniff.cepter.View.NmapActivity;
import su.sniff.cepter.View.ScanActivity;
import su.sniff.cepter.View.adapter.Holder.HostCheckBoxHolder;
import su.sniff.cepter.View.adapter.Holder.HostScanHolder;

/**
 * Created by root on 04/08/17.
 */

public class                    NmapHostCheckerAdapter extends RecyclerView.Adapter<HostCheckBoxHolder> {
    private String              TAG = this.getClass().getName();
    private Activity            activity;
    private List<Host>          hosts;

    public                      NmapHostCheckerAdapter(Activity activity, List<Host> hosts) {
        this.hosts = hosts;
        this.activity = activity;
    }
    @Override
    public HostCheckBoxHolder   onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HostCheckBoxHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_host_checkbox, parent, false));
    }

    @Override
    public void                 onBindViewHolder(HostCheckBoxHolder holder, int position) {
        Host host = hosts.get(position);
        holder.itemView.setOnClickListener(onClickCard(host));
        holder.nameOS.setText(host.getIp());
        holder.checkBox.setVisibility(View.INVISIBLE);
        Host.setOsIcon(activity, host.getDumpInfo(), holder.imageOS);
    }

    private View.OnClickListener onClickCard(final Host host) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NmapActivity)activity).newTarget(host);
            }
        };
    }

    @Override
    public int                  getItemCount() {
        return hosts.size();
    }
}
