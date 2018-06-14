package fr.dao.app.View.DashBoard;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Database.DBNetwork;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Adapter.NetworksAdapter;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Behavior.ViewAnimate;
import fr.dao.app.View.ZViewController.Dialog.RV_dialog;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

public class                        DashboardActivity extends MyActivity {
    private String                  TAG = "DashboardActivity";
    private DashboardActivity       mInstance = this;
    private CoordinatorLayout       mCoordinatorLayout;
    private Toolbar                 mToolbar;
    private AppBarLayout            appBarLayout;
    private TabLayout               mTabs;
    private ImageView               OsImg;
    private ImageView               iconSettings2;

    public void                     onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        postponeEnterTransition();
        initXml();
        setToolbarTitle("Statistique", null);
        initTabs();
        initNetworkFilterBtn();
        pushViewToFront();
    }

    private void                    pushViewToFront() {
        startPostponedEnterTransition();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mInstance.runOnUiThread(new Runnable() {
                    public void run() {
                        initGeneral();
                        ViewAnimate.FabAnimateReveal(mInstance, findViewById(R.id.frame_container), null);
                    }
                });
            }
        }, 100);
    }

    private void                    initNetworkFilterBtn() {
        iconSettings2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new RV_dialog(mInstance)
                        .setAdapter(new NetworksAdapter(mInstance, DBNetwork.getAllAccessPoint()), false)
                        .setTitle("Recorded network")
                        .onPositiveButton("No filter", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Utils.vibrateDevice(mInstance, 100);
                            }
                        })
                        .show();
            }

        });
    }

    private void                    initXml() {
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        mToolbar = findViewById(R.id.toolbar2);
        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(findViewById(R.id.relativeLayout), 6);
                ViewCompat.setElevation(appBarLayout, 4);
            }
        });
        iconSettings2 = findViewById(R.id.iconSettings2);
        OsImg = findViewById(R.id.OsImg);
        mTabs = findViewById(R.id.tabs);
    }

    private void                    initTabs() {
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void         onTabSelected(TabLayout.Tab tab) {
                switch (tab.getText().toString()) {
                    case "GENERAL":
                        initGeneral();
                        appBarLayout.setBackgroundColor(ContextCompat.getColor(mInstance, R.color.DashboardPrimary));
                        break;
                    case "OFFENSIF":
                        initOffensif();
                        appBarLayout.setBackgroundColor(ContextCompat.getColor(mInstance, R.color.redteam_color));
                        break;
                    case "DEFENSIF":
                        initDefensif();
                        appBarLayout.setBackgroundColor(ContextCompat.getColor(mInstance, R.color.blueteam_color));
                        break;
                }
            }
            public void         onTabUnselected(TabLayout.Tab tab) {}
            public void         onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void                    initDefensif() {
        initFragment(new DashboardDefenseFgmnt());
    }

    private void                    initOffensif() {
        initFragment(new DashboardAttackFgmnt());
    }

    private void                    initGeneral() {
        initFragment(new DashboardGeneralFgmnt());
    }

    private void                    initFragment(MyFragment fragment) {
        try {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
        } catch (IllegalStateException e) {
            Log.w("Error MainActivity", "FragmentStack or FragmentManager corrupted");
            showSnackbar("Error in fragment");
            super.onBackPressed();
        }
    }

    public void                     showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

    public void                     setToolbarTitle(final String title, final String subtitle) {
        mInstance.runOnUiThread(new Runnable() {
            public void run() {
                if (title != null)
                    mToolbar.setTitle(title);
                if (subtitle != null)
                    mToolbar.setSubtitle(subtitle);
            }
        });
    }

    public void                     onBackPressed() {
        if (mTabs.getSelectedTabPosition() == 1 || mTabs.getSelectedTabPosition() == 2)
            mTabs.getTabAt(0).select();
        else {
            super.onBackPressed();
            findViewById(R.id.frame_container).setVisibility(View.GONE);
        }
    }

    public void                     onNetworkFocused(Network accessPoint) {
        //TODO: add filter ACCESS POINT
        showSnackbar("Now filtered on : " + accessPoint.Ssid);
    }
}
