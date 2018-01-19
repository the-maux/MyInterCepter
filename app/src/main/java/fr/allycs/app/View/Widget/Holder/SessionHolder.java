package fr.allycs.app.View.Widget.Holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.allycs.app.R;

public class                SessionHolder extends RecyclerView.ViewHolder {
    public CardView         card_view;
    public RelativeLayout   relative_layout;
    public TextView         title, subtitle;
    public ImageView        forward;

    public                  SessionHolder(View itemView) {
        super(itemView);
        card_view = itemView.findViewById(R.id.card_view);
        relative_layout = itemView.findViewById(R.id.relative_layout);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        forward = itemView.findViewById(R.id.icon2);
    }
}
