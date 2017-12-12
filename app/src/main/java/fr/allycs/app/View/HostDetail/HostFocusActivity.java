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
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Databse.DBHost;
import fr.allycs.app.Controller.Core.Databse.DBSession;
import fr.allycs.app.Controller.Misc.MyActivity;
import fr.allycs.app.Controller.Network.Discovery.Fingerprint;
import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.AccessPointAdapter;
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
    private TextView            mPortScan, mVulnerabilitys, mFingerprint, mMitm;
    private TabLayout           mTabs;
    private Host                mFocusedHost;
    private List<AccessPoint>   HistoricAps;
    private RecyclerView        RV_Historic;

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostdetail);
        if (mSingleton.hostsList == null || mSingleton.hostsList.isEmpty()) {
            Snackbar.make(findViewById(R.id.Coordonitor), "Vous n'avez selectionner aucune target", Snackbar.LENGTH_LONG).show();
            onBackPressed();
        } else {
            mFocusedHost = mSingleton.hostsList.get(0);
            initXml();
            init();
            initMenu();
        }
    }

    private void                init() {
        HistoricAps = DBSession.getAllAPWithDeviceIn(mFocusedHost);
        AccessPointAdapter adapter = new AccessPointAdapter(this, HistoricAps);
        RV_Historic.setAdapter(adapter);
        RV_Historic.setHasFixedSize(true);
        RV_Historic.setLayoutManager(new LinearLayoutManager(this));
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
        mTabs  = findViewById(R.id.tabs);
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

    public void                 onAccessPointFocus(AccessPoint ap) {
        List<Session> allSessionWithDeviceIn = new ArrayList<>();
        for (Session session : ap.Sessions) {
            for (Host device : session.listDevices) {
                if (device.mac.equals(mFocusedHost.mac)) {
                    allSessionWithDeviceIn.add(session);
                    break;
                }
            }
        }
        SessionAdapter adapter = new SessionAdapter(this, allSessionWithDeviceIn);
        RV_Historic.setAdapter(adapter);
        RV_Historic.setHasFixedSize(true);
        RV_Historic.setLayoutManager(new LinearLayoutManager(this));
    }

    public void                 onSessionFocused(Session session) {

    }
}
