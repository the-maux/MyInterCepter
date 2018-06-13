package fr.dao.app.View.Proxy;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import fr.dao.app.Core.Configuration.MitManager;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Network.Proxy.HTTPProxy;
import fr.dao.app.R;
import fr.dao.app.View.Sniff.ProxyReaderFrgmnt;
import fr.dao.app.View.ZViewController.Activity.MITMActivity;
import fr.dao.app.View.ZViewController.Adapter.HTTProxyAdapter;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Behavior.ViewAnimate;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

//https://danielmiessler.com/study/bettercap/
public class                            ProxyActivity extends MITMActivity {
    private String                      TAG = "ProxyActivity";
    private ProxyActivity               mInstance = this;
    private CoordinatorLayout           mCoordinatorLayout;
    private AppBarLayout                appBarLayout;
    private Toolbar                     mToolbar;
    private SearchView                  mSearchView;
    private ImageButton                 mSettingsBtn;
    private ImageView                   mAction_add_host;
    private TabLayout                   mTabs;
    private RecyclerView                mProxyRV;
    private HTTProxyAdapter             mAdapterDetailWireshark;
    private HTTPProxy                   proxyActivity;
    private Singleton                   mSingleton = Singleton.getInstance();
    private MyFragment                  mFragment;

    public void                         onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spysniff);
        initXml();
        init();
        setToolbarTitle("Proxy", "Not started");
    }

    private void                        init() {
        initFab();
        initTabs();
        initFragment(new ProxyReaderFrgmnt());
        initNavigationBottomBar(PROXY, true);
        proxyActivity = new HTTPProxy(this);
    }

    private void                        initXml() {
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        mToolbar = findViewById(R.id.toolbar2);
       // mSearchView = findViewById(R.id.searchView);
        mAction_add_host = findViewById(R.id.action_add_host);
        mSettingsBtn = findViewById(R.id.history);
        mFab = findViewById(R.id.fab);
        mTabs = findViewById(R.id.tabs);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(findViewById(R.id.toptoolbar2), 4);
                ViewCompat.setElevation(appBarLayout, 4);
            }
        });
    }

    private void                        initFab() {
//        ViewAnimate.setVisibilityToVisibleQuick(mFab);
        ViewAnimate.FabAnimateReveal(mInstance, mFab);
//        mFab.show();
        mFab.setImageResource(R.mipmap.ic_stop);
        if (!mSingleton.isProxyStarted()) {
            mFab.setImageResource(R.drawable.ic_media_play);
        } else {
            mFab.setImageResource(R.mipmap.ic_stop);
        }
        mFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Utils.vibrateDevice(mInstance);
                mFragment.start();
                if (mSingleton.isProxyStarted()) {
                    mFab.setImageResource(R.drawable.ic_media_play);
                } else {
                    mFab.setImageResource(R.mipmap.ic_stop);
                }
                updateNotifications();
            }
        });
    }

    public void                         onProxystopped() {
        setToolbarTitle("Proxy", "Stopped");
    }

    private void                        initTabs() {
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getText().toString()) {
                    case "GLOBAL":
                        //title.setText("GLOBAL");
                        initFragment(new ProxyReaderFrgmnt());
                        break;
                    case "CREDENTIALS":
                        //title.setText("CREDENTIALS");
                        break;
                    case "RESSOURCES":
                        //title.setText("RESSOURCES");
                        break;
                }
            }
            public void onTabUnselected(TabLayout.Tab tab) {}
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void                        initFragment(MyFragment fragment) {
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

    public void                         setToolbarTitle(final String title, final String subtitle){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (title != null)
                        mToolbar.setTitle(title);
                    if (subtitle != null)
                        mToolbar.setSubtitle(subtitle);
                }
            });
        }
    public void                         showSnackbar(String txt, int color) {
        if (color == -1) {
            Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
        }
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, txt, Snackbar.LENGTH_LONG);
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                .setTextColor(color);
        snackbar.show();
    }

    public void                         onBackPressed() {
        super.onBackPressed();
        mSingleton.hostList = mSingleton.savedHostList;
        //TODO:Check if sniffing was loading
        MitManager.getInstance().stopEverything();
    }
}