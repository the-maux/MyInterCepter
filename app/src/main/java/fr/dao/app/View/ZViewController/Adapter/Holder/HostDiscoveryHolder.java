package fr.dao.app.View.ZViewController.Adapter.Holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.dao.app.R;

public class                HostDiscoveryHolder extends RecyclerView.ViewHolder {
    private final SparseArray<View> views = new SparseArray<>();
    public View             itemView;
    public CardView         cardView;
    public RelativeLayout   relativeLayout;
    public TextView         mac, os, vendor, ipAndHostname;
    public CheckBox         selected;
    public CircleImageView  osIcon, statusIcon;
    public boolean          animate = false;
//    public ImageView        ;

    public                  HostDiscoveryHolder(View v) {
        super(v);
        itemView = v;
        cardView = itemView.findViewById(R.id.card_view);
        relativeLayout = itemView.findViewById(R.id.relative_layout);
        ipAndHostname =  itemView.findViewById(R.id.ipHostname);
        mac = itemView.findViewById(R.id.mac);
        os = itemView.findViewById(R.id.os);
        vendor =  itemView.findViewById(R.id.vendor);
        selected =  itemView.findViewById(R.id.checkbox_selected);
        osIcon =  itemView.findViewById(R.id.icon);
        statusIcon =  itemView.findViewById(R.id.statusIcon);
    }


    @SuppressWarnings("unchecked")
    public <V extends View> V getView(int resId) {
        View v = views.get(resId);
        if (null == v) {
            v = itemView.findViewById(resId);
            views.put(resId, v);
        }
        return (V) v;
    }
}
