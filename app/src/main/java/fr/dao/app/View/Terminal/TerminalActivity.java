package fr.dao.app.View.Terminal;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
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
import android.widget.Toast;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;

public class                    TerminalActivity extends MyActivity {
    private String              TAG = "TerminalActivity";
    private TerminalActivity    mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    private TerminalFrgmnt terminalFragmet;
    private AppBarLayout        appBarLayout;
    private Toolbar             mToolbar;
    TabLayout                   mTabs;
    private ImageView           mSettingsMenu, addTerminal, mScanType, OsImg;
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
        mProgressBar = findViewById(R.id.progressBar3);
        mSettingsMenu = findViewById(R.id.toolbarSettings);
        addTerminal = findViewById(R.id.toolbarBtn2);
        mScanType = findViewById(R.id.toolbarBtn1);
        OsImg = findViewById(R.id.OsImg);
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

                //popup.showAsDropDown(view,x,y);
                // mToolbar.showContextMenuForChild(mSettingsMenu);
            }
        });
        findViewById(R.id.relativeLayout).setBackgroundResource(R.color.material_deep_orange_400);
        MyGlideLoader.loadDrawableInImageView(this, R.drawable.linuxicon, OsImg, true);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);

        mScanType.setImageResource(R.mipmap.ic_root_on);
        mScanType.setOnClickListener(onRootClicker());
        addTerminal.setVisibility(View.GONE);
        addTerminal.setImageResource(R.drawable.ic_add_circle);
        addTerminal.setPadding(18,18,18,18);
        mToolbar.setTitle("Terminal");
        setStatusBarColor(R.color.material_deep_orange_400);
        addTerminal.setOnClickListener(onAddTerminalClick());
    }

    private void                init() {

        mToolbar.setSubtitle(Environment.getExternalStorageDirectory().getPath() + "/Dao/");
        initFragment();
        initTabs();
    }

    private void                initTabs() {
        mTabs.setBackgroundResource(R.color.material_deep_orange_900);
        TabLayout.Tab tmpTab = mTabs.getTabAt(0);
        tmpTab.select();
        tmpTab.setText("Terminal 0");
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                if (mTabs.getVisibility() == View.VISIBLE)
                    terminalFragmet.updateStdout();
            }

            public void onTabUnselected(TabLayout.Tab tab) {
            }

            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private View.OnClickListener onRootClicker() {
        return new View.OnClickListener() {
            public void onClick(View view) {
                terminalFragmet.rootClicker(mScanType);
            }
        };
    }

    private void                initFragment() {
        try {
            terminalFragmet = new TerminalFrgmnt();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, terminalFragmet)
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

    private View.OnClickListener onAddTerminalClick() {
        return new View.OnClickListener() {
            public void onClick(View view) {
                if (mTabs.getVisibility() == View.GONE) {
                    mTabs.setVisibility(View.VISIBLE);
                }
                int nbr = mTabs.getTabCount();
                mTabs.addTab(mTabs.newTab().setText("Terminal " + (nbr+ 1)));
                terminalFragmet.addTerminal();
                mTabs.getTabAt(nbr).select();
            }
        };
    }

    public boolean              onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sniff_bottom_bar, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
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
}
