package su.sniff.cepter.View.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import su.sniff.cepter.Model.Host;
import su.sniff.cepter.R;
import su.sniff.cepter.View.ScanActivity;
import su.sniff.cepter.View.adapter.Holder.HostHolder;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.RecyclerView;


public class                    HostAdapter extends RecyclerView.Adapter<HostHolder> {
    private String              TAG = "HostAdapter";
    private ScanActivity        activity;
    private List<Host>          mHosts = null;
    private List<Host>          originalList;

    public                      HostAdapter(ScanActivity context) {
        activity = context;
    }

    @Override public HostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HostHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_host, parent, false));
    }

    @Override public void       onBindViewHolder(HostHolder holder, int position) {
        Host host = mHosts.get(position);
        holder.relativeLayout.setOnClickListener(onCardClick(position, holder));
        holder.ipHostname.setText(host.getIp() + " " + host.getName());
        holder.mac.setText(host.getMac());
        holder.os.setText(host.getOS());
        holder.vendor.setText(host.getVendor());
        holder.selected.setChecked(host.isSelected());
        Log.d(TAG, "updating :" + position + " with selected:" + host.isSelected());
        Host.setOsIcon(activity, mHosts.get(position).getDumpInfo(), holder.osIcon);
    }

    /**
     * Behavior when item click
     * @param position position
     * @param holder
     * @return listener
     */
    private View.OnClickListener onCardClick(final int position, final HostHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Host host = mHosts.get(position);
                host.setSelected(!host.isSelected());
                holder.selected.setSelected(host.isSelected());
                Log.d(TAG, host.getIp() + " is now isSelected:" + host.isSelected());
                notifyItemChanged(position);
            }
        };
    }

    @Override public int        getItemCount() {
        return (mHosts == null) ? 0 : mHosts.size();
    }

    public ArrayList<String>    getOsList() {
        ArrayList<String> listOs = new ArrayList<>();
        for (Host host : originalList) {
            if (!listOs.contains(host.getOsType().name()))
                listOs.add(host.getOsType().name());
        }
        Log.d(TAG, "listOS:" + listOs);
        return listOs;
    }

    public void                 filterByOs(ArrayList<String> Os) {
        Log.d(TAG, "filterByOs:" + Os);
        mHosts.clear();
        for (Host host : originalList) {
            for (String os : Os) {
                if (os.contains(host.getOsType().name())) {
                    mHosts.add(host);
                    Log.d(TAG, "os OK:" + os);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void                 filterByString(String query) {
        Log.d(TAG, "filterByString:" + query);
        mHosts.clear();
        for (Host host : originalList) {
            if (host.getDumpInfo().toLowerCase().contains(query.toLowerCase()))
                mHosts.add(host);
        }
        notifyDataSetChanged();
    }

    public void                 updateHostList(List<Host> hosts) {
        mHosts = new ArrayList<>();
        mHosts.addAll(hosts);
        originalList = hosts;
        Log.d(TAG, "updateHostList mHost" + mHosts.size() + " and hosts:" + hosts.size());
        notifyDataSetChanged();
    }
}