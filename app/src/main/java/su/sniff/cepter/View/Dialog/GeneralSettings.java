package su.sniff.cepter.View.Dialog;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;

import su.sniff.cepter.Controller.Core.Conf.Singleton;
import su.sniff.cepter.Controller.Core.BinaryWrapper.Tcpdump;
import su.sniff.cepter.R;
import su.sniff.cepter.View.MenuActivity;

/**
 * Created by the-maux on 19/09/17.
 */

public class                    GeneralSettings {
    private String              TAG = "GeneralSettings";
    private Activity            mActivity;
    private Tcpdump             tcpdump;
    private Singleton           singleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    private BottomSheetMenuDialog bottomSheet;

    public                      GeneralSettings(Activity activity, CoordinatorLayout coordinatorLayout, Tcpdump tcpdump) {
        this.tcpdump = tcpdump;
        mActivity = activity;
        mCoordinatorLayout = coordinatorLayout;
        init();
    }


    private void                init() {
        BottomSheetBuilder builder = new BottomSheetBuilder(mActivity)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setBackgroundColor(ContextCompat.getColor(mActivity, R.color.material_light_white));
        builder.addTitleItem("Behavior");
        builder.addItem(0, "Write in Pcap file", (tcpdump.isDumpingInFile) ? R.drawable.ic_checkbox_marked_grey600_24dp: R.drawable.ic_checkbox_blank_outline_grey600_24dp );
        builder.addItem(1, "SslStrip Mode", (singleton.isSslStripModeActived()) ? R.drawable.ic_checkbox_marked_grey600_24dp: R.drawable.ic_checkbox_blank_outline_grey600_24dp );
        builder.addItem(2, "Lockscreen", (singleton.isLockScreen()) ? R.drawable.ic_checkbox_marked_grey600_24dp: R.drawable.ic_checkbox_blank_outline_grey600_24dp );
        builder.addItem(3, "Deep trame analyse", (tcpdump.isDeepAnalyseTrame()) ? R.drawable.ic_checkbox_marked_grey600_24dp: R.drawable.ic_checkbox_blank_outline_grey600_24dp );
        builder.addDividerItem();
        builder.addItem(4, "Port redirect", R.drawable.ic_checkbox_blank_outline_grey600_24dp);
        builder.addItem(5, "Port filtering", R.drawable.ic_checkbox_blank_outline_grey600_24dp);
        builder.addItem(6, "Dns Spoofing", (singleton.isDnsSpoofActived()) ? R.drawable.ic_checkbox_marked_grey600_24dp: R.drawable.ic_checkbox_blank_outline_grey600_24dp )
                .setItemClickListener(onClick())
                .expandOnStart(true);
        bottomSheet = builder.createDialog();
    }

    private BottomSheetItemClickListener onClick() {
        return new BottomSheetItemClickListener() {
            @Override
            public void onBottomSheetItemClick(MenuItem menuItem) {
                Log.d(TAG, "STRING:"+menuItem.getTitle().toString());
                switch (menuItem.getTitle().toString()) {
                    case "Write in Pcap file":
                        tcpdump.isDumpingInFile = !tcpdump.isDumpingInFile;
                        break;
                    case "SslStrip Mode":
                        singleton.setSslStripModeActived(!singleton.isSslStripModeActived());
                        break;
                    case "Lockscreen":
                        singleton.setLockScreen(!singleton.isLockScreen());
                        break;
                    case "Deep trame analyse":
                        tcpdump.setDeepAnalyseTrame(!tcpdump.isDeepAnalyseTrame());
                        break;
                    case "Port redirect":
                        onPortMitm(true);
                        break;
                    case "Port filtering":
                        onPortMitm(false);
                        break;
                    case "Dns Spoofing":
                        singleton.setDnsSpoofActived(!singleton.isDnsSpoofActived());
                        break;
                    default:
                        mActivity.startActivity(new Intent(mActivity, MenuActivity.class));
                        break;
                }
            }
        };
    }

    public void                 show() {
        bottomSheet.show();
    }

    private View.OnClickListener onPortMitm(final boolean flag) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flag) { /** TODO: PortRedirect */

                } else {    /** TODO: PortFiltering */

                }
                Snackbar.make(mCoordinatorLayout, "Non implémenté", Snackbar.LENGTH_SHORT).show();
            }
        };
    }

}
