package fr.allycs.app.View.Widget.Holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.allycs.app.R;

public class SniffSessionHolder extends RecyclerView.ViewHolder {
    public CardView         card_view;
    public RelativeLayout   relative_layout;
    public ImageView        shareButton;
    public TextView         title, susbtitle, description;
    public TextView         ACTION1, ACTION2;

    public SniffSessionHolder(View itemView) {
        super(itemView);
        card_view = itemView.findViewById(R.id.card_view);
        relative_layout = itemView.findViewById(R.id.relative_layout);
        title = itemView.findViewById(R.id.title);
        susbtitle = itemView.findViewById(R.id.susbtitle);
        description = itemView.findViewById(R.id.description);
        shareButton = itemView.findViewById(R.id.shareButton);
        ACTION1 = itemView.findViewById(R.id.ACTION1);
        ACTION2 = itemView.findViewById(R.id.ACTION2);
    }
}
