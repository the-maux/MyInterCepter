package su.sniff.cepter.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import su.sniff.cepter.Controller.RootProcess;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

import java.io.*;

public class                    InitActivity extends Activity {
    private String              TAG = "InitActivity";
    private TextView            monitor;

    public void                 onCreate(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(this).inflate(R.layout.init_acitivty, null);
        super.onCreate(savedInstanceState);
        setContentView(rootView);
        initXml(rootView);
    }

    @Override
    protected void               onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        buildPath();
        globalVariable.resurrection = 1;
        try {
            buildFiles();
        } catch (IOException e) {
            monitor.setText("Error IO");
            e.printStackTrace();
        } catch (InterruptedException e) {
            monitor.setText("Error Interupted");
            e.printStackTrace();
        }
    }

    private void                initXml(View rootView) {
        monitor = (TextView) rootView.findViewById(R.id.monitor);
        monitor.setText("Initialization");
        findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initInfo();
            }
        });
    }

    private void                buildPath() {
        globalVariable.path = String.valueOf(this.getFilesDir());
        Log.d(TAG, "path:" + globalVariable.path);
        globalVariable.PCAP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        monitor.setText("Building Path");
    }

    private void                buildFiles() throws IOException, InterruptedException {
        FileOutputStream        out;
        byte[]                  bufferDroidSheep = new byte[64];

        clearingTmpFiles();
        InputStream cepter = getCepterRessource();
        File cepterFile = new File(globalVariable.path + "/cepter");
        if (cepterFile.exists()) {
            Log.d(TAG, "cepter exist");
            //fDroidSheep.delete();
        } else {
            monitor.setText("Building cepter modules");
            Log.d(TAG, "Building cepter modules");
            out = openFileOutput("cepter", 0);
            while (cepter.read(bufferDroidSheep) > -1) {
                out.write(bufferDroidSheep);
            }
            out.flush();
            out.close();
            Log.d(TAG, "natif chmod +x cepter: " + cepterFile.setExecutable(true, false));
            Log.d(TAG, "natif chmod +r cepter: " + cepterFile.setReadable(true, false));
//            cepterFile.setWritable(true, false);
        }

        File busyboxFile = new File(globalVariable.path + "/busybox");
        if (busyboxFile.exists()) {
            Log.d(TAG, "Busybox exist");
//            fDroidSheep.delete();
        } else {
            Log.d(TAG, "Building busybox");
            monitor.setText("Building busybox");
            out = openFileOutput("busybox", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(out);
            InputStream su = getResources().openRawResource(R.raw.busybox);
            while (su.read(bufferDroidSheep) > -1) {
                oos.write(bufferDroidSheep);
            }
            oos.flush();
            oos.close();
            Log.d(TAG, "natif chmod +x cepter: " + busyboxFile.setExecutable(true, false));
            Log.d(TAG, "natif chmod +r cepter: " + busyboxFile.setReadable(true, false));
        }

/*
        Log.d(TAG, "chmod 777 " + globalVariable.path + "/cepter\n");
        dataOutputStream.writeBytes("chmod 777 " + globalVariable.path + "/cepter\n");
        dataOutputStream.flush();
        Log.d(TAG, "chmod 777 " + globalVariable.path + "/busybox");
        dataOutputStream.writeBytes("chmod 777 " + globalVariable.path + "/busybox\n");
        dataOutputStream.flush();

        Log.d(TAG, "p.waitFor files/busybox");*/
        new RootProcess("Kill cepter").exec("killall cepter").closeProcess();
/*        Process                 p = Runtime.getRuntime().exec("su");
        DataOutputStream        dataOutputStream = new DataOutputStream(p.getOutputStream());

        dataOutputStream.writeBytes("killall cepter\n");
        dataOutputStream.flush();
        dataOutputStream.close();
        p.waitFor();*/
    }

    private void                initInfo() {
        try {
            WifiManager wifiManager = (WifiManager) getSystemService("wifi");
            Log.d(TAG, "init Net infos");
            BufferedReader bufferedReader = cepterList();
            if (bufferedReader == null)
                finish();
            int c = 0;
            while (true) {
                String read = "";
                read = bufferedReader.readLine();
                if (read == null) {
                    Log.d(TAG, "waiting Net Info from cepter");
                    continue;
                } else {
                    Log.d(TAG, "read is " + read); //read: wlan0 : IP: 10.16.184.230 / 255.255.254.0
                }
                globalVariable.adapt_num = c + 1;
                String data = wifiManager.getDhcpInfo().toString();
                String[] res = data.split(" ");
                for (int i = 0; i < res.length; i++) {
                    if (res[i].contains("ipaddr"))
                        globalVariable.own_ip = res[i+1];
                    else if (res[i].contains("gateway")) {
                        globalVariable.gw_ip = res[i+1];
                    } else if (res[i].contains("netmask")) {
                        globalVariable.netmask = res[i+1];
                    }
                }
                if (globalVariable.netmask.contains("0.0.0.0"))
                    globalVariable.netmask = "255.255.255.0";
                Log.d(TAG, "All : " + data);
                Log.d(TAG, "IP:" + globalVariable.own_ip);
                Log.d(TAG, "GW:" + globalVariable.gw_ip);
                Log.d(TAG, "NetMask:" + globalVariable.netmask);
                Intent i = new Intent(this, ScanActivity.class);
                i.putExtra("Key_Int", c);
                i.putExtra("Key_String", globalVariable.own_ip);
                bufferedReader.close();
                startActivity(i);
                finish();
                break;
            }
        } catch (IOException e222) {
            Toast.makeText(getApplicationContext(), "Broken pipe! Reinstall supersu and busybox!", 1).show();
            e222.getStackTrace();
        } catch (InterruptedException e322) {
            e322.getStackTrace();
        }
    }

    private BufferedReader      cepterList() throws IOException, InterruptedException {
        monitor.setText("Testing busybox");
        Process process = Runtime.getRuntime().exec("su", null, new File(globalVariable.path));
        DataOutputStream dataOutputStream = new DataOutputStream(process.getOutputStream());
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        monitor.setText("./cepter list");
        Log.d(TAG, "su " + globalVariable.path + "/cepter list; exit");
        dataOutputStream.writeBytes(globalVariable.path + "/cepter list\n");
        dataOutputStream.flush();
        dataOutputStream.writeBytes("exit\n");
        dataOutputStream.flush();
        process.waitFor();
        return bufferedReader;
    }

    private InputStream         getCepterRessource() {
        InputStream             cepter;

        if (VERSION.SDK_INT < 21) {
            cepter = getResources().openRawResource(R.raw.cepter_android_14_armeabi);
        } else {
            cepter = getResources().openRawResource(R.raw.cepter_android_21_armeabi);
            if (Build.CPU_ABI.contains("x86")) {
                cepter = getResources().openRawResource(R.raw.cepter_android_21_x86);
            }
        }
        if (Build.CPU_ABI.contains("arm64")) {
            cepter = getResources().openRawResource(R.raw.cepter_android_21_arm64_v8a);
        }
        Log.d(TAG, "Return Cepter ressource");
        return cepter;
    }

    private void                clearingTmpFiles() {
        monitor.setText("Clearing previous Data");
        File force = new File(globalVariable.path + "/force");
        if (force.exists()) {
            Log.d(TAG, "Deleting /force");
            force.delete();
        }
        File ck = new File(globalVariable.path + "/ck");
        if (ck.exists()) {
            Log.d(TAG, "Deleting /ck");
            ck.delete();
        }
        File savepath = new File(globalVariable.path + "/savepath");
        if (savepath.exists()) {
            Log.d(TAG, "Deleting /savepath");
            savepath.delete();
        }
        monitor.setText("Clearing previous Data over");
    }

}
