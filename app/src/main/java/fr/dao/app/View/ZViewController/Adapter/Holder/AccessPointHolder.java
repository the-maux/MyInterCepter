package fr.dao.app.View.ZViewController.Adapter.Holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.dao.app.R;

public class                AccessPointHolder extends RecyclerView.ViewHolder {
    public CardView         card_view;
    public RelativeLayout   relative_layout;
    public ImageView        forward, wifi_logo;
    public TextView         ssid, ssid_subtitle;

    public                  AccessPointHolder(View itemView) {
        super(itemView);
        card_view = itemView.findViewById(R.id.card_view);
        relative_layout = itemView.findViewById(R.id.relative_layout);
        ssid = itemView.findViewById(R.id.title);
        ssid_subtitle = itemView.findViewById(R.id.ssid_subtitle);
        forward = itemView.findViewById(R.id.icon2);
        wifi_logo = itemView.findViewById(R.id.icon);
    }
}
