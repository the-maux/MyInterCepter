package fr.dao.app.View.ZViewController.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;

import fr.dao.app.Core.Configuration.MitManager;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Tcpdump.Tcpdump;
import fr.dao.app.R;
import fr.dao.app.View.DnsSpoofing.DnsActivity;
import fr.dao.app.View.Proxy.ProxyActivity;
import fr.dao.app.View.Sniff.SniffActivity;
import fr.dao.app.View.WebServer.WebServerActivity;
import fr.dao.app.View.ZViewController.Behavior.ViewAnimate;


public abstract class               MITMActivity extends MyActivity  {
    protected String                TAG = "MITMActivity";
    protected MITMActivity          mInstance = this;
    protected Bundle                bundle = null;
    protected AHBottomNavigation    mBottomBar;
    protected FloatingActionButton  mFab;
    protected static final int PROXY =0, SNIFFER=1, DNS=2, WEB=3;
    private int                     mType;
    private Singleton               mSingleton = Singleton.getInstance();
    private boolean                 hideBottomBar = false;

    public void                     onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    protected void                  onStart() {
        super.onStart();
        if (mBottomBar == null) {
            mBottomBar = findViewById(R.id.navigation);
            //mBottomBar.setOnNavigationPositionListener(this);
        }
        if (hideBottomBar)
            mBottomBar.setVisibility(View.GONE);
        else
            ViewCompat.setElevation(mBottomBar, 2);
    }

    protected void                  initNavigationBottomBar(int position, boolean useCallback) {
// Create items
        mType = position;
        mBottomBar = findViewById(R.id.navigation);
        if (useCallback) {
            mBottomBar.clearAnimation();
            mBottomBar.removeAllItems();
            Log.d(TAG, "initNavigationBottomBar(" + position + ":" + useCallback + ")");
            AHBottomNavigationItem[] bottomItems = new AHBottomNavigationItem[4];
            bottomItems[0] = new AHBottomNavigationItem(R.string.TRACKER,
                    R.drawable.spy, R.color.spyPrimary);
            bottomItems[1] = new AHBottomNavigationItem(R.string.SNIFFER,
                    R.drawable.ic_sniff_barbutton, R.color.snifferPrimary);
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
            mBottomBar.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
                public void onPositionChange(int y) {

                }
            });
            mBottomBar.setOnTabSelectedListener(onSelectedListener());
        }
    }

    public void                     updateNotifications() {
        if (!hideBottomBar && mBottomBar.getItemsCount() > 2) {
            AHNotification greenNotif = new AHNotification.Builder()
                    .setText(" ")
                    .setBackgroundColor(ContextCompat.getColor(mInstance, R.color.start_color))
                    .setTextColor(ContextCompat.getColor(mInstance, R.color.primary_text))
                    .build();
            AHNotification redNotif = new AHNotification.Builder()
                    .setText(" ")
                    .setBackgroundColor(ContextCompat.getColor(mInstance, R.color.stop_color))
                    .setTextColor(ContextCompat.getColor(mInstance, R.color.primary_text))
                    .build();
            Tcpdump tcpdump = Tcpdump.getTcpdump(this, false);
            if (tcpdump != null)
                mBottomBar.setNotification(Tcpdump.isRunning() ? greenNotif : redNotif, 1);
            else
                mBottomBar.setNotification(redNotif, 1);
            mBottomBar.setNotification(MitManager.getInstance().isDnsmasqRunning() ? greenNotif : redNotif, 2);
            mBottomBar.setNotification(redNotif, 0);
            mBottomBar.setNotification(MitManager.getInstance().iswebSpoofed() ? greenNotif : redNotif, 3);
        }
    }

    private AHBottomNavigation.OnTabSelectedListener onSelectedListener() {
        return new AHBottomNavigation.OnTabSelectedListener() {
            public boolean onTabSelected(final int position, boolean wasSelected) {
                Intent intent = null;
                if (position != mType) {
                    switch (position) {
                        case 0:
                            intent = new Intent(mInstance, ProxyActivity.class);
                            break;
                        case 1:
                            intent = new Intent(mInstance, SniffActivity.class);
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
                    ViewAnimate.FabAnimateHide(mInstance, (FloatingActionButton) findViewById(R.id.fab));
                    Utils.vibrateDevice(mInstance);
                    final Intent finalIntent = intent;
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(170);
                            } catch (InterruptedException e) {
                            }
                            mInstance.runOnUiThread(new Runnable() {
                                public void run() {
                                    if (finalIntent != null) {
                                        finalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(finalIntent/*, options.toBundle()*/);
                                    }
                                    startActivity(finalIntent/*, options.toBundle()*/);
                                }
                            });
                        }
                    }).start();
                    return true;
                }
                return false;
            }
        };
    }

    protected void                  onResume() {
        if (mFab != null)
            ViewAnimate.FabAnimateReveal(mInstance, mFab);
        super.onResume();
        Log.d(TAG, " onResume::setCurrentItem::" + mType);
        mBottomBar.setCurrentItem(mType, false);
        if (!hideBottomBar)
            updateNotifications();
    }

    protected void                  onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mBottomBar.setCurrentItem(mType, false);
        updateNotifications();
    }

    protected void                  hideBottomBar() {
        hideBottomBar = true;
    }
    protected void                  showBottomBar() {
        hideBottomBar = false;
    }

}
