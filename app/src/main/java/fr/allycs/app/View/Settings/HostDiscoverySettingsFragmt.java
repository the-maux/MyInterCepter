package fr.allycs.app.View.Settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HostDiscoverySettingsFragmt extends SettingsFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        nmapConfiguration();
        return rootView;
    }

    private void nmapConfiguration() {
        addItemMenu("Mise en veille de l'ecran",
                "Permet de laisser eveiller l'ecran lorsqu'une action est en cours",
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }
                }),
                mSingleton.isLockScreen());
    }
}
