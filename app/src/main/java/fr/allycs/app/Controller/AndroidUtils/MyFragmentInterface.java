package fr.allycs.app.Controller.AndroidUtils;

import android.app.Activity;
import android.support.design.widget.AppBarLayout;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;


public interface            MyFragmentInterface {
    void                    init();
    boolean                 start();
    void                    stop();
    BottomSheetMenuDialog   onSettingsClick(AppBarLayout appBarLayout, Activity hostDiscoveryActivity);

}
