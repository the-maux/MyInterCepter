package fr.allycs.app.View.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Model.Target.DNSSpoofItem;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.Holder.DnsSpoofConfHolder;
import fr.allycs.app.View.DnsActivity;


public class DnsSpoofConfAdapter extends RecyclerView.Adapter<DnsSpoofConfHolder> {
    private String              TAG = this.getClass().getName();
    private DnsActivity activity;
    private List<DNSSpoofItem>  dnsIntercepts;
    private Singleton           singleton = Singleton.getInstance();

    public DnsSpoofConfAdapter(DnsActivity activity, List<DNSSpoofItem> dnsInterceptList) {
        this.dnsIntercepts = dnsInterceptList;
        this.activity = activity;
    }
    @Override
    public DnsSpoofConfHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DnsSpoofConfHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dnsspoof, parent, false));
    }

    @Override
    public void                 onBindViewHolder(DnsSpoofConfHolder holder, int position) {
        DNSSpoofItem host = dnsIntercepts.get(position);
        holder.nameDNS.setText(host.domainAsked + " -> "+ host.domainSpoofed);
        holder.deleteImage.setOnClickListener(onDeleteDns(host));
    }

    private View.OnClickListener onDeleteDns(final DNSSpoofItem domainAsked) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleton.getDnsControler().removeDomain(domainAsked);
                activity.actualiseDomainspoofed();
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int                  getItemCount() {
        return dnsIntercepts.size();
    }
}
