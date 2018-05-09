package fr.dao.app.View.DashBoard;

import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Database.DBNetwork;
import fr.dao.app.Core.Network.SessionManager;
import fr.dao.app.Model.Config.Action;
import fr.dao.app.Model.Config.Session;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

public class                        DashboardActivity extends MyActivity {
    private String                  TAG = "DashboardActivity";
    private CoordinatorLayout       mCoordinatorLayout;
    private Toolbar                 mToolbar;
    private AppBarLayout            appBarLayout;
    private TabLayout               mTabs;
    private ImageView               OsImg;

    public void                     onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initXml();
        setToolbarTitle("General Statistique", "");
        initTabs();
        initGeneral();
    }

    private void                    initXml() {
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        MyGlideLoader.coordoBackgroundXMM(this, (CoordinatorLayout) findViewById(R.id.coordinatorLayout));
        mToolbar = findViewById(R.id.toolbar2);
        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(findViewById(R.id.relativeLayout), 6);
                ViewCompat.setElevation(appBarLayout, 4);
            }
        });
        OsImg = findViewById(R.id.OsImg);
        mTabs = findViewById(R.id.tabs);
    }

    private void                    initTabs() {
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void         onTabSelected(TabLayout.Tab tab) {
                switch (tab.getText().toString()) {
                    case "GENERAL":
                        initGeneral();
                        break;
                    case "OFFENSIF":
                        initOffensif();
                        break;
                    case "DEFENSIF":
                        initDefensif();
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

    public void onBackPressed() {
        if (mTabs.getSelectedTabPosition() == 1 || mTabs.getSelectedTabPosition() == 2)
            mTabs.getTabAt(0).select();
        else
            super.onBackPressed();
    }
}
