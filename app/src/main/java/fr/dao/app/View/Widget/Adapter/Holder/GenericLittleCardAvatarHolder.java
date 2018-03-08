package fr.dao.app.View.Widget.Adapter.Holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import fr.dao.app.R;

public class                GenericLittleCardAvatarHolder extends RecyclerView.ViewHolder {
    public View             itemView;
    public ConstraintLayout relativeLayout;
    public ImageView        logo, icon;
    public TextView         title, subtitle;

    public                  GenericLittleCardAvatarHolder(View v) {
        super(v);
        itemView = v;
        relativeLayout = itemView.findViewById(R.id.rootView);
        logo = itemView.findViewById(R.id.icon);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle_card);
        icon = itemView.findViewById(R.id.icon2);
    }
}
