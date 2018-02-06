package fr.allycs.app.Controller.AndroidUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import fr.allycs.app.R;
import fr.allycs.app.View.DnsSpoofing.DnsActivity;
import fr.allycs.app.View.Scan.NmapActivity;
import fr.allycs.app.View.Tcpdump.WiresharkActivity;
import fr.allycs.app.View.WebServer.WebServerActivity;


public abstract class           SniffActivity extends MyActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    protected String            TAG = "SniffActivity";
    protected SniffActivity     mInstance = this;
    protected Bundle            bundle = null;
    protected BottomNavigationView navigationView;

    public void                 onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(getContentViewId());
        navigationView = findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    protected void              onStart() {
        super.onStart();
        if (navigationView == null) {
            navigationView = findViewById(R.id.navigation);
            navigationView.setOnNavigationItemSelectedListener(this);
        }
        updateNavigationBarState();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNew Intent");
    }

    public abstract int         getContentViewId();

    public abstract int         getNavigationMenuItemId();

    private void                updateNavigationBarState() {
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    void                        selectBottomNavigationBarItem(int itemId) {
        Menu menu = navigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            boolean shouldBeChecked = item.getItemId() == itemId;
            if (shouldBeChecked) {
                item.setChecked(true);
                break;
            } else
                item.setChecked(false);
        }
    }

    public boolean              onNavigationItemSelected(final MenuItem item) {
        Log.d(TAG, "onNavigationItemSelected::" + item.getItemId());
        Pair<View, String> p1 = Pair.create((View)navigationView, "navigation");
        final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mInstance, p1);
        navigationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;
                switch (item.getItemId()) {
                    case R.id.navigation_nmap:
                        intent = new Intent(mInstance, NmapActivity.class);
                        break;
                    case R.id.navigation_wireshark:
                        intent = new Intent(mInstance, WiresharkActivity.class);
                        break;
                    case R.id.navigation_dns:
                        intent = new Intent(mInstance, DnsActivity.class);
                        break;
                    case R.id.navigation_webserver:
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
        }, 200);
        return true;
    }
}
