package fr.dao.app.View.ZViewController.Adapter.Holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.dao.app.R;

public class                MenuItemHolder extends RecyclerView.ViewHolder {
    public CardView         card_view;
    public CircleImageView  statusIconCardView;
    public ImageView        logo_card;
    public ProgressBar      progressBar_monitor;
    public TextView         titleCard;

    public                  MenuItemHolder(View itemView) {
        super(itemView);
        card_view = itemView.findViewById(R.id.card_view);
        logo_card = itemView.findViewById(R.id.logoCard);
        statusIconCardView = itemView.findViewById(R.id.statusIconCardView);
        progressBar_monitor = itemView.findViewById(R.id.progressBar_monitor);
        titleCard = itemView.findViewById(R.id.titleCard);
    }
}
