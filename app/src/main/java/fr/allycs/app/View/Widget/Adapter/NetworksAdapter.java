package fr.allycs.app.View.Widget.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import fr.allycs.app.Model.Target.Network;
import fr.allycs.app.R;
import fr.allycs.app.View.Activity.HostDiscovery.FragmentHostDiscoveryHistoric;
import fr.allycs.app.View.Behavior.MyGlideLoader;
import fr.allycs.app.View.Widget.Adapter.Holder.SessionHolder;

public class                    NetworksAdapter extends RecyclerView.Adapter<SessionHolder> {
    private String              TAG = "NetworksAdapter";
    private FragmentHostDiscoveryHistoric mFragment;
    private List<Network>       mSessions;

    public                      NetworksAdapter(FragmentHostDiscoveryHistoric fragment, List<Network> sessions) {
        this.mFragment = fragment;
        this.mSessions = sessions;
    }

    public SessionHolder        onCreateViewHolder(ViewGroup parent, int viewType) {
        return new SessionHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_session, parent, false));
    }

    public void                 onBindViewHolder(SessionHolder holder, int position) {
        final Network accessPoint = mSessions.get(position);
        String date = new SimpleDateFormat("dd MMMM k:mm:ss", Locale.FRANCE).format(accessPoint.lastScanDate);
        holder.title.setText(date);
        int size = accessPoint.listDevicesSerialized.split(";").length;
        String subtitle = size + " device" + ((size >= 2) ? "s" : "")+ " decouvert";
        holder.subtitle.setText(subtitle);
        View.OnClickListener onClick = onClick(accessPoint);
        MyGlideLoader.loadDrawableInCircularImageView(mFragment.getContext(), R.drawable.radar, holder.icon);
        holder.forward.setOnClickListener(onClick);
        if (accessPoint.SniffSessions() != null && !accessPoint.SniffSessions().isEmpty()) {
            MyGlideLoader.loadDrawableInImageView(mFragment.getContext(), R.mipmap.ic_forward_round,
                    holder.forward, false);
            holder.wiresharkMiniLogo.setVisibility(View.VISIBLE);
        }
        holder.card_view.setOnClickListener(onClick);
        holder.relative_layout.setOnClickListener(onClick);
    }

    private View.OnClickListener onClick(final Network accessPoint) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                mFragment.onNetworkFocused(accessPoint);
            }
        };
    }

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
}
