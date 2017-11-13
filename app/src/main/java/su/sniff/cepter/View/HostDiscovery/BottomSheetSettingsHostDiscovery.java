package su.sniff.cepter.View.HostDiscovery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;

import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.OSAdapter;
import su.sniff.cepter.View.Dialog.RV_dialog;
import su.sniff.cepter.View.MenuActivity;


public class                            BottomSheetSettingsHostDiscovery {
  /*  private String                      TAG = "SettingsHostDiscovery";
    private View                        bottomSheet;
    private HostDiscoveryActivity       activity;

    public BottomSheetSettingsHostDiscovery(HostDiscoveryActivity activity, CoordinatorLayout coordinatorLayout) {
        this.activity = activity;
        Log.d(TAG, "menu created");
        View bottomSheet = new BottomSheetBuilder(activity, coordinatorLayout)
                .setMode(BottomSheetBuilder.MODE_LIST)
                //.setBackgroundColor(ContextCompat.getColor(activity, R.color.material_light_white))
                .setMenu(R.menu.settings_hostdiscovery)
                .setItemClickListener(onItemClick())
                .createView();
        bottomSheet.setVisibility(View.VISIBLE);
    }

    private BottomSheetItemClickListener onItemClick() {
        return new BottomSheetItemClickListener() {
            @Override
            public void onBottomSheetItemClick(MenuItem menuItem) {
                switch (menuItem.getTitle().toString()) {
                    case "Os filter":
                        activity.osFilterDialog();
                        break;
                    case "Select all targets":
                        activity.mHostAdapter.selectAll();
                        break;
                    case "Mode offline":
                        activity.startActivity(new Intent(activity, MenuActivity.class));
                        break;
                }
            }
        };
    }*/
}
