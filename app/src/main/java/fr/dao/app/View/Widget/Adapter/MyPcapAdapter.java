package fr.dao.app.View.Widget.Adapter;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Model.Net.Pcap;
import fr.dao.app.R;
import fr.dao.app.View.Activity.Wireshark.WiresharkActivity;
import fr.dao.app.View.Behavior.Activity.MyActivity;
import fr.dao.app.View.Behavior.MyGlideLoader;
import fr.dao.app.View.Widget.Adapter.Holder.PcapHolder;

public class                    MyPcapAdapter extends RecyclerView.Adapter<PcapHolder> {
    private String              TAG = "MyPcapAdapter";
    private MyActivity          mActivity;
    private List<Pcap>          mPcaps, mOriginals = new ArrayList<>();

    public                      MyPcapAdapter(MyActivity activity, List<Pcap> pcaps) {
        this.mActivity = activity;
        this.mPcaps = pcaps;
        mOriginals.addAll(pcaps);
    }

    public PcapHolder           onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PcapHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_little_cardview, parent, false));
    }

    public void                 onBindViewHolder(PcapHolder holder, int position) {
        final Pcap pcap = mPcaps.get(position);
        holder.title.setText(pcap.nameFile.replace("_", " "));

        long size = (pcap.getFile().length() / 1024);
        if (size >= 1024)
            holder.subtitle.setText((size / 1024) + "Mb");
        else
            holder.subtitle.setText(size + "kb");
        holder.title.setOnClickListener(onFocusPcapFile(pcap.getFile()));
        holder.subtitle.setOnClickListener(onFocusPcapFile(pcap.getFile()));
        holder.wifi_logo.setOnClickListener(onFocusPcapFile(pcap.getFile()));
        MyGlideLoader.loadDrawableInImageView(mActivity, R.drawable.pcapfile, holder.wifi_logo, false);
    }

    private View.OnClickListener onFocusPcapFile(final File pcap) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, WiresharkActivity.class);
                Log.d(TAG, "newIntent Pcap:"+pcap.getPath());
                intent.putExtra("Pcap", pcap.getPath());
                mActivity.startActivity(intent);

            }
        };
    }

    public int                  getItemCount() {
        return mPcaps.size();
    }

    public void                 filtering(String query) {
        mPcaps.clear();
        for (Pcap pcap : mOriginals) {
            if (pcap.nameFile.contains(query))
                mPcaps.add(pcap);
        }
        notifyDataSetChanged();
    }
}
