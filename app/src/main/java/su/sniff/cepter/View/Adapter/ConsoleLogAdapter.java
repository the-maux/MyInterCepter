package su.sniff.cepter.View.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import su.sniff.cepter.Controller.Core.Singleton;
import su.sniff.cepter.Model.Unix.ConsoleLog;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.Holder.ConsoleLogHolder;


public class                    ConsoleLogAdapter extends RecyclerView.Adapter<ConsoleLogHolder> {
    private String              TAG = this.getClass().getName();
    private Activity            activity;
    private List<ConsoleLog>    listConsole;
    private Singleton           singleton = Singleton.getInstance();
    private RecyclerView        mRV;

    public                      ConsoleLogAdapter(Activity activity, List<ConsoleLog> dnsInterceptList) {
        this.listConsole = dnsInterceptList;
        this.activity = activity;
    }
    @Override
    public ConsoleLogHolder       onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ConsoleLogHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consolelog, parent, false));
    }

    @Override
    public void                 onBindViewHolder(ConsoleLogHolder holder, int position) {
        ConsoleLog line = listConsole.get(position);
        holder.lineConsole.setText(line.line);
    }

    @Override
    public int                  getItemCount() {
        return listConsole.size();
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
