package fr.dao.app.View.HostDiscovery;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.R;
import fr.dao.app.View.Settings.SettingsFrgmnt;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Dialog.QuestionMultipleAnswerDialog;

public class                        HostDiscoverySettingsFrgmnt extends SettingsFrgmnt {
    private MyActivity              mActivity;

    public View                     onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mActivity = (MyActivity) getActivity();
        mActivity.setToolbarTitle("Settings", "Network discovery");
        mActivity.setToolbarBackgroundColor(0x111111);
        buildSettings();
        return rootView;
    }

    private void                    buildSettings() {
        //nmapConfiguration();
        typeOfDiscovery();
    }

    private void                    typeOfDiscovery() {
        /**
         * [X] Voir les host découvert mais HORS LIGNE Aussi
         * [X] Voir les host découvert mais
         * [X] Rescan every host even when know
         * [X] See host even if scan is not OK (it means: present in ARP table but no response from Nmap
         */
        initDiscoveryMode();
        addItemMenu("Scan every time",
                "Start a new scan of the NetworkInformation without loading previous one from database",
                new Runnable() {
                    public void run() {
                        showSnackbar("not implemented");
                    }
                },
                "true",
                ContextCompat.getColor(mActivity, R.color.settingsSwitch),ContextCompat.getColor(mActivity, R.color.settingsSwitchBack));

        addItemMenu("Clever scan",
                "Doesn't scan every devices, just the ones we don't know",
                new Runnable() {
                    public void run() {
                        showSnackbar("not implemented");
                    }
                },
                "true",
                ContextCompat.getColor(mActivity, R.color.settingsSwitch),ContextCompat.getColor(mActivity, R.color.settingsSwitchBack));

        addItemMenu("Show my device",
                "Un/Hide your device in the list of hosts discovered",
                new Runnable() {
                    public void run() {
                        showSnackbar("not implemented");
                    }
                },
                "true",
                ContextCompat.getColor(mActivity, R.color.settingsSwitch),ContextCompat.getColor(mActivity, R.color.settingsSwitchBack));

        addItemMenu("Show Offline devices",
                "Un/show the list of devices previously recorded on the NetworkInformation, but offline anymore",
                new Runnable() {
                    public void run() {
                        showSnackbar("not implemented");
                    }
                },
                "true",
                ContextCompat.getColor(mActivity, R.color.settingsSwitch),ContextCompat.getColor(mActivity, R.color.settingsSwitchBack));

        addItemMenu("Debug mode",
                "Means a lot of thing and can slow the application",
                new Runnable() {
                    public void run() {
                        showSnackbar("not implemented");
                    }
                },
                "false",
                ContextCompat.getColor(mActivity, R.color.settingsSwitch),ContextCompat.getColor(mActivity, R.color.settingsSwitchBack));

        addItemMenu("Save every scan",
                "",
                new Runnable() {
                    public void run() {
                        showSnackbar("not implemented");
                    }
                },
                "true",
                ContextCompat.getColor(mActivity, R.color.settingsSwitch),ContextCompat.getColor(mActivity, R.color.settingsSwitchBack));
    }

    private void                    initDiscoveryMode() {
        final CharSequence[] items = new CharSequence[]{"Discrete", "Basic", "Advanced", "Brutal"};
        Runnable n = new Runnable() {
            public void run() {
                mActivity.runOnUiThread(new Runnable() {
                    public void run() {
                        new QuestionMultipleAnswerDialog(mActivity, items,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                        showSnackbar("Type of scan: " + items[selectedPosition]);
                                        Log.d("SettingsDiscovery", "Type of scan: " + items[selectedPosition]);
                                        Singleton.getInstance().Settings.getUserPreferences().NmapMode = selectedPosition;
                                        Singleton.getInstance().Settings.dump(Singleton.getInstance().Settings.getUserPreferences());
                                    }
                                }, "Type of NetworkInformation discovery", mSingleton.Settings.getUserPreferences().NmapMode, R.drawable.target);
                    }
                });
            }
        };
        addItemMenu(items[mSingleton.Settings.getUserPreferences().NmapMode] + " discovery",
                    "Type of scan who will be launch on the netwotk, Silence, Normal, Agressive, Insane",
                    n,
                    null,
                ContextCompat.getColor(mActivity, R.color.settingsSwitch),ContextCompat.getColor(mActivity, R.color.settingsSwitchBack));
    }

    private void                    showSnackbar(String txt) {
        mActivity.showSnackbar(txt);
    }
}
