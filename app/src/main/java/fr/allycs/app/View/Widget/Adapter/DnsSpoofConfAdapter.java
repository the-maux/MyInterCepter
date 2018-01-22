package fr.allycs.app.View.Widget.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Model.Target.DNSSpoofItem;
import fr.allycs.app.R;
import fr.allycs.app.View.DnsActivity;
import fr.allycs.app.View.Widget.Holder.DnsSpoofConfHolder;


public class                    DnsSpoofConfAdapter extends RecyclerView.Adapter<DnsSpoofConfHolder> {
    private String              TAG = this.getClass().getName();
    private DnsActivity         mActivity;
    private List<DNSSpoofItem>  mDnsIntercepts;
    private Singleton           mSingleton = Singleton.getInstance();

    public                      DnsSpoofConfAdapter(DnsActivity activity, List<DNSSpoofItem> dnsInterceptList) {
        this.mDnsIntercepts = dnsInterceptList;
        this.mActivity = activity;
    }
    @Override
    public DnsSpoofConfHolder   onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DnsSpoofConfHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dnsspoof, parent, false));
    }

    @Override
    public void                 onBindViewHolder(DnsSpoofConfHolder holder, int position) {
        DNSSpoofItem host = mDnsIntercepts.get(position);
        holder.domain.setText("www." + host.domain + "  " + host.domain);
        holder.ip.setText(host.ip);
        holder.deleteImage.setOnClickListener(onDeleteDns(host));
    }

    private View.OnClickListener onDeleteDns(final DNSSpoofItem domain) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSingleton.getDnsControler().removeDomain(domain);
                mActivity.updateToolbarTitle(domain);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int                  getItemCount() {
        return mDnsIntercepts.size();
    }

    public void                 filtering(String query) {
        /*TODO:Log.d(TAG, "filterByString:" + query);
        mHosts.clear();
        for (Host domain : mOriginalList) {
            if (domain.getDumpInfo().toLowerCase().contains(query.toLowerCase()))
                mHosts.add(domain);
        }
        notifyDataSetChanged();*/
    }
}
