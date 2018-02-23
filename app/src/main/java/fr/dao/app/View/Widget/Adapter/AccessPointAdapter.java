package fr.dao.app.View.Widget.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.dao.app.Model.Target.Network;
import fr.dao.app.R;
import fr.dao.app.View.Activity.HostDiscovery.FragmentHostDiscoveryHistoric;
import fr.dao.app.View.Behavior.MyGlideLoader;
import fr.dao.app.View.Widget.Adapter.Holder.AccessPointHolder;

public class                    AccessPointAdapter extends RecyclerView.Adapter<AccessPointHolder> {
    private String              TAG = "AccessPointAdapter";
    private FragmentHostDiscoveryHistoric mFragment;
    private List<Network>   mSessions;

    public enum typeFragment {  HostDetail, HistoricDB }

    public                      AccessPointAdapter(FragmentHostDiscoveryHistoric fragment, List<Network> sessions) {
        this.mFragment = fragment;
        this.mSessions = sessions;
    }

    public AccessPointHolder    onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AccessPointHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_little_cardview, parent, false));
    }

    public void                 onBindViewHolder(AccessPointHolder holder, int position) {
        final Network ap = mSessions.get(position);
        holder.ssid.setText(ap.Ssid);
        String subtitile = "scanned " + ap.nbrScanned + " times" +
                ((ap.nbrScanned) >= 2 ? "s" : "") + " recorded";
        holder.ssid_subtitle.setText(subtitile);
        MyGlideLoader.loadDrawableInImageView(mFragment.getContext(), R.mipmap.ic_wifi_round, holder.wifi_logo, false);
        MyGlideLoader.loadDrawableInImageView(mFragment.getContext(), R.mipmap.ic_forward_round, holder.forward, false);
        View.OnClickListener onClick = new View.OnClickListener() {
            public void onClick(View v) {
               // mFragment.onAccessPointFocus(ap);
            }
        };
        holder.forward.setOnClickListener(onClick);
        holder.card_view.setOnClickListener(onClick);
        holder.relative_layout.setOnClickListener(onClick);
    }

    public int                  getItemCount() {
        return mSessions.size();
    }

    public void                 filtering(String query) {
        /*TODO:Log.d(TAG, "filterByString:" + query);
        mHosts.clear();
        for (Host domain : mOriginalList) {
            if (domain.getDumpInfo().toLowerCase().contains(query.toLowerCase()))
                mHosts.add(domain);
        }
        notifyDataSetChanged();*/
    }
}
