package fr.allycs.app.View.Widget.Holder;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.allycs.app.R;

/**
 * Host holder
 */
public class                DoraHolder extends RecyclerView.ViewHolder {
    public View             itemView;
    public RelativeLayout   relativeLayout, menuView;
    public TextView         diagnose, diagnosPourcentage, uptime, stat, IP, ipHostname;
    public FloatingActionButton fab;

    public                  DoraHolder(View v) {
        super(v);
        itemView = v;
        relativeLayout = itemView.findViewById(R.id.rootView);
        diagnose = itemView.findViewById(R.id.diagnose);
        diagnosPourcentage = itemView.findViewById(R.id.diagnosPourcentage);
        uptime =  itemView.findViewById(R.id.uptime);
        ipHostname = itemView.findViewById(R.id.ipHostname);
        stat = itemView.findViewById(R.id.stat);
        IP = itemView.findViewById(R.id.IP);
        menuView = itemView.findViewById(R.id.menuView);
        fab = itemView.findViewById(R.id.fab);
    }
}
