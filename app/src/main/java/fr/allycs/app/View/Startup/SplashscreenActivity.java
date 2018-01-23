package fr.allycs.app.View.Startup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;

import fr.allycs.app.Controller.Core.Conf.Setup;
import fr.allycs.app.Controller.Core.Conf.Singleton;
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
 //       try {
            Setup.buildPath(this);
            if (isItUpdated()) {
                //Intercepter.getNetworkInfoByCept();
                startActivity(new Intent(this, HostDiscoveryActivity.class));
            } else {
                startActivity(new Intent(SplashscreenActivity.this, SetupActivity.class));
            }
            finish();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
