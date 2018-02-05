package fr.allycs.app.View.HostDiscovery;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;

import fr.allycs.app.Controller.AndroidUtils.MyActivity;
import fr.allycs.app.Controller.AndroidUtils.MyFragment;
import fr.allycs.app.Controller.AndroidUtils.MyGlideLoader;
import fr.allycs.app.Controller.AndroidUtils.Utils;
import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Network.Discovery.NetworkDiscoveryControler;
import fr.allycs.app.Controller.Network.NetUtils;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.R;
import fr.allycs.app.View.TargetMenu.TargetMenuActivity;

/**
 * TODO:    + Add manual target
 *          + filterOs scrollView (bottom or top ?)
 *          + filter Text as SearchView
 *          + Button add -> No target mode / Settings /
 *          + detect target onFly ?
 *          + better Os detection
 */
public class                        HostDiscoveryActivity extends MyActivity {
    private String                  TAG = "HostDiscoveryActivity";
    private HostDiscoveryActivity   mInstance = this;
    private Singleton               mSingleton = Singleton.getInstance();
    private CoordinatorLayout       mCoordinatorLayout;
    private FloatingActionButton    mFab;
    private TextView                mBottomMonitor;
    private int                     mProgress = 0;
    private ImageButton             mAddHostBtn, mSettingsBtn;
    private SearchView              mSearchView;
    private Toolbar                 mToolbar;
    private TabLayout               mTabs;
    private ProgressBar             mProgressBar;
    private MyFragment              mFragment, HistoricFragment = null, NetDiscoveryFragment = null;
    public final int                MAXIMUM_PROGRESS = 8500;
    public Session                  actualSession;

