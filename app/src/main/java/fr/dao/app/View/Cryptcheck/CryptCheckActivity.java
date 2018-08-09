package fr.dao.app.View.Cryptcheck;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Config.Cryptcheck.CryptCheckScan;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;

public class CryptCheckActivity extends MyActivity {
    private String              TAG = "CryptCheckActivity";
    private CryptCheckActivity mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    private CryptFrgmnt         mFragment;
    private AppBarLayout        appBarLayout;
    private Toolbar             mToolbar;
    private CryptCheckScan      mScan;
    TabLayout                   mTabs;
    private ImageView           mSettingsMenu, addTerminal, mScanType, OsImg;
    ProgressBar                 mProgressBar;
    private TextView            grade;

    protected void              onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cryptcheck);
        initXml();
        init();
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        mTabs = findViewById(R.id.tabs);
        mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progressBar3);
        mSettingsMenu = findViewById(R.id.toolbarSettings);
        addTerminal = findViewById(R.id.toolbarBtn2);
        mScanType = findViewById(R.id.toolbarBtn1);
        OsImg = findViewById(R.id.OsImg);
        grade = findViewById(R.id.grade);
        findViewById(R.id.rootView).setBackgroundResource(R.color.black_primary);
        appBarLayout = findViewById(R.id.appBar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(findViewById(R.id.relativeLayout), 2);
                ViewCompat.setElevation(appBarLayout, 4);
            }
        });
        mSettingsMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //mToolbar.showOverflowMenu();
                PopupMenu popup = new PopupMenu(mInstance, mSettingsMenu);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.sniff_bottom_bar, popup.getMenu());
                popup.show();
            }
        });

        MyGlideLoader.loadDrawableInImageView(this, R.mipmap.ic_cryptcheck_png, OsImg, true);

        mScanType.setVisibility(View.GONE);
        addTerminal.setVisibility(View.GONE);
        setToolbarTitle("Cryptcheck","Https Analyse");
        findViewById(R.id.relativeLayout).setBackgroundResource(R.color.cryptcheckPrimary);
        setStatusBarColor(R.color.cryptcheckPrimary);
        initTabs();
    }

    private void                initTabs() {
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                if (mScan != null) {
                    setGrade(grade);
                    mScan.updateOffset(tab.getPosition());
                    mFragment.reloadView();
                    setToolbarTitle(null, mScan.results.get(tab.getPosition()).ip);
                }
            }
            public void onTabUnselected(TabLayout.Tab tab) {}
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void                setGrade(TextView grade) {
        String gradeS = mScan.results.get(mScan.resultOffset).grade;
        if (gradeS.contains("A") || gradeS.contains("B")) {
            grade.setTextColor(ContextCompat.getColor(mInstance, R.color.green));
        } else if (gradeS.contains("C") || gradeS.contains("D")) {
            grade.setTextColor(ContextCompat.getColor(mInstance, R.color.material_orange_700));

        } else if (gradeS.contains("E") || gradeS.contains("F")) {
            grade.setTextColor(ContextCompat.getColor(mInstance, R.color.material_orange_700));
        }
        grade.setText(gradeS);
    }

    private void                init() {
        initFragment();
        mTabs.setVisibility(View.GONE);
    }

    private void                initFragment() {
        try {
            mFragment = new CryptFrgmnt();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, mFragment)
                    .commit();
        } catch (IllegalStateException e) {
            showSnackbar("Error in fragment: " + e.getCause().getMessage());
            e.getStackTrace();
            super.onBackPressed();
        }
    }

    public void                 setToolbarTitle(final String title, final String subtitle) {
        mInstance.runOnUiThread(new Runnable() {
            public void run() {
                if (title != null)
                    mToolbar.setTitle(title);
                if (subtitle != null)
                    mToolbar.setSubtitle(subtitle);
            }
        });
    }

    public void                 showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

    public boolean              onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sniff_bottom_bar, menu);
        return true;
    }


    public boolean              onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ACTION1:
                Log.d(TAG, "ACTION1 item");
                return true;
            case R.id.ACTION2:
                Log.d(TAG, "ACTION2 item");
                return true;
            default:
                Log.d(TAG, "default item");
                return true;
        }
    }

    public void                 onResponseServer(CryptCheckScan scan) {
        OsImg.setVisibility(View.INVISIBLE);
        grade.setVisibility(View.VISIBLE);
        setToolbarTitle(scan.host, scan.results.get(0).ip);
        mScan = scan;
        if (scan.results.size() == 1) {

        } else {
            mTabs.setVisibility(View.VISIBLE);
            mTabs.removeAllTabs();
            for (int i = 0; i < scan.results.size(); i++) {
                mTabs.addTab(mTabs.newTab().setText(scan.results.get(i).ip), i);
            }
        }
    }
}
