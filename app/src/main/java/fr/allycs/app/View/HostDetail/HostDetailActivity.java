package fr.allycs.app.View.HostDetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.allycs.app.Controller.AndroidUtils.MyActivity;
import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Network.Fingerprint;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;
import fr.allycs.app.View.HostDiscovery.FragmentHistoric;
import fr.allycs.app.View.Scan.NmapActivity;
import fr.allycs.app.View.Tcpdump.WiresharkActivity;

public class HostDetailActivity extends MyActivity {
    private String              TAG = "HostDetailActivity";
    private HostDetailActivity mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinator;
    private Toolbar             mToolbar;
    private CircleImageView     osHostImage;
    private TabLayout           mTabs;
    private TextView            mPortScan, mVulnerabilitys, mFingerprint, mMitm;
    private Host                mFocusedHost;
    private FragmentHistoric    mFragmentHistoric;

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostdetail);
        if (mSingleton.selectedHostsList == null || mSingleton.selectedHostsList.isEmpty()) {
            Snackbar.make(findViewById(R.id.Coordonitor), "Vous n'avez selectionner aucune target", Snackbar.LENGTH_LONG).show();
            onBackPressed();
        } else {
            mFocusedHost = mSingleton.selectedHostsList.get(0);
            initXml();
            initMenu();
            initFragment();
        }
    }

    private void                initXml() {
        mCoordinator = findViewById(R.id.Coordonitor);
        osHostImage = findViewById(R.id.OsImg);
        mToolbar = findViewById(R.id.toolbar);
        mPortScan  = findViewById(R.id.PortScanTxt);
        mVulnerabilitys  = findViewById(R.id.VulnerabilityScan);
        mFingerprint = findViewById(R.id.OsScanTxt);
        Fingerprint.setOsIcon(this, mFocusedHost, osHostImage);
        mMitm  = findViewById(R.id.MitmARPTxt);
        Fingerprint.setOsIcon(this, mFocusedHost, osHostImage);
        mToolbar.setTitle(mFocusedHost.ip);
        mTabs  = findViewById(R.id.tabs);
        if (mFocusedHost.getName().contains("-")) {
            mToolbar.setSubtitle(mFocusedHost.mac);
        } else {
            mToolbar.setSubtitle(mFocusedHost.getName());
        }
    }

    private void                initMenu() {
        mPortScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, NmapActivity.class);
                startActivity(intent);
            }
        });
        mVulnerabilitys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(R.id.Coordonitor), "Not implemented yet", Snackbar.LENGTH_LONG).show();
            }
        });
        mFingerprint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(findViewById(R.id.Coordonitor), "Not implemented yet", Snackbar.LENGTH_LONG).show();
            }
        });
        mMitm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mInstance, WiresharkActivity.class);
                startActivity(intent);
            }
        });
    }

    private void                initTabs() {
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "tab.getFirstInputQuestion().toString():" + tab.getText().toString());
                switch (tab.getText().toString()) {
                    case "historic":
                        displayHistoric();
                        break;
                    case "notes":
                        displayNotes();
                        break;
                    case "services":
                        displayServices();
                        break;

                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void                displayNotes() {
        Log.d(TAG, "LOAD FROM BDD THE NOTES OF " + mFocusedHost.ip);
        Snackbar.make(mCoordinator, "Not implemented yet", Snackbar.LENGTH_LONG).show();
    }

    private void                displayHistoric() {
        Log.d(TAG, "LOAD FROM BDD THE HISTORIC OF " + mFocusedHost.ip);
        Snackbar.make(mCoordinator, "Not implemented yet", Snackbar.LENGTH_LONG).show();
    }

    private void                displayServices() {
        Log.d(TAG, "SHOW SERVICES OF " + mFocusedHost.ip);
        Snackbar.make(mCoordinator, "Not implemented yet", Snackbar.LENGTH_LONG).show();
    }

    private void                initFragment() {
        try {
            mFragmentHistoric = new FragmentHistoric();
            Bundle args = new Bundle();
            args.putString("mode", FragmentHistoric.HOST_HISTORIC);
            mFragmentHistoric.setArguments(args);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, mFragmentHistoric)
                    .commit();
        } catch (IllegalStateException e) {
            Log.w("Error MainActivity", "FragmentStack or FragmentManager corrupted");
            Snackbar.make(findViewById(R.id.Coordonitor), "Error in fragment", Snackbar.LENGTH_LONG).show();
            super.onBackPressed();
        }
    }

/*    @Override
    public void                 finish() {
        super.finish();
        supportFinishAfterTransition();
    }*/

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

    public void                 onBackPressed() {
        if (mFragmentHistoric == null || mFragmentHistoric.onBackPressed()) {
            super.onBackPressed();
  //          supportFinishAfterTransition();
        }
        else {
            Log.d(TAG, "Fragment mode: " + mFragmentHistoric.mActualMode.name());
        }
    }
}
