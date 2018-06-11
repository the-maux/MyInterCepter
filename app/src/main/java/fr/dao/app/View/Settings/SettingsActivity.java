package fr.dao.app.View.Settings;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import fr.dao.app.R;
import fr.dao.app.View.DashBoard.HistoricSavedDataFgmnt;
import fr.dao.app.View.DnsSpoofing.DnsSettingsFrgmnt;
import fr.dao.app.View.HostDiscovery.HostDiscoverySettingsFrgmnt;
import fr.dao.app.View.Sniff.SniffSettingsFrgmnt;
import fr.dao.app.View.SpyMITM.SpyMitmSettingsFrgmnt;
import fr.dao.app.View.WebServer.WebserverSettingsFrgmnt;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

/**
 * TODO: SSLStrip
 *          +
 *          + Version
 *          + Credits
 *          + Github
 *
 *          Comprendre
 *                      pk des fois le tcpdump marche pas (surtout lorsqu'il s'agit de relancer tcpdump)
 *                      ajouter son propre device dans le scan
 *                      faire le saveMyPreviousRecord on startUp
 *
 */
public class                        SettingsActivity extends MyActivity {
    private String                  TAG = "SettingsActivity";
    private MyFragment              mFragment;
    private CoordinatorLayout       mCoordinatorLayout;
    private Toolbar                 mToolbar;
    protected AHBottomNavigation    mBottomBar;
    private int                     mType = 0;
    private AppBarLayout            appBarLayout;
    
    public void                     onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initXml();
        init();
    }

    private void                    initXml() {
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        MyGlideLoader.coordoBackgroundXMM(this, (CoordinatorLayout) findViewById(R.id.coordinatorLayout));
        mToolbar = findViewById(R.id.toolbar2);
        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(appBarLayout, 4);
            }
        });
    }

    private void                    init() {
        initBottomBar(0, true);
        initFragment(null);
    }

    protected void                  initBottomBar(int position, boolean useCallback) {
// Create items
        mBottomBar = findViewById(R.id.navigationSettings);
        if (useCallback) {
            mBottomBar.clearAnimation();
            mBottomBar.removeAllItems();
            Log.d(TAG, "initNavigationBottomBarSettings(" + position + ":" + useCallback + ")");
            AHBottomNavigationItem[] bottomItems = new AHBottomNavigationItem[5];
            bottomItems[0] = new AHBottomNavigationItem(R.string.SCANNER,
                    R.drawable.ic_fingerprint_svg, R.color.settingsPrimary);
            bottomItems[1] = new AHBottomNavigationItem(R.string.TRACKER,
                    R.drawable.spy, R.color.spyPrimary);
            bottomItems[2] = new AHBottomNavigationItem(R.string.SNIFFER,
                    R.drawable.ic_sniff_barbutton, R.color.snifferPrimary);
            bottomItems[3] = new AHBottomNavigationItem(R.string.DNS_SPOOFER,
                    R.drawable.ic_dns_btnbar, R.color.dnsSpoofPrimary);
            bottomItems[4] = new AHBottomNavigationItem(R.string.WEB_SPOOFER,
                    R.drawable.ic_world_locked, R.color.webserverSpoofPrimary);
            for (AHBottomNavigationItem bottomItem : bottomItems) {
                mBottomBar.addItem(bottomItem);
            }
            mBottomBar.setDefaultBackgroundColor(Color.parseColor("#414141"));
            mBottomBar.setBehaviorTranslationEnabled(true);
            mBottomBar.setAccentColor(R.color.generic_background_dark);
            mBottomBar.setInactiveColor(Color.parseColor("#747474"));
            mBottomBar.setForceTint(true);
            mBottomBar.setTranslucentNavigationEnabled(true);
            mBottomBar.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
            mBottomBar.setColored(true);
            mBottomBar.setOnTabSelectedListener(onBottomBarSelectedItem());
        }
    }
    private AHBottomNavigation.OnTabSelectedListener onBottomBarSelectedItem() {
        return new AHBottomNavigation.OnTabSelectedListener() {
            public boolean onTabSelected(final int position, boolean wasSelected) {
                if (position != mType) {
                    mType = position;
                    //prevent reselection
                    mBottomBar.post(new Runnable() {
                        @Override
                        public void run() {
                            mInstance.runOnUiThread(new Runnable() {
                                public void run() {
                                    MyFragment fragment = null;
                                    Log.d(TAG, "position:" + position);
                                    switch (position) {
                                        case 0:
                                            fragment = new HostDiscoverySettingsFrgmnt();
                                            appBarLayout.setBackgroundColor(ContextCompat.getColor(mInstance, R.color.settingsPrimary));
                                            break;
                                        case 1:
                                            fragment = new SpyMitmSettingsFrgmnt();
                                            appBarLayout.setBackgroundColor(ContextCompat.getColor(mInstance, R.color.spyPrimary));
                                            break;
                                        case 2:
                                            fragment = new SniffSettingsFrgmnt();
                                            appBarLayout.setBackgroundColor(ContextCompat.getColor(mInstance, R.color.snifferPrimary));
                                            break;
                                        case 3:
                                            fragment = new DnsSettingsFrgmnt();
                                            appBarLayout.setBackgroundColor(ContextCompat.getColor(mInstance, R.color.dnsSpoofPrimary));
                                            break;
                                        case 4:
                                            fragment = new WebserverSettingsFrgmnt();
                                            appBarLayout.setBackgroundColor(ContextCompat.getColor(mInstance, R.color.webserverSpoofPrimary));
                                            break;
                                        default:
                                            fragment = new SettingsFrgmnt();
                                            appBarLayout.setBackgroundColor(ContextCompat.getColor(mInstance, R.color.dnsSpoofPrimary));
                                            break;
                                    }
                                    initFragment(fragment);
                                }
                            });
                        }
                    });
                    return true;
                }
                return false;
            }
        };
    }

    private void                    initFragment(MyFragment fragment) {
        try {
            if (fragment == null)
                fragment = new HostDiscoverySettingsFrgmnt();
            mFragment = fragment;
            Bundle args = new Bundle();
            args.putString("mode", HistoricSavedDataFgmnt.HOST_HISTORIC);
            mFragment.setArguments(args);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, mFragment)
                    .commit();
        } catch (IllegalStateException e) {
            Log.w("Error MainActivity", "FragmentStack or FragmentManager corrupted");
            showSnackbar("Error in fragment");
            super.onBackPressed();
        }
    }

    public void                     showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
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

}
