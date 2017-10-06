package su.sniff.cepter.View.Adapter.Holder;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import su.sniff.cepter.R;

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
