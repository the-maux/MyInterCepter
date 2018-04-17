package fr.dao.app.View.Activity.HostDiscovery;

import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Network.Discovery.NetworkDiscoveryControler;
import fr.dao.app.Core.Network.NetDiscovering;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.R;
import fr.dao.app.View.Activity.SPY.SpyActivity;
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
    private AppBarLayout            appBarLayout;
    private TextView                mBottomMonitor, mTimer;
    private int                     mProgress = 0;
    private ImageView               mSettingsMenu, mHistory;
    private SearchView              mSearchView;
    private Toolbar                 mToolbar;
    private TransitionDrawable      mToolbarBackground;
    private ProgressBar             mProgressBar;
    private MyFragment              HistoricFragment = null, NetDiscoveryFragment = null;
    private MyFragment              mFragment = null;
    public int                      MAXIMUM_PROGRESS = 100;
    public Network                  actualNetwork;
    public Date                     date;
    private Timer                   timer = new Timer();
    FloatingActionButton            mFab;

    public void                     onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostdiscovery);
        initXml();
        try {
            init();
        } catch (Exception e) {
            Log.e(TAG, "Big error in init, closing application");
            showSnackbar("Error in discovery");
            finish();
            e.printStackTrace();
        }
    }

    private void                    initXml() {
        mFab = findViewById(R.id.fab);
        mBottomMonitor = ( findViewById(R.id.Message));
        ViewCompat.setElevation(mBottomMonitor, 2);
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
        mHistory = findViewById(R.id.history);
        mHistory.setOnClickListener(onHistory());
        mSearchView = findViewById(R.id.searchView);
        mToolbar = findViewById(R.id.toolbar2);
        mSettingsMenu = findViewById(R.id.settingsMenu);
        findViewById(R.id.OsImg).setOnClickListener(initTabs());
        mTimer = findViewById(R.id.timer);
        mProgressBar = findViewById(R.id.progressBar);
        mToolbarBackground = (TransitionDrawable)findViewById(R.id.appBarLayout).getBackground();
        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(appBarLayout, 4);
            }
        });
    }

    private void                    init()  {
        NetDiscovering.initNetworkInfo(this);
        if (mSingleton.network == null || mSingleton.network.myIp == null) {
            showSnackbar("You need to be connected to a Network");
            finish();
        } else {
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
        ViewAnimate.FabAnimateReveal(mInstance, mFab);
        //mFab.show();
       // ViewAnimate.setVisibilityToVisibleQuick(mFab);
    }

    private View.OnClickListener    initTabs(){
        return new View.OnClickListener() {
            public void onClick(View view) {
                MyFragment fragment = NetDiscoveryFragment;
                //mFab.setVisibility(View.GONE);
                initFragment(fragment);
                initSearchView();
            }
        };

    }

    private void                    initFabs() {
        mFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mFab.startAnimation(AnimationUtils.loadAnimation(mInstance, R.anim.shake));
                Utils.vibrateDevice(mInstance);
                if (mFragment.getClass().getName().contains("FragmentHostDiscoveryScan") &&
                    ((FragmentHostDiscoveryScan) mFragment).mHostLoaded) {
                    mSingleton.hostList = ((FragmentHostDiscoveryScan) mFragment).getTargetSelectedFromHostList();
                    if (mSingleton.Settings.UltraDebugMode) {
                        Log.d(TAG, "mSingleton.hostList" + mSingleton.hostList);
                        Log.d(TAG, "mSingleton.hostsListSize:" +
                                ((mSingleton.hostList != null) ? mSingleton.hostList.size() : "0"));
                    }
                    mSingleton.actualNetwork = actualNetwork;
                    startActivity(new Intent(mInstance, SpyActivity.class));
                }
                else if (!mFragment.start()) {

                } else if (mSingleton.Settings.UltraDebugMode) {
                    Log.i(TAG, "fragment start false");
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
            monitor += "\n" + mSingleton.network.ssid + " : " + mSingleton.network.gateway;
        } else {
            monitor += "Not Connected";
        }
        if (Singleton.getInstance().network.isConnectedToNetwork())
            mBottomMonitor.setText(monitor);
        else
            mBottomMonitor.setText(mSingleton.network.ssid + ": No connection");
        ViewAnimate.setVisibilityToVisibleQuick(mBottomMonitor);
        ViewAnimate.setVisibilityToVisibleQuick(mTimer);
    }

    public void                     initFragmentSettings() {
        mFragment = new FragmentHostDiscoverySettings();
        initFragment(mFragment);
//        mFab.setVisibility(View.GONE);
        ViewAnimate.FabAnimateHide(mInstance, mFab);
        //mFab.hide();
        mBottomMonitor.setVisibility(View.GONE);
        mToolbarBackground.startTransition(500);
    }

    public void                     initToolbarButton() {
        /*
            typeSetting: 1 == DISCOVERY
                         2 == HISTORIC
                         3 == SETTINGS
         */
        mSettingsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    NetDiscoveryFragment.onSettingsClick((AppBarLayout) findViewById(R.id.appBarLayout), mInstance).show();
                } catch (NoSuchFieldError error) {
                    showSnackbar("BottomSheet is shitty");
                }
            }
        });
    }

    private void                    initSearchView() {
        mFragment.initSearchView(mSearchView, mToolbar);
    }

    private View.OnClickListener    onHistory() {
        return new View.OnClickListener() {
            public void onClick(View view) {
                MyFragment fragment;
                ViewAnimate.setVisibilityToVisibleQuick(mHistory);
                if (HistoricFragment == null)
                    HistoricFragment = new FragmentHistoric();
                fragment = HistoricFragment;
                Bundle args = new Bundle();
                args.putString("mode", FragmentHistoric.DB_HISTORIC);
                fragment.setArguments(args);
                ViewAnimate.setVisibilityToInvisibleQuick(mHistory);
                ViewAnimate.setVisibilityToInvisibleQuick(mSearchView);
                ViewAnimate.FabAnimateHide(mInstance, mFab);
                //ViewAnimate.setVisibilityToGoneQuick(mFab);
                mFab.hide();
                initFragment(fragment);
                initSearchView();
            }
        };
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
                        //ViewAnimate.setVisibilityToVisibleQuick(mFab);
                        //mFab.show();
                        mProgressBar.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    public void                     setMAXIMUM_PROGRESS(int nbrHost) {
        if (nbrHost < 10) {
            Log.d(TAG, "setMAXIMUM_PROGRESS::35 for nbrHost:" + nbrHost);
            mProgressBar.setMax(45);
            MAXIMUM_PROGRESS = 45;
        } else if (nbrHost < 50) {
            Log.d(TAG, "setMAXIMUM_PROGRESS::60 for nbrHost:" + nbrHost);
            mProgressBar.setMax(60);
            MAXIMUM_PROGRESS = 60;
        } else if (nbrHost < 100) {
            Log.d(TAG, "setMAXIMUM_PROGRESS::80 for nbrHost:" + nbrHost);
            mProgressBar.setMax(80);
            MAXIMUM_PROGRESS = 80;
        } else if (nbrHost < 160) {
            Log.d(TAG, "setMAXIMUM_PROGRESS::80 for nbrHost:" + nbrHost);
            mProgressBar.setMax(100);
            MAXIMUM_PROGRESS = 100;
        } else if (nbrHost < 200) {
            Log.d(TAG, "setMAXIMUM_PROGRESS::80 for nbrHost:" + nbrHost);
            mProgressBar.setMax(140);
            MAXIMUM_PROGRESS = 140;
        } else {
            Log.d(TAG, "setMAXIMUM_PROGRESS::80 for nbrHost:" + nbrHost);
            mProgressBar.setMax(160);
            MAXIMUM_PROGRESS = 160;
        }
    }

    public void                     showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

    protected void                  onResume() {
        super.onResume();
        //mFab.show();
//        ViewAnimate.setVisibilityTo:VisibleQuick(mFab);
        if (NetworkDiscoveryControler.over())
            ViewAnimate.FabAnimateReveal(mInstance, mFab);
    }

    protected void                  onPause() {
        ViewAnimate.FabAnimateHide(mInstance, mFab);
        super.onPause();
    }

    public void                     onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            Log.d(TAG, "onBackPressed::" + mFragment.getClass().getName());
            if (mFragment.getClass().getName().contains(FragmentHostDiscoveryScan.class.getName())) {
                finish();
            } else if (mFragment.getClass().getName().contains(FragmentHistoric.class.getName())) {
                if (mFragment.onBackPressed()) {
                    Log.d(TAG, "Fragment historic is over, switching to Netdiscover");
                    ViewAnimate.setVisibilityToVisibleQuick(mHistory, 300);
                    ViewAnimate.setVisibilityToVisibleQuick(mSearchView, 400);
                    //ViewAnimate.setVisibilityToVisibleQuick(mFab, 500);
                    ViewAnimate.FabAnimateReveal(mInstance, mFab);
                    //mFab.show();
                    initFragment(NetDiscoveryFragment);
                    initSearchView();
                }
            } else if (mFragment.getClass().getName().contains(FragmentHostDiscoverySettings.class.getName())){
                mToolbarBackground.reverseTransition(450);
                ViewAnimate.setVisibilityToVisibleQuick(mHistory, 300);
                ViewAnimate.setVisibilityToVisibleQuick(mSearchView, 400);
                ViewAnimate.FabAnimateReveal(mInstance, mFab);
                //mFab.show();
               // ViewAnimate.setVisibilityToVisibleQuick(mFab, 500);
                ViewAnimate.setVisibilityToVisibleQuick(mBottomMonitor, 600);
                getSupportFragmentManager().popBackStackImmediate();
            }
        } else {
            Log.e(TAG, "onBackPressed::NOTHING ON STACK");
            finish();
        }
    }
}