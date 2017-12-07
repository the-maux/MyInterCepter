package fr.allycs.app.View.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.Holder.AccessPointHolder;
import fr.allycs.app.View.HostDetail.HostFocusActivity;

public class                    SessionAdapter extends RecyclerView.Adapter<AccessPointHolder> {
    private String              TAG = this.getClass().getName();
    private HostFocusActivity   mActivity;
    private List<Session>       mSessions;
    private Singleton           mSingleton = Singleton.getInstance();

    public                      SessionAdapter(HostFocusActivity activity, List<Session> sessions) {
        this.mActivity = activity;
        this.mSessions = sessions;
    }

    @Override
    public AccessPointHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AccessPointHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false));
    }

    @Override
    public void                 onBindViewHolder(AccessPointHolder holder, int position) {
        Session ap = mSessions.get(position);
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
