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
    private MyFragment              HistoricFragment = null;
    private AppBarLayout            appBarLayout;
    private LineChart               jcoolGraph;
    private TextView                titleChartDashboard;
    private int                     nbrActionPerformed = 0;
    private SessionManager          sessionManager;
    private TabLayout               mTabs;

    public void                     onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        initXml();
        setToolbarTitle("General Statistique", "");
        nbrActionPerformed = 0;
        sessionManager = new SessionManager();
        initHistoricFragment();
        initGeneral();
        initTabs();
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
        mTabs = findViewById(R.id.tabs);
        titleChartDashboard = findViewById(R.id.titleChartDashboard);
    }


    private void                    initTabs() {
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
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
            public void onTabUnselected(TabLayout.Tab tab) {}
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void initDefensif() {

    }

    private void initOffensif() {

    }

    private void initGeneral() {
        initChart();
    }

    private void                    initHistoricFragment() {
        //TODO: changer ca en fragment dialog
        setToolbarTitle("Dashboard", null);
        MyFragment fragment;
        if (HistoricFragment == null)
            HistoricFragment = new NetDiscoveryHistoricFrgmnt();
        fragment = HistoricFragment;
        Bundle args = new Bundle();
        args.putString("mode", NetDiscoveryHistoricFrgmnt.DB_HISTORIC);
        fragment.setArguments(args);
        initFragment(fragment);
    }

    /**
     * XAxis(<->) = NBR_SESSIONS
     * YAxis(↕) = NBR ACTIONS
     */
    private void                    initChart() {
        jcoolGraph = findViewById(R.id.chart);
        LineDataSet setComp1, setComp2;
        if (42 == 41) {/* Tester les vrai valeurs*/
            setComp1 = initLineDataSet(sessionManager.getEntryFromLoadedSessionsByType(Action.TeamAction.BLUETEAM),
                    "Defense", R.color.blueteam_color);
            setComp2 = initLineDataSet(sessionManager.getEntryFromLoadedSessionsByType(Action.TeamAction.READTEAM),
                    "Attack", R.color.redteam_color);
        } else {
            setComp1 = initLineDataSet(sessionManager.getFakeDefenseEntry(), "Defense", R.color.blueteam_color);
            setComp2 = initLineDataSet(sessionManager.getFakeAttackEntry(), "Attack", R.color.redteam_color);
        }
        List<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(setComp1);
        dataSets.add(setComp2);
        LineData data = new LineData(dataSets);
        Legend legend = jcoolGraph.getLegend();
        legend.setTextColor(ContextCompat.getColor(mInstance, R.color.white_secondary));
        jcoolGraph.getXAxis().setValueFormatter(getSessionValueFormater());
        jcoolGraph.getXAxis().setTextColor(ContextCompat.getColor(mInstance, R.color.white_secondary));
        jcoolGraph.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        jcoolGraph.getAxisLeft().setDrawLabels(false);
        jcoolGraph.getAxisRight().setTextColor(ContextCompat.getColor(mInstance, R.color.white_secondary));
        jcoolGraph.animateY(2000, Easing.EasingOption.Linear);

        jcoolGraph.setData(data);
        String titleChart = (DBNetwork.getAllAccessPoint() == null ? "0" : DBNetwork.getAllAccessPoint().size()) + " network audit";
        Description description = new Description();
        description.setText("6 sessions");
        description.setTextColor(ContextCompat.getColor(mInstance, R.color.white_secondary));
        jcoolGraph.setDescription(description);
        titleChartDashboard.setText(titleChart);
        jcoolGraph.invalidate(); // refresh
        setToolbarTitle("General Statistique", nbrActionPerformed + " actions performed");
    }

    private LineDataSet             initLineDataSet(List<Entry> defenseEntry, String title, int color) {
        LineDataSet setComp1 = new LineDataSet(defenseEntry, title);
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setColor(ContextCompat.getColor(mInstance, color));
        setComp1.setCircleColor(ContextCompat.getColor(mInstance, color));
        setComp1.setCircleColorHole(ContextCompat.getColor(mInstance, color));
        setComp1.setValueTextColor(ContextCompat.getColor(mInstance, R.color.white_secondary));
        setComp1.setLineWidth(1.2f);
        return setComp1;
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

    public IAxisValueFormatter      getSessionValueFormater() {
        return new MyCustomXAxisValueFormatter();
    }

    public Session                  getSessionFromValue(float value) {

        return null;
    }

    /**
     * Print The date of the session in XAxis value
     */
    public class MyCustomXAxisValueFormatter implements IAxisValueFormatter {

        public String getFormattedValue(float value, AxisBase axis) {
            return getSessionFromValue(value) == null ? "00/00" : getSessionFromValue(value).getDateString();

        }
    }
}
