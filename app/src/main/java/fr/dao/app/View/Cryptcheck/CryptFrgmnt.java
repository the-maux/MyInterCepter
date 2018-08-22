package fr.dao.app.View.Cryptcheck;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;

import fr.dao.app.Core.Api.CryptCheckApi;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Config.Cryptcheck.CryptCheckScan;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Adapter.CryptCheckAdapter;
import fr.dao.app.View.ZViewController.Behavior.ViewAnimate;
import fr.dao.app.View.ZViewController.Dialog.QuestionDialogInput;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

public class                    CryptFrgmnt extends MyFragment  {
    private String              TAG = "NmapOutputView";
    public static final String  TLS1 = "TLS 1.0";
    public static final String   TLS11 = "TLS 1.1";
    public static final String   TLS12 = "TLS 1.2";
    private CryptCheckActivity  mActivity;
    private CryptFrgmnt         mInstance = this;
    private RecyclerView        mRV_cryptcheck;
    private String              mDefaultSite;
    private CryptCheckAdapter   mAdapter;
    private CryptCheckScan      mScan = null;
    private TabLayout           tlsTabLayout;

    public View                 onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cryptcheck, container, false);
        mActivity = (CryptCheckActivity)getActivity();
        mDefaultSite = Singleton.getInstance().Settings.getUserPreferences().defaultTarget;
        initXml(rootView);
        init();
        return rootView;
    }

    private void                initXml(View rootView) {
        mRV_cryptcheck = rootView.findViewById(R.id.RV_cryptcheck);
        tlsTabLayout = rootView.findViewById(R.id.tabLayout);
    }

    public void                 init() {
        if (mAdapter == null) {
            mAdapter = new CryptCheckAdapter(mActivity);
            mRV_cryptcheck.setAdapter(mAdapter);
            mRV_cryptcheck.setLayoutManager(new LinearLayoutManager(mActivity));
        }
        final QuestionDialogInput dialog = new QuestionDialogInput(mActivity)
                .hideSecondInput()
                .setIcon(R.mipmap.ic_cryptcheck_png) //IMAGE HOST
                .setTitle("Analyse HTTPS")
                .setHintToEDFirstQuestion(mDefaultSite)
                .hideSecondInput();
        dialog.onPositiveButton("Scan", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                String defaultSite = dialog.getFirstInputQuestion();
                mDefaultSite = (defaultSite.isEmpty()) ? mDefaultSite : defaultSite;
                start();
            }
        }).show();
    }

    private void                initTabs() {
        tlsTabLayout.removeAllTabs();
        if (mScan != null) {
            if (mScan.isTLS10)
                tlsTabLayout.addTab(tlsTabLayout.newTab().setText(TLS1));
            if (mScan.isTLS11)
                tlsTabLayout.addTab(tlsTabLayout.newTab().setText(TLS11));
            if (mScan.isTLS12)
                tlsTabLayout.addTab(tlsTabLayout.newTab().setText(TLS12));
            tlsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                public void onTabSelected(TabLayout.Tab tab) {
                    if (mScan != null) {
                        mAdapter.sort(tab.getText().toString(), mScan);
                    }
                }
                public void onTabUnselected(TabLayout.Tab tab) {}
                public void onTabReselected(TabLayout.Tab tab) {}
            });
            tlsTabLayout.setVisibility(View.VISIBLE);
        } else{
            tlsTabLayout.setVisibility(View.GONE);
        }
    }

    public boolean              start() {
        try {
            if (mScan != null) {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        mActivity.newSearch(mDefaultSite);
                        tlsTabLayout.setVisibility(View.GONE);
                        ViewAnimate.scaleDown(mActivity, mRV_cryptcheck);
                        mAdapter.clear();
                    }
                });
            }
            mActivity.mProgressBar.setVisibility(View.VISIBLE);
            mActivity.setToolbarTitle(null, "Scan in progress");
            mScan = null;
            CryptCheckApi.getInstance().callForSite(mInstance, mDefaultSite);
        } catch (IOException e) {
            e.printStackTrace();
            onResponseServer("");
        }
        return true;
    }

    public void                 onResponseServer(String result) {
        if (result != null && !result.isEmpty())
            mActivity.showSnackbar(result);
        mActivity.mProgressBar.setVisibility(View.GONE);
    }

    public void                 onResponseServer(final CryptCheckScan scan) {
        mScan = scan;
        scan.dump();
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                initTabs();
                mActivity.mProgressBar.setVisibility(View.GONE);
                reloadView();
                mActivity.onResponseServer(scan);
            }
        });
    }

    public void                 reloadView() {
        if (mScan != null) {
            mAdapter.putOnListOfTrame(mScan);
            ViewAnimate.scaleUp(mActivity, mRV_cryptcheck);
        }
    }
    private String              subTAG = "";
    public void                 scanInProgress() {
        String subtitle = subTAG + ".";
        if (subtitle.contains("...."))
            subtitle = subtitle.replace("....", ".");
        mActivity.setToolbarTitle(null, "Scan in progress" + subtitle);
    }
}
