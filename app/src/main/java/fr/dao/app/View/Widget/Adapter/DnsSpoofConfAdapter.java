package fr.dao.app.View.Widget.Adapter;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import fr.dao.app.Core.Configuration.GlideApp;
import fr.dao.app.Core.Configuration.GlideRequest;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Target.DNSSpoofItem;
import fr.dao.app.R;
import fr.dao.app.View.Activity.DnsSpoofing.DnsActivity;
import fr.dao.app.View.Widget.Adapter.Holder.GenericLittleCardAvatarHolder;


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
        ;
        GlideRequest builder = GlideApp.with(mActivity)
                .load(host.domain + "/favicon.ico")
                .placeholder(R.drawable.webserver_icon)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                ;
//        if (override)
//            builder.apply(new RequestOptions()
//                    .fitCenter()
//                    .override(100, 100));
        builder.into(holder.logo);
        holder.icon.setOnClickListener(onDeleteDns(host));
    }

    private View.OnClickListener onDeleteDns(final DNSSpoofItem domain) {
        return new View.OnClickListener() {
            public void onClick(View view) {
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    new AlertDialog.Builder(mActivity)
                        .setTitle(domain.domain)
                        .setMessage("Would you like to remove this spoofed title ?")
                        .setPositiveButton(mActivity.getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
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
