package fr.allycs.app.View.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.Holder.SessionHolder;
import fr.allycs.app.View.HostDetail.HostFocusActivity;

public class                    SessionAdapter extends RecyclerView.Adapter<SessionHolder> {
    private String              TAG = this.getClass().getName();
    private HostFocusActivity   mActivity;
    private List<Session>       mSessions;
    private Singleton           mSingleton = Singleton.getInstance();

    public                      SessionAdapter(HostFocusActivity activity, List<Session> sessions) {
        this.mActivity = activity;
        this.mSessions = sessions;
    }

    @Override
    public SessionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SessionHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false));
    }

    @Override
    public void                 onBindViewHolder(SessionHolder holder, int position) {
        final Session session = mSessions.get(position);
        holder.nameSession.setText(session.toString());
        holder.forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onSessionFocused(session);
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
