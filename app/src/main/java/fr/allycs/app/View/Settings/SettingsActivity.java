package fr.allycs.app.View.Settings;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;


import fr.allycs.app.Controller.Misc.MyActivity;
import fr.allycs.app.Controller.Misc.MyGlideLoader;
import fr.allycs.app.R;

/**
 * TODO: SSLStrip
 *          + Pcap directory
 *          + Version
 *          + Credits
 *          + Github
 *
 *          Comprendre pourquoi les pcap sont pas bien renommé alors que le log est ok
 *                      pk y a pas de sniffSession
 *                      pk des fois le tcpdump marche pas (surtout lorsqu'il s'agit de relancer tcpdump)
 *                      ajouter son propre device dans le scan
 *                      faire le saveMyPreviousRecord on startUp
 *
 */
public class                    SettingsActivity extends MyActivity {
    private String              TAG = "SettingsActivity";

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        MyGlideLoader.coordoBackground(this, (CoordinatorLayout) findViewById(R.id.coordinatorLayout));
    }

}
