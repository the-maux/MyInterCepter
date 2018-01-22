package fr.allycs.app.View.Startup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Tools.Intercepter;
import fr.allycs.app.Controller.Network.NetUtils;
import fr.allycs.app.View.HostDiscovery.HostDiscoveryActivity;


public class                    SplashscreenActivity extends AppCompatActivity {
    public boolean              isItUpdated() {
        try {
            return new File(Singleton.getInstance().FilesPath + "version").exists();//TODO: vérifier que les version concorde
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
        try {
            if (isItUpdated()) {
                if (!NetUtils.initNetworkInfo(this)) {
                    Toast.makeText(this, "Your not connected to a network", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Intercepter.getNetworkInfoByCept();//Is it still usefull? need to test without
                    startActivity(new Intent(this, HostDiscoveryActivity.class));
                }
            } else {
                startActivity(new Intent(SplashscreenActivity.this, SetupActivity.class));

            }
            finish();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
