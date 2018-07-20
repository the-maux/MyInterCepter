package fr.dao.app.View.ZViewController.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.dao.app.Core.Configuration.Comparator.Comparators;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.State;
import fr.dao.app.Model.Unix.Os;
import fr.dao.app.R;
import fr.dao.app.View.HostDetail.HostDetailActivity;
import fr.dao.app.View.ZViewController.Adapter.Holder.HostDiscoveryHolder;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;


public class                    HostDiscoveryAdapter extends RecyclerView.Adapter<HostDiscoveryHolder> {
    private String              TAG = "HostDiscoveryAdapter";
    private Activity            mActivity;
    private List<Host>          mHosts = null, mOriginalList = new ArrayList<>();
    private RecyclerView        mHost_RV;
    private FloatingActionButton mFab;
    private Singleton           mSingleton = Singleton.getInstance();
    private boolean             mIsHistoric = false;
    private int                 nbrPrinted = 0;

    public                      HostDiscoveryAdapter(Activity activity, RecyclerView Host_RV,
                                                     boolean mIsHistoric, FloatingActionButton fab) {
        mActivity = activity;
        mHost_RV = Host_RV;
        this.mIsHistoric = mIsHistoric;
        mFab = fab;
    }

    public HostDiscoveryHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
            return new HostDiscoveryHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hostdiscovery, parent, false));
    }

    public void                 onBindViewHolder(final HostDiscoveryHolder holder, final int position) {
        if (mHosts != null && !mOriginalList.isEmpty()) {
            final Host host = mHosts.get(position);
            String ipHostname = host.ip + ((host.getName().contains(host.ip)) ? "" : " [" + host.getName() + "]");
            holder.ipAndHostname.setText(ipHostname);
            holder.mac.setText(host.mac);
            if (host.state == State.FILTERED) {
                pushThisShyGuyToFront(holder, host);
            }
            //String os = host.os.contains("Unknow") ? "No os information" : host.os;
            holder.os.setText(host.os);
            holder.vendor.setText(host.vendor);
            if (mIsHistoric)
                holder.statusIcon.setVisibility(View.GONE);
            else
                printHostState(holder, host);
            checkedBehavior(holder, host, position);
            holder.relativeLayout.setBackgroundColor(ContextCompat.getColor(mActivity, /*Special background to notice my device*/
                    (host.isItMyDevice) ? R.color.primary_dark : R.color.cardview_dark_background));
            MyGlideLoader.setOsIcon(host, holder.osIcon);
            setAnimation(holder.cardView, holder);
        } //else Log.d(TAG, "Skeleton Views");
    }

    private void                printHostState(HostDiscoveryHolder holder, Host host) {
        int res = R.color.filtered_color;
        switch (host.state) {
            case ONLINE:
                res = R.color.online_color;
                break;
            case OFFLINE:
                res = R.color.offline_color;
                break;
            case FILTERED:
                res = R.color.filtered_color;
                break;
        }
        holder.statusIcon.setImageResource(res);
    }

    private void                pushThisShyGuyToFront(HostDiscoveryHolder holder,Host host) {
        printHostState(holder, host);
    }

    private void                checkedBehavior(HostDiscoveryHolder holder, final Host host, int position) {
        if (mIsHistoric)
            holder.selected.setVisibility(View.GONE);
        else {
            holder.selected.setChecked(host.selected);
            holder.relativeLayout.setOnClickListener(onCardClick(position, holder));
            holder.relativeLayout.setOnLongClickListener(onCardLongClick(host, holder, position));
            holder.selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (buttonView.isPressed()) {
                        host.selected = isChecked;
                    }
                }
            });
        }
    }

    private void                onHostChecked(final HostDiscoveryHolder holder, Host host, final int position) {
        Utils.vibrateDevice(mActivity);
        host.selected = !host.selected;
        Log.d(TAG, "onHostChecked:"+ host.selected);
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
                Utils.vibrateDevice(mActivity, 100);
                onHostChecked(holder, mHosts.get(position), position);
            }
        };
    }
    private View.OnLongClickListener onCardLongClick(final Host host, final HostDiscoveryHolder holder, final int position) {
        return new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        Utils.vibrateDevice(mActivity);
                    }
                });
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        //host.dumpMe();

                        ActivityOptionsCompat options;
                        Intent intent = new Intent(mActivity, HostDetailActivity.class);
                        Pair<View, String> p1 = Pair.create((View)holder.osIcon, "hostPicture");
                        Pair<View, String> p2 = Pair.create((View)holder.ipAndHostname, "hostTitle");
                        options = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, p1, p2);
                        intent.putExtra("mode", "Live");
                        intent.putExtra("macAddress", host.mac);
                        mActivity.startActivity(intent, options.toBundle());
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

    public int                  getItemCount() {
        return (mHosts == null || mOriginalList.isEmpty()) ? 10 : mHosts.size();
    }

    public ArrayList<Integer>        getOsList() {
        ArrayList<Integer> listOs = new ArrayList<>();
        if (mOriginalList != null) {
            for (Host host : mOriginalList) {
                if (!listOs.contains(host.osType))
                    listOs.add(host.osType);
            }
        }
        return listOs;
    }

    public int                  filterByOs(ArrayList<Integer> OsList) {
        mHosts.clear();
        for (Host host : mOriginalList) {
            for (Integer os : OsList) {
                if (Os.toString(os).contentEquals(Os.toString(host.osType))) {
                    mHosts.add(host);
                    break;
                }
            }
        }
        notifyDataSetChanged();
        return mHosts.size();

    }

    public void                 filterByString(String query) {
        mHosts.clear();
        for (Host host : mOriginalList) {
            if ((host.dumpInfo != null && host.dumpInfo.toLowerCase().contains(query.toLowerCase())) ||
                    host.ip.contains(query) || host.mac.contains(query) ||
                    host.vendor.contains(query) || host.name.contains(query))
                mHosts.add(host);
        }
        notifyDataSetChanged();
    }

    public void                 updateHostList(final List<Host> hosts) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Collections.sort(hosts, Comparators.getHostComparator());
                mHosts = new ArrayList<>();
                mHosts.addAll(hosts);
                mOriginalList.clear();
                mOriginalList.addAll(hosts);
                notifyDataSetChanged();
            }
        });
        Log.d(TAG, "updateHostList");
    }

    public void                 setAnimation(CardView cardView, HostDiscoveryHolder holder) {
        if (!holder.animate) {

        }
    }
}
