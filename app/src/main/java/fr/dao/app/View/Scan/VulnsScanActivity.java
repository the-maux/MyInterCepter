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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Adapter.DoraAdapter;
import fr.dao.app.View.ZViewController.Adapter.VunlsProtoAdapter;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;

public class                    VulnsScanActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private VulnsScanActivity   mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    private AppBarLayout        appBarLayout;
    private SearchView          searchView;
    private TabItem             radar, signalQuality;
    private ImageView           add, more;
    private RecyclerView        mRV_Vulns;
    private ProgressBar         progressBar;
    private FloatingActionButton mFab;
    private VunlsProtoAdapter   mRv_Adapter;
    private Host                mFocusedHost;
    ProgressDialog              dialog;

    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vulnscanner);
        initXml();
        analyseTarget();
        // Toolbar Btn => HailMary
    }

    private void                analyseTarget() {
        // Create ItemTab for every Open Port
        bundle = getIntent().getExtras();
        if (bundle == null || bundle.getString("macAddress") == null) {
            showSnackbar("No target selected");
        } else {
            mFocusedHost = DBHost.getDevicesFromMAC(bundle.getString("macAddress"));
   //         dialog = ProgressDialog.show(mInstance, "Analyzing " + mFocusedHost.getName(), "Scanning. Please wait...", true);
        }
    }

    /**
     * Host has been updated
     */
    public void                 updateUIForHost(Host host) {
        //Create all the tab for every Open Port + 1 General
        mRv_Adapter = new VunlsProtoAdapter(mInstance, host);
        mFocusedHost = host;
        mRV_Vulns.setAdapter(mRv_Adapter);
        mRV_Vulns.setLayoutManager(new LinearLayoutManager(mInstance));
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
        searchView =  findViewById(R.id.searchView);
        radar = findViewById(R.id.radar);
        signalQuality = findViewById(R.id.signalQuality);
        add = findViewById(R.id.add);
        more = findViewById(R.id.action_add_host);
        more.setOnClickListener(onMoreClicked());
        mRV_Vulns = findViewById(R.id.RV_dora);
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

    private View.OnClickListener onMoreClicked() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        };
    }

    private void                onFabClick() {
        Utils.vibrateDevice(mInstance);
        //TODO: start a scan or Check its not running
        launchScan();
    }

    private void                launchScan() {
        //TODO: make a loading visible
        if (mSingleton.hostList == null || mSingleton.hostList.isEmpty()) {

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
