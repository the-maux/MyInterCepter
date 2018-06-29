package fr.dao.app.View.HostDiscovery;

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
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import fr.dao.app.Core.Configuration.MitManager;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Network.Discovery.NetworkDiscoveryControler;
import fr.dao.app.Core.Network.NetDiscovering;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.R;
import fr.dao.app.View.DashBoard.HistoricSavedDataFgmnt;
import fr.dao.app.View.Proxy.ProxyActivity;
import fr.dao.app.View.Startup.HomeActivity;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Behavior.ViewAnimate;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

/**
 * TODO:    + Add manual target
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
    private ImageView               mSettingsMenu, mOsFilter;
    private SearchView              mSearchView;
    private Toolbar                 mToolbar;
    private TransitionDrawable      mToolbarBackground;
    private MyFragment              NetDiscoveryFragment = null, mFragment = null;
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
        mOsFilter = findViewById(R.id.OsFilter);
        mOsFilter.setOnClickListener(onOsFilter());
        mSearchView = findViewById(R.id.searchView);
        mToolbar = findViewById(R.id.toolbar2);
        mSettingsMenu = findViewById(R.id.toolbarSettings);
        findViewById(R.id.OsImg).setOnClickListener(initTabs());
        mTimer = findViewById(R.id.timer);
        mProgressBar = findViewById(R.id.progressBar);
        appBarLayout = findViewById(R.id.appBar);
        mToolbarBackground = (TransitionDrawable)(appBarLayout).getBackground();

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(appBarLayout, 4);
            }
        });
        mFab.setImageResource(R.drawable.ic_media_play);
        setStatusBarColor(R.color.generic_background);
    }

    private void                    init()  {
        NetDiscovering.initNetworkInfo(this);
        if (mSingleton.NetworkInformation == null || mSingleton.NetworkInformation.myIp == null) {
            showSnackbar("You need to be connected to a NetworkInformation");
            onBackPressed();
        } else {
            initFabs();
            initMonitor();
            mFragment = new HostDiscoveryScanFrgmnt();
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
       // ViewAnimate.setVisibilityToGoneLong(mBottomMonitor);
        ViewAnimate.FabAnimateHide(this, mBottomMonitor, null);
        ViewAnimate.FabAnimateReveal(mInstance, mFab);
        //mFab.show();
       // ViewAnimate.setVisibilityToVisibleQuick(mFab);
        Log.d(TAG, "onScanOver");
    }

    private View.OnClickListener    initTabs(){
        return new View.OnClickListener() {
            public void onClick(View view) {
                Utils.vibrateDevice(mInstance, 100);
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
                Log.i(TAG, "FabClicked::"+mFragment.getClass().getName());
                mFab.startAnimation(AnimationUtils.loadAnimation(mInstance, R.anim.shake));
                Utils.vibrateDevice(mInstance);
                if (mFragment.getClass().getName().contains("HostDiscoveryScanFrgmnt") &&
                    ((HostDiscoveryScanFrgmnt) mFragment).mHostLoaded) {
                    mSingleton.savedHostList = mSingleton.hostList;
                    mSingleton.hostList = ((HostDiscoveryScanFrgmnt) mFragment).getTargetSelectedFromHostList();
                    if (mSingleton.hostList == null) {
                        Log.e(TAG, "HostList is null");
                        mSingleton.hostList = mSingleton.savedHostList;
                        return;
                    }
                    if (mSingleton.Settings.UltraDebugMode) {
                        Log.d(TAG, "mSingleton.hostList" + mSingleton.hostList);
                        Log.d(TAG, "mSingleton.hostsListSize:" +
                                ((mSingleton.hostList != null) ? mSingleton.hostList.size() : "0"));
                    }
                    MitManager.getInstance().loadHost(mSingleton.hostList);
                    startActivity(new Intent(mInstance, ProxyActivity.class));
                }
                else if (!mFragment.start()) {
                    Log.i(TAG, "fragment start false");
                } else
                    Log.i(TAG, "FabClicked but no action");
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
            monitor += " Ip Address : " + mSingleton.NetworkInformation.myIp;
            monitor += "\n" + mSingleton.NetworkInformation.ssid + " : " + mSingleton.NetworkInformation.gateway;
        } else {
            monitor += "Not Connected";
        }
        if (Singleton.getInstance().NetworkInformation.isConnectedToNetwork())
            mBottomMonitor.setText(monitor);
        else
            mBottomMonitor.setText(mSingleton.NetworkInformation.ssid + ": No connection");
        //ViewAnimate.setVisibilityToVisibleQuick(mBottomMonitor);
        ViewAnimate.FabAnimateReveal(this, mBottomMonitor, new Runnable() {
            public void run() {
                ViewAnimate.setVisibilityToVisibleQuick(mTimer);
            }
        });
    }

    public void                     initFragmentSettings() {
        mFragment = new HostDiscoverySettingsFrgmnt();
        initFragment(mFragment);
//        mFab.setVisibility(View.GONE);
        ViewAnimate.FabAnimateHide(mInstance, mFab);
        //mFab.hide();
        mBottomMonitor.setVisibility(View.GONE);
        mToolbarBackground.setCrossFadeEnabled(true);
        mToolbarBackground.startTransition(500);
    }

    public void                     initToolbarButton() {
        mSettingsMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Utils.vibrateDevice(mInstance, 100);
                    NetDiscoveryFragment.onSettingsClick((AppBarLayout) findViewById(R.id.appBar), mInstance).show();
                } catch (Exception error) {
                    showSnackbar("BottomSheet is shitty");
                }
            }
        });
    }

    private void                    initSearchView() {
        mFragment.initSearchView(mSearchView, mToolbar);
    }

    private View.OnClickListener    onOsFilter() {
        return new View.OnClickListener() {
            public void onClick(View view) {
                if (mFragment.getClass().getName().contains(HostDiscoveryScanFrgmnt.class.getName())) {
                    ((HostDiscoveryScanFrgmnt)mFragment).osFilterDialog();
                }
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
            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                mToolbar.setBackgroundColor(color);
            }

            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void                     progressAnimation() {
        mProgressBar.setVisibility(View.VISIBLE);
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

    protected void                  onPostResume() {
        super.onPostResume();
        if (NetworkDiscoveryControler.over()) {
            //ViewAnimate.FabAnimateReveal(mInstance, mFab);
            ViewAnimate.setVisibilityToVisibleQuick(mFab);
            mFab.show();
            Log.d(TAG, "Scan is over so reveal FAB& opaque:"+mFab.isOpaque());
            Log.d(TAG, "dirty:"+mFab.isDirty());
            mFab.clearColorFilter();
            mFab.clearAnimation();
        } else
            Log.i(TAG, "not showing FAB cause no !mInstance.inLoading of NetworkDiscoveryControler");
//        mFragment = new HostDiscoveryScanFrgmnt();
        initFragment(mFragment);
    }

    protected void                  onPause() {
        ViewAnimate.FabAnimateHide(mInstance, mFab);
        super.onPause();
    }

    public void                     onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            Log.d(TAG, "onBackPressed::" + mFragment.getClass().getName());
            if (mFragment.getClass().getName().contains(HostDiscoveryScanFrgmnt.class.getName())) {
                startActivity(new Intent(mInstance, HomeActivity.class));
            } else if (mFragment.getClass().getName().contains(HistoricSavedDataFgmnt.class.getName())) {
                if (mFragment.onBackPressed()) {
                    Log.d(TAG, "Fragment historic is over, switching to Netdiscover");
                    ViewAnimate.setVisibilityToVisibleQuick(mOsFilter, 300);
                    ViewAnimate.setVisibilityToVisibleQuick(mSearchView, 400);
                    //ViewAnimate.setVisibilityToVisibleQuick(mFab, 500);
                    ViewAnimate.FabAnimateReveal(mInstance, mFab);
                    //mFab.show();
                    initFragment(NetDiscoveryFragment);
                    initSearchView();
                }
            } else if (mFragment.getClass().getName().contains(HostDiscoverySettingsFrgmnt.class.getName())){
                mToolbarBackground.reverseTransition(450);
                ViewAnimate.setVisibilityToVisibleQuick(mOsFilter, 300);
                ViewAnimate.setVisibilityToVisibleQuick(mSearchView, 400);
                ViewAnimate.FabAnimateReveal(mInstance, mFab);
                //mFab.show();
               // ViewAnimate.setVisibilityToVisibleQuick(mFab, 500);
                getSupportFragmentManager().popBackStackImmediate();
                mFragment = NetDiscoveryFragment;
            }
        } else {
            Log.d(TAG, "Backked");
            super.onBackPressed();
            //Pair<View, String> p1 = Pair.create(findViewById(R.id.OsImg), "attackIcon");
            //startActivity(new Intent(mInstance, HomeActivity.class),  ActivityOptionsCompat.makeSceneTransitionAnimation(mInstance, p1).toBundle());
        }
    }
}