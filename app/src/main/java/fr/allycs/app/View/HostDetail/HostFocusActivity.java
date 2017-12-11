package fr.allycs.app.View.HostDetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Databse.DBSession;
import fr.allycs.app.Controller.Misc.MyActivity;
import fr.allycs.app.Controller.Network.Discovery.Fingerprint;
import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.AccessPointAdapter;
import fr.allycs.app.View.Adapter.HostDiscoveryAdapter;
import fr.allycs.app.View.Adapter.SessionAdapter;
import fr.allycs.app.View.NmapActivity;
import fr.allycs.app.View.WiresharkActivity;

public class                    HostFocusActivity extends MyActivity {
    private String              TAG = "HostFocusActivity";
    private HostFocusActivity   mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinator;
    private Toolbar             mToolbar;
    private CircleImageView     osHostImage;
    private TextView            mPortScan, mVulnerabilitys, mFingerprint, mMitm, mNoHistoric;
    private TabLayout           mTabs;
    private Host                mFocusedHost;
    private List<AccessPoint>   HistoricAps;
    private RecyclerView        RV_Historic;
    private RelativeLayout      mDetailSessionLayout;
    private enum HistoricMode { ApHistoric, SessionsOfAp, devicesOfSession, detailSession, noHistoric}
    private HistoricMode        mActualMode = HistoricMode.noHistoric;
    private RecyclerView.Adapter  RV_AdapterAp = null, RV_AdapterSessions = null, RV_AdapterHostSession = null;

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostdetail);
        if (mSingleton.hostsList == null || mSingleton.hostsList.isEmpty()) {
            Snackbar.make(findViewById(R.id.Coordonitor), "Vous n'avez selectionner aucune target", Snackbar.LENGTH_LONG).show();
            onBackPressed();
        } else {
            mFocusedHost = mSingleton.hostsList.get(0);
            initXml();
            initMenu();
            initHistoricFromDB();
        }
    }

    private void                initXml() {
        mCoordinator = findViewById(R.id.Coordonitor);
        osHostImage = findViewById(R.id.OsImg);
        mToolbar = findViewById(R.id.toolbar);
        mPortScan  = findViewById(R.id.PortScanTxt);
        mVulnerabilitys  = findViewById(R.id.VulnerabilityScan);
        mFingerprint = findViewById(R.id.OsScanTxt);
        mMitm  = findViewById(R.id.MitmARPTxt);
        RV_Historic = findViewById(R.id.RV_Historic);
        mDetailSessionLayout = findViewById(R.id.detailSessionLayout);
        mDetailSessionLayout.setVisibility(View.GONE);
        mTabs  = findViewById(R.id.tabs);
        mNoHistoric = findViewById(R.id.noHistoric);
        Fingerprint.setOsIcon(this, mFocusedHost, osHostImage);
        mToolbar.setTitle(mFocusedHost.ip);
        if (mFocusedHost.getName().contains("-")) {
            mToolbar.setSubtitle(mFocusedHost.mac);
        } else {
            mToolbar.setSubtitle(mFocusedHost.getName());
        }
    }

    private void                initMenu() {
        mPortScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, NmapActivity.class);
                startActivity(intent);
            }
        });
        mVulnerabilitys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(R.id.Coordonitor), "Not implemented yet", Snackbar.LENGTH_LONG).show();
            }
        });
        mFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(R.id.Coordonitor), "Not implemented yet", Snackbar.LENGTH_LONG).show();
            }
        });
        mMitm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, WiresharkActivity.class);
                startActivity(intent);
            }
        });
        RV_Historic.setHasFixedSize(true);
        RV_Historic.setLayoutManager(new LinearLayoutManager(this));
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
        Snackbar.make(findViewById(R.id.Coordonitor), "Not implemented yet", Snackbar.LENGTH_LONG).show();
    }

    private void                displayHistoric() {
        Log.d(TAG, "LOAD FROM BDD THE HISTORIC OF " + mFocusedHost.ip);
        Snackbar.make(findViewById(R.id.Coordonitor), "Not implemented yet", Snackbar.LENGTH_LONG).show();
    }

    private void                displayServices() {
        Log.d(TAG, "SHOW SERVICES OF " + mFocusedHost.ip);
        Snackbar.make(findViewById(R.id.Coordonitor), "Not implemented yet", Snackbar.LENGTH_LONG).show();
    }

    private void                initHistoricFromDB() {
        HistoricAps = DBSession.getAllAPWithDeviceIn(mFocusedHost);
        if (HistoricAps.isEmpty()) {
            mActualMode = HistoricMode.noHistoric;
            mNoHistoric.setVisibility(View.VISIBLE);
            RV_Historic.setVisibility(View.GONE);
        } else {
            if (RV_AdapterAp == null) {
                RV_AdapterAp = new AccessPointAdapter(this, HistoricAps);
            }
            RV_Historic.setAdapter(RV_AdapterAp);
            mActualMode = HistoricMode.ApHistoric;
            mNoHistoric.setVisibility(View.GONE);
        }
    }

    public void                 onAccessPointFocus(AccessPoint ap) {
        mActualMode = HistoricMode.SessionsOfAp;
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
        mActualMode = HistoricMode.detailSession;
        RV_Historic.setVisibility(View.GONE);
        mDetailSessionLayout.setVisibility(View.VISIBLE);
        //TODO: afficher les détail de la session
    }

    public void                hostOfSessionsFocused(Session session) {
        mDetailSessionLayout.setVisibility(View.GONE);
        RV_Historic.setVisibility(View.VISIBLE);
        if (RV_AdapterHostSession == null) {
            mActualMode = HistoricMode.devicesOfSession;
            HostDiscoveryAdapter hostAdapter = new HostDiscoveryAdapter(this, RV_Historic, true);
            hostAdapter.updateHostList(session.listDevices);
            RV_AdapterHostSession = hostAdapter;
        }
        RV_Historic.setAdapter(RV_AdapterHostSession);
    }

    @Override
    public void                 onBackPressed() {
        //super.onBackPressed();
        switch (mActualMode) {
            case ApHistoric:
                super.onBackPressed();
                break;
            case noHistoric:
                super.onBackPressed();
                break;
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
                super.onBackPressed();
        }
    }
}
