package fr.dao.app.View.ZViewController.Adapter.Holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.dao.app.R;

public class                SessionHolder extends RecyclerView.ViewHolder {
    public CardView         card_view;
    public ConstraintLayout relative_layout;
    public TextView         title, subtitle;
    public ImageView        forward, icon;// wiresharkMiniLogo, doraMiniLogo,

    public                  SessionHolder(View itemView) {
        super(itemView);
        card_view = itemView.findViewById(R.id.card_view);
        relative_layout = itemView.findViewById(R.id.rootView);
        title = itemView.findViewById(R.id.title);
        icon = itemView.findViewById(R.id.icon);
        subtitle = itemView.findViewById(R.id.subtitle);
        forward = itemView.findViewById(R.id.icon2);
     //   wiresharkMiniLogo = itemView.findViewById(R.id.wiresharkMiniLogo);
    //    doraMiniLogo = itemView.findViewById(R.id.doraMiniLogo);
    }
}
