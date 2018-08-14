package fr.dao.app.View.Cryptcheck;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;

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
    private CryptCheckActivity  mActivity;
    private CryptFrgmnt         mInstance = this;
    private RadioButton         rd_tls10, rd_tls11, rd_tls12;
    private RecyclerView        mRV_cryptcheck;
    private String              mDefaultSite;
    private CryptCheckAdapter   mAdapter;
    private CryptCheckScan      mScan = null;

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
        rd_tls10 = rootView.findViewById(R.id.radioButtonTLS10);
        rd_tls11 = rootView.findViewById(R.id.radioButtonTLS2);
        rd_tls12 = rootView.findViewById(R.id.radioButtonTLS3);
    }

    public void                 init() {
        if (mAdapter == null) {
            mAdapter = new CryptCheckAdapter(mActivity);
            mRV_cryptcheck.setAdapter(mAdapter);
            mRV_cryptcheck.setLayoutManager(new LinearLayoutManager(mActivity));
            rd_tls10.setOnCheckedChangeListener(onCheckedChange());
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
                if (mScan != null) {
                    mActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            mActivity.newSearch(mDefaultSite);
                            ViewAnimate.scaleDown(mActivity, mRV_cryptcheck);
                            mAdapter.clear();
                        }
                    });
                }
                mScan = null;
                start();
            }
        }).show();
    }

    private CompoundButton.OnCheckedChangeListener onCheckedChange() {
        return new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAdapter.sort(buttonView);
            }
        };
    }

    public boolean              start() {

        try {
            CryptCheckApi.getInstance().callForSite(mInstance, mDefaultSite);
        } catch (IOException e) {
            e.printStackTrace();
            onResponseServer("");
        }
        return true;
    }

    public void                 onResponseServer(String result) {
        if (result == null || result.isEmpty())
            mActivity.showSnackbar(result);
        mActivity.mProgressBar.setVisibility(View.GONE);
    }

    public void                 onResponseServer(final CryptCheckScan scan) {
        mScan = scan;
        scan.dump();
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                rd_tls10.setEnabled(scan.isTLS10);
                rd_tls11.setEnabled(scan.isTLS11);
                rd_tls12.setEnabled(scan.isTLS12);
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
}
