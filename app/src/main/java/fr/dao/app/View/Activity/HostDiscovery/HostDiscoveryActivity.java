package fr.dao.app.View.Activity.HostDiscovery;

import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
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

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Network.NetDiscovering;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.R;
import fr.dao.app.View.Activity.Scan.NmapActivity;
import fr.dao.app.View.Behavior.Activity.MyActivity;
import fr.dao.app.View.Behavior.Fragment.MyFragment;
import fr.dao.app.View.Behavior.MyGlideLoader;
import fr.dao.app.View.Behavior.ViewAnimate;

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
    private TextView                mBottomMonitor, mTimer;
    private int                     mProgress = 0;
    private ImageButton             mAddHostBtn, mSettingsBtn;
    private SearchView              mSearchView;
    private Toolbar                 mToolbar;
    private TabLayout               mTabs;
    private TransitionDrawable      mToolbarBackground;
    private ProgressBar             mProgressBar;
    private MyFragment              HistoricFragment = null, NetDiscoveryFragment = null;
    private MyFragment              mFragment = null, mLastFragment = null;
    public int                      MAXIMUM_PROGRESS = 100, MAX_TIME_ONE_HOST = 1;
    public Network actualNetwork;
    public Date                     date;
    private Timer                   timer = new Timer();

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
        mSettingsBtn = findViewById(R.id.script);
        mSearchView = findViewById(R.id.searchView);
        mToolbar = findViewById(R.id.toolbar2);
        mTabs = findViewById(R.id.tabs);
        mTimer = findViewById(R.id.timer);
        mProgressBar = findViewById(R.id.progressBar);
        mToolbarBackground = (TransitionDrawable)(findViewById(R.id.topToolbar)).getBackground();
    }

    private void                    init()  {
        NetDiscovering.initNetworkInfo(this);
        if (mSingleton.network == null || mSingleton.network.myIp == null) {
            showSnackbar("You need to be connected to a Network");
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

    public void                     initTimer() {
        class UpdateTimer extends TimerTask {
            private Date start = Calendar.getInstance().getTime();
            public void run() {
                mInstance.runOnUiThread(new Runnable() {
                    public void run() {
                        mTimer.setText(Utils.TimeDifference(start));
                    }
                });
            }
        }
        if (timer == null)
            timer = new Timer();
        timer.scheduleAtFixedRate(new UpdateTimer(), 0, 1000);
    }

    public void                     onScanOver() {
        if (timer != null) {
            timer.cancel();
            ViewAnimate.setVisibilityToGoneLong(mTimer);
            timer = null;
        }
        ViewAnimate.setVisibilityToGoneLong(mBottomMonitor);
        ViewAnimate.setVisibilityToVisibleQuick(mFab);
    }

    private void                    initTabs(){
        final String                ARP_TAB_NAME = "Devices\nDiscovery",
                                    HISTORIC_TAB_NAME = "Audit\nHistoric";
        mTabs.addTab(mTabs.newTab().setText(ARP_TAB_NAME), 0);
        mTabs.addTab(mTabs.newTab().setText(HISTORIC_TAB_NAME), 1);
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                MyFragment fragment;
                switch (tab.getText().toString()) {
                    case ARP_TAB_NAME:
                        fragment = NetDiscoveryFragment;
                        break;
                    case HISTORIC_TAB_NAME:
                        if (HistoricFragment == null)
                            HistoricFragment = new FragmentHostDiscoveryHistoric();
                        fragment = HistoricFragment;
                        Bundle args = new Bundle();
                        args.putString("mode", FragmentHostDiscoveryHistoric.DB_HISTORIC);
                        fragment.setArguments(args);
                        break;
                    default:
                        return ;
                }
                mFab.setVisibility(View.GONE);
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
                if (mFragment.getClass().getName().contains("FragmentHostDiscoveryScan") &&
                    ((FragmentHostDiscoveryScan) mFragment).mHostLoaded) {
                    mSingleton.selectedHostsList = ((FragmentHostDiscoveryScan) mFragment).getTargetSelectedFromHostList();
                    if (mSingleton.UltraDebugMode) {
                        Log.d(TAG, "mSingleton.selectedHostsList" + mSingleton.selectedHostsList);
                        Log.d(TAG, "mSingleton.hostsListSize:" +
                                ((mSingleton.selectedHostsList != null) ? mSingleton.selectedHostsList.size() : "0"));
                    }
                    mSingleton.actualNetwork = actualNetwork;
                    startActivity(new Intent(mInstance, NmapActivity.class));
                }
                else if (!mFragment.start()) {

                } else if (mSingleton.UltraDebugMode) {
                    Log.i(TAG, "fragment start false");
                }
            }
        });
    }

    private void                    initFragment(MyFragment fragment) {
        try {
            mLastFragment = mFragment;
            mFragment = fragment;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, mFragment)
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
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

    public void                     initFragmentSettings() {
        mFragment = new FragmentHostDiscoverySettings();
        initFragment(mFragment);
        mFab.setVisibility(View.GONE);
        mTabs.setVisibility(View.GONE);
        mBottomMonitor.setVisibility(View.GONE);
        mToolbarBackground.startTransition(500);
    }

    public void                     initToolbarButton() {
        /*
            typeSetting: 1 == DISCOVERY
                         2 == HISTORIC
                         3 == SETTINGS
         */
        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NetDiscoveryFragment.onSettingsClick((AppBarLayout) findViewById(R.id.appbar), mInstance).show();
            }
        });
        mFragment.onAddButtonClick(mAddHostBtn);
    }

    private void                    initSearchView() {
        mFragment.initSearchView(mSearchView);
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

    public void                     setProgressState(final int progress){
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
                if (progress != -1) {
                    if (progress >= MAXIMUM_PROGRESS)
                        mProgressBar.setVisibility(View.GONE);
                    mProgress = progress;
                }
            }
        });
    }

    public void                     progressAnimation() {
        mProgressBar.setVisibility(View.VISIBLE);
        mFab.setImageResource(R.drawable.ic_loop_search);
        mProgressBar.setProgress(0);
        mProgressBar.setMax(MAXIMUM_PROGRESS);
        new Thread(new Runnable() {
            public void run() {
                mProgress = 0;
                while (mProgress <= (MAXIMUM_PROGRESS)) {//1 tour == 0,8s == 1HOST
                    try {
                        Thread.sleep(500);
                        mProgress += 1;
                        if (mProgress >= MAXIMUM_PROGRESS)
                            mProgress -= MAXIMUM_PROGRESS / 8;
                        final int prog2 = mProgress;
                        mInstance.runOnUiThread(new Runnable() {
                            public void run() {
                                mProgressBar.setProgress(prog2);
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mInstance.runOnUiThread(new Runnable() {
                    public void run() {
                        mFab.setImageResource(R.drawable.ic_media_play);
                        ViewAnimate.setVisibilityToVisibleQuick(mFab);
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    public void                     setMAXIMUM_PROGRESS(int nbrHost) {
        int icmpLoopTime = 4;
        Log.d(TAG, "MAXIMUM PROGRESS SET : [" + icmpLoopTime + (nbrHost) + "] estimation[" + icmpLoopTime + (nbrHost) * 400 / 1000+ "s]");
        if (nbrHost < 10) {
            mProgressBar.setMax(50);
            MAXIMUM_PROGRESS = 50;
        } else if (nbrHost < 50) {
            mProgressBar.setMax(180);
            MAXIMUM_PROGRESS = 180;
        } else {
            mProgressBar.setMax(240);
            MAXIMUM_PROGRESS = 240;
        }
    }

    public void                     showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

    public void                     onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            Log.d(TAG, "onBackPressed::" + mFragment.getClass().getName());
            if (mFragment.getClass().getName().contains(FragmentHostDiscoveryScan.class.getName())) {
                finish();
            } else if (mFragment.getClass().getName().contains(FragmentHostDiscoveryHistoric.class.getName())) {
                if (mFragment.onBackPressed())
                    mTabs.getTabAt(0).select();
            } else if (mFragment.getClass().getName().contains(FragmentHostDiscoverySettings.class.getName())){
                mToolbarBackground.reverseTransition(700);
                mFab.setVisibility(View.VISIBLE);
                mTabs.setVisibility(View.VISIBLE);
                getSupportFragmentManager().popBackStackImmediate();
                mBottomMonitor.setVisibility(View.VISIBLE);
            }
        } else {
            Log.e(TAG, "onBackPressed::NOTHING ON STACK");
            finish();
        }
    }
}