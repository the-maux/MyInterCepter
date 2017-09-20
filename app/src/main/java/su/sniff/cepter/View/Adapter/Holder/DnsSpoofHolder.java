package su.sniff.cepter.View.Adapter.Holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import su.sniff.cepter.R;

/**
 * Host holder
 */
public class                DnsSpoofHolder extends RecyclerView.ViewHolder {
    public View             itemView;
    public RelativeLayout   relativeLayout;
    public ImageView        imageDNS, deleteImage;
    public TextView         nameDNS;

    public                  DnsSpoofHolder(View v) {
        super(v);
        itemView = v;
        relativeLayout = (RelativeLayout) itemView.findViewById(R.id.rootView);
        nameDNS = (TextView) itemView.findViewById(R.id.nameDNS);
        deleteImage = (ImageView) itemView.findViewById(R.id.deleteImage);
        imageDNS = (ImageView) itemView.findViewById(R.id.imageDNS);
    }
}
