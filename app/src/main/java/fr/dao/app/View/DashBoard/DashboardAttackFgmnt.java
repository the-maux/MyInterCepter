package fr.dao.app.View.DashBoard;

import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import fr.dao.app.Core.Database.DBSniffSession;
import fr.dao.app.Core.Network.SessionManager;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Adapter.SniffSessionAdapter;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;


public class                    DashboardAttackFgmnt extends MyFragment {
    private String              TAG = "DashboardAttackFgmnt";
    private DashboardActivity   mActivity;
    private ConstraintLayout    mRootView;
    private RadarChart          mChart;
    private RecyclerView        mAttack_RV;
    private TextView            titleChartDashboard;
    private SessionManager      sessionManager;
    private int                 nbrActionPerformed = 0;


    public View                 onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dash_attack, container, false);
        initXml(rootView);
        mActivity = (DashboardActivity) getActivity();
        return rootView;
    }

    private void                initXml(View rootView) {
        mRootView = rootView.findViewById(R.id.rootView);
        titleChartDashboard = rootView.findViewById(R.id.titleChartDashboard);
        mChart = rootView.findViewById(R.id.chart);
        mAttack_RV = rootView.findViewById(R.id.Attack_RV);
    }

    public void                 init() {
        mActivity.setToolbarTitle("Attack stats", null);
        sessionManager = new SessionManager();
        initChart();
        initRV();
    }

    private void                initRV() {
        //TODO: les sniff sessions ne sont pas lié au sessions !!!
        SniffSessionAdapter adapter = new SniffSessionAdapter(mActivity, DBSniffSession.getAllSniffSession());
        mAttack_RV.setAdapter(adapter);
        mAttack_RV.setHasFixedSize(true);
        mAttack_RV.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    public void                 onResume() {
        super.onResume();
        init();
    }


    private void                initChart() {
        String[] mNameNetworkSaved = getAllWifiSniffed();

        mChart.setWebLineWidth(1f);
        mChart.setWebColor(Color.LTGRAY);
        mChart.setWebLineWidthInner(1f);
        mChart.setWebColorInner(Color.LTGRAY);
        mChart.setWebAlpha(100);
        int nbrNetworkSaved = mNameNetworkSaved.length, nbrTypeOfAttack = 5, range = 5;

        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        for (String nameNetwork : mNameNetworkSaved) {
            ArrayList<RadarEntry> entries = new ArrayList<>();
            for (int i = 0; i < nbrTypeOfAttack; i++) {
                float val = (float) new Random().nextInt(50);
                entries.add(new RadarEntry(val));
            }
            RadarDataSet set1 = new RadarDataSet(entries, nameNetwork);
            Log.d(TAG, "radar added network : " + nameNetwork);
            int color = getColorForWifi();
            set1.setColor(color);
            set1.setFillColor(color);
            set1.setDrawFilled(true);
            set1.setFillAlpha(180);
            set1.setLineWidth(1f);
            set1.setDrawHighlightCircleEnabled(true);
            set1.setDrawHighlightIndicators(false);
            sets.add(set1);
        }

        RadarData data = new RadarData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);
        mChart.invalidate();

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(9f);
        xAxis.setYOffset(0f);
        xAxis.setXOffset(0f);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            private String[] mActivities = new String[]{"Proxy", "Wireshark", "Dns Spoofing", "Nmap", "Exploit"};

            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) value % mActivities.length];
            }
        });
        xAxis.setTextColor(Color.WHITE);

        YAxis yAxis = mChart.getYAxis();
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setDrawLabels(false);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(3f);//7
        l.setYEntrySpace(2f);//5
        l.setTextColor(Color.WHITE);

        Description description = mChart.getDescription();
        description.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
        description.setText("5 types d'attaques");
    }

    private String[]            getAllWifiSniffed() {
        String[] mNameNetworkSaved = new String[] { "GROUPEADEO", "MonSFR", "FakeAP", "SFR-4358", "FREEWIFI"};
        return mNameNetworkSaved;
    }


    public int                  getColorForWifi() {
        List<Integer> allColors = null;
        try {
            allColors = getAllMaterialColors();
            int randomIndex = new Random().nextInt(allColors.size());
            int randomColor = allColors.get(randomIndex);
            return randomColor;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return Color.rgb(121, 162, 175);
    }

    //https://stackoverflow.com/questions/33101202/get-android-material-design-color-randomly
    private List<Integer>       getAllMaterialColors() throws IOException, XmlPullParserException {
        XmlResourceParser xrp = getContext().getResources().getXml(R.xml.material_color);
        List<Integer> allColors = new ArrayList<>();
        int nextEvent;
        while ((nextEvent = xrp.next()) != XmlResourceParser.END_DOCUMENT) {
            String s = xrp.getName();
            if ("color".equals(s)) {
                String color = xrp.nextText();
                allColors.add(Color.parseColor(color));
            }
        }
        return allColors;
    }

}