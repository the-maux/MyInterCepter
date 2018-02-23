package fr.dao.app.View.Activity.Startup;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Setup;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.View.Activity.HostDiscovery.HostDiscoveryActivity;

/**
 * Baigne toi dans le sang des mots si ils te plaisent, leurs sens ne t'appartiendra jamais. KT
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
            new Handler().postDelayed(new Runnable() {
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
