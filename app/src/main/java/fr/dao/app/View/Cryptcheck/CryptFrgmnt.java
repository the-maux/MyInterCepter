package fr.dao.app.View.Cryptcheck;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import fr.dao.app.Core.Api.CryptCheckApi;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Config.CryptCheckModel;
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
    }

    public void                 onResponseServer(String result) {
        if (result == null || result.isEmpty())
            mActivity.showSnackbar("Server didnt answer");
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
    }
}
