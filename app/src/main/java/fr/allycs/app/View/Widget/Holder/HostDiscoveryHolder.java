package fr.allycs.app.View.Widget.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.allycs.app.R;

/**
 * Host holder
 */
public class HostDiscoveryHolder extends RecyclerView.ViewHolder {
    public View             itemView;
    public RelativeLayout   relativeLayout;
    public TextView         ipHostname;
    public TextView         mac;
    public TextView         os;
    public TextView         vendor;
    public CheckBox         selected;
    public CircleImageView  osIcon;

    public HostDiscoveryHolder(View v) {
        super(v);
        itemView = v;
        relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative_layout);
        ipHostname = (TextView) itemView.findViewById(R.id.ipHostname);
        mac = (TextView) itemView.findViewById(R.id.mac);
        os = (TextView) itemView.findViewById(R.id.os);
        vendor = (TextView) itemView.findViewById(R.id.vendor);
        selected = (CheckBox) itemView.findViewById(R.id.checkbox_selected);
        osIcon = (CircleImageView) itemView.findViewById(R.id.icon);
    }
}
