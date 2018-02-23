package fr.dao.app.View.Activity.HostDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.R;
import fr.dao.app.View.Activity.HostDiscovery.HostDiscoveryActivity;
import fr.dao.app.View.Behavior.Fragment.MyFragment;


public class                    HostDetailFragment extends MyFragment {
    private String              TAG = "HostDetailFragment";
    private CoordinatorLayout   mCoordinatorLayout;
    private List<Network>       HistoricAps;
    private RecyclerView        mRV;
    private RelativeLayout      mDetailSessionLayout;
    public enum HistoricDetailMode { ApHistoric, SessionsOfAp, devicesOfSession, detailSession, noHistoric}
    public HistoricDetailMode mActualMode = HistoricDetailMode.noHistoric;
    private Host                mFocusedHost;//TODO need to be init
    private RecyclerView.Adapter RV_AdapterAp = null, RV_AdapterSessions = null, RV_AdapterHostSession = null;
    private TextView            mNoHistoric;
    private Context             mCtx = getActivity();
    private HostDiscoveryActivity mActivity;


    private Network focusedSession = null;

    public View                 onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hostdetail, container, false);
        initXml(rootView);
        if (mSingleton.hostList == null) {
            Snackbar.make(mCoordinatorLayout, "No target saved, You need to scan the Network", Snackbar.LENGTH_LONG).show();
            startActivity(new Intent(getActivity(), HostDiscoveryActivity.class));
            getActivity().finish();
        }
        mFocusedHost = mSingleton.hostList.get(0);
        if (mRV.getVisibility() == View.GONE) {
            mDetailSessionLayout.setVisibility(View.GONE);
            mRV.setVisibility(View.VISIBLE);
        }
        return rootView;
    }
    
    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        mDetailSessionLayout = rootView.findViewById(R.id.detailSessionLayout);
        mDetailSessionLayout.setVisibility(View.GONE);
        mNoHistoric = rootView.findViewById(R.id.noHistoric);
        mRV = rootView.findViewById(R.id.RV_Historic);
        mRV.setHasFixedSize(true);
        mRV.setLayoutManager(new LinearLayoutManager(mCtx));
    }


    public void                 focusOneTarget(Host host) {
        mSingleton.actualNetwork = mActivity.actualNetwork;
        if (mSingleton.hostList == null)
            mSingleton.hostList = new ArrayList<>();
        else
            mSingleton.hostList.clear();
        mSingleton.hostList.add(host);
        Intent intent = new Intent(mActivity, HostDetailActivity.class);
        startActivity(intent);
    }

}


