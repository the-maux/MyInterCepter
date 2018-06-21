package fr.dao.app.View.Sniff;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.dao.app.R;
import fr.dao.app.View.Settings.SettingsFrgmnt;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Dialog.QuestionMultipleAnswerDialog;

public class SniffSettingsFrgmnt extends SettingsFrgmnt {
    private String                  Title = "Settings Wireshark";
    private MyActivity              mActivity;

    public View                     onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mActivity = (MyActivity) getActivity();
        setTitle(Title);
        mActivity.setToolbarBackgroundColor(0x111111);//TODO change for the right color
        buildSettings();
        return rootView;
    }

    private void                    buildSettings() {
        /**
         * [X] Voir les host découvert mais HORS LIGNE Aussi
         * [X] Voir les host découvert mais
         * [X] Rescan every host even when know
         * [X] See host even if scan is not OK (it means: present in ARP table but no response from Nmap
         */
        initAskModeSetting();
        addItemMenu(Title,
                "No info",
                new Thread(new Runnable() {
                    public void run() {
                        showSnackbar("not implemented");
                    }
                }),
                "true",
                ContextCompat.getColor(mActivity, R.color.snifferSwitch),ContextCompat.getColor(mActivity, R.color.snifferSwitchBack));
        }

    private void                    initAskModeSetting() {
        final CharSequence[] items = new CharSequence[]{"Choice 1", "Choice 2", "Choice 3", "Choice 4"};
        //TODO: actualize title after click?
        Thread t = new Thread(new Runnable() {
            public void run() {
                final DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        showSnackbar("Type of scan: " + items[whichButton]);
                    }
                };
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        new QuestionMultipleAnswerDialog(mActivity, items,
                                click, "Type of NetworkInformation discovery", mSingleton.Settings.getUserPreferences().NmapMode);
                    }
                });
            }
        });
        addItemMenu(items[mSingleton.Settings.getUserPreferences().NmapMode] + " discovery",
                    "Choose your mode",
                    t,
                    null, 0, 0);
    }

    private void                    showSnackbar(String txt) {
        mActivity.showSnackbar(txt);
    }
}
