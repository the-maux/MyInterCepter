package fr.allycs.app.View.Adapter.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.allycs.app.R;

public class                AccessPointHolder extends RecyclerView.ViewHolder {
    public ImageView        forward, wifi_logo;
    public TextView         ssid;

    public                  AccessPointHolder(View itemView) {
        super(itemView);
        ssid = (TextView) itemView.findViewById(R.id.ssid);
        forward = (ImageView) itemView.findViewById(R.id.icon2);
        wifi_logo = (ImageView) itemView.findViewById(R.id.icon);
    }
}
