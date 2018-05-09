package fr.dao.app.View.DashBoard;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;


public class                    DashboardDefenseFgmnt extends MyFragment {
    private String              TAG = "DashboardDefenseFgmnt";
    private DashboardActivity   mActivity;
    private ConstraintLayout    mRootView;

    public View                 onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dash_defense, container, false);
        initXml(rootView);
        mActivity = (DashboardActivity) getActivity();
        init();
        return rootView;
    }

    private void                initXml(View rootView) {
        mRootView = rootView.findViewById(R.id.rootView);
    }

    public void                 init() {
        mActivity.setToolbarTitle("BlueTeam Statistique", null);
    }

    public void                 onResume() {
        super.onResume();
        init();
    }
}