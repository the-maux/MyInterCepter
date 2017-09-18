package su.sniff.cepter.View.Adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import su.sniff.cepter.Model.Pcap.Protocol;
import su.sniff.cepter.Model.Pcap.Trame;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.Holder.WiresharkHolder;


public class                WiresharkAdapter extends RecyclerView.Adapter<WiresharkHolder> {
    private String          TAG = "WiresharkAdapter";
    private ArrayList<Trame> listOfTrame, originalListOfTrames;
    private Activity        activity;
    public boolean          arp = true, http = true, https = true, tcp = true, dns = true, udp = true, ip = true;

    public                  WiresharkAdapter(Activity activity, ArrayList<Trame> trames) {
        this.listOfTrame = new ArrayList<>();
        this.originalListOfTrames = trames;
        listOfTrame.addAll(originalListOfTrames);
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
            holder.dest.setText("00:16:7F:13:4A:DD");
        } else {
            holder.dest.setText(trame.StringDest.toUpperCase());
        }
        holder.proto.setText(trame.protocol.name().toUpperCase());
        holder.info.setText(trame.info);
        setBackgroundColor(trame.backgroundColor, holder);
    }

    private void            setBackgroundColor(int color, WiresharkHolder holder) {
        holder.No.setBackgroundColor(ContextCompat.getColor(activity, color));
        holder.time.setBackgroundColor(ContextCompat.getColor(activity, color));
        holder.source.setBackgroundColor(ContextCompat.getColor(activity, color));
        holder.dest.setBackgroundColor(ContextCompat.getColor(activity, color));
        holder.proto.setBackgroundColor(ContextCompat.getColor(activity, color));
        holder.info.setBackgroundColor(ContextCompat.getColor(activity, color));
        holder.relativeLayout.setBackgroundColor(ContextCompat.getColor(activity, R.color.material_grey_700));
        holder.No.setTypeface(null, Typeface.NORMAL);
        holder.time.setTypeface(null, Typeface.NORMAL);
        holder.source.setTypeface(null, Typeface.NORMAL);
        holder.dest.setTypeface(null, Typeface.NORMAL);
        holder.proto.setTypeface(null, Typeface.NORMAL);
        holder.info.setTypeface(null, Typeface.NORMAL);
    }

    @Override
    public int              getItemCount() {
        return listOfTrame.size();
    }

    private void            addOnList(Trame trame, boolean reverse) {
        if (reverse) {
            listOfTrame.add(0, trame);
        } else {
            listOfTrame.add(trame);
        }
    }
    private void            addTrameFiltered(Trame trame, boolean reverse) {
        switch (trame.protocol) {
            case ARP:
                if (arp)
                    addOnList(trame, reverse);
                break;
            case HTTP:
                if (http)
                    addOnList(trame, reverse);
                break;
            case HTTPS:
                if (https)
                    addOnList(trame, reverse);
                break;
            case DNS:
                if (dns)
                    addOnList(trame, reverse);
                break;
            case TCP:
                if (tcp)
                    addOnList(trame, reverse);
                break;
            case UDP:
                if (udp)
                    addOnList(trame, reverse);
                break;
            case IP:
                if (ip)
                    addOnList(trame, reverse);
                break;
            default:
                Log.e(TAG, "Trame unknow:" + trame.toString());
        }

    }
    public void             addTrameOnAdapter(Trame trame) {
        addTrameFiltered(trame, true);
        originalListOfTrames.add(0, trame);
    }

    public void             changePermissionFilter(Protocol protocol) {
        switch (protocol) {
            case ARP:
                arp = !arp;
                break;
            case HTTP:
                http = !http;
                break;
            case HTTPS:
                https = !https;
                break;
            case DNS:
                dns = !dns;
                break;
            case TCP:
                tcp = !tcp;
                break;
            case UDP:
                udp = !udp;
                break;
            case IP:
                ip = !ip;
                break;
        }
        listOfTrame.clear();
        for (Trame trame : originalListOfTrames) {
            addTrameFiltered(trame, false);
        }
        notifyDataSetChanged();
    }
    public void             clear() {
        listOfTrame.clear();
        originalListOfTrames.clear();
        notifyDataSetChanged();
    }
}
