package fr.dao.app.View.ZViewController.Adapter.Holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.dao.app.R;

public class                PacketHolder extends RecyclerView.ViewHolder {
    public CardView         card_view;
    public ConstraintLayout contraint_layout;
    public TextView         nbr_packets_protocol;
    public ImageView        logo_protocol;

    public PacketHolder(View itemView) {
        super(itemView);
        card_view = itemView.findViewById(R.id.rootView);
        contraint_layout = itemView.findViewById(R.id.contraint);
        nbr_packets_protocol = itemView.findViewById(R.id.nbr_packets_protocol);
        logo_protocol = itemView.findViewById(R.id.logo_protocol);
    }
}
