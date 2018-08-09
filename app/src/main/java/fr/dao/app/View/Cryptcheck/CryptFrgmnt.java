package fr.dao.app.View.Cryptcheck;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Api.CryptCheckApi;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Words;
import fr.dao.app.Model.Config.Cryptcheck.CryptCheckScan;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Adapter.CryptCheckAdapter;
import fr.dao.app.View.ZViewController.Dialog.QuestionDialogInput;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

public class                    CryptFrgmnt extends MyFragment  {
    private String              TAG = "NmapOutputView";
    private CryptCheckActivity  mActivity;
    private CryptFrgmnt         mInstance = this;
    private ConstraintLayout    mCoordinatorLayout;
    private ProgressBar         progressBarCrypt;
    private RecyclerView        mRV_cryptcheck;
    private String              mDefaultSite = Singleton.getInstance().Settings.getUserPreferences().defaultTarget;
    private CryptCheckAdapter   mAdapter;
    private CryptCheckScan      mScan;
    private HorizontalBarChart  jcoolGraph;

    public View                 onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cryptcheck, container, false);
        mActivity = (CryptCheckActivity)getActivity();
        initXml(rootView);
        init();
        return rootView;
    }

    public boolean              start() {
        progressBarCrypt.setVisibility(View.VISIBLE);
        try {
            CryptCheckApi.getInstance().callForSite(mInstance, mDefaultSite);
        } catch (IOException e) {
            e.printStackTrace();
            onResponseServer("");
        }
        return true;
    }

    public void                 init() {
        final QuestionDialogInput dialog = new QuestionDialogInput(mActivity)
                .hideSecondInput()
                .setIcon(R.mipmap.ic_cryptcheck_png) //IMAGE HOST
                .setTitle("Analyse HTTPS")
                .setHintToEDFirstQuestion(mDefaultSite)
                .hideSecondInput();
        dialog.onPositiveButton("Run", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                String defaultSite = dialog.getFirstInputQuestion();
                mDefaultSite = (defaultSite.isEmpty()) ? mDefaultSite : defaultSite;
                mActivity.setToolbarTitle(null, mDefaultSite);
                start();
            }
        }).show();
        mAdapter = new CryptCheckAdapter(mActivity);
        mRV_cryptcheck.setAdapter(mAdapter);
        mRV_cryptcheck.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        progressBarCrypt = rootView.findViewById(R.id.progressBarCrypt);
        mRV_cryptcheck = rootView.findViewById(R.id.RV_cryptcheck);
        jcoolGraph = rootView.findViewById(R.id.cryptGraph);
    }

    public void                 onResponseServer(String result) {
        if (result == null || result.isEmpty())
            mActivity.showSnackbar(result);
        progressBarCrypt.setVisibility(View.GONE);
    }

    public void                 onResponseServer(final CryptCheckScan scan) {
        mScan = scan;
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                reloadView();
                mActivity.onResponseServer(scan);
                progressBarCrypt.setVisibility(View.GONE);
            }
        });
    }

    public void                 reloadView() {
        if (mScan != null) {
            updateHeader();
            mAdapter.putOnListOfTrame(mScan.getProtos());
        }
    }

    private void                updateHeader() {
        List<BarEntry> entrys = initLineDataSet();

        BarDataSet setComp1 = new BarDataSet(entrys, "");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setValueTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));

        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(setComp1);
        BarData data = new BarData(dataSets);
        Legend legend = jcoolGraph.getLegend();
        legend.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
        jcoolGraph.getXAxis().setValueFormatter(getSessionValueFormater());
        jcoolGraph.getXAxis().mAxisMaximum = 4;
        jcoolGraph.getXAxis().mAxisRange = 4;
        jcoolGraph.getXAxis().setLabelCount(4);
        jcoolGraph.getXAxis().setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
        jcoolGraph.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        jcoolGraph.getAxisLeft().setDrawLabels(false);
        jcoolGraph.getAxisLeft().setAxisMaximum(100f);
        jcoolGraph.getAxisRight().setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
        jcoolGraph.animateY(2000, Easing.EasingOption.Linear);
        jcoolGraph.setData(data);
        Description description = new Description();
        description.setText(Words.getGenericLightDateFormat(mScan.date));
        description.setTextColor(ContextCompat.getColor(mActivity, R.color.white_secondary));
        jcoolGraph.setDescription(description);
        jcoolGraph.invalidate(); // refresh
    }

    private List<BarEntry>             initLineDataSet() {
        List<BarEntry> defenseEntry = new ArrayList<BarEntry>();
        int raxattack = 0;
        for (;raxattack <= 4;raxattack++) {
            switch (raxattack) {
                case 1:

                    defenseEntry.add(new BarEntry(0f, mScan.results.get(mScan.resultOffset).grade_score));
                    break;
                case 2:
                    defenseEntry.add(new BarEntry(1f, mScan.results.get(mScan.resultOffset).grade_cipher_strengths));
                    break;
                case 3:
                    defenseEntry.add(new BarEntry(2f, mScan.results.get(mScan.resultOffset).grade_key_exchange));
                    break;
                case 4:
                    defenseEntry.add(new BarEntry(3f, mScan.results.get(mScan.resultOffset).grade_protocol));
                    break;
            }
        }
        return defenseEntry;
    }

    public IAxisValueFormatter getSessionValueFormater() {
        return new CryptFrgmnt.MyCustomXAxisValueFormatter();
    }

    boolean Protocol = false, KeyExchange = false, Cipher = false, Overall = false;
    public class MyCustomXAxisValueFormatter implements IAxisValueFormatter {


        public String getFormattedValue(float value, AxisBase axis) {
            switch ((int)value) {
                case 0:
                    return "Protocol";
                case 1:
                    return "Key exchange";
                case 2:
                    return "Cipher";
                case 3:
                    return "Overall";
                default:
                    return "Overall";
            }
        }
    }
}
