package fr.dao.app.View.Behavior.Fragment;


import android.app.Activity;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageButton;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;

import fr.dao.app.Core.Configuration.Singleton;

public class                        MyFragment extends Fragment implements MyFragmentInterface {
    protected Singleton             mSingleton = Singleton.getInstance();

    public void                     init() {}

    public boolean                  start() {
        return false;
    }

    public void                     stop() {    }

    public BottomSheetMenuDialog    onSettingsClick(AppBarLayout appBarLayout, Activity activity) {
        Log.e("My Fragment", "onSettingsClick is not set, returning null");
        return null;
    }

    public void                     initSearchView(SearchView mSearchView, Toolbar toolbar) {}

    public void                     onAddButtonClick(ImageButton mAddHostBtn) {}

    public boolean                  onBackPressed() {
        return true;
    }
}
