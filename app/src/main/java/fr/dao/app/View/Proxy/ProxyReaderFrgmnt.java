package fr.dao.app.View.Proxy;

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

import fr.dao.app.Core.Configuration.MitManager;
import fr.dao.app.Core.Tcpdump.Proxy;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Adapter.HTTProxyAdapter;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;


public class                    ProxyReaderFrgmnt extends MyFragment {
    private String              TAG = "ProxyReaderFrgmnt";
    private Context             mCtx;
    private ConstraintLayout    rootViewForDashboard;
    private RelativeLayout      rootViewForLiveFlux;
    private ProxyActivity       mActivity;
    private RecyclerView        mProxy_RV;
    private HTTProxyAdapter     mAdapterWireshark;
    private Proxy               mProxy;
    ProgressDialog              dialog;

    public View                 onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_proxy, container, false);
        mCtx = getActivity();
        initXml(rootView);
        mActivity = (ProxyActivity) getActivity();
        mProxy = Proxy.getProxy(this, true);
        return rootView;
    }

    public void                 onResume() {
        super.onResume();
        init();
    }

    private void                initXml(View rootView) {
        mProxy_RV = rootView.findViewById(R.id.recyclerView2);
    }

    public void                 init() {
        String mode = getArguments().getString("mode");
        Log.d(TAG, "init => mode[" + mode + "]");
        initRV(mode == null ? HTTProxyAdapter.mode.HTTP.name() : mode);
    }

    private void                initRV(String mode) {
        mAdapterWireshark = new HTTProxyAdapter(mActivity, HTTProxyAdapter.mode.valueOf(mode));
        mProxy_RV.setAdapter(mAdapterWireshark);
        mProxy_RV.setItemAnimator(null);
        mProxy_RV.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    public boolean              start() {
        Log.d(TAG, "start");
        if (MitManager.getInstance().isProxyRunning()) {
            MitManager.getInstance().stopProxy();
        } else {
            mActivity.setToolbarTitle("Proxy starting", null);
            return MitManager.getInstance().initProxy(mProxy_RV, mAdapterWireshark);
        }
        return true;
    }

    public void                 onProxyStopped() {
        Log.d(TAG, " onProxyStopped");
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                mActivity.setToolbarTitle("Proxy stopped", null);
                mActivity.onProxystopped();
            }
        });
    }
}