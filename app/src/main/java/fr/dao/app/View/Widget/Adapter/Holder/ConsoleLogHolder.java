package fr.dao.app.View.Widget.Adapter.Holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import fr.dao.app.R;

/**
 * Host holder
 */
public class                ConsoleLogHolder extends RecyclerView.ViewHolder {
    public ConstraintLayout relative_layout;
    public TextView title, subtitle;

    public                  ConsoleLogHolder(View itemView) {
        super(itemView);
        relative_layout = itemView.findViewById(R.id.rootViewCard);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.title_valu);
    }
}
