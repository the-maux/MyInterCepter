package fr.allycs.app.View.Widget.Adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.concurrent.CopyOnWriteArrayList;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Model.Net.Protocol;
import fr.allycs.app.Model.Net.Trame;
import fr.allycs.app.R;
import fr.allycs.app.View.Widget.Holder.WiresharkHolder;


public class                WiresharkAdapter extends RecyclerView.Adapter<WiresharkHolder> {
    private String          TAG = "WiresharkAdapter";
    private CopyOnWriteArrayList<Trame> originalListOfTrames;
    private CopyOnWriteArrayList<Trame> listOfTrame;
    private Activity        activity;
    public boolean          arp = true, http = true, https = true,
                            tcp = true, dns = true, udp = true, ip = true;
    public RecyclerView     mRV_Wireshark;
    private boolean         mActualize = false;

    public                  WiresharkAdapter(Activity activity, CopyOnWriteArrayList<Trame> trames, RecyclerView recyclerView) {
        this.listOfTrame = new CopyOnWriteArrayList<>();
        this.originalListOfTrames = trames;
        listOfTrame.addAll(originalListOfTrames);
        this.activity = activity;
        this.mRV_Wireshark = recyclerView;
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

    private void            addOnList(final Trame trame, final boolean reverse) {
        //Log.d(TAG, "addOnList:trame:" + trame.offsett);
        if (mActualize) {
            if (reverse) {
                listOfTrame.add(0, trame);
            } else {
                listOfTrame.add(trame);
            }
        } else {
            this.mRV_Wireshark.post(new Runnable() {
                @Override
                public void run() {
                    if (reverse) {
                        listOfTrame.add(0, trame);
                    } else {
                        listOfTrame.add(trame);
                    }
                }
            });
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
                break;
//                Log.e(TAG, "Trame unknow:" + trame.toString());
        }

    }
    public void             addTrameOnAdapter(Trame trame) {
        addTrameFiltered(trame, true);
        originalListOfTrames.add(0, trame);
    }

    public boolean          changePermissionFilter(Protocol protocol) {
        boolean ret;
        switch (protocol) {
            case ARP:
                arp = !arp;
                ret = arp;
                break;
            case HTTP:
                http = !http;
                ret = http;
                break;
            case HTTPS:
                https = !https;
                ret = https;
                break;
            case DNS:
                dns = !dns;
                ret = dns;
                break;
            case TCP:
                tcp = !tcp;
                ret = tcp;
                break;
            case UDP:
                udp = !udp;
                ret = udp;
                break;
            case IP:
                ip = !ip;
                ret = ip;
                break;
            default:
                ret = true;
        }
        this.mRV_Wireshark.post(new Runnable() {
            @Override
            public void run() {
                mActualize = true;
                listOfTrame.clear();
                if (Singleton.getInstance().UltraDebugMode)
                    dump();
                for (Trame trame : originalListOfTrames) {
                    addTrameFiltered(trame, false);
                }
                notifyDataSetChanged();
                mActualize = false;
            }
        });
        return ret;
    }
    public void             clear() {
        this.mRV_Wireshark.post(new Runnable() {
            @Override
            public void run() {
                listOfTrame.clear();
                originalListOfTrames.clear();
                notifyDataSetChanged();
            }
        });

    }
    private void            dump() {
        Log.d(TAG, "--------------------------------");
        for (Trame trame : originalListOfTrames) {
            Log.d(TAG, "OFFSET:" + trame.offsett);
        }
        Log.d(TAG, "--------------------------------");
    }
}
