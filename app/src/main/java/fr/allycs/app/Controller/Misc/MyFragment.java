package fr.allycs.app.Controller.Misc;


import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;

public class                        MyFragment extends Fragment implements MyFragmentInterface {

    @Override public void           init() {

    }

    @Override public boolean        start() {
        return false;
    }

    @Override public void           stop() {

    }

    @Override public BottomSheetMenuDialog onSettingsClick(AppBarLayout appBarLayout) {
        return null;
    }
}
