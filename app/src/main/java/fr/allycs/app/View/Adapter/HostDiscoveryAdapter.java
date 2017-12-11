package fr.allycs.app.View.Adapter;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Network.Discovery.Fingerprint;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;
import fr.allycs.app.View.HostDiscovery.HostDiscoveryActivity;
import fr.allycs.app.View.Adapter.Holder.HostDiscoveryHolder;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;


public class HostDiscoveryAdapter extends RecyclerView.Adapter<HostDiscoveryHolder> {
    private String              TAG = "HostDiscoveryAdapter";
    private Activity            mActivity;
    private List<Host>          mHosts = null;
    private List<Host>          mOriginalList;
    private RecyclerView        mHost_RV;
    private boolean             isHistoric = false;

    public HostDiscoveryAdapter(Activity context, RecyclerView Host_RV, boolean isHistoric) {
        mActivity = context;
        mHost_RV = Host_RV;
        this.isHistoric = isHistoric;
    }

    @Override public HostDiscoveryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HostDiscoveryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_hostdiscovery, parent, false));
    }

    @Override public void       onBindViewHolder(final HostDiscoveryHolder holder, final int position) {
        final Host host = mHosts.get(position);
        String ipHostname = host.ip + " " + host.getName();
        holder.ipHostname.setText(ipHostname);
        holder.mac.setText(host.mac);
        holder.os.setText(host.os);
        holder.vendor.setText(host.vendor);
        Fingerprint.setOsIcon(mActivity, host, holder.osIcon);
        if (isHistoric)
            holder.selected.setVisibility(View.GONE);
        else {

            holder.selected.setChecked(host.selected);
            holder.relativeLayout.setOnClickListener(onCardClick(position, holder));
            holder.relativeLayout.setOnLongClickListener(onCardLongClick(host));
            holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isPressed()) {
                        host.selected = isChecked;
                    }
                }
            });
        }
        isItMyDevice(host, holder);
    }

    private void                isItMyDevice(Host host, HostDiscoveryHolder holder) {
        if (host.ip.contains(Singleton.getInstance().network.myIp) &&
                Singleton.getInstance().network.myIp.length() == host.ip.length()) {
            holder.relativeLayout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.primary_dark));
            holder.ipHostname.setText(host.ip + " " + host.getName() + " MY DEVICE");
        } else {
            holder.relativeLayout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.cardview_dark_background));
        }
    }

    private void                 onHostChecked(final HostDiscoveryHolder holder, Host host, final int position) {
        host.selected = !host.selected;
        holder.selected.setSelected(host.selected);
        mHost_RV.post(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(position);
            }
        });
    }

    private View.OnClickListener onCardClick(final int position, final HostDiscoveryHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onHostChecked(holder, mHosts.get(position), position);
            }
        };
    }
    private View.OnLongClickListener onCardLongClick(final Host host) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ((HostDiscoveryActivity)mActivity).focusOneTarget(host);
                    }
                });
                return false;
            }
        };
    }
    public void                 selectAll() {
        if (mHosts != null && mHosts.size() > 0) {
            for (Host host : mHosts) {
                host.selected = true;
            }
            notifyDataSetChanged();
        }
    }

    @Override public int        getItemCount() {
        return (mHosts == null) ? 0 : mHosts.size();
    }

    public ArrayList<String>    getOsList() {
        ArrayList<String> listOs = new ArrayList<>();
        if (mOriginalList != null) {
            for (Host host : mOriginalList) {
                if (host.osType != null && !listOs.contains(host.osType.name()))
                    listOs.add(host.osType.name());
            }
        }
        return listOs;
    }

    public void                 filterByOs(ArrayList<String> Os) {
        mHosts.clear();
        for (Host host : mOriginalList) {
            for (String os : Os) {
                if (os.contains(host.osType.name())) {
                    mHosts.add(host);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void                 filterByString(String query) {
        Log.d(TAG, "filterByString:" + query);
        mHosts.clear();
        for (Host host : mOriginalList) {
            if (host.dumpInfo.toLowerCase().contains(query.toLowerCase()))
                mHosts.add(host);
        }
        notifyDataSetChanged();
    }

    public void                 updateHostList(List<Host> hosts) {
        mHosts = new ArrayList<>();
        mHosts.addAll(hosts);
        mOriginalList = hosts;
        notifyDataSetChanged();
    }
}
