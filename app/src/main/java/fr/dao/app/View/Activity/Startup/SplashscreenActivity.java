package fr.dao.app.View.Activity.Startup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Setup;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.View.Activity.HostDiscovery.HostDiscoveryActivity;

public class                    SplashscreenActivity extends AppCompatActivity {
    private int                 MAXIMUM_TRY_PERMISSION = 42, try_permission = 0;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Setup.buildPath(this);
        getRootPermission();
    }

    private void             getRootPermission() {
        Intent intent;
        Log.d("Splashscreen", "getRootPermission");
        if (rootCheck()) {
            if ((new File(Singleton.getInstance().FilesPath + "version").exists()))
                intent = new Intent(this, HostDiscoveryActivity.class);
            else
                intent = new Intent(this, SetupActivity.class);
            Log.d("Splashscreen", "getRootPermission:: OK");
            startActivity(intent);
            finish();
        } else {
            Log.d("Splashscreen", "getRootPermission::Error::Retry");

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (try_permission++ > MAXIMUM_TRY_PERMISSION)
                        finish();
                    getRootPermission();
                }
            }, 5000);
        }
    }
    private boolean             rootCheck() {
        return true;
//        try {
//
//            String RootOk = new RootProcess("RootCheck").exec("id").getReader().readLine();
//            return (RootOk != null && RootOk.contains("uid=0(root)"));
//        } catch (IOException e) {
//            Log.d("Splashscreen", "RootOK[IOException]");
//            e.printStackTrace();
//            return false;
//        }
    }
}
