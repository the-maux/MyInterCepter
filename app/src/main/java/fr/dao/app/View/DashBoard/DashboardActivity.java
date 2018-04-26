package fr.dao.app.View.DashBoard;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
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
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

public class                        DashboardActivity extends MyActivity {
    private String                  TAG = "DashboardActivity";
    private CoordinatorLayout       mCoordinatorLayout;
    private Toolbar                 mToolbar;
    private MyFragment              HistoricFragment = null;
    private AppBarLayout            appBarLayout;
    private JcoolGraph jcoolGraph;
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
            HistoricFragment = new NetDiscoveryHistoricFrgmnt();
        fragment = HistoricFragment;
        Bundle args = new Bundle();
        args.putString("mode", NetDiscoveryHistoricFrgmnt.DB_HISTORIC);
        fragment.setArguments(args);
        initFragment(fragment);
        initChart();
    }

    private void                    initChart() {
        jcoolGraph = findViewById(R.id.sug_recode_line);

        List<Jchart> pointOfGraphAttack = new ArrayList<>();
        List<Jchart> pointOfGraphDefense = new ArrayList<>();
        for(int i = 0; i < chartNum; i++) {
            Jchart tmp = new Jchart(new SecureRandom().nextInt(50)+chartNum, ContextCompat.getColor(mInstance, R.color.redteam_color));
            pointOfGraphAttack.add(tmp);
            //TODO: ADD Name on point
            //lines.add(new Jchart(10,new SecureRandom().nextInt(50) + 15,"test", Color.parseColor("#b8e986")));
        }
        for(int i = 0; i < chartNum; i++) {
            Jchart tmp = new Jchart(new SecureRandom().nextInt(50)+chartNum, ContextCompat.getColor(mInstance, R.color.blueteam_color));
            pointOfGraphDefense.add(tmp);
            //TODO: ADD Name on point
            //lines.add(new Jchart(10,new SecureRandom().nextInt(50) + 15,"test", Color.parseColor("#b8e986")));
        }
//        for(Jchart line : lines) {
//            line.setStandedHeight(100);
//        }
        //        lines.get(new SecureRandom().nextInt(chartNum-1)).setUpper(0);
        pointOfGraphAttack.get(1).setUpper(0);
        pointOfGraphAttack.get(new SecureRandom().nextInt(chartNum - 1)).setLower(10);
        pointOfGraphAttack.get(chartNum-2).setUpper(0);
        pointOfGraphDefense.get(1).setUpper(0);
        pointOfGraphDefense.get(new SecureRandom().nextInt(chartNum - 1)).setLower(10);
        pointOfGraphDefense.get(chartNum-2).setUpper(0);
        //        jcoolGraph.setScrollAble(true);
//        jcoolGraph.setLineMode();

        //pointOfGraphDefense.add(tmp);
        jcoolGraph.setLinePointRadio((int) jcoolGraph.getLineWidth());
//        jcoolGraph.setLineMode(JcoolGraph.LineMode.LINE_DASH_0);
//        jcoolGraph.setLineStyle(JcoolGraph.LineStyle.LINE_BROKEN);
        jcoolGraph.setYaxisValues("Yvalue0", "Yvalue1", "Yvalue2");
        //        jcoolGraph.setSelectedMode(BaseGraph.SelectedMode.SELECETD_MSG_SHOW_TOP);
        jcoolGraph.setNormalColor(ContextCompat.getColor(mInstance, R.color.redteam_color));//Color of the line

        jcoolGraph.feedData(pointOfGraphAttack);
        jcoolGraph.feedData(pointOfGraphDefense);
        ( (FrameLayout) jcoolGraph.getParent() ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                jcoolGraph.postInvalidate();
            }
        });
        loopThisTwoGraph(pointOfGraphAttack, pointOfGraphDefense, true);
    }

    private void                    loopThisTwoGraph(final List<Jchart> pointOfGraphAttack, final List<Jchart> pointOfGraphDefense, final boolean isDefense) {
        jcoolGraph.postDelayed(new Runnable() {
                    public void run() {
                        mInstance.runOnUiThread(new Runnable() {
                            public void run() {
                                jcoolGraph.setNormalColor(ContextCompat.getColor(mInstance, (isDefense) ? R.color.blueteam_color : R.color.redteam_color));//Color of the line
                                jcoolGraph.aniChangeData(isDefense ? pointOfGraphDefense : pointOfGraphAttack);
                            }
                        });
                        loopThisTwoGraph(pointOfGraphAttack, pointOfGraphDefense, !isDefense);
                    }
                }, 2500);
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
