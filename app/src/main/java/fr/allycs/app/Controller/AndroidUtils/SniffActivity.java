package fr.allycs.app.Controller.AndroidUtils;

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

import fr.allycs.app.R;
import fr.allycs.app.View.DnsSpoofing.DnsActivity;
import fr.allycs.app.View.Scan.NmapActivity;
import fr.allycs.app.View.Tcpdump.WiresharkActivity;
import fr.allycs.app.View.WebServer.WebServerActivity;


public abstract class           SniffActivity extends MyActivity  {
    protected String            TAG = "SniffActivity";
    protected SniffActivity     mInstance = this;
    protected Bundle            bundle = null;
    protected AHBottomNavigation mBottomBar;
    protected FloatingActionButton mFab;
    protected static final int   SCANNER=0, SNIFFER=1, DNS=2, WEB=3;
    private int                  mType;

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
        mBottomBar.clearAnimation();
        mBottomBar.removeAllItems();
        if (useCallback) {
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
            mBottomBar.setBehaviorTranslationEnabled(false);
            mBottomBar.setAccentColor(R.color.accent);
            mBottomBar.setInactiveColor(Color.parseColor("#747474"));
            mBottomBar.setForceTint(true);
            mBottomBar.setTranslucentNavigationEnabled(true);
            mBottomBar.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
            mBottomBar.setColored(true);
            mBottomBar.setOnTabSelectedListener(onSelectedListener());
            mBottomBar.setCurrentItem(position);
            updateNotifications();
        }
    }

    private void                    updateNotifications() {
        // Customize notification (title, background, typeface)
        mBottomBar.setNotificationBackgroundColor(ContextCompat.getColor(mInstance, R.color.material_red_600));

// Add or remove notification for each item
//        mBottomBar.setNotification(" ", 0);
// OR
        mBottomBar.setNotification(" ", 1);//Singleton like menu
        mBottomBar.setNotification(" ", 2);
        mBottomBar.setNotification(" ", 3);
        /*AHNotification notification = new AHNotification.Builder()
                .setText("0")
                .setBackgroundColor(ContextCompat.getColor(mInstance, R.color.material_green_400))
                .setTextColor(ContextCompat.getColor(mInstance, R.color.primary_text))
                .build();*/
       // mBottomBar.setNotification(notification, 1);
    }

    private AHBottomNavigation.OnTabSelectedListener onSelectedListener() {
        return new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(final int position, boolean wasSelected) {
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
                                if (position != mType) {
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
                            }
                        });
                    }
                });
                return true;
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        initNavigationBottomBar(mType, true);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(TAG, "onNew Intent mTypeRecorded(" + mType + ") currentItem(" + mBottomBar.getCurrentItem() + ") on "+ mBottomBar.getItemsCount() + " items");
//        mBottomBar.enableItemAtPosition(mType);
//        if (mType != 0)
//            mBottomBar.disableItemAtPosition(0);
//        if (mType != 1)
//            mBottomBar.disableItemAtPosition(1);
//        if (mType != 2)
//            mBottomBar.disableItemAtPosition(2);
//        if (mType != 3)
//            mBottomBar.disableItemAtPosition(3);
        initNavigationBottomBar(mType, true);
    }

    public abstract int         getContentViewId();
}
