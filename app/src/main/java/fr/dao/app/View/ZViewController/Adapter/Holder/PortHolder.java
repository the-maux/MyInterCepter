package fr.dao.app.View.ZViewController.Adapter.Holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import fr.dao.app.R;

public class                PortHolder extends RecyclerView.ViewHolder {
    public ConstraintLayout constraintLayout;
    public TextView         port, state, service;

    public                  PortHolder(View itemView) {
        super(itemView);
        constraintLayout = itemView.findViewById(R.id.rootViewItemPort);
        port = itemView.findViewById(R.id.PORT);
        state = itemView.findViewById(R.id.State);
        service = itemView.findViewById(R.id.Source);
    }
}
