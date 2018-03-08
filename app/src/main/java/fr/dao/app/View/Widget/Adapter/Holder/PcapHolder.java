package fr.dao.app.View.Widget.Adapter.Holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.dao.app.R;

public class                PcapHolder extends RecyclerView.ViewHolder {
    public CardView         card_view;
    public RelativeLayout   relative_layout;
    public ImageView        forward, icon;
    public TextView         title, subtitle;

    public                  PcapHolder(View itemView) {
        super(itemView);
        card_view = itemView.findViewById(R.id.card_view);
        relative_layout = itemView.findViewById(R.id.relative_layout);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.ssid_subtitle);
        forward = itemView.findViewById(R.id.icon2);
        icon = itemView.findViewById(R.id.icon);
    }
}
