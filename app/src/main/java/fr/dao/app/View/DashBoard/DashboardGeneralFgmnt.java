package fr.dao.app.View.DashBoard;

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

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

import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Core.Database.DBNetwork;
import fr.dao.app.Core.Network.SessionManager;
import fr.dao.app.Model.Config.Action;
import fr.dao.app.Model.Config.Session;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.HostDiscovery.HostDiscoveryActivity;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;


public class                    DashboardGeneralFgmnt extends MyFragment {
    private String              TAG = "DashboardGeneralFgmnt";
    private DashboardActivity   mActivity;
    private ConstraintLayout    mRootView;
    private LineChart           jcoolGraph;
    private TextView            titleChartDashboard;
    private SessionManager      sessionManager;
    private int                     nbrActionPerformed = 0;

    public View                 onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dash_general, container, false);
        initXml(rootView);
        mActivity = (DashboardActivity) getActivity();
        init();

        return rootView;
    }
    
    private void                initXml(View rootView) {
        mRootView = rootView.findViewById(R.id.rootView);
        titleChartDashboard = rootView.findViewById(R.id.titleChartDashboard);
        jcoolGraph = rootView.findViewById(R.id.chart);
    }

    public void                 init() {
        nbrActionPerformed = 0;
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
        legend.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
        jcoolGraph.getXAxis().setValueFormatter(getSessionValueFormater());
        jcoolGraph.getXAxis().setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
        jcoolGraph.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        jcoolGraph.getAxisLeft().setDrawLabels(false);
        jcoolGraph.getAxisRight().setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
        jcoolGraph.animateY(2000, Easing.EasingOption.Linear);

        jcoolGraph.setData(data);
        String titleChart = (DBNetwork.getAllAccessPoint() == null ? "0" : DBNetwork.getAllAccessPoint().size()) + " network audit";
        Description description = new Description();
        description.setText("6 sessions");
        description.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
        jcoolGraph.setDescription(description);
        titleChartDashboard.setText(titleChart);
        jcoolGraph.invalidate(); // refresh
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
        return new MyCustomXAxisValueFormatter();
    }

    public Session getSessionFromValue(float value) {

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