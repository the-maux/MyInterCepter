package su.sniff.cepter.View.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.List;

import su.sniff.cepter.Model.Host;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.Holder.HostCheckBoxHolder;


public class                    TcpdumpHostCheckerADapter extends RecyclerView.Adapter<HostCheckBoxHolder> {
    private String              TAG = this.getClass().getName();
    private Activity            activity;
    private List<Host>          hosts, listHostSelected;

    public                      TcpdumpHostCheckerADapter(Activity activity, ArrayList<Host> hostsList, List<Host> hostsSelected) {
        this.hosts = hostsList;
        this.activity = activity;
        this.listHostSelected = hostsSelected;
    }
    @Override
    public HostCheckBoxHolder   onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HostCheckBoxHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_host_checkbox, parent, false));
    }

    @Override
    public void                 onBindViewHolder(HostCheckBoxHolder holder, int position) {
        final Host host = hosts.get(position);
        holder.nameOS.setText(host.getIp());
        Host.setOsIcon(activity, host.getDumpInfo(), holder.imageOS);
        holder.checkBox.setChecked(false);
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    listHostSelected.add(host);
                else
                    listHostSelected.remove(host);
            }
        });
    }

    @Override
    public int                  getItemCount() {
        return hosts.size();
    }
}
