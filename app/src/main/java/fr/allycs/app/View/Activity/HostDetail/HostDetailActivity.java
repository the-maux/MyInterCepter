package fr.allycs.app.View.Activity.HostDetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Core.Database.DBManager;
import fr.allycs.app.Core.Nmap.Fingerprint;
import fr.allycs.app.Model.Net.Pcap;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;
import fr.allycs.app.View.Activity.HostDiscovery.FragmentHistoric;
import fr.allycs.app.View.Activity.Scan.NmapActivity;
import fr.allycs.app.View.Activity.Wireshark.WiresharkActivity;
import fr.allycs.app.View.Behavior.Activity.MyActivity;
import fr.allycs.app.View.Behavior.Fragment.MyFragment;

public class                    HostDetailActivity extends MyActivity {
    private String              TAG = "HostDetailActivity";
    private HostDetailActivity  mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinator;
    private Toolbar             mToolbar;
    private ImageView           osHostImage;
    private TabLayout           mTabs;
    private TextView            mPortScan, mVulnerabilitys, mFingerprint, mMitm;
    private Host                mFocusedHost;
    private MyFragment          mCurrentFragment;
    private List<Pcap>          mPcapsList;

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
            initTabs();
            displayHistoric();
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
        if (mFocusedHost.getName().isEmpty()) {
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
        mTabs.addTab(mTabs.newTab().setText("Historic"), 0);
        if (mFocusedHost.Notes != null && !mFocusedHost.Notes.isEmpty())
            mTabs.addTab(mTabs.newTab().setText("Notes"), 0);
        mPcapsList = DBManager.getListPcapFormHost(mFocusedHost);
        if (mPcapsList != null && !mPcapsList.isEmpty())
            mTabs.addTab(mTabs.newTab().setText("Pcap"), 0);
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "tab.getFirstInputQuestion().toString():" + tab.getText().toString());
                switch (tab.getText().toString().toLowerCase()) {
                    case "historic":
                        displayHistoric();
                        break;
                    case "notes":
                        displayNotes();
                        break;
                    case "pcap":
                        displayPcap();
                        break;
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void                displayNotes() {
        initFragment(new HostNotesFragment());
    }

    private void                displayHistoric() {
        MyFragment fragment = new FragmentHistoric();
        Bundle args = new Bundle();
        args.putString("mode", FragmentHistoric.HOST_HISTORIC);
        fragment.setArguments(args);
        initFragment(fragment);
    }

    private void                displayPcap() {
        Log.d(TAG, "SHOW Pcaps OF " + mFocusedHost.ip);

        List<Pcap> pcapList = DBManager.getListPcapFormHost(mFocusedHost);
        if (pcapList == null || pcapList.isEmpty())
            Snackbar.make(mCoordinator, "No Pcap Recorded for " + mFocusedHost.getOneName(), Snackbar.LENGTH_LONG).show();
        else {
            //TODO: faire l'adapter de pcap
        }
    }

    private void                initFragment(MyFragment fragment) {
        try {
            mCurrentFragment = fragment;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
        } catch (IllegalStateException e) {
            Log.w("Error MainActivity", "FragmentStack or FragmentManager corrupted");
            Snackbar.make(findViewById(R.id.Coordonitor), "Error in fragment", Snackbar.LENGTH_LONG).show();
            super.onBackPressed();
        }
    }

    public void                 setToolbarTitle(final String title, final String subtitle) {
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
        if (mCurrentFragment == null || mCurrentFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
