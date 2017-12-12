package fr.allycs.app.View.Adapter.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.allycs.app.R;

public class                SessionHolder extends RecyclerView.ViewHolder {
    public TextView         nameSession;
    public ImageView        forward;

    public                  SessionHolder(View itemView) {
        super(itemView);
        nameSession = itemView.findViewById(R.id.nameSession);
        forward = itemView.findViewById(R.id.icon2);
    }
}
