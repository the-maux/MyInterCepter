package su.sniff.cepter.View.adapter.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import su.sniff.cepter.R;

/**
 * Host holder
 */
public class                HostCheckBoxHolder extends RecyclerView.ViewHolder {
    public View             itemView;
    public RelativeLayout   relativeLayout;
    public CheckBox         checkBox;
    public CircleImageView  imageOS;
    public TextView         nameOS;

    public                  HostCheckBoxHolder(View v) {
        super(v);
        itemView = v;
        relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rootView);
        checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        imageOS = (CircleImageView) itemView.findViewById(R.id.imageOS);
        nameOS = (TextView) itemView.findViewById(R.id.nameOS);
    }
}
