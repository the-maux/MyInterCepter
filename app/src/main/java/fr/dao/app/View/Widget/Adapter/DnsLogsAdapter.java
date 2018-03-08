package fr.dao.app.View.Widget.Adapter;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import fr.dao.app.Model.Unix.DNSLog;
import fr.dao.app.R;
import fr.dao.app.View.Behavior.MyGlideLoader;
import fr.dao.app.View.Widget.Adapter.Holder.ConsoleLogHolder;
import fr.dao.app.View.Widget.Adapter.Holder.DnsLogHolder;


public class                    DnsLogsAdapter extends RecyclerView.Adapter<DnsLogHolder> {
    private String              TAG = "DnsLogsAdapter";
    private Activity            activity;
    private List<DNSLog>        mListConsole;
    private RecyclerView        mRV;

    public DnsLogsAdapter(Activity activity, List<DNSLog> dnsInterceptList) {
        this.mListConsole = dnsInterceptList;
        this.activity = activity;
    }

    public DnsLogHolder         onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DnsLogHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dnsmasqlog, parent, false));
    }

    public void                 onBindViewHolder(final DnsLogHolder holder, int position) {
        final DNSLog dnslog = mListConsole.get(position);
        holder.nameHost.setText(dnslog.domain);
        setCurrentTypeLogo(dnslog.currentType, holder.DNSTypeImg);
        holder.viewFullLogsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.RV_layout.getVisibility() == View.GONE) {
                    holder.DnsRVLogs.setAdapter(setDetailLogsAdapter(dnslog, holder.DnsRVLogs));
                    holder.DnsRVLogs.setHasFixedSize(true);
                    holder.DnsRVLogs.setLayoutManager(new LinearLayoutManager(activity));
                    holder.RV_layout.setVisibility(View.VISIBLE);
                } else
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
        MyGlideLoader.loadDrawableInImageView(activity, TypeLogo, DNSTypeImg, true);
    }

    public RecyclerView         getRecyclerview() {
        return mRV;
    }

    public void                 onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRV = recyclerView;
    }

    public int                  getItemCount() {
        return mListConsole.size();
    }

    public void                 filtering(String query) {
        /*TODO:Log.d(TAG, "filterByString:" + query);
        mHosts.clear();
        for (Host title : mOriginalList) {
            if (title.getDumpInfo().toLowerCase().contains(query.toLowerCase()))
                mHosts.add(title);
        }
        notifyDataSetChanged();*/
    }

    private RecyclerView.Adapter<ConsoleLogHolder>  setDetailLogsAdapter(final DNSLog logs, RecyclerView dnsRVLogs) {
        RecyclerView.Adapter<ConsoleLogHolder> adapter =  new RecyclerView.Adapter<ConsoleLogHolder>() {
            public ConsoleLogHolder                 onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ConsoleLogHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consolelog, parent, false));
            }

            public void                             onBindViewHolder(ConsoleLogHolder holder, int position) {
                DNSLog log = logs.logs.get(position);
                holder.subtitle.setText(log.data);
                holder.title.setVisibility(View.GONE);
            }
            public int                              getItemCount() {
                return logs.logs.size();
            }
        };
        logs.setAdapter(adapter, dnsRVLogs);
        return adapter;
    }


}
