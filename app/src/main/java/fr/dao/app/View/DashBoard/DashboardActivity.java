package fr.dao.app.View.DashBoard;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Database.DBNetwork;
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
    private LineChart               jcoolGraph;
    private TextView                titleChartDashboard;


    public void                     onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initXml();
        init();
        setToolbarTitle("General Statistique", "64 action performed");
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
        titleChartDashboard = findViewById(R.id.titleChartDashboard);
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


    //https://github.com/PhilJay/MPAndroidChart/issues/756
    private void                    initChart() {
        jcoolGraph = findViewById(R.id.chart);
        List<Entry> attackEntry = getAttackEntry();
        List<Entry> defenseEntry = getDefenseEntry();

        LineDataSet setComp1 = initLineDataSet(defenseEntry, "Defense", R.color.blueteam_color);
        LineDataSet setComp2 = initLineDataSet(attackEntry, "Attack", R.color.redteam_color);

        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);
        LineData data = new LineData(dataSets);
        Legend legend = jcoolGraph.getLegend();
        legend.setTextColor(ContextCompat.getColor(mInstance, R.color.white_secondary));
        jcoolGraph.getXAxis().setTextColor(ContextCompat.getColor(mInstance, R.color.white_secondary));
        jcoolGraph.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        jcoolGraph.getAxisLeft().setDrawLabels(false);
        jcoolGraph.getAxisRight().setTextColor(ContextCompat.getColor(mInstance, R.color.white_secondary));
        jcoolGraph.animateY(2000, Easing.EasingOption.Linear);
        jcoolGraph.setData(data);
        String titleChart = DBNetwork.getAllAccessPoint() == null ? "0" : DBNetwork.getAllAccessPoint().size() + "NetworkInformation pentested";
        Description description = new Description();
        description.setText("6 sessions");
        description.setTextColor(ContextCompat.getColor(mInstance, R.color.white_secondary));
        jcoolGraph.setDescription(description);
        titleChartDashboard.setText(titleChart);
        jcoolGraph.invalidate(); // refresh
    }

    private LineDataSet                     initLineDataSet(List<Entry> defenseEntry, String title, int color) {
        LineDataSet setComp1 = new LineDataSet(defenseEntry, title);
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setColor(ContextCompat.getColor(mInstance, color));
        setComp1.setCircleColor(ContextCompat.getColor(mInstance, color));
        setComp1.setCircleColorHole(ContextCompat.getColor(mInstance, color));
        setComp1.setValueTextColor(ContextCompat.getColor(mInstance, R.color.white_secondary));
        setComp1.setLineWidth(1.2f);
        return setComp1;
    }

    private List<Entry>                    getAttackEntry() {
        List<Entry> attackEntry = new ArrayList<Entry>();
        //FOR TEST X: nbrAttack Y: nbrDef
        //Simulate 9 Session
        int raxattack = 0;
        for (;raxattack< 10;raxattack++) { //For nbr session in networkFocused
            switch (raxattack) {
               case 1:
                    attackEntry.add(new Entry(0f, 5f));
                    break;
                case 2:
                    attackEntry.add(new Entry(1f, 2f));
                    break;
                case 3:
                    attackEntry.add(new Entry(2f, 6f));
                    break;
                case 4:
                    attackEntry.add(new Entry(3f, 4f));
                    break;
                case 5:
                    attackEntry.add(new Entry(4f, 8f));
                    break;
                case 6:
                    attackEntry.add(new Entry(5f, 4f));
                    break;
                case 7:
                    attackEntry.add(new Entry(6f, 7f));
                    break;
            }
        }
        return attackEntry;
    }

    private List<Entry>                    getDefenseEntry() {
        List<Entry> defenseEntry = new ArrayList<Entry>();
        //FOR TEST X: nbrAttack Y: nbrDef
        //Simulate 9 Session
        int raxattack = 0;
        for (;raxattack< 10;raxattack++) { //For nbr session in networkFocused
            switch (raxattack) {
                case 1:
                    defenseEntry.add(new Entry(0f, 2f));
                    break;
                case 2:
                    defenseEntry.add(new Entry(1f, 7f));
                    break;
                case 3:
                    defenseEntry.add(new Entry(2f, 4f));
                    break;
                case 4:
                    defenseEntry.add(new Entry(3f, 17f));
                    break;
                case 5:
                    defenseEntry.add(new Entry(4f, 12f));
                    break;
                case 6:
                    defenseEntry.add(new Entry(5f, 2f));
                    break;
                case 7:
                    defenseEntry.add(new Entry(6f, 5f));
                    break;
            }
        }
        return defenseEntry;
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

}
