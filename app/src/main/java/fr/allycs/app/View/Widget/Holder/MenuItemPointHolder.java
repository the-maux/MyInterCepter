package fr.allycs.app.View.Widget.Holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.allycs.app.R;

public class                MenuItemPointHolder extends RecyclerView.ViewHolder {
    public CardView         card_view;
    public ConstraintLayout Button;
    public ImageView        monitor, image;
    public TextView         name;

    public                  MenuItemPointHolder(View itemView) {
        super(itemView);
        card_view = itemView.findViewById(R.id.card_view);
        Button = itemView.findViewById(R.id.Button);
        monitor = itemView.findViewById(R.id.monitor);
        image = itemView.findViewById(R.id.image);
        name = itemView.findViewById(R.id.name);
    }
}
