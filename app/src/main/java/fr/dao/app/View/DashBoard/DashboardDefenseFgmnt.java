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

import java.util.ArrayList;

import fr.dao.app.Core.Database.DBNetwork;
import fr.dao.app.Model.Config.Action;
import fr.dao.app.Model.Config.Session;
import fr.dao.app.Model.Target.Network;
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
    private RecyclerView        mRV;
    private int                 nbrActionPerformed = 0;


    public View                 onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_dash_defense, container, false);
        mActivity = (DashboardActivity) getActivity();
        initXml(rootView);
        return rootView;
    }

    private void                initXml(View rootView) {
        mRootView = rootView.findViewById(R.id.rootView);
        titleChartDashboard = rootView.findViewById(R.id.titleChartDashboard);
        jcoolGraph = rootView.findViewById(R.id.chart);
        mRV = rootView.findViewById(R.id.Defense_RV);
        mActivity.statusBarColor(R.color.blueteam_color);
    }

    public void                 init() {
        mActivity.setToolbarTitle("Defense", null);
        initChart();
        initRV();
    }

    public void                 onResume() {
        super.onResume();
        init();
    }
    private void                initRV() {
        for (Session session : mActivity.sessionManager.getActualListSessions()) {
            nbrActionPerformed = nbrActionPerformed + session.getNbrActionType(Action.TeamAction.BLUETEAM);
        }
        mActivity.setToolbarTitle(null, nbrActionPerformed + " actions performed");
        SessionAdapter adapter = new SessionAdapter((MyActivity) getActivity(), mActivity.sessionManager.getActualListSessions(), R.color.blueteam_color);
        mRV.setAdapter(adapter);
        mRV.setHasFixedSize(true);
        mRV.setLayoutManager(new LinearLayoutManager(mActivity));
        Log.d(TAG, "Loaded " + mActivity.sessionManager.getSessionsFromDate(null, null).size() + " sessions");
    }

    /**
     * XAxis(<->) = NBR_SESSIONS
     * YAxis(↕) = NBR ACTIONS
     */
    private void                    initChart() {
        int count = 4;
        ArrayList<PieEntry> entries1 = new ArrayList<>();
        for (Network network : DBNetwork.getAllAccessPoint()) {
            PieEntry entry = new PieEntry((float) network.nbrScanned, network.Ssid);
            entries1.add(entry);
        }
        PieDataSet ds1 = new PieDataSet(entries1, DBNetwork.getAllAccessPoint() + " network");
        ds1.setColors(
                ContextCompat.getColor(mActivity, mSingleton.Settings.preferedColors[0]),
                ContextCompat.getColor(mActivity, mSingleton.Settings.preferedColors[1]),
                ContextCompat.getColor(mActivity, mSingleton.Settings.preferedColors[2]),
                ContextCompat.getColor(mActivity, mSingleton.Settings.preferedColors[3]),
                ContextCompat.getColor(mActivity, mSingleton.Settings.preferedColors[4]),
                ContextCompat.getColor(mActivity, mSingleton.Settings.preferedColors[5]),
                ContextCompat.getColor(mActivity, mSingleton.Settings.preferedColors[6]),
                ContextCompat.getColor(mActivity, mSingleton.Settings.preferedColors[7]),
                ContextCompat.getColor(mActivity, mSingleton.Settings.preferedColors[8]),
                ContextCompat.getColor(mActivity, mSingleton.Settings.preferedColors[9]));
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(12f);
        PieData d = new PieData(ds1);
        jcoolGraph.animateX(2000);
        jcoolGraph.setData(d);
        d.setDrawValues(true);
        d.setHighlightEnabled(true);
        jcoolGraph.setHoleColor(ContextCompat.getColor(mActivity, R.color.trans));
    }

}

/**
 *
 **/