    public void                     onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostdiscovery);
        initXml();
        try {
            init();
        } catch (Exception e) {
            showSnackbar("Big error lors de l'init:");
            e.printStackTrace();
        }
    }

    private void                    initXml() {
        mFab = findViewById(R.id.fab);
        mBottomMonitor = ( findViewById(R.id.Message));
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
        mAddHostBtn = findViewById(R.id.action_add_host);
        mSettingsBtn = findViewById(R.id.settings);
        mSearchView = findViewById(R.id.searchView);
        mToolbar = findViewById(R.id.toolbar2);
        mTabs = findViewById(R.id.tabs);
        mProgressBar = findViewById(R.id.progressBar);
    }

    private void                    init()  throws Exception {
        NetUtils.initNetworkInfo(this);
        if (mSingleton.network == null || mSingleton.network.myIp == null) {
            showSnackbar("You need to be connected to a network");
            finish();
        } else {
            initTabs();
            initFabs();
            initMonitor();
            mFragment = new FragmentHostDiscoveryScan();
            NetDiscoveryFragment = mFragment;
            initFragment(NetDiscoveryFragment);
            initSearchView();
        }
    }

    private void                    initTabs(){
        final String                ARP_TAB_NAME = "Devices\nDiscovery",
                                    SERVICES_TAB_NAME = "Services\nDiscovery",
                                    HISTORIC_TAB_NAME = "Audit\nHistoric";
        mTabs.addTab(mTabs.newTab().setText(ARP_TAB_NAME), 0);
        //mTabs.addTab(mTabs.newTab().setText(SERVICES_TAB_NAME), 1);
        mTabs.addTab(mTabs.newTab().setText(HISTORIC_TAB_NAME), 1);
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                MyFragment fragment;
                Log.d(TAG, "onTabSelected:[" + tab.getText().toString().replace("\n", " ") + "]");
                switch (tab.getText().toString()) {
                    case ARP_TAB_NAME:
                        fragment = NetDiscoveryFragment;
                        break;
/*                    case SERVICES_TAB_NAME:
                        typeScan = NetworkDiscoveryControler.typeScan.Services;
                        fragment = NetDiscoveryFragment;
                        break;*/
                    case HISTORIC_TAB_NAME:
                        if (HistoricFragment == null)
                            HistoricFragment = new FragmentHistoric();
                        fragment = HistoricFragment;
                        Bundle args = new Bundle();
                        args.putString("mode", FragmentHistoric.DB_HISTORIC);
                        fragment.setArguments(args);
                        break;
                    default:
                        return ;
                }
                initFragment(fragment);
                initSearchView();
            }
            public void onTabUnselected(TabLayout.Tab tab) {}
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void                    initFabs() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFab.startAnimation(AnimationUtils.loadAnimation(mInstance, R.anim.shake));
                Utils.vibrateDevice(mInstance);
                if (!mFragment.start()) {
                    // Yes it's ugly, missconception herei admit, but lazy
                    ((FragmentHostDiscoveryScan)NetDiscoveryFragment).launchMenu();
                    mSingleton.actualSession = actualSession;
                    startActivity(new Intent(mInstance, TargetMenuActivity.class));
                } else if (mSingleton.DebugMode) {
                    Log.i(TAG, "fragment startAsLive false");
                }
            }
        });
    }

    private void                    initFragment(MyFragment fragment) {
        try {
            mFragment = fragment;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, mFragment)
                    .commit();
            //mFragment.startAsLive();
        } catch (IllegalStateException e) {
            showSnackbar("Error in fragment: " + e.getCause().getMessage());
            e.getStackTrace();
            super.onBackPressed();
        }
    }

    public void                     initMonitor() {
        String monitor = "";
        if (!monitor.contains("WiFi")) {
            monitor += " Ip Address : " + mSingleton.network.myIp;
            monitor += "\n" + mSingleton.network.Ssid + " : " + mSingleton.network.gateway;
        } else {
            monitor += "Not Connected";
        }
        if (Singleton.getInstance().network.isConnectedToNetwork())
            mBottomMonitor.setText(monitor);
        else
            mBottomMonitor.setText(mSingleton.network.Ssid + ": No connection");
    }

    public void                     initSettingsButton() {
        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragment = new HostDiscoverySettingsFragmt();
                initFragment(mFragment);
                mFab.setVisibility(View.GONE);
                mTabs.setVisibility(View.GONE);
            }
        });
        mFragment.onAddButtonClick(mAddHostBtn);
    }

    private void                    initSearchView() {
        mFragment.initSearchView(mSearchView);
    }

    public void                     setProgressState(final int progress){
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
                if (progress != -1)
                    mProgress = progress;
            }
        });
    }

    public void                     setToolbarTitle(final String title, final String subtitle) {
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

    public void                     setToolbarBackgroundColor(final int color) {
        Animation fadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        mToolbar.setAnimation(fadeOut);
        fadeOut.start();
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mToolbar.setBackgroundColor(color);
                mTabs.startAnimation(AnimationUtils.loadAnimation(mInstance, android.R.anim.fade_in));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void                     setBottombarTitle(final String title) {
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mBottomMonitor.setText(title);
            }
        });
    }

    public void                     progressAnimation() {
        mProgressBar.setVisibility(View.VISIBLE);
        mFab.setImageResource(R.drawable.my_icon_search);
        mProgressBar.setProgress(0);
        mProgressBar.setMax(MAXIMUM_PROGRESS);
        new Thread(new Runnable() {
            public void run() {
                mProgress = 0;
                while (mProgress <= MAXIMUM_PROGRESS) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mProgress += 10;
                    final int prog2 = mProgress;
                    mInstance.runOnUiThread(new Runnable() {
                        public void run() {
                            mProgressBar.setProgress(prog2);
                        }
                    });
                }
                mInstance.runOnUiThread(new Runnable() {
                    public void run() {
                        mFab.setImageResource(android.R.drawable.ic_media_play);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    public void                     showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

    public void                     onBackPressed() {
        if ((mFragment.getClass().getName().contains("HostDiscoverySettingsFragmt")) ||
            (HistoricFragment != null && HistoricFragment.isVisible())) {
            mFab.setVisibility(View.GONE);
            mTabs.setVisibility(View.GONE);
            Log.d(TAG, "onBackPressed custom on historic fragment");
            if (HistoricFragment == null || HistoricFragment.onBackPressed()) {
                mTabs.getTabAt(0).select();
            } else {
                Log.d(TAG, "Fragment mode: " + ((FragmentHistoric) HistoricFragment).mActualMode.name());
            }
        } else {
            super.onBackPressed();
        }
    }
}
