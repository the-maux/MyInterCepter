package su.sniff.cepter.View.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Model.Wrap.ConsoleLog;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.Holder.ConsoleLogHolder;
import su.sniff.cepter.View.Adapter.Holder.DnsSpoofHolder;


public class                    ConsoleLogAdapter extends RecyclerView.Adapter<ConsoleLogHolder> {
    private String              TAG = this.getClass().getName();
    private Activity            activity;
    private List<ConsoleLog>    listConsole;
    private Singleton           singleton = Singleton.getInstance();

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
}
