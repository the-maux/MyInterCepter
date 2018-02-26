package fr.dao.app.View.Activity.HostDetail;

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

import fr.dao.app.Core.Database.DBManager;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.Activity.HostDiscovery.HostDiscoveryActivity;
import fr.dao.app.View.Behavior.Fragment.MyFragment;
import fr.dao.app.View.Widget.Adapter.MyPcapAdapter;


public class                    PcapFragment extends MyFragment {
    private String              TAG = "HostNotesFragment";
    private CoordinatorLayout   mCoordinatorLayout;
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
        if (mSingleton.hostList == null) {
            Snackbar.make(mCoordinatorLayout, "No target saved, You need to scan the Network", Snackbar.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(), HostDiscoveryActivity.class));
            getActivity().onBackPressed();
        } else {
            mFocusedHost = mSingleton.hostList.get(0);
            mRV.setAdapter(new MyPcapAdapter(this, DBManager.getListPcapFormHost(mFocusedHost)));
            mRV.setHasFixedSize(true);
            mRV.setLayoutManager(new LinearLayoutManager(getActivity()));
        }
    }
}