package su.sniff.cepter.View.Adapter.Holder;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import su.sniff.cepter.R;

/**
 * Host holder
 */
public class                DoraHolder extends RecyclerView.ViewHolder {
    public View             itemView;
    public RelativeLayout   relativeLayout, menuView;
    public AppCompatTextView diagnose, diagnosPourcentage, uptime, stat, IP;
    public FloatingActionButton fab, stopFab;

    public                  DoraHolder(View v) {
        super(v);
        itemView = v;
        relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rootView);
        diagnose = (AppCompatTextView) itemView.findViewById(R.id.diagnose);
        diagnosPourcentage = (AppCompatTextView) itemView.findViewById(R.id.diagnosPourcentage);
        uptime = (AppCompatTextView) itemView.findViewById(R.id.uptime);
        stat = (AppCompatTextView) itemView.findViewById(R.id.stat);
        IP = (AppCompatTextView) itemView.findViewById(R.id.IP);
        menuView = (RelativeLayout) itemView.findViewById(R.id.menuView);
        fab = (FloatingActionButton) itemView.findViewById(R.id.fab);
        stopFab = (FloatingActionButton) itemView.findViewById(R.id.stopFab);
    }
}
