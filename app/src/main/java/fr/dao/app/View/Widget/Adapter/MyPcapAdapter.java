package fr.dao.app.View.Widget.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import fr.dao.app.Model.Net.Pcap;
import fr.dao.app.R;
import fr.dao.app.View.Activity.HostDetail.PcapFragment;
import fr.dao.app.View.Behavior.Fragment.MyFragment;
import fr.dao.app.View.Behavior.MyGlideLoader;
import fr.dao.app.View.Widget.Adapter.Holder.PcapHolder;

public class                    MyPcapAdapter extends RecyclerView.Adapter<PcapHolder> {
    private String              TAG = "AccessPointAdapter";
    private MyFragment          mFragment;
    private List<Pcap>          mPcaps;

    public                      MyPcapAdapter(PcapFragment fragment, List<Pcap> pcaps) {
        this.mFragment = fragment;
        this.mPcaps = pcaps;
    }

    public PcapHolder           onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PcapHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_little_cardview, parent, false));
    }

    public void                 onBindViewHolder(PcapHolder holder, int position) {
        final Pcap pcap = mPcaps.get(position);
        holder.title.setText(pcap.nameFile.replace("_", " "));
        holder.subtitle.setText(pcap.nameFile);
        MyGlideLoader.loadDrawableInImageView(mFragment.getContext(), R.drawable.wireshark, holder.wifi_logo, false);
        MyGlideLoader.loadDrawableInImageView(mFragment.getContext(), R.mipmap.ic_forward_round, holder.forward, false);
        //TODO: Get read it on wireshark
    }

    public int                  getItemCount() {
        return mPcaps.size();
    }

    public void                 filtering(String query) {
        /*TODO:Log.d(TAG, "filterByString:" + query);*/
    }
}
