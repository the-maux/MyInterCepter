package fr.dao.app.View.Sniff;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.io.File;
import java.util.ArrayList;

import fr.dao.app.Core.Configuration.MitManager;
import fr.dao.app.Core.Tcpdump.Proxy;
import fr.dao.app.Model.Net.Trame;
import fr.dao.app.R;
import fr.dao.app.View.Proxy.ProxyActivity;
import fr.dao.app.View.ZViewController.Adapter.SniffPacketsAdapter;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;


public class                    ProxyReaderFrgmnt extends MyFragment {
    private String              TAG = "SniffReaderFrgmnt";
    private CoordinatorLayout   mCoordinatorLayout;
    private Context             mCtx;
    private ConstraintLayout    rootViewForDashboard;
    private RelativeLayout      rootViewForLiveFlux;
    private ProxyActivity       mActivity;
    private RecyclerView mProxy_RV;
    private SniffPacketsAdapter mAdapterWireshark;
    private Proxy               mProxy;
    ProgressDialog              dialog;

    public View                 onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wireshark, container, false);
        mCtx = getActivity();
        initXml(rootView);
        mActivity = (ProxyActivity) getActivity();
        mProxy = Proxy.getProxy(this, true);
        init();
        return rootView;
    }
    
    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        mProxy_RV = rootView.findViewById(R.id.RV_Wireshark);
        rootViewForLiveFlux = rootView.findViewById(R.id.rootViewForLiveFlux);
        rootViewForLiveFlux.setVisibility(View.VISIBLE);
    }

    public void                 init() {
        initRV();
    }

    private void                initRV() {
        mAdapterWireshark = new SniffPacketsAdapter(mActivity, mProxy_RV);
        mProxy_RV.setAdapter(mAdapterWireshark);
        mProxy_RV.setItemAnimator(null);
        mProxy_RV.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    public boolean              start() {
        if (mSingleton.isProxyStarted()) {
            MitManager.getInstance().stopProxy();
        } else {
            return MitManager.getInstance().initProxy(mProxy_RV, mAdapterWireshark);
        }
        return true;
    }

    public void                 onSniffingOver(final ArrayList<Trame> bufferOfTrame) {
        Log.d(TAG, " onSniffingOver:" + bufferOfTrame.size());
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mAdapterWireshark.loadListOfTrame(bufferOfTrame, dialog);
                mActivity.setToolbarTitle(null, bufferOfTrame.size() + " packets");
            }
        });
    }
    private int rax = 0;
    public synchronized void    loadingMonitor() {
        final String monitor = "Loading. Reading " + rax++ + " packets";
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.setMessage(monitor);
            }
        });
    }

    public void                 onError() {
    }

    public void                 onProxystopped() {
        Log.e(TAG, "onProxystopped");
        mActivity.onProxystopped();
    }
}