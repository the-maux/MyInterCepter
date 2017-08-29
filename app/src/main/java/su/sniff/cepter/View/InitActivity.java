package su.sniff.cepter.View;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import su.sniff.cepter.Controller.Network.NetUtils;
import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Controller.System.RootProcess;
import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.Model.Target.NetworkInformation;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

import java.io.*;
import java.util.Arrays;

public class                    InitActivity extends MyActivity {
    private String              TAG = "InitActivity";
    private InitActivity        mInstance = this;
    private TextView            monitor;

    /*static {
        System.loadLibrary("native-lib");
    }*/

    public void                 onCreate(Bundle savedInstanceState) {
        View rootView = LayoutInflater.from(this).inflate(R.layout.activity_init, null);
        super.onCreate(savedInstanceState);
        setContentView(rootView);
        initXml(rootView);
        new RootProcess("Init").closeProcess();
    }

    private void                initXml(View rootView) {
        monitor = (TextView) rootView.findViewById(R.id.monitor);
        monitor("Initialization");
    }

    @Override protected void    onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        buildPath();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        buildFiles();
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
        Singleton.FilesPath = this.getFilesDir().getPath() + '/';
        Singleton.BinaryPath = Singleton.FilesPath;//shouldn't be the same as FilesPath
        Log.d(TAG, "path:" + Singleton.FilesPath);
        globalVariable.PCAP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        monitor("Building Path");
    }

    private void                initInfo() throws FileNotFoundException {
        monitor("Initialization...");
        try {
            Log.d(TAG, "init Net infos");
            getNetworkInfoByCept();//Is it still usefull? need to test without
        } catch (InterruptedException e2) {
            e2.getStackTrace();
        } catch (IOException e) {
            e.getStackTrace();
        }
        globalVariable.adapt_num = 1;
        String data = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getDhcpInfo().toString();
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
        if (!data.contains("ipaddr") || !data.contains("gateway") || !data.contains("netmask") ) {
            Toast.makeText(this, "Your not connected to a network", Toast.LENGTH_LONG).show();
            finish();
            return ;
        }
        if (res[netmask].contains("0.0.0.0"))
            res[netmask] = "255.255.255.0";
        Singleton.network = new NetworkInformation(res[ip], res[gw], res[netmask], NetUtils.getMac(res[ip], res[gw]));
        Intent i = new Intent(this, ScanActivity.class);
        i.putExtra("Key_Int", 1);//1 est censé représenté l'interface network, en l'occurence 1=> eth0
        i.putExtra("Key_String", Singleton.network.myIp);
        startActivity(i);
        finish();

    }

    private RootProcess         getNetworkInfoByCept() throws IOException, InterruptedException {
        monitor("Get network Information Loading");
        RootProcess process = new RootProcess("getNetworkInfoByCept", Singleton.FilesPath);
        monitor("Testing busybox... OK");
        Log.d(TAG, "su " + Singleton.FilesPath + "/cepter list; exit");
        process.exec(Singleton.FilesPath + "/cepter list");
        process.exec("exit");
        monitor("Get network Information");
        process.waitFor();
        return process;
    }

    private void                clearingTmpFiles() {
        monitor("Clearing previous Data");
        File force = new File(Singleton.FilesPath + "/force");
        if (force.exists()) {
            Log.d(TAG, "Deleting /force");
            force.delete();
        }
        File ck = new File(Singleton.FilesPath + "/ck");
        if (ck.exists()) {
            Log.d(TAG, "Deleting /ck");
            ck.delete();
        }
        File savepath = new File(Singleton.FilesPath + "/savepath");
        if (savepath.exists()) {
            Log.d(TAG, "Deleting /savepath");
            savepath.delete();
        }
        monitor("Clearing previous Data over");
    }

    private InputStream         getCepterRessource() {
        InputStream cepter;
        cepter = getResources().openRawResource(R.raw.cepter_android_21_armeabi);
        if (Build.CPU_ABI.contains("x86")) {
            cepter = getResources().openRawResource(R.raw.cepter_android_21_x86);
        }
        if (Build.CPU_ABI.contains("arm64")) {
            cepter = getResources().openRawResource(R.raw.cepter_android_21_arm64_v8a);
        }
        Log.d(TAG, "Return Cepter ressource");
        return cepter;
    }

    private void                buildFile(String nameFile, int ressource) throws IOException, InterruptedException {
        File file = new File(Singleton.FilesPath + "/" + nameFile);
        file.delete();
        monitor("Building " + nameFile);
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
        File cepterFile = new File(Singleton.FilesPath + "/cepter");
        if (cepterFile.exists() && cepterFile.canExecute()) {
            Log.d(TAG, "cepter exist, nothing to do");
        } else {
            cepterFile.delete();
            monitor("Building cepter modules");
            Log.d(TAG, "Building cepter binary");
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
        RootProcess process = new RootProcess("UNZIP FILES", Singleton.FilesPath);
        process.exec(Singleton.BinaryPath + "/busybox unzip ettercap_archive")
                .exec(Singleton.BinaryPath + "/busybox unzip archive_nmap")
                .exec("chmod 777 " + Singleton.BinaryPath + "/nmap/*")
                .exec("mount -o rw,remount /system")
                .exec("cp ./ping /system/bin/")
                .exec("echo \"nameserver `getprop net.dns1`\" > /etc/resolv.conf")
                .exec("rm -f " + Singleton.FilesPath + "/Raw/*;")
                .exec("rm -f " + Singleton.FilesPath + "/dnss ;")
                .exec("rm -f " + Singleton.FilesPath + "/hostlist;")
                .exec("rm -f " + Singleton.FilesPath + "/*Activity")
                .exec(Singleton.BinaryPath + "busybox killall cepter")
                .exec(Singleton.BinaryPath + "busybox killall tcpdump")
                .exec(Singleton.BinaryPath + "busybox killall arpspoof")
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
