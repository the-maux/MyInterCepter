package fr.allycs.app.View.Startup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import fr.allycs.app.Controller.Core.Configuration.Setup;
import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.Core.RootProcess;
import fr.allycs.app.View.HostDiscovery.HostDiscoveryActivity;

/**
 * Init App &
 * Check Root
 * Checking latest version in file for update
 */
public class                    SplashscreenActivity extends AppCompatActivity {

    protected void              onPostResume() {
        super.onPostResume();
        Setup.buildPath(this);
        Intent intent = null;
        try {
            Log.d("SplashscreenActivity", Singleton.getInstance().FilesPath + "version");
            if ((new BufferedReader(new RootProcess("Whoami").exec("id")/*Check Root Process*/
                    .getInputStreamReader()).readLine().contains("uid=0(root)")) &&
                    (new File(Singleton.getInstance().FilesPath + "version").exists()))/*Versionning Update*/
                intent = new Intent(this, HostDiscoveryActivity.class);
            else
                intent = new Intent(this, SetupActivity.class);
        } catch (Exception e) {
            e.getStackTrace();
            intent = new Intent(this, SetupActivity.class);
        } finally {
            startActivity(intent);
            finish();
        }
    }
}
