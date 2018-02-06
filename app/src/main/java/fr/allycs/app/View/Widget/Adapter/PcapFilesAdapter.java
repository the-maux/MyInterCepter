package fr.allycs.app.View.Widget.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.allycs.app.Controller.AndroidUtils.MyFragment;
import fr.allycs.app.Controller.AndroidUtils.MyGlideLoader;
import fr.allycs.app.Model.Net.Pcap;
import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.R;
import fr.allycs.app.View.HostDetail.PcapFragment;
import fr.allycs.app.View.HostDiscovery.FragmentHistoric;
import fr.allycs.app.View.Widget.Holder.PcapHolder;

public class                    PcapFilesAdapter extends RecyclerView.Adapter<PcapHolder> {
    private String              TAG = "AccessPointAdapter";
    private MyFragment          mFragment;
    private List<Pcap>          mPcaps;

    public PcapFilesAdapter(PcapFragment fragment, List<Pcap> pcaps) {
        this.mFragment = fragment;
        this.mPcaps = pcaps;
    }

    public PcapHolder    onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PcapHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_accesspoint, parent, false));
    }

    public void                 onBindViewHolder(PcapHolder holder, int position) {
        final Pcap pcap = mPcaps.get(position);
        holder.title.setText(pcap.nameFile.replace("_", " "));
        holder.subtitle.setText(pcap.nameFile);
        MyGlideLoader.loadDrawableInImageView(mFragment.getContext(), R.drawable.wireshark, holder.wifi_logo, false);
        MyGlideLoader.loadDrawableInImageView(mFragment.getContext(), R.mipmap.ic_forward_round, holder.forward, false);
//TODO: Get read it on wireshark
/*       View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment.onAccessPointFocus(ap);
            }
        };
        holder.forward.setOnClickListener(onClick);
        holder.card_view.setOnClickListener(onClick);
        holder.relative_layout.setOnClickListener(onClick);*/
    }

    public int                  getItemCount() {
        return mPcaps.size();
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