package su.sniff.cepter.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.hdodenhof.circleimageview.CircleImageView;
import su.sniff.cepter.Model.Host;
import su.sniff.cepter.R;
import su.sniff.cepter.View.ScanActivity;
import su.sniff.cepter.adapter.Holder.HostHolder;

import java.util.List;

import android.support.v7.widget.RecyclerView;

import com.bumptech.glide.Glide;


public class                    HostAdapter extends RecyclerView.Adapter<HostHolder> {
    private String              TAG = "HostAdapter";
    private ScanActivity        activity;
    private List<Host>          mHosts;

    public HostAdapter(List<Host> hosts, ScanActivity context) {
        mHosts = hosts;
        activity = context;
    }

    @Override
    public HostHolder           onCreateViewHolder(ViewGroup parent, int viewType) {
        return new HostHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_host, parent, false));
    }

    @SuppressWarnings("WrongConstant")
    @Override
    public void                 onBindViewHolder(HostHolder holder, int position) {
        Host host = mHosts.get(position);
        holder.relativeLayout.setOnClickListener(onCardClick(position, holder));
        holder.ipHostname.setText(host.getIp() + " " + host.getName());
        holder.mac.setText(host.getMac());
        holder.os.setText(host.getOS());
        holder.vendor.setText(host.getVendor());
        holder.selected.setChecked(host.isSelected());
        Log.d(TAG, "updating :" + position + " with selected:" + host.isSelected());
        setOsIcon(mHosts.get(position).getDumpInfo(), holder.osIcon);
    }

    private void                setOsIcon(String InfoDevice, CircleImageView osImageView) {
        int ImageRessource;
        if (InfoDevice.contains("Windows")) {
            ImageRessource = R.drawable.winicon;
        } else if (InfoDevice.contains("Apple")) {
            ImageRessource = R.drawable.ios;
        } else if (InfoDevice.contains("Android") || InfoDevice.contains("Mobile") || InfoDevice.contains("Samsung")) {
            ImageRessource = R.drawable.android;
        } else if (InfoDevice.contains("Cisco")) {
            ImageRessource = R.drawable.cisco;
        } else if (InfoDevice.contains("Raspberry")) {
            ImageRessource = R.drawable.rasp;
        } else if (InfoDevice.contains("QUANTA")) {
            ImageRessource = R.drawable.quanta;
        } else if (InfoDevice.contains("Bluebird")) {
            ImageRessource = R.drawable.bluebird;
        } else if (InfoDevice.contains("Ios")) {
            ImageRessource = R.drawable.ios;
        } else if (!(!InfoDevice.contains("Unix") && !InfoDevice.contains("Linux") && !InfoDevice.contains("BSD"))) {
            ImageRessource = R.drawable.linuxicon;
        } else
            ImageRessource = R.drawable.monitor;
        Glide.with(activity)
                .load(ImageRessource)
                .centerCrop()
                .into(osImageView);
    }

    /**
     * Behavior when item click
     * @param position position
     * @param holder
     * @return listener
     */
    private View.OnClickListener onCardClick(final int position, final HostHolder holder) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Host host = mHosts.get(position);
                host.setSelected(!host.isSelected());
                holder.selected.setSelected(host.isSelected());
                Log.d(TAG, host.getIp() + " is now isSelected:" + host.isSelected());
                notifyItemChanged(position);
            }
        };
    }

    @Override
    public int                  getItemCount() {
        return mHosts.size();
    }
}
