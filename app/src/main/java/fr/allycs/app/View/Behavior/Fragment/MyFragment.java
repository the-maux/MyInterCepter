package fr.allycs.app.View.Behavior.Fragment;


import android.app.Activity;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.widget.ImageButton;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;

public class                        MyFragment extends Fragment implements MyFragmentInterface {

    public void                     init() {

    }

    public boolean                  start() {
        return false;
    }

    public void                     stop() {

    }

    public BottomSheetMenuDialog onSettingsClick(AppBarLayout appBarLayout, Activity activity) {
        return null;
    }

    public void                     initSearchView(SearchView mSearchView) {

    }

    public void                     onAddButtonClick(ImageButton mAddHostBtn) {

    }

    public boolean                  onBackPressed() {
        return true;
    }
}
