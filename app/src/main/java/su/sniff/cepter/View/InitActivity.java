package su.sniff.cepter.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import su.sniff.cepter.Controller.RootProcess;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

import java.io.*;
import java.util.Arrays;

public class                    InitActivity extends Activity {
    private String              TAG = "InitActivity";
    private InitActivity        mInstance = this;
    private TextView            monitor;

    /*static {
        System.loadLibrary("native-lib");
    }*/

    public void                 onCreate(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(this).inflate(R.layout.init_acitivty, null);
        super.onCreate(savedInstanceState);
        setContentView(rootView);
        initXml(rootView);
        new RootProcess("Init").closeProcess();
    }

    private void                initXml(View rootView) {
        monitor = (TextView) rootView.findViewById(R.id.monitor);
        monitor.setText("Initialization");
        findViewById(R.id.button7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initInfo();
                    }
                }).start();
            }
        });
    }

    @Override protected void    onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        buildPath();
        globalVariable.resurrection = 1;
        try {
            buildFiles();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    initInfo();
                }
            }).start();
        } catch (IOException e) {
            monitor.setText("Error IO");
            e.printStackTrace();
        } catch (InterruptedException e) {
            monitor.setText("Error Interupted");
            e.printStackTrace();
        }
    }

    private void                buildPath() {
        globalVariable.path = this.getFilesDir().getPath();
        Log.d(TAG, "path:" + globalVariable.path);
        globalVariable.PCAP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        monitor.setText("Building Path");
    }

    private void                initInfo() {
        try {
            Log.d(TAG, "init Net infos");
            RootProcess process = getNetworkInfoByCept();
            BufferedReader bufferedReader;
            int c = 0;
            String read;
            if ((bufferedReader = process.getReader()) == null)
                finish();
            while ((read = bufferedReader.readLine()) != null) {
                Log.d(TAG, "read is " + read); //read: wlan0 : IP: 10.16.184.230 / 255.255.254.0
                monitor("Initialization...");
                globalVariable.adapt_num = c + 1;
                String data = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getDhcpInfo().toString();
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
            }
        } catch (IOException e222) {
            //Toast.makeText(getApplicationContext(), "Broken pipe! Reinstall supersu and busybox!", Toast.LENGTH_SHORT).show();
            e222.getStackTrace();
        } catch (InterruptedException e322) {
            e322.getStackTrace();
        }
    }

    private RootProcess         getNetworkInfoByCept() throws IOException, InterruptedException {
        monitor("Get network Information Loading");
        RootProcess process = new RootProcess("getNetworkInfoByCept", globalVariable.path);
        monitor("Testing busybox... OK");
        Log.d(TAG, "su " + globalVariable.path + "/cepter list; exit");
        process.exec(globalVariable.path + "/cepter list");
        process.exec("exit");
        monitor("Get network Information");
        process.waitFor();
        return process;
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

    private void                buildFile(String nameFile, int ressource) throws IOException, InterruptedException {
        File file = new File(globalVariable.path + "/" + nameFile);
        file.delete();
        monitor.setText("Building " + nameFile);
        int size;
        InputStream inputStream = getResources().openRawResource(ressource);
        int sizeOfInputStram = inputStream.available();
        byte[] bufferDroidSheep = new byte[sizeOfInputStram];
        Arrays.fill( bufferDroidSheep, (byte) 0 );
        size = inputStream.read(bufferDroidSheep, 0, sizeOfInputStram);
        FileOutputStream out = openFileOutput(nameFile, Context.MODE_PRIVATE);
        out.write(bufferDroidSheep, 0, size);
        out.flush();
        out.close();
        inputStream.close();
        out.close();
        Log.d(TAG, "buildFile " + nameFile + "(" + sizeOfInputStram + "octet) and write :" + size);
        file.setExecutable(true, false);
    }

    private void                buildFiles() throws IOException, InterruptedException {
        FileOutputStream        out;
        byte[]                  bufferDroidSheep = new byte[64];

        clearingTmpFiles();
        InputStream cepter = getCepterRessource();
        File cepterFile = new File(globalVariable.path + "/cepter");
        if (cepterFile.exists() && cepterFile.canExecute()) {
            Log.d(TAG, "cepter exist");
        } else {
            cepterFile.delete();
            monitor.setText("Building cepter modules");
            Log.d(TAG, "Building cepter modules");
            out = openFileOutput("cepter", 0);
            while (cepter.read(bufferDroidSheep) > -1) {
                out.write(bufferDroidSheep);
            }
            out.flush();
            out.close();
            cepter.close();
        }
        cepterFile.setExecutable(true, false);
        buildFile("busybox", R.raw.busybox);
        buildFile("tcpdump", R.raw.tcpdump);
        buildFile("hydra", R.raw.hydra);
        buildFile("usernames", R.raw.usernames);
        buildFile("arpspoof", R.raw.arpspoof);
        buildFile("ettercap_archive", R.raw.ettercap_archive);
        buildFile("archive_nmap", R.raw.nmap);
        new RootProcess("UNZIP FILES", globalVariable.path)
                .exec("./busybox unzip ettercap_archive")
                .exec("./busybox unzip archive_nmap")
                .exec("chmod 777 ./nmap/*")
                .exec("chmod 777 ./*")
                .exec("rm -f ./Raw/*")
                .exec("killall cepter")
                .closeProcess();
    }

    private void                monitor(final String log) {
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
