package fr.allycs.app.View;

import android.os.Bundle;

import fr.allycs.app.Controller.Misc.MyActivity;
import fr.allycs.app.R;

/**
 * TODO: SSLStrip
 *          + Pcap directory
 *          + Version
 *          + Credits
 *          + Github
 */
public class                    SettingsActivity extends MyActivity {
    private String              TAG = "SettingsActivity";

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

}
