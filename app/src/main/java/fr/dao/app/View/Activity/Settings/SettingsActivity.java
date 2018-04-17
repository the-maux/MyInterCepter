package fr.dao.app.View.Activity.Settings;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import fr.dao.app.R;
import fr.dao.app.View.Activity.DnsSpoofing.DnsActivity;
import fr.dao.app.View.Activity.HostDiscovery.FragmentHistoric;
import fr.dao.app.View.Activity.HostDiscovery.FragmentHostDiscoverySettings;
import fr.dao.app.View.Activity.SPY.SpyActivity;
import fr.dao.app.View.Activity.WebServer.WebServerActivity;
import fr.dao.app.View.Activity.Wireshark.WiresharkActivity;
import fr.dao.app.View.Behavior.Activity.MyActivity;
import fr.dao.app.View.Behavior.Fragment.MyFragment;
import fr.dao.app.View.Behavior.MyGlideLoader;
import fr.dao.app.View.Behavior.ViewAnimate;

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
public class                    SettingsActivity extends MyActivity {
    private String              TAG = "SettingsActivity";
    private MyFragment          mFragment;
    private CoordinatorLayout   mCoordinatorLayout;
    protected AHBottomNavigation    mBottomBar;
    private int                     mType;
    public static final String  GLOBAL_SETTINGS = "GLOBAL";
    public static final String  HOSTDISCOVERY_SETTINGS = "HOSTDISCOVERY";
    public static final String  WIRESHARK_SETTINGS = "WIRESHARK";
    public static final String  DNSMASQ_SETTINGS = "DNSMASQ";
    public static final String  DORA_SETTINGS = "DORA";
    public static final String  WEBSERVER_SETTINGS = "WEBSERVER";
    public static final String  DATABASE_SETTINGS = "DATABASE";
    
    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initXml();
        init();
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        MyGlideLoader.coordoBackgroundXMM(this, (CoordinatorLayout) findViewById(R.id.coordinatorLayout));

    }

    protected void              initBottomBar(int position, boolean useCallback) {
// Create items
        mBottomBar = findViewById(R.id.navigation);
        if (useCallback) {
            mBottomBar.clearAnimation();
            mBottomBar.removeAllItems();
            Log.d(TAG, "initNavigationBottomBarSettings(" + position + ":" + useCallback + ")");
            AHBottomNavigationItem[] bottomItems = new AHBottomNavigationItem[5];
            bottomItems[0] = new AHBottomNavigationItem(R.string.SCANNER,
                    R.drawable.ic_fingerprint_svg, R.color.spyPrimary);
            bottomItems[1] = new AHBottomNavigationItem(R.string.SCANNER,
                    R.drawable.spy, R.color.spyPrimary);
            bottomItems[2] = new AHBottomNavigationItem(R.string.SNIFFER,
                    R.drawable.ic_sniff_barbutton, R.color.wiresharkPrimary);
            bottomItems[3] = new AHBottomNavigationItem(R.string.DNS_SPOOFER,
                    R.drawable.ic_dns_btnbar, R.color.dnsSpoofPrimary);
            bottomItems[4] = new AHBottomNavigationItem(R.string.WEB_SPOOFER,
                    R.drawable.ic_world_locked, R.color.webserverSpoofPrimary);
            for (AHBottomNavigationItem bottomItem : bottomItems) {
                mBottomBar.addItem(bottomItem);
            }
            mBottomBar.setDefaultBackgroundColor(Color.parseColor("#414141"));
            mBottomBar.setBehaviorTranslationEnabled(true);
            mBottomBar.setAccentColor(R.color.accent);
            mBottomBar.setInactiveColor(Color.parseColor("#747474"));
            mBottomBar.setForceTint(true);
            mBottomBar.setTranslucentNavigationEnabled(true);
            mBottomBar.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
            mBottomBar.setColored(true);
            mBottomBar.setOnTabSelectedListener(onSelectedListener());
        }
    }
    private AHBottomNavigation.OnTabSelectedListener onSelectedListener() {
        return new AHBottomNavigation.OnTabSelectedListener() {
            public boolean onTabSelected(final int position, boolean wasSelected) {
                if (position != mType) {//prevent reselection
                    mBottomBar.post(new Runnable() {
                        @Override
                        public void run() {
                            mInstance.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Log.d(TAG, "onNavigationItemSelected::" + position);
                                    //initFragment
                                    switch (position) {
                                        case 0:
                                            //initFragment
                                            break;
                                        case 1:
                                            //initFragment
                                            break;
                                        case 2:
                                            //initFragment
                                            break;
                                        case 3:
                                            //initFragment
                                            break;
                                        default:
                                            //initFragment
                                            break;
                                    }
                                    //initFragment
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

    private void                init() {
        initBottomBar(0, true);
        if (getIntent().getExtras() != null) {
            String typeFramgent = getIntent().getExtras().getString("SETTINGS_TYPE");
            initFragment(typeFramgent);
        } else {
            Log.e(TAG, "ERROR SETTINGS LOADED WITH NO TYPE");
            onBackPressed();
        }
    }

    private void                initFragment(String typeFramgent) {
        try {
            mFragment = getFragmentFromType(typeFramgent);
            Bundle args = new Bundle();
            args.putString("mode", FragmentHistoric.HOST_HISTORIC);
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

    private MyFragment          getFragmentFromType(String typeFragment) {
        switch (typeFragment) {//TODO: Create the architecture of settings
            case WIRESHARK_SETTINGS:
                return new WiresharkSettingsFragmt();
            case GLOBAL_SETTINGS:
                return new GlobalSettingsFragmt();
            case DNSMASQ_SETTINGS:
                return new WiresharkSettingsFragmt();
            case DATABASE_SETTINGS:
                return new WiresharkSettingsFragmt();
            case WEBSERVER_SETTINGS:
                return new WiresharkSettingsFragmt();
            case DORA_SETTINGS:
                return new WiresharkSettingsFragmt();
            case HOSTDISCOVERY_SETTINGS:
                return new FragmentHostDiscoverySettings();
            default:
                return new WiresharkSettingsFragmt();
        }
    }

    public void                 showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

}
