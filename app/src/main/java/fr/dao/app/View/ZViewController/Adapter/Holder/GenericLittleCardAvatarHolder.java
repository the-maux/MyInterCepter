package fr.dao.app.View.ZViewController.Adapter.Holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.dao.app.R;

public class                GenericLittleCardAvatarHolder extends RecyclerView.ViewHolder {
    public View             itemView;
    public CardView         card_view;
    public ConstraintLayout constraintLayout;
    public CircleImageView  logo;
    public ImageView        icon;
    public TextView         title, subtitle;

    public                  GenericLittleCardAvatarHolder(View v) {
        super(v);
        itemView = v;
        card_view = itemView.findViewById(R.id.card_view);
        constraintLayout = itemView.findViewById(R.id.rootView);
        logo = itemView.findViewById(R.id.icon);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle_card);
        icon = itemView.findViewById(R.id.icon2);
    }
}
