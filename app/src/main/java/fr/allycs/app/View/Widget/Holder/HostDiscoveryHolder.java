package fr.allycs.app.View.Widget.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.allycs.app.R;

public class                HostDiscoveryHolder extends RecyclerView.ViewHolder {
    public View             itemView;
    public RelativeLayout   relativeLayout;
    public TextView         ipHostname;
    public TextView         mac;
    public TextView         os;
    public TextView         vendor;
    public CheckBox         selected;
    public ImageView        osIcon;

    public HostDiscoveryHolder(View v) {
        super(v);
        itemView = v;
        relativeLayout = itemView.findViewById(R.id.relative_layout);
        ipHostname =  itemView.findViewById(R.id.ipHostname);
        mac = itemView.findViewById(R.id.mac);
        os = itemView.findViewById(R.id.os);
        vendor =  itemView.findViewById(R.id.vendor);
        selected =  itemView.findViewById(R.id.checkbox_selected);
        osIcon =  itemView.findViewById(R.id.icon);
    }
}
