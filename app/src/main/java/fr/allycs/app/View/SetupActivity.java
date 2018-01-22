package fr.allycs.app.View;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import fr.allycs.app.Controller.Core.Conf.PreferenceControler;
import fr.allycs.app.Controller.Core.Conf.Setup;
import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Tools.Intercepter;
import fr.allycs.app.Controller.Core.Tools.RootProcess;
import fr.allycs.app.Controller.Misc.MyActivity;
import fr.allycs.app.Controller.Network.NetUtils;
import fr.allycs.app.R;
import fr.allycs.app.View.HostDiscovery.HostDiscoveryActivity;

public class                    SetupActivity extends MyActivity {
    private String              TAG = "SetupActivity";
    private SetupActivity       mInstance = this;
    private TextView            monitor;
    private Singleton           mSingleton = Singleton.getInstance();
    private final int           REQUEST_PERMISSION = 1;
    private static final int    PERMISSIONS_MULTIPLE_REQUEST = 123;
    /*static {
        System.loadLibrary("native-lib");
    }*/

    public void                 onCreate(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_setup, null);
        this.splashscreen = false;
        super.onCreate(savedInstanceState);
        setContentView(rootView);
        initXml(rootView);
        Log.d(TAG, "SetupActivity::onPostResume");
        new RootProcess("Init").closeProcess();
        if (getPermission())
            initialisation();
        else
            Snackbar.make(findViewById(R.id.Coordonitor), "You need to accept root permission to use the app", Snackbar.LENGTH_LONG).show();
    }

    private boolean             getPermission() {
        String[]     PERMISSION_STORAGE = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };

        int writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (writePermission != PackageManager.PERMISSION_GRANTED ||
                readPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, PERMISSION_STORAGE, PERMISSIONS_MULTIPLE_REQUEST);
            return false;
        }
        return true;
    }

    public void                 onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    Snackbar.make(findViewById(R.id.Coordonitor), "Vous ne pouvez pas utiliser l'application sans ces permissions", Snackbar.LENGTH_LONG).show();
                    getPermission();
                }
            }
        }
    }

    private void                initXml(View rootView) {
        monitor = rootView.findViewById(R.id.monitor);
        monitor("Initialization");
    }

    private void                initialisation() {
        buildPath();
        Log.d(TAG, "SetupActivity::initialisation");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    install();
                    initInfo();
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

    private void                buildPath() {
        mSingleton.FilesPath = mInstance.getFilesDir().getPath() + '/';
        mSingleton.BinaryPath = mSingleton.FilesPath;//shouldn't be the same as FilesPath
        mSingleton.PcapPath = Environment.getExternalStorageDirectory().getPath() + "/Pcap/";
        mSingleton.userPreference = new PreferenceControler(mSingleton.FilesPath);
        Log.d(TAG, "nameFile:" + mSingleton.FilesPath);
        monitor("Building Path");
    }

    private void                initInfo() throws FileNotFoundException {
        try {
            monitor("Initialization...");
            Log.d(TAG, "SetupActivity::initInfo");
            //Log.d(TAG, "init Net infos");
            Intercepter.getNetworkInfoByCept();//Is it still usefull? need to test without
            monitor("Discovering network architecture");
            if (!NetUtils.initNetworkInfo(this)) {
                Toast.makeText(this, "Your not connected to a network", Toast.LENGTH_LONG).show();
                finish();
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(mInstance, HostDiscoveryActivity.class));
                        finish();
                    }
                });
            }
        } catch (InterruptedException e2) {
            e2.getStackTrace();
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private void                install() throws IOException, InterruptedException {
        new Setup(this).install();
    }//10:68:3f:7a:65:ef ___ 10.16.186.54/23 brd 10.16.187.255

    public void                 monitor(final String log) {
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                monitor.setText(log);
            }
        });
    }

    //
    //   public native String        stringFromJNI();
}
