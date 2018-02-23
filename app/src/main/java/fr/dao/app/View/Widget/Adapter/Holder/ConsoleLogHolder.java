package fr.dao.app.View.Widget.Adapter.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import fr.dao.app.R;

/**
 * Host holder
 */
public class                ConsoleLogHolder extends RecyclerView.ViewHolder {
    public View             itemView;
    public TextView         lineConsole;

    public                  ConsoleLogHolder(View v) {
        super(v);
        itemView = v;
        lineConsole = (TextView) itemView.findViewById(R.id.line);
    }
}
