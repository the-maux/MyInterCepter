package fr.allycs.app.View.Widget.Dialog;

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

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Tools.Tcpdump.Tcpdump;
import fr.allycs.app.R;
import fr.allycs.app.View.TargetMenu.TargetMenuActivity;

public class                    GeneralSettings {
    private String              TAG = "GeneralSettings";
    private Activity            mActivity;
    private Tcpdump             mTcpdump;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    private BottomSheetMenuDialog mBottomSheet;

    private final String        PCAP_DUMP = "Write in Pcap file",
                                SSLSTRIP_MODE = "SslStrip Mode",
                                LOCKSCREEN = "Lockscreen",
                                ADVANCED_SNIFF_ANAL = "Advanced trame analyse",
                                PORT_REDIRECT = "Port redirect",
                                PORT_FILTERING = "Port filtering",
                                DNS_SPOOFING = "Dns Spoofing";

    public                      GeneralSettings(Activity activity, CoordinatorLayout coordinatorLayout, Tcpdump tcpdump) {
        this.mTcpdump = tcpdump;
        mActivity = activity;
        mCoordinatorLayout = coordinatorLayout;
        init();
    }


    private void                init() {
        BottomSheetBuilder builder = new BottomSheetBuilder(mActivity)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setBackgroundColor(ContextCompat.getColor(mActivity, R.color.material_light_white));
        builder.addTitleItem("Behavior");
        builder.addItem(0, PCAP_DUMP, (mTcpdump.isDumpingInFile) ? R.drawable.ic_checkbox_marked_grey600_24dp: R.drawable.ic_checkbox_blank_outline_grey600_24dp );
        builder.addItem(1, SSLSTRIP_MODE, (mSingleton.isSslstripMode()) ? R.drawable.ic_checkbox_marked_grey600_24dp: R.drawable.ic_checkbox_blank_outline_grey600_24dp );
        builder.addItem(2, LOCKSCREEN, (mSingleton.isLockScreen()) ? R.drawable.ic_checkbox_marked_grey600_24dp: R.drawable.ic_checkbox_blank_outline_grey600_24dp );
        builder.addItem(3, ADVANCED_SNIFF_ANAL, (mTcpdump.ismAdvancedAnalyseTrame()) ? R.drawable.ic_checkbox_marked_grey600_24dp: R.drawable.ic_checkbox_blank_outline_grey600_24dp );
        builder.addDividerItem();
        builder.addItem(4, PORT_REDIRECT, R.drawable.ic_checkbox_blank_outline_grey600_24dp);
        builder.addItem(5, PORT_FILTERING, R.drawable.ic_checkbox_blank_outline_grey600_24dp);
        builder.addItem(6, DNS_SPOOFING, (mSingleton.isDnsControlstarted()) ? R.drawable.ic_checkbox_marked_grey600_24dp: R.drawable.ic_checkbox_blank_outline_grey600_24dp )
                .setItemClickListener(onClick())
                .expandOnStart(true);
        mBottomSheet = builder.createDialog();
    }

    private BottomSheetItemClickListener onClick() {
        return new BottomSheetItemClickListener() {
            @Override
            public void onBottomSheetItemClick(MenuItem menuItem) {
                Log.d(TAG, "Menu Wireshark [" + menuItem.getTitle().toString() + "]");
                switch (menuItem.getTitle().toString()) {
                    case PCAP_DUMP:
                        mTcpdump.isDumpingInFile = !mTcpdump.isDumpingInFile;
                        break;
                    case SSLSTRIP_MODE:
                        mSingleton.setSslstripMode(!mSingleton.isSslstripMode());
                        break;
                    case LOCKSCREEN:
                        mSingleton.setLockScreen(!mSingleton.isLockScreen());
                        break;
                    case ADVANCED_SNIFF_ANAL:
                        mTcpdump.setmAdvancedAnalyseTrame(!mTcpdump.ismAdvancedAnalyseTrame());
                        break;
                    case PORT_REDIRECT:
                        onPortMitm(true);
                        break;
                    case PORT_FILTERING:
                        onPortMitm(false);
                        break;
                    case DNS_SPOOFING:
                        mSingleton.setDnsControlstarted(!mSingleton.isDnsControlstarted());
                        break;
                    default:
                        mActivity.startActivity(new Intent(mActivity, TargetMenuActivity.class));
                        break;
                }
            }
        };
    }

    public void                 show() {
        mBottomSheet.show();
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
