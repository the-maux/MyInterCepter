package fr.allycs.app.View.Adapter.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.allycs.app.R;

public class                SessionHolder extends RecyclerView.ViewHolder {
    public TextView         title, subtitle;
    public ImageView        forward;

    public                  SessionHolder(View itemView) {
        super(itemView);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        forward = itemView.findViewById(R.id.icon2);
    }
}
