package fr.dao.app.View.Widget.Adapter.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.dao.app.R;

/**
 * Host holder
 */
public class                DnsLogHolder extends RecyclerView.ViewHolder {
    public View             itemView;
    public RelativeLayout   RV_layout;
    public TextView         nameHost;
    public ImageView        DNSTypeImg, viewFullLogsBtn;
    public RecyclerView     DnsRVLogs;

    public                  DnsLogHolder(View v) {
        super(v);
        itemView = v;
        RV_layout = (RelativeLayout) itemView.findViewById(R.id.RV_layout);
        nameHost = (TextView) itemView.findViewById(R.id.host);
        DNSTypeImg = (ImageView) itemView.findViewById(R.id.DNSTypeImg);
        viewFullLogsBtn = (ImageView) itemView.findViewById(R.id.viewFullLogsBtn);
        DnsRVLogs = (RecyclerView) itemView.findViewById(R.id.DnsRVLogs);
    }
}
