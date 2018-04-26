package fr.dao.app.View.ZViewController.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import fr.dao.app.Core.Configuration.GlideApp;
import fr.dao.app.Core.Configuration.GlideRequest;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Model.Target.DNSSpoofItem;
import fr.dao.app.R;
import fr.dao.app.View.DnsSpoofing.DnsActivity;
import fr.dao.app.View.ZViewController.Adapter.Holder.GenericLittleCardAvatarHolder;


public class                    DnsSpoofConfAdapter extends RecyclerView.Adapter<GenericLittleCardAvatarHolder> {
    private String              TAG = "DnsSpoofConfAdapter";
    private DnsActivity         mActivity;
    private List<DNSSpoofItem>  mDnsIntercepts;
    private Singleton           mSingleton = Singleton.getInstance();

    public                      DnsSpoofConfAdapter(DnsActivity activity, List<DNSSpoofItem> dnsInterceptList) {
        this.mDnsIntercepts = dnsInterceptList;
        this.mActivity = activity;
    }

    public GenericLittleCardAvatarHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GenericLittleCardAvatarHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_generic_little, parent, false));
    }

    public void                 onBindViewHolder(GenericLittleCardAvatarHolder holder, int position) {
        DNSSpoofItem host = mDnsIntercepts.get(position);
        holder.title.setText("www." + host.domain + "  " + host.domain);
        holder.subtitle.setText(host.ip);
        holder.icon.setOnClickListener(onDeleteDns(host));
        try {
            GlideRequest builder = GlideApp.with(mActivity)
                    .load("http://" + host.domain + "/favicon.ico")
                    .placeholder(R.drawable.webserver_icon)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
            builder.into(holder.logo);
        } catch (Exception e) {
            Log.d(TAG, "Error loading in HTTP, go to HTTPS");
            GlideRequest builder = GlideApp.with(mActivity)
                    .load("https://" + host.domain + "/favicon.ico")
                    .placeholder(R.drawable.webserver_icon)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
            builder.into(holder.logo);
        }
    }

    private View.OnClickListener onDeleteDns(final DNSSpoofItem domain) {
        return new View.OnClickListener() {
            public void onClick(View view) {
                Utils.vibrateDevice(mActivity, 100);
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    new AlertDialog.Builder(mActivity)
                        .setTitle(domain.domain)
                        .setMessage("Would you like to remove this spoofed title ?")
                        .setPositiveButton(mActivity.getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Utils.vibrateDevice(mActivity, 100);
                                mSingleton.getDnsControler().removeDomain(domain);
                                mActivity.onDnsmasqConfChanged(domain + " deleted from configuration");
                                mActivity.setToolbarTitle(null, mDnsIntercepts.size() + " title spoofable");
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(mActivity.getResources().getString(android.R.string.no), null)
                        .show();
                    }
                });

            }
        };
    }

    public int                  getItemCount() {
        return mDnsIntercepts.size();
    }

    public void                 filtering(String query) {
        /*TODO:Log.d(TAG, "filterByString:" + query);
        mHosts.reset();
        for (Host title : mOriginalList) {
            if (title.getDumpInfo().toLowerCase().contains(query.toLowerCase()))
                mHosts.add(title);
        }
        notifyDataSetChanged();*/
    }
}
