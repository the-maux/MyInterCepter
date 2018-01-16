package fr.allycs.app.View.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Misc.MyGlideLoader;
import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.Holder.AccessPointHolder;
import fr.allycs.app.View.HostDiscovery.FragmentHistoric;

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
        MyGlideLoader.loadDrawableInImageView(mFragment.getContext(), R.mipmap.ic_wifi_round, holder.wifi_logo);
        MyGlideLoader.loadDrawableInImageView(mFragment.getContext(), R.mipmap.ic_forward_round, holder.forward);
        holder.forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.onAccessPointFocus(ap);
            }
        });
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
