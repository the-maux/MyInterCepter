package fr.allycs.app.View.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.Holder.SessionHolder;
import fr.allycs.app.View.HostDetail.HostDetailFragment;

public class                    SessionAdapter extends RecyclerView.Adapter<SessionHolder> {
    private String              TAG = this.getClass().getName();
    private HostDetailFragment  mFragment;
    private List<Session>       mSessions;
    private Singleton           mSingleton = Singleton.getInstance();

    public                      SessionAdapter(HostDetailFragment fragment, List<Session> sessions) {
        this.mFragment = fragment;
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
        String date = new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE).format(session.Date);
        if (session.name == null || session.name.isEmpty()) {
            holder.title.setText(date);
        } else {
            holder.title.setText(session.name);
            holder.subtitle.setText(date);
        }
        holder.subtitle.setText(session.listDevices.size() + " devices decouvert");
        holder.forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.onSessionFocused(session);
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
