package fr.dao.app.View.DashBoard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

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
    private CombinedChart           jcoolGraph;
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


    //https://github.com/PhilJay/MPAndroidChart/issues/756
    private void                    initChart() {
        jcoolGraph = findViewById(R.id.chart);
//https://github.com/PhilJay/MPAndroidChart/wiki/Setting-Colors
//        List<Entry> defenseEntry = new ArrayList<Entry>();
        //FOR TEST X: nbrAttack Y: nbrDef
        //Simulate 9 Session
   /*     int rax = 0;
        for (;rax< 10;rax++) { //For nbr session in networkFocused
            switch (rax) {
                case 1:
                    defenseEntry.add(new Entry(0f, 2));
                    break;
                case 2:
                    defenseEntry.add(new Entry(1f, 4));
                    break;
                case 3:
                    defenseEntry.add(new Entry(2f, 1));
                    break;
                case 4:
                    defenseEntry.add(new Entry(3f, 7));
                    break;
                case 5:
                    defenseEntry.add(new Entry(4f, 12));
                    break;
                case 6:
                    defenseEntry.add(new Entry(5f, 6));
                    break;
                case 7:
                    defenseEntry.add(new Entry(6f, 5));
                    break;
            }
        }


*/
        List<Entry> attackEntry = new ArrayList<Entry>();
        //FOR TEST X: nbrAttack Y: nbrDef
        //Simulate 9 Session
        int raxattack = 0;
        for (;raxattack< 10;raxattack++) { //For nbr session in networkFocused
            switch (raxattack) {
                case 1:
                    attackEntry.add(new Entry(0f, 5));
                    break;
                case 2:
                    attackEntry.add(new Entry(1f, 2));
                    break;
                case 3:
                    attackEntry.add(new Entry(2f, 6));
                    break;
                case 4:
                    attackEntry.add(new Entry(3f, 4));
                    break;
                case 5:
                    attackEntry.add(new Entry(4f, 8));
                    break;
                case 6:
                    attackEntry.add(new Entry(5f, 2));
                    break;
                case 7:
                    attackEntry.add(new Entry(6f, 7));
                    break;
            }
        }
        LineDataSet dataSetattack = new LineDataSet(attackEntry, "Attack"); // add entries to dataset
    //    dataSetattack.setColor(ContextCompat.getColor(mInstance, R.color.redteam_color));
        dataSetattack.setColor(Color.rgb(240, 238, 70));
        dataSetattack.setLineWidth(2.5f);
        dataSetattack.setCircleColor(Color.rgb(240, 238, 70));
        dataSetattack.setCircleSize(5f);
        dataSetattack.setFillColor(Color.rgb(240, 238, 70));
        dataSetattack.setDrawCubic(true);
        dataSetattack.setDrawValues(true);
        dataSetattack.setValueTextSize(10f);
        dataSetattack.setAxisDependency(YAxis.AxisDependency.LEFT);

/*        LineDataSet dataSetDefenze = new LineDataSet(defenseEntry, "Defense"); // add entries to dataset
        dataSetDefenze.setColor(ContextCompat.getColor(mInstance, R.color.blueteam_color));
        dataSetDefenze.setValueTextColor(ContextCompat.getColor(mInstance, R.color.material_light_white)); // styling, ...
*/
//        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        CombinedData data = new CombinedData();

        LineData attackLine = new LineData();
        attackLine.addDataSet(dataSetattack);
      //  LineData defenseLine = new LineData();
      //  defenseLine.addDataSet(dataSetDefenze);

        data.addDataSet(dataSetattack);
     //   data.addDataSet(dataSetDefenze);
        jcoolGraph.setDescription("Attack&Defense");
        jcoolGraph.setData(data);
        jcoolGraph.setBackgroundColor(ContextCompat.getColor(mInstance, R.color.divider));
        jcoolGraph.invalidate(); // refresh
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
