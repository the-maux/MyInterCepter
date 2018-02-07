package fr.allycs.app.View.Widget.Holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import fr.allycs.app.R;

public class                WiresharkHolder extends RecyclerView.ViewHolder {
    public View             rootView;
    public ConstraintLayout relativeLayout;
    public TextView         No, time, source, dest, proto, info;

    public                  WiresharkHolder(View v) {
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
