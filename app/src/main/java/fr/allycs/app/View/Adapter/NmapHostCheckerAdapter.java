package fr.allycs.app.View.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.allycs.app.Controller.Core.Databse.DBHost;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.Holder.HostSelectionHolder;

public class                    NmapHostCheckerAdapter extends RecyclerView.Adapter<HostSelectionHolder> {
    private String              TAG = this.getClass().getName();
    private Activity            activity;
    private List<Host>          hosts;

    public                      NmapHostCheckerAdapter(Activity activity, List<Host> hosts) {
        this.hosts = hosts;
        this.activity = activity;
    }
    @Override
    public HostSelectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HostSelectionHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_host_checkbox, parent, false));
    }

    @Override
    public void                 onBindViewHolder(HostSelectionHolder holder, int position) {
        Host host = hosts.get(position);
        holder.itemView.setOnClickListener(onClickCard(host));
        holder.nameOS.setText(host.ip);
        holder.checkBox.setVisibility(View.INVISIBLE);
        Fingerprint.setOsIcon(activity, host, holder.imageOS);
    }

    private View.OnClickListener onClickCard(final Host host) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //((NmapActivity)activity).newTarget(host);
            }
        };
    }

    @Override
    public int                  getItemCount() {
        return hosts.size();
    }
}
