package fr.allycs.app.View.HostDetail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Databse.DBSession;
import fr.allycs.app.Controller.Misc.MyActivity;
import fr.allycs.app.Controller.Network.Discovery.Fingerprint;
import fr.allycs.app.Model.Target.AccessPoint;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Target.Session;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.AccessPointAdapter;
import fr.allycs.app.View.Adapter.HostDiscoveryAdapter;
import fr.allycs.app.View.Adapter.SessionAdapter;
import fr.allycs.app.View.NmapActivity;
import fr.allycs.app.View.WiresharkActivity;

public class                    HostFocusActivity extends MyActivity {
    private String              TAG = "HostFocusActivity";
    private HostFocusActivity   mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinator;
    private Toolbar             mToolbar;
    private CircleImageView     osHostImage;
    private TextView            mPortScan, mVulnerabilitys, mFingerprint, mMitm;
    private Host                mFocusedHost;
    private HostDetailFragment  mFragment;

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostdetail);
        if (mSingleton.hostsList == null || mSingleton.hostsList.isEmpty()) {
            Snackbar.make(findViewById(R.id.Coordonitor), "Vous n'avez selectionner aucune target", Snackbar.LENGTH_LONG).show();
            onBackPressed();
        } else {
            mFocusedHost = mSingleton.hostsList.get(0);
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

    private void                initFragment() {
        try {
            mFragment = new HostDetailFragment();
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, mFragment)
                    .commit();
        } catch (IllegalStateException e) {
            Log.w("Error MainActivity", "FragmentStack or FragmentManager corrupted");
            Snackbar.make(findViewById(R.id.Coordonitor), "Error in fragment", Snackbar.LENGTH_LONG).show();
            super.onBackPressed();
        }
    }

    @Override public void       onBackPressed() {
        if (mFragment.onBackPressed())
            super.onBackPressed();
    }
}
