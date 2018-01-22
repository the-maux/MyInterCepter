package fr.allycs.app.View.Widget.Adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Model.Unix.ConsoleLog;
import fr.allycs.app.R;
import fr.allycs.app.View.Widget.Holder.ConsoleLogHolder;


public class                    ConsoleLogAdapter extends RecyclerView.Adapter<ConsoleLogHolder> {
    private String              TAG = "ConsoleLogAdapter";
    private List<ConsoleLog>    listConsole;
    private RecyclerView        mRV;

    public                      ConsoleLogAdapter(List<ConsoleLog> dnsInterceptList) {
        this.listConsole = dnsInterceptList;
    }

    public ConsoleLogHolder     onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ConsoleLogHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consolelog, parent, false));
    }

    public void                 onBindViewHolder(ConsoleLogHolder holder, int position) {
        ConsoleLog line = listConsole.get(position);
        holder.lineConsole.setText(line.line);
    }

    public int                  getItemCount() {
        return listConsole.size();
    }

    public void                 onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRV = recyclerView;
    }

    public RecyclerView         getRecyclerview() {
        return mRV;
    }
}
