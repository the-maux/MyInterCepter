package fr.dao.app.View.Scan;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Network.Discovery.NetworkDiscoveryControler;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Adapter.DoraAdapter;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;

public class HunterActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private HunterActivity mInstance = this;

    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    private AppBarLayout        appBarLayout;
    private SearchView          searchView;
    private TabItem             radar, signalQuality;
    private ImageView           add, more;
    private RecyclerView        mRV_dora;
    private ProgressBar         progressBar;
    private DoraAdapter         mRv_Adapter;
    private FloatingActionButton mFab;
    private int                 REFRESH_TIME = 1000;// == 1seconde
    ProgressDialog              dialog;

    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dora);
        initXml();
        initRV();
        //TODO: Analyze the target (check all port for VulnScan)
        // Create ItemTab for every Open Port
        // FAB = Launch Scan
        // Toolbar Btn => HailMary
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
        searchView =  findViewById(R.id.searchView);
        radar = findViewById(R.id.radar);
        signalQuality = findViewById(R.id.signalQuality);
        add = findViewById(R.id.add);
        more = findViewById(R.id.action_add_host);
        mRV_dora = findViewById(R.id.RV_dora);
        mFab = findViewById(R.id.fab);
        progressBar = findViewById(R.id.progressBarDora);
        mFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onFabClick();
            }
        });
        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(appBarLayout, 4);
            }
        });
    }

    private void                onFabClick() {
        Utils.vibrateDevice(mInstance);
        //TODO: start a scan or Check its not running
        launchScan();
    }

    private void                initRV() {
       // mRv_Adapter = new DoraAdapter(mInstance, mDoraWrapper.getmListOfHostDored());
        mRV_dora.setAdapter(mRv_Adapter);
        mRV_dora.setHasFixedSize(true);
        mRV_dora.setLayoutManager(new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false));
    }

    private void                launchScan() {
        //TODO: make a loading visible
        if (mSingleton.hostList == null || mSingleton.hostList.isEmpty()) {
            Log.d(TAG, "launchScan::mSingleton.hostList is null or empty");
            if (!NetworkDiscoveryControler.getInstance(this).inLoading)
                NetworkDiscoveryControler.getInstance(this).run(true);
            else
                Log.d(TAG, "launchScan::scan is already running");
        } else {
            Log.d(TAG, "launchScan::starting");
            mRv_Adapter.setIsRunning(true);
            mFab.setImageResource(R.drawable.ic_stop);
        }
    }

    public void                 showSnackbar(String txt) {
        super.showSnackbar(txt);
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }
}
