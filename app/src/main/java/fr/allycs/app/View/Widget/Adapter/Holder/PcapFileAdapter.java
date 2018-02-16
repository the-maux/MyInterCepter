package fr.allycs.app.View.Widget.Adapter.Holder;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.util.List;

import fr.allycs.app.R;
import fr.allycs.app.View.Behavior.MyGlideLoader;

public class                    PcapFileAdapter extends RecyclerView.Adapter<PcapHolder> {
    private String              TAG = "AccessPointAdapter";
    private Activity            mActivity;
    private List<File>          mPcaps;

    public PcapFileAdapter(Activity activity, List<File> pcaps) {
        this.mActivity = activity;
        this.mPcaps = pcaps;
    }

    public PcapHolder           onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PcapHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_accesspoint, parent, false));
    }

    public void                 onBindViewHolder(PcapHolder holder, int position) {
        final File pcap = mPcaps.get(position);

        holder.title.setText(pcap.getName());
        holder.subtitle.setVisibility(View.GONE);
        holder.card_view.setOnClickListener(onFocusPcapFile());
        MyGlideLoader.loadDrawableInImageView(mActivity, R.drawable.wireshark, holder.wifi_logo, false);
    }

    private View.OnClickListener onFocusPcapFile() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: reload on wireshark (Create WiresharkReadFileFragment)
//                mActivity.startActivity();
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
