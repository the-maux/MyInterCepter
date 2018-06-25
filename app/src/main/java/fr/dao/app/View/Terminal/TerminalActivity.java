package fr.dao.app.View.Terminal;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;

public class                    TerminalActivity extends MyActivity {
    private String              TAG = "NmapActivity";
    private TerminalActivity    mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    private TerminalFrgmnt      nmapOutputFragment;
    private AppBarLayout        appBarLayout;
    private Toolbar             mToolbar;
    TabLayout                   mTabs;
    private ImageView           mSettingsMenu, mScript, mScanType, OsImg;
    ProgressBar                 mProgressBar;


    protected void              onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmap);
        initXml();
        init();
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        mTabs = findViewById(R.id.tabs);
        mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progressBar);
        mSettingsMenu = findViewById(R.id.settingsMenu);
        mScript = findViewById(R.id.scriptBtn);
        mScanType = findViewById(R.id.typeScanBtn);
        OsImg = findViewById(R.id.OsImg);
        appBarLayout = findViewById(R.id.appBar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(findViewById(R.id.relativeLayout), 2);
                ViewCompat.setElevation(appBarLayout, 4);
            }
        });

    }

    private void                init() {
        mTabs.setBackgroundResource(R.color.material_deep_orange_900);
        findViewById(R.id.relativeLayout).setBackgroundResource(R.color.material_deep_orange_400);
        MyGlideLoader.loadDrawableInImageView(this, R.drawable.linuxicon, OsImg, true);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
        mScanType.setVisibility(View.GONE);
        mScript.setImageResource(R.drawable.ic_add_circle);
        mScript.setPadding(12,12,12,12);
        mToolbar.setTitle("Terminal");
        mToolbar.setSubtitle(Environment.getExternalStorageDirectory().getPath() + "/Dao/");
        initFragment();
    }

    private void                initFragment() {
        try {
            nmapOutputFragment = new TerminalFrgmnt();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, nmapOutputFragment)
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

}
