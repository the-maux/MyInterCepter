package fr.allycs.app.View.Activity.Settings;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.widget.Toast;

import fr.allycs.app.View.Behavior.Activity.MyActivity;
import fr.allycs.app.View.Behavior.Fragment.MyFragment;
import fr.allycs.app.View.Behavior.MyGlideLoader;
import fr.allycs.app.R;
import fr.allycs.app.View.Activity.HostDiscovery.FragmentHistoric;
import fr.allycs.app.View.Activity.HostDiscovery.HostDiscoverySettingsFragmt;

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
        MyGlideLoader.coordoBackgroundXMM(this, (CoordinatorLayout) findViewById(R.id.coordinatorLayout));
        if (getIntent().getExtras() != null) {
            String typeFramgent = getIntent().getExtras().getString("SETTINGS_TYPE");
            initFragment(typeFramgent);
        } else {
            Log.e(TAG, "ERROR SETTINGS LOADED WITH NO TYPE");
            onBackPressed();
        }
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
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
        switch (typeFragment) {
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
                return new HostDiscoverySettingsFragmt();
            default:
                return new WiresharkSettingsFragmt();
        }
    }

    public void                 showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

}
