package su.sniff.cepter.View.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Model.Pcap.DnsIntercept;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.Holder.DnsSpoofHolder;

/**
 * Created by root on 04/08/17.
 */

public class                    DnsSpoofAdapter extends RecyclerView.Adapter<DnsSpoofHolder> {
    private String              TAG = this.getClass().getName();
    private Activity            activity;
    private List<DnsIntercept>  dnsIntercepts;

    public                      DnsSpoofAdapter(Activity activity, List<DnsIntercept> dnsInterceptList) {
        this.dnsIntercepts = dnsInterceptList;
        this.activity = activity;
    }
    @Override
    public DnsSpoofHolder       onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DnsSpoofHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dns_checkbox, parent, false));
    }

    @Override
    public void                 onBindViewHolder(DnsSpoofHolder holder, int position) {
        DnsIntercept host = dnsIntercepts.get(position);
        holder.nameDNS.setText(host.domainAsked + " -> "+ host.domainSpoofed);
        holder.deleteImage.setOnClickListener(onDeleteDns(host.domainAsked));
    }

    private View.OnClickListener onDeleteDns(final String domainAsked) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (DnsIntercept dnsIntercept : Singleton.getInstance().dnsSpoofed) {
                    if (domainAsked.contains(dnsIntercept.domainAsked)) {
                        Singleton.getInstance().dnsSpoofed.remove(dnsIntercept);
                        break;
                    }
                }
            }
        };
    }

    @Override
    public int                  getItemCount() {
        return dnsIntercepts.size();
    }
}
