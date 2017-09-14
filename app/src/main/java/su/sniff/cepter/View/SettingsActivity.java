package su.sniff.cepter.View;

import android.os.Bundle;

import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.R;

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
