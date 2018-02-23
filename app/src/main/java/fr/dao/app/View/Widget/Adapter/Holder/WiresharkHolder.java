package fr.dao.app.View.Widget.Adapter.Holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import fr.dao.app.R;

public class                WiresharkHolder extends RecyclerView.ViewHolder {
    public View             rootView;
    public ConstraintLayout relativeLayout;
    public TextView         No, time, source, dest, proto, info;

    public                  WiresharkHolder(View v, boolean isTopBar) {
        super(v);
        rootView = v;
        relativeLayout =  rootView.findViewById(R.id.lllayout);
        No = rootView.findViewById(R.id.No);
        time = rootView.findViewById(R.id.time);
        source = rootView.findViewById(R.id.Source);
        dest = rootView.findViewById(R.id.Destination);
        proto = rootView.findViewById(R.id.Protocol);
        info = rootView.findViewById(R.id.Info);
    }
}
