package fr.allycs.app.View.Widget.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.R;
import fr.allycs.app.View.Activity.HostDiscovery.FragmentHostDiscoveryHistoric;
import fr.allycs.app.View.Behavior.MyGlideLoader;
import fr.allycs.app.View.Widget.Adapter.Holder.SessionHolder;

public class                    SessionAdapter extends RecyclerView.Adapter<SessionHolder> {
    private String              TAG = "SessionAdapter";
    private FragmentHostDiscoveryHistoric mFragment;
    private List<Session>       mSessions;
    private AccessPoint         mAp;

    public                      SessionAdapter(FragmentHostDiscoveryHistoric fragment, List<Session> sessions, AccessPoint ap) {
        this.mFragment = fragment;
        this.mSessions = sessions;
        this.mAp = ap;
    }

    public SessionHolder        onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SessionHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false));
    }

    public void                 onBindViewHolder(SessionHolder holder, int position) {
        final Session session = mSessions.get(position);
        String date = new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE).format(session.Date);
        holder.title.setText(date);
        int size = session.listDevicesSerialized.split(";").length;
        holder.subtitle.setText(size + " device" + ((size >= 2) ? "s" : "")+ " decouvert");
        View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.onSessionFocused(session);
            }
        };
        MyGlideLoader.loadDrawableInCircularImageView(mFragment.getContext(), R.drawable.radar,
                holder.icon);
        holder.forward.setOnClickListener(onClick);
        if (session.isSniffed) {
            MyGlideLoader.loadDrawableInImageView(mFragment.getContext(), R.mipmap.ic_forward_round,
                    holder.forward, false);
            holder.wiresharkMiniLogo.setVisibility(View.VISIBLE);
        }
        holder.card_view.setOnClickListener(onClick);
        holder.relative_layout.setOnClickListener(onClick);
    }

    @Override
    public int                  getItemCount() {
        return mSessions.size();
    }

    public void                 filtering(String query) {
/*
        mHosts.clear();
        for (Host domain : mOriginalList) {
            if (domain.getDumpInfo().toLowerCase().contains(query.toLowerCase()))
                mHosts.add(domain);
        }
        notifyDataSetChanged();*/
    }

    public AccessPoint          getAccessPoint() {
        return mAp;
    }
}
