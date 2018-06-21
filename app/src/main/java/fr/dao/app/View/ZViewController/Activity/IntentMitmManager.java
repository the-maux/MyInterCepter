package fr.dao.app.View.ZViewController.Activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.support.v4.util.Pair;
import android.view.View;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;

import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.R;
import fr.dao.app.View.DnsSpoofing.DnsActivity;
import fr.dao.app.View.Proxy.ProxyActivity;
import fr.dao.app.View.Sniff.SniffActivity;
import fr.dao.app.View.WebServer.WebServerActivity;
import fr.dao.app.View.ZViewController.Behavior.ViewAnimate;

public class IntentMitmManager {
    private String TAG = "IntentManager";
    private static final IntentMitmManager ourInstance = new IntentMitmManager();

    public static IntentMitmManager getInstance() {
        return ourInstance;
    }

    private IntentMitmManager() {
    }

    private Intent proxy = null, sniff = null, dns = null, web = null;
    public AHBottomNavigation.OnTabSelectedListener onSelectedListener(
            final MITMActivity activity, final int mType, final AHBottomNavigation mBottomBar) {
        return new AHBottomNavigation.OnTabSelectedListener() {
            public boolean onTabSelected(final int position, boolean wasSelected) {
                Intent intent = null;
//                Pair<View, String> p1 = Pair.create((View) mBottomBar, "navigation");
//                Pair<View, String> p2 = Pair.create(activity.findViewById(R.id.appBar), "appBarTransition");
//                final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, p1/*, p2*/);
                if (position != mType) {
                    switch (position) {
                        case 0:
                            if (proxy == null)
                                proxy = new Intent(activity, ProxyActivity.class);
                            intent = proxy;
                            break;
                        case 1:
                            if (sniff == null)
                                sniff = new Intent(activity, SniffActivity.class);
                            intent = sniff;
                            break;
                        case 2:
                            if (dns == null)
                                dns = new Intent(activity, DnsActivity.class);
                            intent = dns;
                            break;
                        case 3:
                            if (web == null)
                                web = new Intent(activity, WebServerActivity.class);
                            intent = web;
                            break;
                        default:
                            Log.d(TAG, "No activity found");
                            break;
                    }
                    ViewAnimate.FabAnimateHide(activity, (FloatingActionButton) activity.findViewById(R.id.fab));
                    Utils.vibrateDevice(activity);
                    final Intent finalIntent = intent;
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(170);
                            } catch (InterruptedException e) {
                            }
                            activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    activity.startActivity(finalIntent);
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

}
