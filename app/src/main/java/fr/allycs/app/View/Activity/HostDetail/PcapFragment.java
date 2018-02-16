package fr.allycs.app.View.Activity.HostDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Core.Database.DBManager;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;
import fr.allycs.app.View.Activity.HostDiscovery.HostDiscoveryActivity;
import fr.allycs.app.View.Behavior.Fragment.MyFragment;
import fr.allycs.app.View.Widget.Adapter.MyPcapAdapter;


public class                    PcapFragment extends MyFragment {
    private String              TAG = "HostNotesFragment";
    private CoordinatorLayout   mCoordinatorLayout;
    private Singleton           mSingleton = Singleton.getInstance();
    private Host                mFocusedHost;
    private Context             mCtx = getActivity();
    private HostDiscoveryActivity mActivity;
    private RecyclerView        mRV;

    public View                 onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pcaps, container, false);
        initXml(rootView);
        init();
        return rootView;
    }
    
    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        mRV = rootView.findViewById(R.id.central_layout);
    }

    public void                 init() {
        if (mSingleton.selectedHostsList == null) {
            Snackbar.make(mCoordinatorLayout, "No target saved, You need to scan the network", Snackbar.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(), HostDiscoveryActivity.class));
            getActivity().onBackPressed();
        } else {
            mFocusedHost = mSingleton.selectedHostsList.get(0);
            mRV.setAdapter(new MyPcapAdapter(this, DBManager.getListPcapFormHost(mFocusedHost)));
            mRV.setHasFixedSize(true);
            mRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }
}