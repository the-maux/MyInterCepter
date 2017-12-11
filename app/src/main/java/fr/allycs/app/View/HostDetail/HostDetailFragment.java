package fr.allycs.app.View.HostDetail;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.Core.Databse.DBSession;
import fr.allycs.app.Controller.Network.Discovery.Fingerprint;
import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.AccessPointAdapter;
import fr.allycs.app.View.Adapter.HostDiscoveryAdapter;
import fr.allycs.app.View.Adapter.SessionAdapter;
import fr.allycs.app.View.HostDiscovery.HostDiscoveryActivity;


public class                    HostDetailFragment extends android.app.Fragment{
    private String              TAG = "HostDetailFragment";
    private TabLayout           mTabs;
    private CoordinatorLayout   mCoordinatorLayout;
    private List<AccessPoint>   HistoricAps;
    private RecyclerView        RV_Historic;
    private RelativeLayout      mDetailSessionLayout;
    public enum HistoricMode { ApHistoric, SessionsOfAp, devicesOfSession, detailSession, noHistoric}
    private Host                mFocusedHost;//TODO need to be init
    public  HistoricMode        mActualMode = HostDetailFragment.HistoricMode.noHistoric;
    private RecyclerView.Adapter  RV_AdapterAp = null, RV_AdapterSessions = null, RV_AdapterHostSession = null;
    private TextView            mNoHistoric;
    private Context             mCtx = getActivity();
    private HostDiscoveryActivity mActivity;
    
    @Override public View       onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hostdetail, container, false);
        initXml(rootView);
        initHistoricFromDB();
        return rootView;
    }
    
    private void                initXml(View rootView) {
        RV_Historic = rootView.findViewById(R.id.RV_Historic);
        mDetailSessionLayout = rootView.findViewById(R.id.detailSessionLayout);
        mDetailSessionLayout.setVisibility(View.GONE);
        mTabs  = rootView.findViewById(R.id.tabs);
        mNoHistoric = rootView.findViewById(R.id.noHistoric);
        RV_Historic.setHasFixedSize(true);
        RV_Historic.setLayoutManager(new LinearLayoutManager(mCtx));
    }

    private void                initTabs() {
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "tab.getHost().toString():" + tab.getText().toString());
                switch (tab.getText().toString()) {
                    case "historic":
                        displayHistoric();
                        break;
                    case "notes":
                        displayNotes();
                        break;
                    case "services":
                        displayServices();
                        break;

                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void                displayNotes() {
        Log.d(TAG, "LOAD FROM BDD THE NOTES OF " + mFocusedHost.ip);
        Snackbar.make(mCoordinatorLayout, "Not implemented yet", Snackbar.LENGTH_LONG).show();
    }

    private void                displayHistoric() {
        Log.d(TAG, "LOAD FROM BDD THE HISTORIC OF " + mFocusedHost.ip);
        Snackbar.make(mCoordinatorLayout, "Not implemented yet", Snackbar.LENGTH_LONG).show();
    }

    private void                displayServices() {
        Log.d(TAG, "SHOW SERVICES OF " + mFocusedHost.ip);
        Snackbar.make(mCoordinatorLayout, "Not implemented yet", Snackbar.LENGTH_LONG).show();
    }

    public void                initHistoricFromDB() {
        HistoricAps = DBSession.getAllAPWithDeviceIn(mFocusedHost);
        if (HistoricAps.isEmpty()) {
            mActualMode = HostDetailFragment.HistoricMode.noHistoric;
            mNoHistoric.setVisibility(View.VISIBLE);
            RV_Historic.setVisibility(View.GONE);
        } else {
            if (RV_AdapterAp == null) {
                RV_AdapterAp = new AccessPointAdapter(this, HistoricAps);
            }
            RV_Historic.setAdapter(RV_AdapterAp);
            mActualMode = HostDetailFragment.HistoricMode.ApHistoric;
            mNoHistoric.setVisibility(View.GONE);
        }
    }

    public void                 onAccessPointFocus(AccessPoint ap) {
        mActualMode = HostDetailFragment.HistoricMode.SessionsOfAp;
        if (RV_AdapterSessions == null) {
            List<Session> allSessionWithDeviceIn = new ArrayList<>();
            for (Session session : ap.Sessions) {
                for (Host device : session.listDevices) {
                    if (device.mac.equals(mFocusedHost.mac)) {
                        allSessionWithDeviceIn.add(session);
                        break;
                    }
                }
            }
            RV_AdapterSessions = new SessionAdapter(this, allSessionWithDeviceIn);
        }
        RV_Historic.setAdapter(RV_AdapterSessions);
    }

    public void                 onSessionFocused(Session session) {
        mActualMode = HostDetailFragment.HistoricMode.detailSession;
        RV_Historic.setVisibility(View.GONE);
        mDetailSessionLayout.setVisibility(View.VISIBLE);
        TextView date, name, nbrServiceDiscovered, typeScan;

        //TODO: afficher les détail de la session
    }

    public void                 hostOfSessionsFocused(Session session) {
        mDetailSessionLayout.setVisibility(View.GONE);
        RV_Historic.setVisibility(View.VISIBLE);
        if (RV_AdapterHostSession == null) {
            mActualMode = HostDetailFragment.HistoricMode.devicesOfSession;
            HostDiscoveryAdapter hostAdapter = new HostDiscoveryAdapter(getActivity(), RV_Historic, true);
            hostAdapter.updateHostList(session.listDevices);
            RV_AdapterHostSession = hostAdapter;
        }
        RV_Historic.setAdapter(RV_AdapterHostSession);
    }

    public boolean              onBackPressed() {
        switch (mActualMode) {
            case ApHistoric:
                return true;
            case noHistoric:
                return true;
            case SessionsOfAp:
                initHistoricFromDB();
                break;
            case detailSession:
                onAccessPointFocus(null);
                break;
            case devicesOfSession:
                onSessionFocused(null);
                break;
            default:
                return true;
        }
        return false;
    }

}


