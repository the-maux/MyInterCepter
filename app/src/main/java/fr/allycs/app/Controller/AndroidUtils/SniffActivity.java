package fr.allycs.app.Controller.AndroidUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import fr.allycs.app.R;
import fr.allycs.app.View.DnsSpoofing.DnsActivity;
import fr.allycs.app.View.Scan.NmapActivity;
import fr.allycs.app.View.Tcpdump.WiresharkActivity;
import fr.allycs.app.View.WebServer.WebServerActivity;


public abstract class           SniffActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    protected String            TAG = this.getClass().getName();
    protected SniffActivity     mInstance = this;
    protected Bundle            bundle = null;
    protected BottomNavigationView navigationView;

    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(getContentViewId());
        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
    }

    public void                 onBackPressed() {
        super.onBackPressed();
    }

    public abstract int         getContentViewId();

    public abstract int         getNavigationMenuItemId();

    private void updateNavigationBarState() {
        int actionId = getNavigationMenuItemId();
        selectBottomNavigationBarItem(actionId);
    }

    void selectBottomNavigationBarItem(int itemId) {
        Menu menu = navigationView.getMenu();
        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            boolean shouldBeChecked = item.getItemId() == itemId;
            if (shouldBeChecked) {
                item.setChecked(true);
                break;
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        navigationView.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (item.getItemId()) {
                    case R.id.navigation_nmap:
                        startActivity(new Intent(mInstance, NmapActivity.class));
                        break;
                    case R.id.navigation_wireshark:
                        startActivity(new Intent(mInstance, WiresharkActivity.class));
                        break;
                    case R.id.navigation_dns:
                        startActivity(new Intent(mInstance, DnsActivity.class));
                        break;
                    case R.id.navigation_webserver:
                        startActivity(new Intent(mInstance, WebServerActivity.class));
                        break;
                }
            }
        }, 300);
        return true;
    }
}
