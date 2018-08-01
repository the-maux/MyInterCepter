package fr.dao.app.View.Cryptcheck;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;

import fr.dao.app.Core.Api.CryptCheckApi;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Model.Config.CryptCheckModel;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Dialog.QuestionDialogInput;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

public class                    CryptFrgmnt extends MyFragment  {
    private String              TAG = "NmapOutputView";
    private CryptCheckActivity  mActivity;
    private CryptFrgmnt         mInstance = this;
    private ConstraintLayout    mCoordinatorLayout;
    private TextView            output;
    private ProgressBar         progressBarCrypt;
    private String              mDefaultSite = Singleton.getInstance().Settings.getUserPreferences().defaultTarget;

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
    }

    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        output = rootView.findViewById(R.id.textView2);
        progressBarCrypt = rootView.findViewById(R.id.progressBarCrypt);
    }

    public void                 onResponseServer(String result) {
        if (result == null || result.isEmpty())
            mActivity.showSnackbar("Server didnt answer");
        else
            output.setText(result);
        progressBarCrypt.setVisibility(View.GONE);
    }

    public void                 onResponseServer(CryptCheckModel siteAnal) {
        if (!siteAnal.analysed)
            mActivity.showSnackbar("Server didnt answer");
        else
            output.setText("RESULT");
        progressBarCrypt.setVisibility(View.GONE);
    }
}
