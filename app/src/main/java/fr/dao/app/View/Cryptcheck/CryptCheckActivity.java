package fr.dao.app.View.Cryptcheck;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Words;
import fr.dao.app.Model.Config.Cryptcheck.CryptCheckScan;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;

public class CryptCheckActivity extends MyActivity {
    private String              TAG = "CryptCheckActivity";
    private CryptCheckActivity mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    private CryptFrgmnt         mFragment;
    private AppBarLayout        appBarLayout;
    private Toolbar             mToolbar;
    private CryptCheckScan      mScan;
    TabLayout                   mTabs;
    private ImageView           mSettingsMenu, searchNewSite, mScanType, OsImg;
    ProgressBar                 mProgressBar;
    private TextView            grade;
    private HorizontalBarChart  jcoolGraph;

    protected void              onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cryptcheck);
        initXml();
        init();
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        mTabs = findViewById(R.id.tabs);
        mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progressBar5);
        mSettingsMenu = findViewById(R.id.toolbarSettings);
        searchNewSite = findViewById(R.id.toolbarBtn2);
        mScanType = findViewById(R.id.toolbarBtn1);
        OsImg = findViewById(R.id.OsImg);
        grade = findViewById(R.id.grade);
        findViewById(R.id.rootView).setBackgroundResource(R.color.black_primary);
        appBarLayout = findViewById(R.id.appBar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(findViewById(R.id.headerAppBar), 8);
                ViewCompat.setElevation(findViewById(R.id.headerAppBarStat), 6);
                ViewCompat.setElevation(findViewById(R.id.tabs), 4);
            }
        });
        jcoolGraph = findViewById(R.id.cryptGraph);
        mSettingsMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //mToolbar.showOverflowMenu();
                PopupMenu popup = new PopupMenu(mInstance, mSettingsMenu);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.sniff_bottom_bar, popup.getMenu());
                popup.show();
            }
        });

        MyGlideLoader.loadDrawableInImageView(this, R.mipmap.ic_cryptcheck_png, OsImg, true);
        searchNewSite.setOnClickListener(onSearchNewSiteClick());
        mScanType.setVisibility(View.GONE);
        setToolbarTitle("Cryptcheck","Https Analyse");
        setStatusBarColor(R.color.cryptcheckPrimary);
        initTabs();
    }

    private View.OnClickListener onSearchNewSiteClick() {
        return new View.OnClickListener() {
            public void onClick(View view) {
                mFragment.init();
            }
        };
    }

    private void                initTabs() {
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                if (mScan != null) {
                    setGrade(grade);
                    mScan.updateOffset(tab.getPosition());
                    mFragment.reloadView();
                    setToolbarTitle(null, mScan.results.get(tab.getPosition()).ip);
                }
            }
            public void onTabUnselected(TabLayout.Tab tab) {}
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void                setGrade(TextView grade) {
        String gradeS = mScan.results.get(mScan.resultOffset).grade;
        if (gradeS.contains("A") || gradeS.contains("B")) {
            grade.setTextColor(ContextCompat.getColor(mInstance, R.color.green));
        } else if (gradeS.contains("C") || gradeS.contains("D")) {
            grade.setTextColor(ContextCompat.getColor(mInstance, R.color.material_orange_700));

        } else if (gradeS.contains("E") || gradeS.contains("F")) {
            grade.setTextColor(ContextCompat.getColor(mInstance, R.color.material_orange_700));
        }
        grade.setText(gradeS);
    }

    private void                init() {
        initFragment();
        mTabs.setVisibility(View.GONE);
    }

    private void                initFragment() {
        try {
            mFragment = new CryptFrgmnt();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, mFragment)
                    .commit();
        } catch (IllegalStateException e) {
            showSnackbar("Error in fragment: " + e.getCause().getMessage());
            e.getStackTrace();
            super.onBackPressed();
        }
    }

    public void                 setToolbarTitle(final String title, final String subtitle) {
        mInstance.runOnUiThread(new Runnable() {
            public void run() {
                if (title != null)
                    mToolbar.setTitle(title);
                if (subtitle != null)
                    mToolbar.setSubtitle(subtitle);
            }
        });
    }

    public void                 showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

    public boolean              onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sniff_bottom_bar, menu);
        return true;
    }


    public boolean              onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.ACTION1:
                Log.d(TAG, "ACTION1 item");
                return true;
            case R.id.ACTION2:
                Log.d(TAG, "ACTION2 item");
                return true;
            default:
                Log.d(TAG, "default item");
                return true;
        }
    }

    public void                 onResponseServer(CryptCheckScan scan) {
        OsImg.setVisibility(View.INVISIBLE);
        grade.setVisibility(View.VISIBLE);
        setToolbarTitle(scan.host, scan.results.get(0).ip);
        mScan = scan;
        if (scan.results.size() == 1) {

        } else {
            mTabs.setVisibility(View.VISIBLE);
            mTabs.removeAllTabs();
            for (int i = 0; i < scan.results.size(); i++) {
                mTabs.addTab(mTabs.newTab().setText(scan.results.get(i).ip), i);
            }
        }
    }


    public void                updateHeader(boolean nodata) {
        List<BarEntry> entrys;
        if (nodata) {
            jcoolGraph.clearValues();
            jcoolGraph.clear();
            jcoolGraph.animateY(1000);
            jcoolGraph.invalidate();
            return;
        } else {
            entrys = initLineDataSet();
        }

        MyBarDataSet set = new MyBarDataSet(entrys, "");
        set.setColors(ContextCompat.getColor(mInstance, R.color.material_green_500),
                ContextCompat.getColor(mInstance, R.color.material_blue_grey_700),
                ContextCompat.getColor(mInstance, R.color.material_deep_orange_400),
                ContextCompat.getColor(mInstance, R.color.material_red_500));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setValueTextColor(ContextCompat.getColor(mInstance, R.color.white_secondary));

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);
        BarData data = new BarData(dataSets);
        Legend legend = jcoolGraph.getLegend();
        legend.setTextColor(ContextCompat.getColor(mInstance, R.color.white_secondary));
        Description description = new Description();
        description.setText((mScan == null) ? "" : Words.getGenericLightDateFormat(mScan.date));
        description.setTextColor(ContextCompat.getColor(mInstance, R.color.white_secondary));

        jcoolGraph.getXAxis().setValueFormatter(getSessionValueFormater());
        jcoolGraph.getXAxis().mAxisMaximum = 4;
        jcoolGraph.getXAxis().mAxisRange = 4;
        jcoolGraph.getXAxis().setLabelCount(4);
        jcoolGraph.getXAxis().setGridColor(ContextCompat.getColor(mInstance, R.color.primary_white));
        jcoolGraph.getXAxis().setTextColor(ContextCompat.getColor(mInstance, R.color.white_secondary));
        jcoolGraph.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        jcoolGraph.setGridBackgroundColor(ContextCompat.getColor(mInstance, R.color.primary_white));
        jcoolGraph.getAxisRight().setAxisMaximum(100f);
        jcoolGraph.getAxisRight().setAxisMaximum(0f);
        jcoolGraph.getAxisRight().setGridColor(ContextCompat.getColor(mInstance, R.color.primary_white));
        jcoolGraph.getAxisRight().setTextColor(ContextCompat.getColor(mInstance, R.color.white_secondary));
        jcoolGraph.getAxisLeft().setGridColor(ContextCompat.getColor(mInstance, R.color.primary_white));
        jcoolGraph.getAxisLeft().setDrawLabels(false);
        jcoolGraph.getAxisLeft().setAxisMaximum(100f);
        jcoolGraph.animateY(2000, Easing.EasingOption.Linear);
        jcoolGraph.setData(data);
        jcoolGraph.setBorderColor(ContextCompat.getColor(mInstance, R.color.primary_white));
        jcoolGraph.setDescription(description);
        jcoolGraph.invalidate(); // refresh
    }

    private List<BarEntry>      initLineDataSet() {
        List<BarEntry> defenseEntry = new ArrayList<BarEntry>();
        if (mScan != null) {
            defenseEntry.add(new BarEntry(0f, mScan.results.get(mScan.resultOffset).grade_score));
            defenseEntry.add(new BarEntry(1f, mScan.results.get(mScan.resultOffset).grade_cipher_strengths));
            defenseEntry.add(new BarEntry(2f, mScan.results.get(mScan.resultOffset).grade_key_exchange));
            defenseEntry.add(new BarEntry(3f, mScan.results.get(mScan.resultOffset).grade_protocol));
        } else {
            defenseEntry.add(new BarEntry(0f, 0f));
            defenseEntry.add(new BarEntry(1f, 0f));
            defenseEntry.add(new BarEntry(2f, 0f));
            defenseEntry.add(new BarEntry(3f, 0f));
        }
        return defenseEntry;
    }

    public IAxisValueFormatter getSessionValueFormater() {
        return new CryptCheckActivity.MyCustomXAxisValueFormatter();
    }

    public class MyCustomXAxisValueFormatter implements IAxisValueFormatter {
        public String getFormattedValue(float value, AxisBase axis) {
            switch ((int)value) {
                case 0:
                    return "Overall";
                case 1:
                    return "Cipher";
                case 2:
                    return "Key exchange";
                case 3:
                    return "Protocol";
                default:
                    return "Overall";
            }
        }
    }
    public class MyBarDataSet extends BarDataSet {
        public MyBarDataSet(List<BarEntry> yVals, String label) {
            super(yVals, label);
        }

        public int getColor(int index) {
            if (getEntryForIndex(index).getY()  <= 25) {
                return mColors.get(3);
            } else if (getEntryForIndex(index).getY() <= 50) {
                return mColors.get(2);
            } else if (getEntryForIndex(index).getY() <= 75) {
                return mColors.get(1);
            } else {
                return mColors.get(0);

            }
        }

    }
}
