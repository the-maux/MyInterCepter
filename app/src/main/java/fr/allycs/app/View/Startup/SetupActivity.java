package fr.allycs.app.View.Startup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import fr.allycs.app.Controller.AndroidUtils.MyActivity;
import fr.allycs.app.Controller.Core.Configuration.Setup;
import fr.allycs.app.Controller.Core.RootProcess;
import fr.allycs.app.Controller.Network.NetUtils;
import fr.allycs.app.R;
import fr.allycs.app.View.HostDiscovery.HostDiscoveryActivity;

public class                    SetupActivity extends MyActivity {
    private String              TAG = "SetupActivity";
    private SetupActivity       mInstance = this;
    private static final int    PERMISSIONS_MULTIPLE_REQUEST = 123;
    /*static {
        System.loadLibrary("native-lib");
    }*/

    public void                 onCreate(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_setup, null);
        super.onCreate(savedInstanceState);
        setContentView(rootView);
        monitor("Initialization");
        if (getPermission()) {
            new RootProcess("Init").exec("ls").closeProcess();
            initialisation();
        }
        else
            monitor("You need to accept root permission to use the app");
    }

    private boolean             getPermission() {
        String[]     PERMISSION_STORAGE = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
             ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSION_STORAGE, PERMISSIONS_MULTIPLE_REQUEST);
            return false;
        }
        return true;
    }

    public void                 onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        final int REQUEST_PERMISSION = 1;
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    monitor("Vous ne pouvez pas utiliser l'application sans ces permissions");
                    getPermission();
                }
            }
        }
    }

    private void                initialisation() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    Setup.buildPath(mInstance);
                    Log.d(TAG, "SetupActivity::initialisation");
                    new Setup(mInstance).install();
                    monitor("Initialization..");
                    NetUtils.initNetworkInfo(mInstance);
                    monitor("Initialization...");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(mInstance, HostDiscoveryActivity.class));
                            finish();
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
