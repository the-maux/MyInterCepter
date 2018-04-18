package fr.dao.app.View.ZViewController.Adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.List;

import fr.dao.app.R;
import fr.dao.app.View.Wireshark.WiresharkActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Adapter.Holder.PcapHolder;
import fr.dao.app.View.ZViewController.Fragment.PcapListerFragment;

public class                    PcapFileAdapter extends RecyclerView.Adapter<PcapHolder> {
    private String              TAG = "AccessPointAdapter";
    private Activity            mActivity;
    private List<File>          mPcaps;
    private PcapListerFragment  pcapListerFragment;

    public                      PcapFileAdapter(Activity activity, List<File> pcaps, PcapListerFragment pcapListerFragment) {
        this.mActivity = activity;
        this.mPcaps = pcaps;
        this.pcapListerFragment = pcapListerFragment;
    }

    public PcapHolder           onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PcapHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_little_cardview, parent, false));
    }

    public void                 onBindViewHolder(PcapHolder holder, int position) {
        final File pcap = mPcaps.get(position);
        holder.title.setText(pcap.getName());
        long size = (pcap.length() / 1024);
        if (size >= 1024)
            holder.subtitle.setText((size / 1024) + " Mbyte");
        else if (size > 0)
            holder.subtitle.setText(size + " Kbyte");
        else
            holder.subtitle.setText(pcap.length() + " bytes");
        holder.title.setOnClickListener(onFocusPcapFile(pcap));
        holder.subtitle.setOnClickListener(onFocusPcapFile(pcap));
        holder.icon.setOnClickListener(onFocusPcapFile(pcap));
        MyGlideLoader.loadDrawableInImageView(mActivity, R.drawable.pcapfile, holder.icon, false);
    }

    private View.OnClickListener onFocusPcapFile(final File pcap) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, WiresharkActivity.class);
                Log.d(TAG, "newIntent Pcap:"+pcap.getPath());
                intent.putExtra("Pcap", pcap.getPath());
                if (pcapListerFragment != null)
                    pcapListerFragment.dismiss();
                mActivity.startActivity(intent);
            }
        };
    }

    public int                  getItemCount() {
        return mPcaps.size();
    }

    public void                 filtering(String query) {
        /*TODO:Log.d(TAG, "filterByString:" + query);*/
    }
}
