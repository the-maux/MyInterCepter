package fr.dao.app.View.Behavior.Coordinator;


import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;

public class                FooterBarBehavior extends CoordinatorLayout.Behavior<com.aurelhubert.ahbottomnavigation.AHBottomNavigation> {

    public FooterBarBehavior() {
        //Used when attached to a view class as the default behavior
    }

    public FooterBarBehavior(Context context, AttributeSet atts) {
        super(context, atts);
        //USed when attached to a view via xml
    }

    public boolean layoutDependsOn(CoordinatorLayout parent, AHBottomNavigation child, View dependency) {
        if (dependency instanceof Snackbar.SnackbarLayout) {
            updateFabTranslationForSnackBar(parent, child, dependency);
        } else if (dependency instanceof AppBarLayout) {
            updateFabVisibility(parent, (AppBarLayout) dependency, child);
        }
        return super.layoutDependsOn(parent, child, dependency);
    }

    private void                updateFabVisibility(CoordinatorLayout parent, AppBarLayout dependency, AHBottomNavigation child) {

    }

    private void                updateFabTranslationForSnackBar(CoordinatorLayout parent, AHBottomNavigation child, View dependency) {

    }
}
