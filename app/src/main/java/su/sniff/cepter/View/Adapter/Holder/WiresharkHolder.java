package su.sniff.cepter.View.Adapter.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import su.sniff.cepter.R;

/**
 * Host holder
 */
public class                WiresharkHolder extends RecyclerView.ViewHolder {
    public View             rootView;
    public LinearLayout     relativeLayout;
    public TextView         No, time, source, dest, proto, info;

    public                  WiresharkHolder(View v) {
        super(v);
        rootView = v;
        relativeLayout = (LinearLayout) rootView.findViewById(R.id.lllayout);
        No = (TextView) rootView.findViewById(R.id.No);
        time = (TextView) rootView.findViewById(R.id.time);
        source = (TextView) rootView.findViewById(R.id.Source);
        dest = (TextView) rootView.findViewById(R.id.Destination);
        proto = (TextView) rootView.findViewById(R.id.Protocol);
        info = (TextView) rootView.findViewById(R.id.Info);
    }
}
