package fr.dao.app.View.DashBoard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Network.SessionManager;
import fr.dao.app.Model.Config.Session;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;


public class                    DashboardDefenseFgmnt extends MyFragment {
    private String              TAG = "DashboardDefenseFgmnt";
    private DashboardActivity   mActivity;
    private ConstraintLayout    mRootView;
    private PieChart            jcoolGraph;
    private TextView            titleChartDashboard;
    private SessionManager      sessionManager;
    private int                 nbrActionPerformed = 0;


    public View                 onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dash_defense, container, false);
        initXml(rootView);
        mActivity = (DashboardActivity) getActivity();
        return rootView;
    }

    private void                initXml(View rootView) {
        mRootView = rootView.findViewById(R.id.rootView);
        titleChartDashboard = rootView.findViewById(R.id.titleChartDashboard);
        jcoolGraph = rootView.findViewById(R.id.chart);
    }

    public void                 init() {
        mActivity.setToolbarTitle("BlueTeam Statistique", null);
        sessionManager = new SessionManager();
        initChart();
    }

    public void                 onResume() {
        super.onResume();
        init();
    }

    /**
     * XAxis(<->) = NBR_SESSIONS
     * YAxis(↕) = NBR ACTIONS
     */
    private void                    initChart() {
        int count = 4;
        ArrayList<PieEntry> entries1 = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            entries1.add(new PieEntry((float) ((Math.random() * 60) + 40), "DORA " + (i+1)));
        }
        PieDataSet ds1 = new PieDataSet(entries1, "Quarterly Revenues 2015");
        ds1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(12f);

        PieData d = new PieData(ds1);
        jcoolGraph.animateX(2000);
        jcoolGraph.setData(d);
        mActivity.setToolbarTitle("General Statistique", nbrActionPerformed + " actions performed");
    }

    private LineDataSet             initLineDataSet(List<Entry> defenseEntry, String title, int color) {
        LineDataSet setComp1 = new LineDataSet(defenseEntry, title);
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setColor(ContextCompat.getColor(mActivity, color));
        setComp1.setCircleColor(ContextCompat.getColor(mActivity, color));
        setComp1.setCircleColorHole(ContextCompat.getColor(mActivity, color));
        setComp1.setValueTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
        setComp1.setLineWidth(1.2f);
        return setComp1;
    }

    public IAxisValueFormatter getSessionValueFormater() {
        return new DashboardDefenseFgmnt.MyCustomXAxisValueFormatter();
    }

    public Session getSessionFromValue(float value) {
        //TODO get Session from value in graph
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