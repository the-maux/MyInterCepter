package fr.allycs.app.View.Adapter.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import fr.allycs.app.R;

/**
 * Host holder
 */
public class DnsSpoofConfHolder extends RecyclerView.ViewHolder {
    public View             itemView;
    public RelativeLayout   relativeLayout;
    public ImageView        imageDNS, deleteImage;
    public TextView         nameDNS;

    public DnsSpoofConfHolder(View v) {
        super(v);
        itemView = v;
        relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rootView);
        nameDNS = (TextView) itemView.findViewById(R.id.nameDNS);
        deleteImage = (ImageView) itemView.findViewById(R.id.deleteImage);
        imageDNS = (ImageView) itemView.findViewById(R.id.imageDNS);
    }
}
