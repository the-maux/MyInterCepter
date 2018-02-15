package fr.allycs.app.View.Activity.HostDiscovery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.allycs.app.View.Activity.Settings.SettingsFragment;

public class FragmentHostDiscoverySettings extends SettingsFragment {
    private HostDiscoveryActivity   mActivity;

    public View                     onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        mActivity = (HostDiscoveryActivity)getActivity();
        setTitle("Host discovery settings");
        mActivity.setToolbarBackgroundColor(0x111111);
        buildSettings();
        return rootView;
    }

    private void                    buildSettings() {
        //nmapConfiguration();
        typeOfDiscovery();
    }

    private void                    typeOfDiscovery() {
        addItemMenu("Aggresive discovery",
                "Type of scan who will be launch on the netwotk, Silence, Normal, Agressive, Insane",
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        showSnackbar("not implemented");
                    }
                }),
                null);

        addItemMenu("Scan every time",
                "Start a new scan of the network without loading previous one from database",
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        showSnackbar("not implemented");
                    }
                }),
                "true");

        addItemMenu("Debug mode",
                "Means a lot of thing and can slow the application",
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                    }
                }),
                null);

        addItemMenu("Save every scan",
                "",
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                    }
                }),
                "true");
    }

    private void                    showSnackbar(String txt) {
        mActivity.showSnackbar(txt);
    }
}
