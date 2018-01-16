package fr.allycs.app.Controller.Misc;


import android.app.Activity;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.View;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;

public class                        MyFragment extends Fragment implements MyFragmentInterface {

    @Override public void           init() {

    }

    @Override public boolean        start() {
        return false;
    }

    @Override public void           stop() {

    }

    @Override public BottomSheetMenuDialog onSettingsClick(AppBarLayout appBarLayout, Activity activity) {
        return null;
    }

    public void                     initSearchView(SearchView mSearchView) {

    }

    public void                     onAddButtonClick(View mAddHostBtn) {

    }
}
