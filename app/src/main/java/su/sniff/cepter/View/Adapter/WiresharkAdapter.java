package su.sniff.cepter.View.Adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import su.sniff.cepter.Model.Pcap.Trame;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.Holder.WiresharkHolder;


public class                WiresharkAdapter extends RecyclerView.Adapter<WiresharkHolder> {
    private String          TAG = "WiresharkAdapter";
    private ArrayList<Trame> listOfTrame;
    private Activity        activity;

    public                  WiresharkAdapter(Activity activity, ArrayList<Trame> trames) {
        this.listOfTrame = trames;
        this.activity = activity;
    }

    @Override
    public WiresharkHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WiresharkHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tcpdump, parent, false));
    }

    @Override
    public void             onBindViewHolder(WiresharkHolder holder, int position) {
        Trame trame = listOfTrame.get(position);
        holder.No.setText(trame.offsett + "");
        holder.time.setText(trame.time);
        holder.source.setText(trame.StringSrc);
        if (trame.StringDest.contains("00:16:7f")) {
            Log.d(TAG, "trame.StringDest:" + trame.StringDest + "&Upper=>" + trame.StringDest.toUpperCase());
            holder.dest.setText("00:16:7F:13:4A:DD");
        }
        holder.dest.setText(trame.StringDest.toUpperCase());
        holder.proto.setText(trame.protocol.name().toUpperCase());
        holder.info.setText(trame.info);
        setBacgroundColor(trame.backgroundColor, holder);
    }

    private void            setBacgroundColor(int color, WiresharkHolder holder) {
        holder.No.setBackgroundColor(ContextCompat.getColor(activity, color));
        holder.time.setBackgroundColor(ContextCompat.getColor(activity, color));
        holder.source.setBackgroundColor(ContextCompat.getColor(activity, color));
        holder.dest.setBackgroundColor(ContextCompat.getColor(activity, color));
        holder.proto.setBackgroundColor(ContextCompat.getColor(activity, color));
        holder.info.setBackgroundColor(ContextCompat.getColor(activity, color));
    }

    @Override
    public int              getItemCount() {
        return listOfTrame.size();
    }
}
