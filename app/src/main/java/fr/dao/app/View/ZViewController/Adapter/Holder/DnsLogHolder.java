package fr.dao.app.View.ZViewController.Adapter.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.dao.app.R;

public class                DnsLogHolder extends RecyclerView.ViewHolder {
    public View             itemView;
    public RelativeLayout   RV_layout;
    public TextView         nameHost;
    public ImageView        DNSTypeImg, viewFullLogsBtn;
    public RecyclerView     DnsRVLogs;

    public                  DnsLogHolder(View v) {
        super(v);
        itemView = v;
        RV_layout = itemView.findViewById(R.id.RV_layout);
        nameHost = itemView.findViewById(R.id.host);
        DNSTypeImg = itemView.findViewById(R.id.DNSTypeImg);
        viewFullLogsBtn = itemView.findViewById(R.id.viewFullLogsBtn);
        DnsRVLogs = itemView.findViewById(R.id.DnsRVLogs);
    }
}
