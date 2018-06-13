package fr.dao.app.View.ZViewController.Adapter.Holder;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.TextView;

import fr.dao.app.R;

public class                SettingHolder extends RecyclerView.ViewHolder {
    public ConstraintLayout constraintLayout;
    public TextView         title, subtitle;
    public SwitchCompat     switch_sw;

    public SettingHolder(View itemView) {
        super(itemView);
        constraintLayout = itemView.findViewById(R.id.rootView);
        title = itemView.findViewById(R.id.title);
        subtitle = itemView.findViewById(R.id.subtitle);
        switch_sw = itemView.findViewById(R.id.switch_sw);
    }
}
