package fr.allycs.app.View.Startup;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import fr.allycs.app.Controller.Core.Configuration.Setup;
import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.RootProcess;
import fr.allycs.app.View.HostDiscovery.HostDiscoveryActivity;

/**
 * Init App &
 * Check Root
 * Checking latest version in file for update
 */
public class                    SplashscreenActivity extends AppCompatActivity {
    private int                 MAXIMUM_TRY_PERMISSION = 42, try_permission = 0;

    protected void              onPostResume() {
        super.onPostResume();
        Setup.buildPath(this);
        getRootPermission();
    }

    private void             getRootPermission() {
        Intent intent;
        if (rootCheck()) {
            /** TODO: Versionning */
            if ((new File(Singleton.getInstance().FilesPath + "version").exists()))
                intent = new Intent(this, HostDiscoveryActivity.class);
            else
                intent = new Intent(this, SetupActivity.class);
            startActivity(intent);
            finish();
        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (try_permission++ > MAXIMUM_TRY_PERMISSION)
                        finish();
                    getRootPermission();
                }
            }, 5000);
        }
    }
    private boolean             rootCheck() {
        try {
            String RootOk = new RootProcess("RootCheck").exec("id").getReader().readLine();
            return (RootOk != null && RootOk.contains("uid=0(root)"));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
