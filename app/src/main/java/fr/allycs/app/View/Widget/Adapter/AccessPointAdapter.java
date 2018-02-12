package fr.allycs.app.View.Widget.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.allycs.app.View.Behavior.MyGlideLoader;
import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.R;
import fr.allycs.app.View.Activity.HostDiscovery.FragmentHistoric;
import fr.allycs.app.View.Widget.Adapter.Holder.AccessPointHolder;

public class                    AccessPointAdapter extends RecyclerView.Adapter<AccessPointHolder> {
    private String              TAG = "AccessPointAdapter";
    private FragmentHistoric    mFragment;
    private List<AccessPoint>   mSessions;

    public enum typeFragment {  HostDetail, HistoricDB }

    public                      AccessPointAdapter(FragmentHistoric fragment, List<AccessPoint> sessions) {
        this.mFragment = fragment;
        this.mSessions = sessions;
    }

    public AccessPointHolder    onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AccessPointHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_accesspoint, parent, false));
    }

    public void                 onBindViewHolder(AccessPointHolder holder, int position) {
        final AccessPoint ap = mSessions.get(position);
        holder.ssid.setText(ap.Ssid);
        holder.ssid_subtitle.setText(ap.sessions().size() + " session" +
                ((ap.sessions().size()) >= 2 ? "s" : "") + " recorded");
        MyGlideLoader.loadDrawableInImageView(mFragment.getContext(), R.mipmap.ic_wifi_round, holder.wifi_logo, false);
        MyGlideLoader.loadDrawableInImageView(mFragment.getContext(), R.mipmap.ic_forward_round, holder.forward, false);
        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.onAccessPointFocus(ap);
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
