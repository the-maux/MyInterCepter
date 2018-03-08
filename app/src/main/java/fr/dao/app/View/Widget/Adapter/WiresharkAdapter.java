package fr.dao.app.View.Widget.Adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Net.Protocol;
import fr.dao.app.Model.Net.Trame;
import fr.dao.app.R;
import fr.dao.app.View.Widget.Adapter.Holder.WiresharkHolder;

public class                WiresharkAdapter extends RecyclerView.Adapter<WiresharkHolder> {
    private String          TAG = "WiresharkAdapter";
    private CopyOnWriteArrayList<Trame> originalListOfTrames;
    private CopyOnWriteArrayList<Trame> listOfTrame;
    private Activity        mActivity;
    private boolean         mActualize = false;
    private RecyclerView    mRV_Wireshark;
    private boolean         arp = true, http = true, https = true,
                            tcp = true, dns = true, udp = true, ip = true;

    public                  WiresharkAdapter(Activity activity, RecyclerView recyclerView) {
        this.listOfTrame = new CopyOnWriteArrayList<>();
        this.originalListOfTrames = new CopyOnWriteArrayList<>();
        listOfTrame.addAll(originalListOfTrames);
        this.mActivity = activity;
        this.mRV_Wireshark = recyclerView;
    }

    public WiresharkHolder  onCreateViewHolder(ViewGroup parent, int viewType) {
        return new WiresharkHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tcpdump, parent, false), false);
    }

    public void             onBindViewHolder(WiresharkHolder holder, int position) {
        Trame trame = listOfTrame.get(position);
        String s = "" + position;
        holder.No.setText(s);
        holder.time.setText(trame.time);
        holder.source.setText(trame.StringSrc);
        holder.dest.setText(trame.StringDest.toUpperCase());
        holder.proto.setText(trame.protocol.name().toUpperCase());
        holder.info.setText(trame.info);
        setBackgroundColor(trame.backgroundColor, holder);
    }

    private void            setBackgroundColor(int color, WiresharkHolder holder) {
        holder.No.setBackgroundColor(ContextCompat.getColor(mActivity, color));
        holder.time.setBackgroundColor(ContextCompat.getColor(mActivity, color));
        holder.source.setBackgroundColor(ContextCompat.getColor(mActivity, color));
        holder.dest.setBackgroundColor(ContextCompat.getColor(mActivity, color));
        holder.proto.setBackgroundColor(ContextCompat.getColor(mActivity, color));
        holder.info.setBackgroundColor(ContextCompat.getColor(mActivity, color));
        holder.relativeLayout.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.material_grey_700));

    }

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
    private synchronized void addTrameFiltered(Trame trame, boolean reverse) {
        if (trame.protocol != null)
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
    public synchronized void addTrameOnAdapter(Trame trame) {
        trame.offsett = originalListOfTrames.size();
        addTrameFiltered(trame, true);
        originalListOfTrames.add(0, trame);
    }

    public void             loadListOfTrame(ArrayList<Trame> trames, ProgressDialog dialog) {
        listOfTrame.addAll(trames);
        notifyDataSetChanged();
        dialog.hide();
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
