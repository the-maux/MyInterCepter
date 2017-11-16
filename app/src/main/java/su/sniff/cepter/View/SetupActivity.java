package su.sniff.cepter.View;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import su.sniff.cepter.Controller.Core.Conf.Setup;
import su.sniff.cepter.Controller.Network.NetUtils;
import su.sniff.cepter.Controller.Core.Conf.Singleton;
import su.sniff.cepter.Controller.Core.BinaryWrapper.RootProcess;
import su.sniff.cepter.Controller.Misc.MyActivity;
import su.sniff.cepter.Model.Net.NetworkInformation;
import su.sniff.cepter.R;
import su.sniff.cepter.View.HostDiscovery.HostDiscoveryActivity;

import java.io.*;
import java.util.Arrays;

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
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_init, null);
        this.splashscreen = false;
        super.onCreate(savedInstanceState);
        setContentView(rootView);
        initXml(rootView);
    }

    @Override protected void    onResume() {
        super.onResume();
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
                return;
            }
        }
    }

    private void                initXml(View rootView) {
        monitor = (TextView) rootView.findViewById(R.id.monitor);
        monitor("Initialization");
    }

    private void                initialisation() {
        buildPath();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!isItUpdated()) {
                        install();
                    }
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
        mSingleton.PcapPath = "/sdcard/Pcap/";
        Log.d(TAG, "path:" + mSingleton.FilesPath);
        monitor("Building Path");
    }

    private void                initInfo() throws FileNotFoundException {
        monitor("Initialization...");
        try {
            //Log.d(TAG, "init Net infos");
            getNetworkInfoByCept();//Is it still usefull? need to test without
        } catch (InterruptedException e2) {
            e2.getStackTrace();
        } catch (IOException e) {
            e.getStackTrace();
        }
        DhcpInfo dhcpInfo = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getDhcpInfo();
        String data = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getDhcpInfo().toString();
        if (!data.contains("ipaddr") || !data.contains("gateway") || !data.contains("netmask") ) {
            Toast.makeText(this, "Your not connected to a network", Toast.LENGTH_LONG).show();
            finish();
            return ;
        }
        String[] res = data.split(" ");
        int ip = 0, gw = 0, netmask = 0;
        for (int i = 0; i < res.length; i++) {
            if (res[i].contains("ipaddr")) {
                ip = i + 1;
            } else if (res[i].contains("gateway")) {
                gw = i + 1;
            } else if (res[i].contains("netmask")) {
                netmask = i + 1;
            }
        }
        if (res[netmask].contains("0.0.0.0"))
            res[netmask] = "255.255.255.0";
        monitor("Discovering network architecture");
        mSingleton.network = new NetworkInformation(dhcpInfo, NetUtils.getMac(res[ip], res[gw]));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(mInstance, HostDiscoveryActivity.class));
                finish();
            }
        });
    }

    private RootProcess         getNetworkInfoByCept() throws IOException, InterruptedException {
        Log.d(TAG, "su " + mSingleton.FilesPath + "cepter list; exit");
        monitor("Get network Information");
        RootProcess process = new RootProcess("getNetworkInfoByCept", mSingleton.FilesPath)
                .exec(mSingleton.FilesPath + "cepter list")
                .exec("exit");
        return process;
    }

    private void                install() throws IOException, InterruptedException {
        new Setup(this).install();
    }//10:68:3f:7a:65:ef ___ 10.16.186.54/23 brd 10.16.187.255

    public void                monitor(final String log) {
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                monitor.setText(log);
            }
        });
    }

    public boolean              isItUpdated() {
        return new File(mSingleton.FilesPath + "version").exists();//TODO: vérifier que les version concorde
    }

    //
    //   public native String        stringFromJNI();
}
