package su.sniff.cepter.View.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Model.Pcap.DNSSpoofItem;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.Holder.DnsSpoofHolder;


public class                    DnsSpoofAdapter extends RecyclerView.Adapter<DnsSpoofHolder> {
    private String              TAG = this.getClass().getName();
    private Activity            activity;
    private List<DNSSpoofItem>  dnsIntercepts;
    private Singleton           singleton = Singleton.getInstance();

    public                      DnsSpoofAdapter(Activity activity, List<DNSSpoofItem> dnsInterceptList) {
        this.dnsIntercepts = dnsInterceptList;
        this.activity = activity;
    }
    @Override
    public DnsSpoofHolder       onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DnsSpoofHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dnsspoof, parent, false));
    }

    @Override
    public void                 onBindViewHolder(DnsSpoofHolder holder, int position) {
        DNSSpoofItem host = dnsIntercepts.get(position);
        holder.nameDNS.setText(host.domainAsked + " -> "+ host.domainSpoofed);
        holder.deleteImage.setOnClickListener(onDeleteDns(host));
    }

    private View.OnClickListener onDeleteDns(final DNSSpoofItem domainAsked) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singleton.dnsSpoofed.removeDomain(domainAsked);

                notifyDataSetChanged();
            }
        };
    }

    @Override
    public int                  getItemCount() {
        return dnsIntercepts.size();
    }
}
