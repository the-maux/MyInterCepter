package fr.allycs.app.View.Widget.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Misc.MyGlideLoader;
import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.R;
import fr.allycs.app.View.HostDiscovery.FragmentHistoric;
import fr.allycs.app.View.Widget.Holder.AccessPointHolder;

public class                    AccessPointAdapter extends RecyclerView.Adapter<AccessPointHolder> {
    private String              TAG = this.getClass().getName();
    private FragmentHistoric    mFragment;
    private List<AccessPoint>   mSessions;
    private Singleton           mSingleton = Singleton.getInstance();

    public enum typeFragment {  HostDetail, HistoricDB }

    public                      AccessPointAdapter(FragmentHistoric fragment, List<AccessPoint> sessions) {
        this.mFragment = fragment;
        this.mSessions = sessions;
    }

    @Override
    public AccessPointHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AccessPointHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_accesspoint, parent, false));
    }

    @Override
    public void                 onBindViewHolder(AccessPointHolder holder, int position) {
        final AccessPoint ap = mSessions.get(position);
        holder.ssid.setText(ap.Ssid);
        holder.ssid_subtitle.setText(ap.sessions().size() + " session(s) recorded");
        MyGlideLoader.loadDrawableInImageView(mFragment.getContext(), R.mipmap.ic_wifi_round, holder.wifi_logo);
        MyGlideLoader.loadDrawableInImageView(mFragment.getContext(), R.mipmap.ic_forward_round, holder.forward);
        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "Click for AccessPoint detail : " + ap);
                mFragment.onAccessPointFocus(ap);
            }
        };
        holder.forward.setOnClickListener(onClick);
        holder.card_view.setOnClickListener(onClick);
        holder.relative_layout.setOnClickListener(onClick);
    }

    @Override
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
