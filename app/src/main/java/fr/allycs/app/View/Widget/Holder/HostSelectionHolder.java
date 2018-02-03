package fr.allycs.app.View.Widget.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.allycs.app.R;

public class                HostSelectionHolder extends RecyclerView.ViewHolder {
    public View             itemView;
    public RelativeLayout   relativeLayout;
    public CheckBox         checkBox;
    public ImageView        imageOS;
    public TextView         nameOS;

    public HostSelectionHolder(View v) {
        super(v);
        itemView = v;
        relativeLayout = itemView.findViewById(R.id.rootView);
        checkBox = itemView.findViewById(R.id.deleteImage);
        imageOS = itemView.findViewById(R.id.imageDNS);
        nameOS = itemView.findViewById(R.id.nameOS);
    }
}
