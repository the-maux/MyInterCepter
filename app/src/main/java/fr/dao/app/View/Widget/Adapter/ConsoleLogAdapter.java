package fr.dao.app.View.Widget.Adapter;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import fr.dao.app.R;


public class                    ConsoleLogAdapter extends RecyclerView.Adapter<ConsoleLogAdapter.ConsoleLogH> {
    private String              TAG = "ConsoleLogAdapter";
    private ArrayList<String[]> listConsole = new ArrayList<String[]>();
    private RecyclerView        mRV;

    public                      ConsoleLogAdapter() {
//        this.listConsole = dnsInterceptList;
    }

    public ConsoleLogH     onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ConsoleLogH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consolelog, parent, false));
    }

    public void                 onBindViewHolder(ConsoleLogH holder, int position) {
        String[] line = listConsole.get(position);
        holder.title.setText(line[0]);
        holder.subtitle.setText(line[1]);
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

    public void                 updateList(ArrayList<String[]> arrayList) {
        listConsole.clear();
        listConsole.addAll(arrayList);
        notifyItemRangeInserted(0, arrayList.size());
    }

    class                       ConsoleLogH extends RecyclerView.ViewHolder {
        public ConstraintLayout relative_layout;
        public TextView title, subtitle;

        ConsoleLogH(View itemView) {
            super(itemView);
            relative_layout = itemView.findViewById(R.id.rootViewCard);
            title = itemView.findViewById(R.id.title);
            subtitle = itemView.findViewById(R.id.title_valu);
        }
    }
}
