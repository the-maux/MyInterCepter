package fr.dao.app.View.Activity.HostDetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Database.DBManager;
import fr.dao.app.Core.Nmap.Fingerprint;
import fr.dao.app.Model.Net.Pcap;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.ScrollingActivity;
import fr.dao.app.View.Activity.HostDiscovery.FragmentHistoric;
import fr.dao.app.View.Activity.Scan.NmapActivity;
import fr.dao.app.View.Activity.Wireshark.WiresharkActivity;
import fr.dao.app.View.Behavior.Activity.MyActivity;
import fr.dao.app.View.Behavior.Fragment.MyFragment;

public class                    HostDetailActivity extends MyActivity {
    private String              TAG = "HostDetailActivity";
    private HostDetailActivity  mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinator;
    private Toolbar             mToolbar;
    private ImageView           osHostImage;
    private TabLayout           mTabs;
    private TextView            mPortScan, mVulnerabilitys, mFingerprint, mMitm;
    private Host                mFocusedHost;
    private MyFragment          mCurrentFragment;
    private List<Pcap>          mPcapsList;

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostdetail);
        init();
    }

    private void                init() {
        try {
            int position = getIntent().getExtras().getInt("position");
            mFocusedHost = mSingleton.hostList.get(position);
            initXml();
            initMenu();
            initTabs();
            displayHistoric();
        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.Coordonitor), "Vous n'avez selectionner aucune target", Snackbar.LENGTH_LONG).show();
            Log.e(TAG, "Error in init, Back to previous fragment");
            onBackPressed();
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
        Fingerprint.setOsIcon(this, mFocusedHost, osHostImage);
        mToolbar.setTitle(mFocusedHost.ip);
        mTabs  = findViewById(R.id.tabs);
        mToolbar.setSubtitle(mFocusedHost.getName());
//        DisplayMetrics displaymetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//        int width = displaymetrics.widthPixels;
//        int appbar_height = (int)Math.round(width/1.5);
//        CoordinatorLayout.LayoutParams layoutParams = new CoordinatorLayout.LayoutParams(AppBarLayout.LayoutParams.MATCH_PARENT, appbar_height);
//        AppBarLayout appBarLayout = findViewById(R.id.appbar);
//        //AppBarLayout.LayoutParams layoutParams = appBarLayout.getLayoutParams();
//        appBarLayout.setLayoutParams(layoutParams);
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
                Intent intent = new Intent(mInstance, ScrollingActivity.class);
                startActivity(intent);
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
        mTabs.addTab(mTabs.newTab().setText("Historic"), 0);
        if (mFocusedHost.Notes != null && !mFocusedHost.Notes.isEmpty())
            mTabs.addTab(mTabs.newTab().setText("Notes"), 0);
        mPcapsList = DBManager.getListPcapFormHost(mFocusedHost);
        if (mPcapsList != null && !mPcapsList.isEmpty())
            mTabs.addTab(mTabs.newTab().setText("Pcap"), 0);
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "tab.getFirstInputQuestion().toString():" + tab.getText().toString());
                switch (tab.getText().toString().toLowerCase()) {
                    case "historic":
                        displayHistoric();
                        break;
                    case "notes":
                        displayNotes();
                        break;
                    case "pcap":
                        displayPcap();
                        break;
                }
            }
            public void onTabUnselected(TabLayout.Tab tab) {}
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void                displayNotes() {
        initFragment(new HostNotesFragment());
    }

    private void                displayHistoric() {
        MyFragment fragment = new FragmentHistoric();
        Bundle args = new Bundle();
        args.putString("mode", FragmentHistoric.HOST_HISTORIC);
        fragment.setArguments(args);
        initFragment(fragment);
    }

    private void                displayPcap() {
        Log.d(TAG, "SHOW Pcaps OF " + mFocusedHost.ip);

        List<Pcap> pcapList = DBManager.getListPcapFormHost(mFocusedHost);
        if (pcapList == null || pcapList.isEmpty())
            Snackbar.make(mCoordinator, "No Pcap Recorded for " + mFocusedHost.getName(), Snackbar.LENGTH_LONG).show();
        else {
            //TODO: faire l'adapter de pcap
        }
    }

    private void                initFragment(MyFragment fragment) {
        try {
            mCurrentFragment = fragment;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
        } catch (IllegalStateException e) {
            Log.w("Error MainActivity", "FragmentStack or FragmentManager corrupted");
            Snackbar.make(findViewById(R.id.Coordonitor), "Error in fragment", Snackbar.LENGTH_LONG).show();
            super.onBackPressed();
        }
    }

    public void                 setToolbarTitle(final String title, final String subtitle) {
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (title != null)
                    mToolbar.setTitle(title);
                if (subtitle != null)
                    mToolbar.setSubtitle(subtitle);
            }
        });
    }

    public void                 onBackPressed() {
        if (mCurrentFragment == null || mCurrentFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
