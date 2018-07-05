package fr.dao.app.View.Startup;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.IOException;

import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.Core.Configuration.Setup;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Network.NetDiscovering;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;

public class                    SetupActivity extends MyActivity {
    private String              TAG = "SetupActivity";
    private SetupActivity       mInstance = this;
    private static final int    PERMISSIONS_MULTIPLE_REQUEST = 123;
    private boolean flag = false;
    /*static {
        System.loadLibrary("native-lib");
    }*/
    //TODO: TU DOIS PROTEGER CONTRE LE NOROOT ET FAIRE LA VISU INSTALL CLASS
    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_setup, null);
        setContentView(rootView);
        monitor("Requesting permission");
        setStatusBarColor(R.color.generic_background);
    }

    private void                getPermission() {
        if (!flag) {
            Log.i(TAG, "Get PERMISSION");
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
                if (rootCheck())
                    initialisation();
                else {
                    Intent i = new Intent(mInstance, HomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
            }
        } else {
            Log.e(TAG, "multiple init DETECTED /!\\");
        }
    }
    private boolean             rootCheck() {
        try {
            String RootOk = new RootProcess("RootCheck").exec("id").getReader().readLine();
            return (RootOk != null && RootOk.contains("uid=0(root)"));
        } catch (IOException e) {
            Log.d("Splashscreen", "RootOK[IOException]");
            e.printStackTrace();
            return false;
        }
    }
    public void                 onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            monitor("Vous ne pouvez pas utiliser l'application sans ces permissions");
        } else
            getPermission();
    }

    protected void              onPostResume() {
        super.onPostResume();
        getPermission();
    }


    private void                initialisation() {
        new Thread(new Runnable() {
            public void run() {
                Log.e(TAG, "INSTALL OK /!\\");
                flag = true;
                try {
                    Singleton.getInstance().init(mInstance);
                    Log.d(TAG, "Installation");
                    monitor("Network initialization");
                    NetDiscovering.initNetworkInfo(mInstance);
                    new Setup(mInstance).install();
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
            public void run() {
                TextView t = findViewById(R.id.monitor);
                String n = String.valueOf(t.getText()).replace(log.replace(" OK", "")+"\n", "").replace(log+"\n", "");
                t.setText(String.format("%s%s\n", n, log));
                ((ScrollView) findViewById(R.id.scrollView)).fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    public void                 setupFinished() {
        runOnUiThread(new Runnable() {
            public void run() {
                Pair<View, String> p1;
                Intent i = new Intent(mInstance, HomeActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(i);
            }
        });
    }

    @Override
    public void             onBackPressed() {
        //super.onBackPressed();
    }

    //
    //   public native String        stringFromJNI();
}
