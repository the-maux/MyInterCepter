package fr.dao.app.View.DashBoard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import fr.dao.app.External.jgraph.graph.JcoolGraph;
import fr.dao.app.External.jgraph.models.Jchart;
import fr.dao.app.R;
import fr.dao.app.View.HostDiscovery.HostDiscoveryHistoricFrgmnt;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

public class                        DashboardActivity extends MyActivity {
    private String                  TAG = "DashboardActivity";
    private CoordinatorLayout       mCoordinatorLayout;
    private Toolbar                 mToolbar;
    private MyFragment              HistoricFragment = null;
    private AppBarLayout            appBarLayout;
    private JcoolGraph              mLineChar;
    private int                     chartNum = 14;

    public void                     onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initXml();
        init();
    }

    private void                    initXml() {
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        MyGlideLoader.coordoBackgroundXMM(this, (CoordinatorLayout) findViewById(R.id.coordinatorLayout));
        mToolbar = findViewById(R.id.toolbar2);
        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(appBarLayout, 4);
            }
        });
    }

    private void                    init() {
        setToolbarTitle("Dashboard", null);
        MyFragment fragment;
        if (HistoricFragment == null)
            HistoricFragment = new HostDiscoveryHistoricFrgmnt();
        fragment = HistoricFragment;
        Bundle args = new Bundle();
        args.putString("mode", HostDiscoveryHistoricFrgmnt.DB_HISTORIC);
        fragment.setArguments(args);
        initFragment(fragment);
        initChart();
    }

    private void                    initChart() {
        mLineChar = (JcoolGraph)findViewById(R.id.sug_recode_line);

        List<Jchart> lines = new ArrayList<>();
        for(int i = 0; i<chartNum; i++) {
            Jchart tmp = new Jchart(new SecureRandom().nextInt(50)+15, Color.parseColor("#5F77F6"));
            lines.add(tmp);
            //TODO: ADD Name on point
            //lines.add(new Jchart(10,new SecureRandom().nextInt(50) + 15,"test", Color.parseColor("#b8e986")));
        }
//        for(Jchart line : lines) {
//            line.setStandedHeight(100);
//        }
        //        lines.get(new SecureRandom().nextInt(chartNum-1)).setUpper(0);
        lines.get(1).setUpper(0);
        lines.get(new SecureRandom().nextInt(chartNum-1)).setLower(10);
        lines.get(chartNum-2).setUpper(0);
        //        mLineChar.setScrollAble(true);
//        mLineChar.setLineMode();
        mLineChar.setLinePointRadio((int)mLineChar.getLineWidth());
//        mLineChar.setLineMode(JcoolGraph.LineMode.LINE_DASH_0);
//        mLineChar.setLineStyle(JcoolGraph.LineStyle.LINE_BROKEN);

        //        mLineChar.setYaxisValues("test","测试","text");
        //        mLineChar.setSelectedMode(BaseGraph.SelectedMode.SELECETD_MSG_SHOW_TOP);
        mLineChar.setNormalColor(Color.parseColor("#676567"));
        mLineChar.feedData(lines);
        ( (FrameLayout)mLineChar.getParent() ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                mLineChar.postInvalidate();
            }
        });
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
            @Override
            public void run() {
                if (title != null)
                    mToolbar.setTitle(title);
                if (subtitle != null)
                    mToolbar.setSubtitle(subtitle);
            }
        });
    }

}
