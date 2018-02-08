package fr.allycs.app.View.Activity.Startup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import fr.allycs.app.Controller.AndroidUtils.MyActivity;
import fr.allycs.app.Controller.AndroidUtils.MyGlideLoader;
import fr.allycs.app.Controller.Core.Configuration.Setup;
import fr.allycs.app.Controller.Network.NetUtils;
import fr.allycs.app.R;
import fr.allycs.app.View.Activity.HostDiscovery.HostDiscoveryActivity;

public class                    SetupActivity extends MyActivity {
    private String              TAG = "SetupActivity";
    private SetupActivity       mInstance = this;
    private static final int    PERMISSIONS_MULTIPLE_REQUEST = 123;
    /*static {
        System.loadLibrary("native-lib");
    }*/

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_setup, null);
        setContentView(rootView);
        MyGlideLoader.coordoBackgroundXMM(this, (CoordinatorLayout)findViewById(R.id.Coordonitor));
        monitor("Requesting permission");
        getPermission();
    }

    private void                getPermission() {
        String[] PERMISSION_STORAGE = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            monitor("Need write permission for many reasons");
            ActivityCompat.requestPermissions(this, PERMISSION_STORAGE, PERMISSIONS_MULTIPLE_REQUEST);
            return ;
        } else {
            initialisation();
        }
    }

    public void                 onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            monitor("Vous ne pouvez pas utiliser l'application sans ces permissions");
        }
        getPermission();
    }

    private void                initialisation() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Setup.buildPath(mInstance);
                    Log.d(TAG, "Installation");
                    new Setup(mInstance).install();
                    monitor("Network initialization");
                    NetUtils.initNetworkInfo(mInstance);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Pair<View, String> p1;
                            p1 = Pair.create(findViewById(R.id.monitor), "title");
                            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(mInstance, p1);
                            startActivity(new Intent(mInstance, HostDiscoveryActivity.class), options.toBundle());

                        }
                    });
                } catch (IOException e) {
                    monitor("Error IO");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    monitor("Error Interupted");
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void                 monitor(final String log) {
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((TextView)findViewById(R.id.monitor)).setText(log);
            }
        });
    }

    //
    //   public native String        stringFromJNI();
}
