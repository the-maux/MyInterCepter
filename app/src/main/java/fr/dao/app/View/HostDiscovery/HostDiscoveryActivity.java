package fr.dao.app.View.HostDiscovery;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ImageViewCompat;
import android.support.v7.app.AlertDialog;
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
import fr.dao.app.View.ZViewController.Dialog.QuestionDialog;
import fr.dao.app.View.ZViewController.Dialog.QuestionMultipleAnswerDialog;
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
        mTimer = findViewById(R.id.timer);
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
            initToolbarIcon((ImageView)findViewById(R.id.OsImg));
            mFragment = new HostDiscoveryScanFrgmnt();
            NetDiscoveryFragment = mFragment;
            initFragment(NetDiscoveryFragment);
            initSearchView();
        }
    }

    private void                    initToolbarIcon(final ImageView viewById) {
        viewById.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        final CharSequence[] items = new CharSequence[]{"Discrete", "Basic", "Advanced", "Brutal"};
                        new QuestionMultipleAnswerDialog(mInstance, items,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                        Log.d("SettingsDiscovery", "Type of scan: " + items[selectedPosition]);
                                        Singleton.getInstance().Settings.getUserPreferences().NmapMode = selectedPosition;
                                        Singleton.getInstance().Settings.dump(Singleton.getInstance().Settings.getUserPreferences());
                                        updateScanColor(viewById);
                                        new QuestionDialog(mInstance)
                                                .setTitle("Relancer le scan ?")
                                                .setText("Vous avez changé les parametres, voulez vous restart le scan ?")
                                                .onPositiveButton("Oui", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        mFragment.start();
                                                    }
                                                })
                                                .show();
                                    }
                                }, "Type of NetworkInformation discovery", mSingleton.Settings.getUserPreferences().NmapMode, R.drawable.target);
                    }
                });
            }
        });
        updateScanColor(viewById);
    }

    private void                    updateScanColor(final ImageView viewById) {
        runOnUiThread(new Runnable() {
            public void run() {
                int res = ContextCompat.getColor(mInstance, R.color.white_secondary);
                switch (mSingleton.Settings.getUserPreferences().NmapMode) {
                    case 0:
                        res = ContextCompat.getColor(mInstance, R.color.green);
                        break;
                    case 1:
                        res = ContextCompat.getColor(mInstance, R.color.white_secondary);
                        break;
                    case 2:
                        res = ContextCompat.getColor(mInstance, R.color.filtered_color);
                        break;
                    case 3:
                        res = ContextCompat.getColor(mInstance, R.color.material_red_500);
                        break;
                    case 4:
                        res = ContextCompat.getColor(mInstance, R.color.material_red_900);
                        break;
                }
                ImageViewCompat.setImageTintList(viewById, ColorStateList.valueOf(res));
            }
        });
    }

    public void                     onScanStarted() {
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
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        ViewAnimate.FabAnimateHide(mInstance, mFab);
    }

    public void                     onScanOver() {
        if (timer != null) {
            timer.cancel();
            ViewAnimate.setVisibilityToGoneLong(mTimer);
            timer = null;
        }
        ViewAnimate.setVisibilityToGoneQuick(findViewById(R.id.progressBar));
        ViewAnimate.reveal(mInstance, mFab);
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
        ViewAnimate.reveal(this, mBottomMonitor, new Runnable() {
            public void run() {
                ViewAnimate.setVisibilityToVisibleQuick(mTimer);
            }
        });
    }

    public void                     initFragmentSettings() {
        mFragment = new HostDiscoverySettingsFrgmnt();
        initFragment(mFragment);
        ViewAnimate.FabAnimateHide(mInstance, mFab);
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

    public void                     showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

    protected void                  onPostResume() {
        super.onPostResume();
        if (NetworkDiscoveryControler.over()) {
            //ViewAnimate.reveal(mInstance, mFab);
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
                    ViewAnimate.reveal(mInstance, mFab);
                    //mFab.show();
                    initFragment(NetDiscoveryFragment);
                    initSearchView();
                }
            } else if (mFragment.getClass().getName().contains(HostDiscoverySettingsFrgmnt.class.getName())){
                mToolbarBackground.reverseTransition(450);
                ViewAnimate.setVisibilityToVisibleQuick(mOsFilter, 300);
                ViewAnimate.setVisibilityToVisibleQuick(mSearchView, 400);
                ViewAnimate.reveal(mInstance, mFab);
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