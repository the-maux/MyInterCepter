package fr.allycs.app.View.Adapter;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Unix.DNSLog;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.Holder.ConsoleLogHolder;
import fr.allycs.app.View.Adapter.Holder.DnsLogHolder;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;


public class                    DnsLogsAdapter extends RecyclerView.Adapter<DnsLogHolder> {
    private String              TAG = this.getClass().getName();
    private Activity            activity;
    private List<DNSLog>        mListConsole;
    private Singleton           mSingleton = Singleton.getInstance();
    private RecyclerView        mRV;

    public DnsLogsAdapter(Activity activity, List<DNSLog> dnsInterceptList) {
        this.mListConsole = dnsInterceptList;
        this.activity = activity;
    }

    @Override
    public DnsLogHolder         onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DnsLogHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dnsmasqlog, parent, false));
    }

    @Override
    public void                 onBindViewHolder(final DnsLogHolder holder, int position) {
        DNSLog dnslog = mListConsole.get(position);
        holder.nameHost.setText(dnslog.host);
        setCurrentTypeLogo(dnslog.currentType, holder.DNSTypeImg);
        holder.DnsRVLogs.setAdapter(setDetailLogsAdapter(dnslog));
        holder.DnsRVLogs.setHasFixedSize(true);
        holder.DnsRVLogs.setLayoutManager(new LinearLayoutManager(activity));
        holder.viewFullLogsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.RV_layout.getVisibility() == View.GONE)
                    holder.RV_layout.setVisibility(View.VISIBLE);
                else
                    holder.RV_layout.setVisibility(View.GONE);
            }
        });
    }

    private void                setCurrentTypeLogo(DNSLog.Type currentType, ImageView DNSTypeImg) {
        int TypeLogo;
        switch (currentType) {
            case Reply:
                TypeLogo = R.mipmap.ic_dns_replay;
                break;
            case Forward:
                TypeLogo = R.mipmap.ic_dns_forward;
                break;
            case Other:
                TypeLogo = R.mipmap.ic_dns_text;
                break;
            case Query:
                TypeLogo = R.mipmap.ic_dns_query;
                break;
            default:
                TypeLogo = R.mipmap.ic_dns_text;
        }
        Glide.with(activity)
                .load(TypeLogo)
                .apply(new RequestOptions()
                        .fitCenter()
                        .override(100, 100))
                .transition(withCrossFade())
                .into(DNSTypeImg);
    }

    @Override
    public int                  getItemCount() {
        return mListConsole.size();
    }

    @Override
    public void                 onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRV = recyclerView;
    }

    private RecyclerView.Adapter<ConsoleLogHolder> setDetailLogsAdapter(final DNSLog logs) {
        RecyclerView.Adapter<ConsoleLogHolder> adapter =  new RecyclerView.Adapter<ConsoleLogHolder>() {
            @Override
            public ConsoleLogHolder     onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ConsoleLogHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consolelog, parent, false));
            }

            @Override
            public void                 onBindViewHolder(ConsoleLogHolder holder, int position) {
                DNSLog log = logs.logs.get(position);
                holder.lineConsole.setText(log.data);
                //holder.lineConsole.setTextColor(log.color);
            }

            @Override
            public int                  getItemCount() {
                return logs.logs.size();
            }
        };
        logs.setAdapter(adapter);
        return adapter;
    }

    public RecyclerView         getRecyclerview() {
        return mRV;
    }

    public void                 filtering(String query) {
        /*TODO:Log.d(TAG, "filterByString:" + query);
        mHosts.clear();
        for (Host host : mOriginalList) {
            if (host.getDumpInfo().toLowerCase().contains(query.toLowerCase()))
                mHosts.add(host);
        }
        notifyDataSetChanged();*/
    }
}
