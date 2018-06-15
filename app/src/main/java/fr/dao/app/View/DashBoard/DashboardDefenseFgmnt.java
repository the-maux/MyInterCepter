package fr.dao.app.View.DashBoard;

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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import fr.dao.app.Core.Network.SessionManager;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Adapter.SessionAdapter;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;


public class                    DashboardDefenseFgmnt extends MyFragment {
    private String              TAG = "DashboardDefenseFgmnt";
    private DashboardActivity   mActivity;
    private ConstraintLayout    mRootView;
    private PieChart            jcoolGraph;
    private TextView            titleChartDashboard;
    private SessionManager      sessionManager;
    private RecyclerView        mRV;
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
        mRV = rootView.findViewById(R.id.Defense_RV);
    }

    public void                 init() {
        mActivity.setToolbarTitle("BlueTeam Statistique", null);
        sessionManager = new SessionManager();
        initChart();
        initRV();
    }

    public void                 onResume() {
        super.onResume();
        init();
    }
    private void                initRV() {
        SessionAdapter adapter = new SessionAdapter((MyActivity) getActivity(), sessionManager.getSessionsFromDate(null, null), R.color.blueteam_color);
        mRV.setAdapter(adapter);
        mRV.setHasFixedSize(true);
        mRV.setLayoutManager(new LinearLayoutManager(mActivity));
        Log.d(TAG, "Loaded " + sessionManager.getSessionsFromDate(null, null).size() + " sessions");
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
        jcoolGraph.setHoleColor(ContextCompat.getColor(mActivity, R.color.trans));
        mActivity.setToolbarTitle("General Statistique", "27 actions performed");
    }
}