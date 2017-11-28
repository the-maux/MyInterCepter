package su.sniff.cepter.View.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import su.sniff.cepter.Controller.Core.Conf.Singleton;
import su.sniff.cepter.Model.Target.DNSSpoofItem;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.Holder.DnsSpoofConfHolder;
import su.sniff.cepter.View.DNSSpoofingActivity;


public class DnsSpoofConfAdapter extends RecyclerView.Adapter<DnsSpoofConfHolder> {
    private String              TAG = this.getClass().getName();
    private DNSSpoofingActivity activity;
    private List<DNSSpoofItem>  dnsIntercepts;
    private Singleton           singleton = Singleton.getInstance();

    public DnsSpoofConfAdapter(DNSSpoofingActivity activity, List<DNSSpoofItem> dnsInterceptList) {
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
