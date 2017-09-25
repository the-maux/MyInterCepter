package su.sniff.cepter.View;

import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import su.sniff.cepter.Controller.Network.NetUtils;
import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Controller.System.Wrapper.RootProcess;
import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.Model.Target.NetworkInformation;
import su.sniff.cepter.R;

import java.io.*;
import java.util.Arrays;

public class                    InitActivity extends MyActivity {
    private String              TAG = "InitActivity";
    private InitActivity        mInstance = this;
    private TextView            monitor;
    private Singleton           singleton = Singleton.getInstance();

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
                        if (!isItUpdated())
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
        singleton.FilesPath = mInstance.getFilesDir().getPath() + '/' + "Files/";
        singleton.BinaryPath = singleton.FilesPath;//shouldn't be the same as FilesPath
        singleton.PcapPath = "/sdcard/Pcap/";
        Log.d(TAG, "path:" + singleton.FilesPath);
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
        singleton.network = new NetworkInformation(dhcpInfo, NetUtils.getMac(res[ip], res[gw]));
        startActivity(new Intent(this, ScanActivity.class));
        finish();
    }

    private RootProcess         getNetworkInfoByCept() throws IOException, InterruptedException {
        monitor("Get network Information Loading");
        RootProcess process = new RootProcess("getNetworkInfoByCept", singleton.FilesPath);
        monitor("Testing busybox... OK");
        Log.d(TAG, "su " + singleton.FilesPath + "cepter list; exit");
        process.exec(singleton.FilesPath + "cepter list");
        process.exec("exit");
        monitor("Get network Information");
        process.waitFor();
        return process;
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
        monitor("Building " + nameFile);
        File file = new File(singleton.FilesPath + nameFile);
        file.delete();
        file.createNewFile();
        InputStream inputStream = (nameFile.contains("cepter")) ? //Choix architecture doit etre fait sur tout les binaires
                getCepterRessource() : getResources().openRawResource(ressource);
        int sizeOfInputStram = inputStream.available();
        byte[] bufferDroidSheep = new byte[sizeOfInputStram];
        Arrays.fill(bufferDroidSheep, (byte) 0 );
        int size = inputStream.read(bufferDroidSheep, 0, sizeOfInputStram);
        FileOutputStream out = new FileOutputStream(file);
        out.write(bufferDroidSheep, 0, size);
        out.flush();
        out.close();
        inputStream.close();
        out.close();
        Log.d(TAG, "buildFile " + nameFile + "(" + sizeOfInputStram + "octet) and write :" + size);
        new RootProcess("Install ").exec("chmod 744 " + singleton.FilesPath + nameFile).closeProcess();
    }

    private void                install() throws IOException, InterruptedException {
        /*  Build directory    */
        new RootProcess("Install ").exec("mkdir -p /sdcard/Pcap").closeProcess();
        new RootProcess("Install ").exec("mkdir -p " + singleton.FilesPath ).closeProcess();
        new RootProcess("Install ").exec("chmod 777 " + singleton.FilesPath ).closeProcess();

        buildFile("busybox", R.raw.busybox);
        buildFile("cepter", R.raw.busybox);
        buildFile("tcpdump", R.raw.tcpdump);
        buildFile("macchanger", R.raw.arpspoof);
        buildFile("usernames", R.raw.usernames);
        buildFile("arpspoof", R.raw.arpspoof);


        buildFile("ettercap_archive", R.raw.ettercap_archive);
        new RootProcess("UNZIP FILES", singleton.FilesPath).exec(singleton.BinaryPath + "busybox unzip ettercap_archive").closeProcess();

        buildFile("archive_nmap", R.raw.nmap);
        new RootProcess("Install ", singleton.FilesPath).exec(singleton.BinaryPath + "busybox unzip archive_nmap").closeProcess();
        new RootProcess("Install ").exec("chmod 744 " + singleton.BinaryPath + "/nmap/*").closeProcess();

        /*  ping binary    */
        buildFile("ping", R.raw.arpspoof);
        new RootProcess("Install ").exec("mount -o rw,remount /system;").closeProcess();
        new RootProcess("Install ").exec("cp ./ping /system/bin/;").closeProcess();
        /*  Dns Stuff    */
        new RootProcess("Install ").exec("echo \"nameserver `getprop net.dns1`\" > /etc/resolv.conf").closeProcess();
        /*  Clean    */
        new RootProcess("Install ").exec("rm " + singleton.BinaryPath).closeProcess();
        new RootProcess("Install ").exec(singleton.BinaryPath + "busybox killall cepter").closeProcess();
        new RootProcess("Install ").exec(singleton.BinaryPath + "busybox killall tcpdump").closeProcess();
        new RootProcess("Install ").exec(singleton.BinaryPath + "busybox killall arpspoof").closeProcess();
        new RootProcess("Install ").exec("rm -f " + singleton.FilesPath + "Raw/*").closeProcess();
        new RootProcess("Install ").exec("rm -f " + singleton.FilesPath + "dnss").closeProcess();
        new RootProcess("Install ").exec("rm -f " + singleton.FilesPath + "hostlist").closeProcess();
        new RootProcess("Install ").exec("rm -f " + singleton.FilesPath + "*Activity").closeProcess();
        new RootProcess("Install ").exec("rm -f " + singleton.FilesPath + "archive_nmap").closeProcess();
        new RootProcess("Install ").exec("rm -f " + singleton.FilesPath + "ettercap_archive").closeProcess();
        new RootProcess("Install ").exec("echo '" + singleton.VERSION + "' > " + singleton.FilesPath + "version").closeProcess();
    }

    private void                monitor(final String log) {
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                monitor.setText(log);
            }
        });
    }

    public boolean              isItUpdated() {
        return new File(singleton.FilesPath + "version").exists();//TODO: vérifier que les version concorde
    }

    //
    //   public native String        stringFromJNI();
}
