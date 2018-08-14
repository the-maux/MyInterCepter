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
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import fr.dao.app.Core.Api.CryptCheckApi;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Config.Cryptcheck.Ciphers;
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
    private TextView            monitorProto;

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
        rd_tls10 = rootView.findViewById(R.id.radioButtonTLS1_0);
        rd_tls11 = rootView.findViewById(R.id.radioButtonTLS1_1);
        rd_tls12 = rootView.findViewById(R.id.radioButtonTLS1_2);
        monitorProto = rootView.findViewById(R.id.monitor_Protocol);
    }

    public void                 init() {
        if (mAdapter == null) {
            mAdapter = new CryptCheckAdapter(mActivity);
            mRV_cryptcheck.setAdapter(mAdapter);
            mRV_cryptcheck.setLayoutManager(new LinearLayoutManager(mActivity));
            mRV_cryptcheck.addOnScrollListener(new RecyclerView.OnScrollListener() {
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int pastVisibleItems =  ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastVisibleItemPosition();
                    updateSignal(pastVisibleItems);
                }
            });
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
                            monitorProto.setVisibility(View.GONE);
                            mActivity.newSearch(mDefaultSite);
                            ViewAnimate.scaleDown(mActivity, mRV_cryptcheck);
                            mAdapter.clear();
                        }
                    });
                }
                mActivity.mProgressBar.setVisibility(View.VISIBLE);
                mActivity.setToolbarTitle(null, "Scan in progress");
                mScan = null;
                start();
            }
        }).show();
    }

    private void                updateSignal(int pastVisibleItems) {
        /* Ici on affiche le title du protocol en lecture */
        if (mScan == null) {
            monitorProto.setVisibility(View.GONE);
            return;
        }
        ArrayList<Ciphers> c = mScan.getProtos(true, true, true, true);
        if (mScan.getTlsVersionFromItem(pastVisibleItems, c) == 0) {
            monitorProto.setText("TLSv1.0");
        } else if (mScan.getTlsVersionFromItem(pastVisibleItems, c) == 1) {
            monitorProto.setText("TLSv1.1");
        } else if (mScan.getTlsVersionFromItem(pastVisibleItems, c ) == 2) {
            monitorProto.setText("TLSv1.2");
        }

    }

    private CompoundButton.OnCheckedChangeListener onCheckedChange() {
        return new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAdapter.sort(buttonView, mScan);
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
        if (result != null && !result.isEmpty())
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
                monitorProto.setVisibility(View.VISIBLE);
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
