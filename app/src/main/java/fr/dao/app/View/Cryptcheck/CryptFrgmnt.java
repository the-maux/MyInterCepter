package fr.dao.app.View.Cryptcheck;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
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
    private CryptCheckActivity  mActivity;
    private CryptFrgmnt         mInstance = this;
    private NestedScrollView    rootViewCryptFragment;
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

    private void                initXml(View rootView) {
        mRV_cryptcheck = rootView.findViewById(R.id.RV_cryptcheck);
        rootViewCryptFragment = rootView.findViewById(R.id.rootViewCryptFragment);
        rootViewCryptFragment.setVisibility(View.INVISIBLE);
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
        dialog.onPositiveButton("Run", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                String defaultSite = dialog.getFirstInputQuestion();
                mDefaultSite = (defaultSite.isEmpty()) ? mDefaultSite : defaultSite;
                mActivity.setToolbarTitle(null, mDefaultSite);
                mScan = null;
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        mActivity.mProgressBar.setVisibility(View.VISIBLE);
                    }
                });
                mActivity.updateHeader(true);
                mAdapter.clear();
                start();
            }
        }).show();
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
                mActivity.mProgressBar.setVisibility(View.GONE);
                reloadView();
                mActivity.onResponseServer(scan);
                ViewAnimate.reveal(mActivity, rootViewCryptFragment, null);
            }
        });
    }

    public void                 reloadView() {
        if (mScan != null) {
            mAdapter.putOnListOfTrame(mScan.getProtos());
        }
    }


}
