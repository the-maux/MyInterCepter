package su.sniff.cepter.View.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.R;
import su.sniff.cepter.View.ScanActivity;
import su.sniff.cepter.View.Adapter.Holder.HostScanHolder;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;


public class                    HostScanAdapter extends RecyclerView.Adapter<HostScanHolder> {
    private String              TAG = "HostScanAdapter";
    private ScanActivity        activity;
    private List<Host>          mHosts = null;
    private List<Host>          originalList;
    private RecyclerView        mHost_RV;

    public                      HostScanAdapter(ScanActivity context, RecyclerView Host_RV) {
        activity = context;
        mHost_RV = Host_RV;
    }

    @Override public HostScanHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HostScanHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan_host, parent, false));
    }

    @Override public void       onBindViewHolder(final HostScanHolder holder, final int position) {
        final Host host = mHosts.get(holder.getAdapterPosition());
        holder.ipHostname.setText(host.getIp() + " " + host.getName());
        holder.mac.setText(host.getMac());
        holder.os.setText(host.getOS());
        holder.vendor.setText(host.getVendor());
        holder.selected.setChecked(host.isSelected());
        holder.relativeLayout.setOnClickListener(onCardClick(holder.getAdapterPosition(), holder));
        holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    host.setSelected(isChecked);
                    Log.d(TAG, "onCheckedChanged");
                }
            }
        });
        Host.setOsIcon(activity, mHosts.get(holder.getAdapterPosition()).getDumpInfo(), holder.osIcon);
    }

    private void                 onHostChecked(final HostScanHolder holder, Host host, final int position) {
        host.setSelected(!host.isSelected());
        holder.selected.setSelected(host.isSelected());
        Log.d(TAG, host.getIp() + " is now isSelected:" + host.isSelected());
        mHost_RV.post(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(position);
            }
        });
    }

    /**
     * Behavior when item click
     * @param position position
     * @param holder
     * @return listener
     */
    private View.OnClickListener onCardClick(final int position, final HostScanHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHostChecked(holder, mHosts.get(position), position);
            }
        };
    }

    public void                 selectAll() {
        if (mHosts != null && mHosts.size() > 0) {
            for (Host host : mHosts) {
                host.setSelected(true);
            }
            notifyDataSetChanged();
        }
    }

    @Override public int        getItemCount() {
        return (mHosts == null) ? 0 : mHosts.size();
    }

    public ArrayList<String>    getOsList() {
        ArrayList<String> listOs = new ArrayList<>();
        if (originalList != null) {
            for (Host host : originalList) {
                if (host.getOsType() != null && !listOs.contains(host.getOsType().name()))
                    listOs.add(host.getOsType().name());
            }
            Log.d(TAG, "listOS:" + listOs);
        }
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
