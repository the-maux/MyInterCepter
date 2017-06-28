package su.sniff.cepter.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

import java.io.*;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;

public class                    InitActivity extends Activity {
    private String              TAG = "InitActivity";
    private Context             mCtx;

    private void                buildPath() {
        globalVariable.path = String.valueOf(mCtx.getFilesDir());
        Log.d(TAG, "path:" + globalVariable.path);
        globalVariable.PCAP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
    }


    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mCtx = this;
        buildPath();
        globalVariable.resurrection = 1;
        WifiManager wifiManager = (WifiManager) getSystemService("wifi");
        try {
            buildCepter();
            BufferedReader bufferedReader = buildBusybox();


            int c = 0;
            boolean found = false;
            while (true) {
                String read = "";
                read = bufferedReader.readLine();
                if (read == null) {
                    continue;
                } else {
                    Log.d(TAG, "read is " + read); //read: wlan0 : IP: 10.16.184.230 / 255.255.254.0
                }
                globalVariable.adapt_num = c + 1;
                found = true;
                int b = read.indexOf(" / ") + 3;
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
                Intent i = new Intent(this.mCtx, ScanActivity.class);
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


    private void                buildCepter() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("su");
        DataOutputStream dataOutputStream = new DataOutputStream(p.getOutputStream());
        InputStream cepter;


        File force = new File(globalVariable.path + "/force");
        if (force.exists()) {
            force.delete();
        }
        File ck = new File(globalVariable.path + "/ck");
        if (ck.exists()) {
            ck.delete();
        }
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
        File fDroidSheep = new File(globalVariable.path + "/cepter");
        if (fDroidSheep.exists()) {
            Log.d(TAG, "je n'ai pas supprimer le cepter");
            fDroidSheep.delete();
            fDroidSheep.setExecutable(true);
            fDroidSheep.setReadable(true);
        }
        FileOutputStream out = openFileOutput("cepter", 0);
        byte[] bufferDroidSheep = new byte[64];
        while (cepter.read(bufferDroidSheep) > -1) {
            out.write(bufferDroidSheep);
        }
        out.flush();
        out.close();


        fDroidSheep = new File(globalVariable.path + "/busybox");
        if (fDroidSheep.exists()) {
            fDroidSheep.delete();
            fDroidSheep.setExecutable(true);
            fDroidSheep.setReadable(true);
        }

        fDroidSheep = new File(globalVariable.path + "/savepath");
        if (fDroidSheep.exists()) {
            fDroidSheep.delete();
            fDroidSheep.setExecutable(true);
            fDroidSheep.setReadable(true);
        }

        out = openFileOutput("busybox", Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(out);
        InputStream su = getResources().openRawResource(R.raw.busybox);
        while (su.read(bufferDroidSheep) > -1) {
            oos.write(bufferDroidSheep);
        }
        oos.flush();
        oos.close();
        Log.d(TAG, "chmod 777 " + globalVariable.path + "/cepter\n");
        dataOutputStream.writeBytes("chmod 777 " + globalVariable.path + "/cepter\n");
        dataOutputStream.flush();
        Log.d(TAG, "chmod 777 " + globalVariable.path + "/busybox");
        dataOutputStream.writeBytes("chmod 777 " + globalVariable.path + "/busybox\n");
        dataOutputStream.flush();

        Log.d(TAG, "p.waitFor files/busybox");

        dataOutputStream.writeBytes("killall cepter\n");
        dataOutputStream.flush();
        dataOutputStream.close();
        p.waitFor();
    }
    private BufferedReader      buildBusybox() throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("su");
        DataOutputStream dataOutputStream = new DataOutputStream(p.getOutputStream());
        Context cc = this;
        cc = this;
        Process rootProcess = Runtime.getRuntime().exec("chmod");
        new DataOutputStream(rootProcess.getOutputStream()).close();
        rootProcess.waitFor();
        Log.d(TAG, "chmod rootProcess.waitFor()");


        if (!((ConnectivityManager) getSystemService("connectivity")).getNetworkInfo(1).isConnected()) {
            Toast.makeText(getApplicationContext(), "NO ACTIVE CONNECTION! TURN ON WIFI!", 1).show();
        }


        Process process = Runtime.getRuntime().exec("su", null, new File(globalVariable.path));
        dataOutputStream = new DataOutputStream(process.getOutputStream());

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        Log.d(TAG, "su " + globalVariable.path + "/cepter list");
        dataOutputStream.writeBytes(globalVariable.path + "/cepter list\n");
        dataOutputStream.flush();
        dataOutputStream.writeBytes("exit\n");
        dataOutputStream.flush();
        process.waitFor();
        return bufferedReader;
    }
}
