package fr.allycs.app.View.Activity.HostDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import fr.allycs.app.View.Behavior.Fragment.MyFragment;
import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;
import fr.allycs.app.View.Activity.HostDiscovery.HostDiscoveryActivity;


public class                    HostNotesFragment extends MyFragment {
    private String              TAG = "HostNotesFragment";
    private CoordinatorLayout   mCoordinatorLayout;
    private Host                mFocusedHost;//TODO need to be init
    private Context             mCtx;
    private HostDiscoveryActivity mActivity;
    private ScrollView mCentral_layout;

    public View                 onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notes, container, false);
        initXml(rootView);
        init();
        mCtx = getActivity();
        return rootView;
    }
    
    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        mCentral_layout = rootView.findViewById(R.id.central_layout);
        if (mSingleton.selectedHostsList == null) {
            Snackbar.make(mCoordinatorLayout, "No target saved, You need to scan the network", Snackbar.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(), HostDiscoveryActivity.class));
            getActivity().onBackPressed();
        } else {
            mFocusedHost = mSingleton.selectedHostsList.get(0);
        }
        TextView valueTV = new TextView(getContext());
        valueTV.setText(mFocusedHost.Notes);
        mCentral_layout.addView(valueTV);
    }

    public void                 init() {

//        for (String note : mFocusedHost.Notes) {
//
//        }
    }
}