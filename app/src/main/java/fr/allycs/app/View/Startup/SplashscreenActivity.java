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

import fr.allycs.app.Controller.Core.Conf.Setup;
import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Tools.RootProcess;
import fr.allycs.app.View.HostDiscovery.HostDiscoveryActivity;


public class                    SplashscreenActivity extends AppCompatActivity {

    public boolean              isItUpdated() {
        try {
            Log.d("SplashscreenActivity", Singleton.getInstance().FilesPath + "version");
            return new File(Singleton.getInstance().FilesPath + "version").exists();
        } catch (Exception e) {
            e.getStackTrace();
            return false;
        }
    }

    public void                 onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void              onPostResume() {
        super.onPostResume();
        Setup.buildPath(this);
        if (!checkRoot())
            Toast.makeText(this, "You need root privilege", Toast.LENGTH_LONG).show();
        else {
            if (isItUpdated()) {
                startActivity(new Intent(this, HostDiscoveryActivity.class));
            } else {
                startActivity(new Intent(this, SetupActivity.class));
            }
            finish();
        }
    }

    private boolean             checkRoot() {
        try {
            String idUser = new BufferedReader(new RootProcess("Whoami").exec("id").getInputStreamReader()).readLine();
            Log.d("SplashscreenActivity", "whoami:" + idUser);
            if (idUser != null && idUser.contains("uid=0(root)") && idUser.contains("gid=0(root)")) {
                Log.d("SplashscreenActivity", "You have root privilege");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
