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
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.Core.BinaryWrapper.Intercepter;
import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Misc.MyActivity;
import fr.allycs.app.Controller.Misc.MyFragment;
import fr.allycs.app.Controller.Misc.MyGlideLoader;
import fr.allycs.app.Controller.Misc.Utils;
import fr.allycs.app.Controller.Network.Discovery.HostDiscoveryScan;
import fr.allycs.app.Model.Net.Service;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.R;
import fr.allycs.app.View.MenuActivity;

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
    public CoordinatorLayout        mCoordinatorLayout;
    private AppBarLayout            mAppbar;
    private FloatingActionButton    mFab;
    private TextView                mBottomMonitor;
    private int                     mProgress = 0;
    private ImageButton             mAddHostBtn, mSettingsBtn;
    private SearchView              mSearchView;
    private Toolbar                 mToolbar;
    private TabLayout               mTabs;
    private ProgressBar             mProgressBar;
    final int                       MAXIMUM_PROGRESS = 6500;
    public Session                  mActualSession;
    private MyFragment mFragment;
    public HostDiscoveryScan.typeScan typeScan = HostDiscoveryScan.typeScan.Arp;

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
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setUseCompatPadding(true);
        mFab.setSoundEffectsEnabled(true);
        mAppbar = (AppBarLayout) findViewById(R.id.appbar);
        mBottomMonitor = ((TextView) findViewById(R.id.Message));
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        MyGlideLoader.coordoBackground(this, mCoordinatorLayout);
        mAddHostBtn = (ImageButton) findViewById(R.id.action_add_host);
        mSettingsBtn = (ImageButton) findViewById(R.id.settings);
        mSearchView = (SearchView) findViewById(R.id.searchView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar2);
        mTabs = (TabLayout) findViewById(R.id.tabs);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        initFabs();
    }

    private void                    initFragment(MyFragment fragment) {
        try {
            mFragment = fragment;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, mFragment)
                    .commit();
            mFragment.init();
        } catch (IllegalStateException e) {
            Snackbar.make(findViewById(R.id.Coordonitor), "Error in fragment", Snackbar.LENGTH_LONG).show();
            e.getStackTrace();
            super.onBackPressed();
        }
    }

    private void                    init()  throws Exception {
        if (mSingleton.network == null || mSingleton.network.myIp == null) {
            showSnackbar("You need to be connected to a network");
            finish();
        } else {
            Intercepter.initCepter(mSingleton.network.mac);
            initMonitor();
            initTabs();
            initFragment(new FragmentHostDiscoveryScan());
            initSearchView();
        }
        //initToolbarButton();
    }

    private void                    initFabs() {
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.vibrateDevice(mInstance);
                mFab.startAnimation(AnimationUtils.loadAnimation(mInstance, R.anim.shake));
                //if () TODO: CHECK POUR LE MODE HISTORIC
                if (!mFragment.start()) {
                    try {
                        launchMenu();
                    } catch (IOException e) {
                        Log.e(TAG, "Error in start attack");
                        e.getStackTrace();
                    }
                }
            }
        });
    }

    private void                    initTabs() {
        /*
            TODO: Build the click to switchToNewFragment
         */
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "tab.getHost().toString():" + tab.getText().toString());
                switch (tab.getText().toString()) {
                    case "arp\nDiscovery":
                        Log.d(TAG, "Nmap Tab");
                        typeScan = HostDiscoveryScan.typeScan.Nmap;
                        mFragment.start();
                        break;
                    case "Icmp\ndiscovery":
                        Log.d(TAG, "ARP TAB");
                        typeScan = HostDiscoveryScan.typeScan.Arp;
                        break;
                    case "Services\nDiscovery":
                        Log.d(TAG, "Service TAB");
                        typeScan = HostDiscoveryScan.typeScan.Services;
                        break;

                }
                initSearchView();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    public void                     initMonitor() {
        String monitor = "";
        if (!monitor.contains("WiFi")) {
            monitor += " Ip Adress : " + mSingleton.network.myIp;
            monitor += "\n" + mSingleton.network.Ssid + " : " + mSingleton.network.gateway;
        } else {
            monitor += "Not Connected";
        }
        if (Singleton.getInstance().network.isConnectedToNetwork())
            mBottomMonitor.setText(monitor);
        else
            mBottomMonitor.setText(mSingleton.network.Ssid + ": No connection");
    }

    public void                     initToolbarButton() {
        final BottomSheetMenuDialog bottomSheet;
        switch (typeScan) {
            case Arp:
                bottomSheet = mFragment.onSettingsClick(mAppbar, this);
                break;
//            case Nmap:
//                bottomSheet = mFragment.onSettingsClick();
//                break;
//            case Services:
//                bottomSheet = mFragment.onSettingsClick();
//                break;
            default:
                bottomSheet = mFragment.onSettingsClick(mAppbar, this);
                break;
        }
        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "showing settings");
                bottomSheet.show();
            }
        });
        mFragment.onAddButtonClick(mAddHostBtn);
    }

    private void                    initSearchView() {
        mFragment.initSearchView(mSearchView);
    }

    public void                     setProgressState(int progress){
        mProgressBar.setVisibility(View.VISIBLE);
        if (progress != -1)
            this.mProgress = progress;
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

    private ArrayList<Host>         extractAndDumpSelectedHost(ArrayList<Host> hostList) {
        ArrayList<Host> selectedHost = new ArrayList<>();
        try {
            boolean noTargetSelected = true;
            FileOutputStream out = openFileOutput("targets", 0);
            for (Host host : hostList) {
                if (host.selected) {
                    selectedHost.add(host);
                    noTargetSelected = false;
                    String dumpHost = host.ip + ":" + host.mac + "\n";
                    out.write(dumpHost.getBytes());
                }
            }
            out.close();
            if (noTargetSelected) {
                showSnackbar("No target selected!");
                return null;
            }
            return selectedHost;
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
    }

    private void                    launchMenu() throws IOException {
        ArrayList<Host> selectedHost = mSingleton.hostsList;
        mSingleton.hostsList = extractAndDumpSelectedHost(selectedHost);
        if (selectedHost != null && !selectedHost.isEmpty()) {
            mSingleton.actualSession = mActualSession;
            startActivity(new Intent(mInstance, MenuActivity.class));
        }
    }

    public void                     notifiyServiceAllScaned(List<Service> listOfServiceFound) {
        showSnackbar("Scanning service on network finished");
        //TODO: Need to refacto this on ManyToMany link
        mActualSession.services = listOfServiceFound;
        mActualSession.save();
    }

    public void                     showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

}
