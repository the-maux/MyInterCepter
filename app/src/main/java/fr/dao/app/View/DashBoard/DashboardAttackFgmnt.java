package fr.dao.app.View.DashBoard;

import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Network.SessionManager;
import fr.dao.app.Model.Config.Session;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;


public class                    DashboardAttackFgmnt extends MyFragment {
    private String              TAG = "DashboardAttackFgmnt";
    private DashboardActivity   mActivity;
    private ConstraintLayout    mRootView;
    private ScatterChart jcoolGraph;
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
        jcoolGraph = rootView.findViewById(R.id.chart);
    }

    public void                 init() {
        mActivity.setToolbarTitle("ReadTeam Statistique", null);
        sessionManager = new SessionManager();
        initChart();
    }

    public void                 onResume() {
        super.onResume();
        init();
    }

    private String[] mLabels = new String[] { "Sniffing", "VulnScan", "InterCutter", "DnsSpoofed", "WebServer", "Scan" };
    private String getLabel(int i) {
        return mLabels[i];
    }

    private void                    initChart() {
        ArrayList<IScatterDataSet> sets = new ArrayList<>();
        int dataSets = mLabels.length, count = 5, range = 5;
        ScatterChart.ScatterShape[] shapes = ScatterChart.ScatterShape.getAllDefaultShapes();

        for(int i = 0; i < dataSets; i++) {

            ArrayList<Entry> entries = new ArrayList<Entry>();

            for(int j = 0; j < count; j++) {
                entries.add(new Entry(j, (float) (Math.random() * range) + range / 4));
            }

            ScatterDataSet ds = new ScatterDataSet(entries, getLabel(i));
            ds.setScatterShapeSize(12f);
            ds.setScatterShape(shapes[i % shapes.length]);
            ds.setColors(ColorTemplate.COLORFUL_COLORS);
            ds.setScatterShapeSize(9f);
            sets.add(ds);
        }

        ScatterData d = new ScatterData(sets);
        jcoolGraph.animateY(2000);

        jcoolGraph.setData(d);
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
        return new DashboardAttackFgmnt.MyCustomXAxisValueFormatter();
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