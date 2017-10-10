package su.sniff.cepter.View.Adapter.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import su.sniff.cepter.R;

/**
 * Host holder
 */
public class DnsLogHolder extends RecyclerView.ViewHolder {
    public View             itemView;
    public TextView         lineConsole;

    public DnsLogHolder(View v) {
        super(v);
        itemView = v;
        lineConsole = (TextView) itemView.findViewById(R.id.line);
    }
}
