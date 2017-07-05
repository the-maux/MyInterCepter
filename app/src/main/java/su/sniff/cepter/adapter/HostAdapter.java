package su.sniff.cepter.adapter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import de.hdodenhof.circleimageview.CircleImageView;
import su.sniff.cepter.BuildConfig;
import su.sniff.cepter.R;
import su.sniff.cepter.View.ScanActivity;

import java.util.List;

public class                    HostAdapter<T> extends ArrayAdapter<T> {
    private ScanActivity        Activity;
    private boolean[]           itemToggled;

    public                      HostAdapter(ScanActivity context, int resource, int textViewResourceId, List<T> objects, boolean[] itemToggled) {
        super(context, resource, textViewResourceId, objects);
        this.Activity = context;
        this.itemToggled = itemToggled;
    }

    public View                 getView(int position, View convertView, ViewGroup parent) {
        View itemView = super.getView(position, convertView, parent);

        ((ImageView) itemView.findViewById(R.id.icon2))
                .setImageResource(itemToggled[position] ? android.R.drawable.checkbox_on_background : android.R.drawable.checkbox_off_background);
        CircleImageView IconTypeDevice = (CircleImageView) itemView.findViewById(R.id.icon);
        IconTypeDevice.setImageResource(R.drawable.monitor);
        TextView tt = (TextView) itemView.findViewById(R.id.label);
        String a = tt.getText().toString();
        tt.setText(a.replaceAll("\\(-\\)", BuildConfig.FLAVOR));
        chooseTheIconForTheRightSystem(IconTypeDevice, a);
        return itemView;
    }

    private void                chooseTheIconForTheRightSystem(CircleImageView IconTypeDevice, String InfoDevice) {
        int ImageRessource;
        if (InfoDevice.contains("Windows")) {
            ImageRessource = R.drawable.winicon;
        } else if (InfoDevice.contains("Apple")) {
            ImageRessource = R.drawable.ios;
        } else if (InfoDevice.contains("Android") || InfoDevice.contains("Mobile") || InfoDevice.contains("Samsung")) {
            ImageRessource = R.drawable.android;
        } else if (InfoDevice.contains("Cisco")) {
            ImageRessource = R.drawable.cisco;
        } else if (InfoDevice.contains("Raspberry")) {
            ImageRessource = R.drawable.rasp;
        } else if (InfoDevice.contains("QUANTA")) {
            ImageRessource = R.drawable.quanta;
        } else if (InfoDevice.contains("Bluebird")) {
            ImageRessource = R.drawable.bluebird;
        } else if (InfoDevice.contains("Ios")) {
            ImageRessource = R.drawable.ios;
        } else if (!(!InfoDevice.contains("Unix") && !InfoDevice.contains("Linux") && !InfoDevice.contains("BSD"))) {
            ImageRessource = R.drawable.linuxicon;
        } else
            ImageRessource = R.drawable.monitor;

        Glide.with(Activity)
                .load(ImageRessource)
                .centerCrop()
                .into(IconTypeDevice);
    }
}
