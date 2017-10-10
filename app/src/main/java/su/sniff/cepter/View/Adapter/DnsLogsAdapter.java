package su.sniff.cepter.View.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Model.Unix.DNSLog;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.Holder.DnsLogHolder;


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
    public void                 onBindViewHolder(DnsLogHolder holder, int position) {
        DNSLog dnslog = mListConsole.get(position);
        holder.lineConsole.setText(dnslog.data);
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

    public RecyclerView          getRecyclerview() {
        return mRV;
    }
}
