package fr.allycs.app.View.Behavior.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Core.Tcpdump.Tcpdump;
import fr.allycs.app.R;
import fr.allycs.app.View.Activity.DnsSpoofing.DnsActivity;
import fr.allycs.app.View.Activity.Scan.NmapActivity;
import fr.allycs.app.View.Activity.Tcpdump.WiresharkActivity;
import fr.allycs.app.View.Activity.WebServer.WebServerActivity;


public abstract class           SniffActivity extends MyActivity  {
    protected String            TAG = "SniffActivity";
    protected SniffActivity     mInstance = this;
    protected Bundle            bundle = null;
    protected AHBottomNavigation mBottomBar;
    protected FloatingActionButton mFab;
    protected static final int   SCANNER=0, SNIFFER=1, DNS=2, WEB=3;
    private int                  mType;
    private Singleton           mSingleton = Singleton.getInstance();

    public void                 onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(getContentViewId());
    }

    protected void              onStart() {
        super.onStart();
        if (mBottomBar == null) {
            mBottomBar = findViewById(R.id.navigation);
            //mBottomBar.setOnNavigationPositionListener(this);
        }
    }

    protected void               initNavigationBottomBar(int position, boolean useCallback) {
// Create items
        mType = position;
        mBottomBar = findViewById(R.id.navigation);
        if (useCallback) {
            mBottomBar.clearAnimation();
            mBottomBar.removeAllItems();
            Log.d(TAG, "initNavigationBottomBar(" + position + ":" + useCallback + ")");
            AHBottomNavigationItem[] bottomItems = new AHBottomNavigationItem[4];
            bottomItems[0] = new AHBottomNavigationItem(R.string.SCANNER,
                    R.drawable.ic_nmap_icon_tabbutton, R.color.NmapPrimary);
            bottomItems[1] = new AHBottomNavigationItem(R.string.SNIFFER,
                    R.drawable.ic_sniff_barbutton, R.color.wiresharkPrimary);
            bottomItems[2] = new AHBottomNavigationItem(R.string.DNS_SPOOFER,
                    R.drawable.ic_dns_btnbar, R.color.dnsSpoofPrimary);
            bottomItems[3] = new AHBottomNavigationItem(R.string.WEB_SPOOFER,
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
        //mBottomBar.setCurrentItem(position, false);
    }

    protected void                    updateNotifications() {
        AHNotification greenNotif = new AHNotification.Builder()
                .setText(" ")
                .setBackgroundColor(ContextCompat.getColor(mInstance, R.color.material_green_500))
                .setTextColor(ContextCompat.getColor(mInstance, R.color.primary_text))
                .build();
        AHNotification redNotif = new AHNotification.Builder()
                .setText(" ")
                .setBackgroundColor(ContextCompat.getColor(mInstance, R.color.material_red_500))
                .setTextColor(ContextCompat.getColor(mInstance, R.color.primary_text))
                .build();
        Tcpdump tcpdump = Tcpdump.getTcpdump(this, false);
        if (tcpdump != null)
            mBottomBar.setNotification(tcpdump.isRunning ? greenNotif : redNotif, 1);
        else
            mBottomBar.setNotification(redNotif, 1);
        mBottomBar.setNotification(mSingleton.isDnsControlstarted() ? greenNotif : redNotif, 2);
        mBottomBar.setNotification(mSingleton.iswebSpoofed() ? greenNotif : redNotif, 3);
    }

    private AHBottomNavigation.OnTabSelectedListener onSelectedListener() {
        return new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(final int position, boolean wasSelected) {
                if (position != mType) {
                    mBottomBar.post(new Runnable() {
                        @Override
                        public void run() {
                            mInstance.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG, "onNavigationItemSelected::" + position);
                                    Intent intent = null;
                                    Pair<View, String> p1 = Pair.create((View) mBottomBar, "navigation");
                                    final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mInstance, p1);
                                    switch (position) {
                                        case 0:
                                            intent = new Intent(mInstance, NmapActivity.class);
                                            break;
                                        case 1:
                                            intent = new Intent(mInstance, WiresharkActivity.class);
                                            break;
                                        case 2:
                                            intent = new Intent(mInstance, DnsActivity.class);
                                            break;
                                        case 3:
                                            intent = new Intent(mInstance, WebServerActivity.class);
                                            break;
                                        default:
                                            Log.d(TAG, "No activity found");
                                            break;
                                    }
                                    if (intent != null) {
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent, options.toBundle());
                                    }
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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, " onResume::setCurrentItem::" + mType);
        mBottomBar.setCurrentItem(mType, false);
        updateNotifications();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNew Intent mTypeRecorded(" + mType + ") currentItem(" + mBottomBar.getCurrentItem() + ") on "+ mBottomBar.getItemsCount() + " items");
        Log.d(TAG, " onNewIntent::setCurrentItem::" + mType);
        mBottomBar.setCurrentItem(mType, false);
        updateNotifications();
    }

    public abstract int         getContentViewId();
}
