package fr.allycs.app.Controller.Core.Configuration;

import android.app.Activity;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import fr.allycs.app.Controller.Core.Dnsmasq.DnsmasqConfig;
import fr.allycs.app.Controller.Core.RootProcess;
import fr.allycs.app.R;
import fr.allycs.app.View.Startup.SetupActivity;

public class                    Setup {
    private String              TAG = "Setup";
    private SetupActivity       mActivity;
    private Singleton           mSingleton = Singleton.getInstance();

    public Setup(SetupActivity activity) {
       this.mActivity = activity;
    }

    public void                 install() throws IOException, InterruptedException {
        exec("Creating Directory:", "mkdir -p " + mSingleton.PcapPath);/*  Build directory    */
        exec("Creating Directory:", "mkdir -p " + mSingleton.FilesPath);
        exec("Creating Directory:", "chmod 777 " + mSingleton.FilesPath);
        buildFiles();
        exec("Dumping binary:","mount -o rw,remount /system");
        exec("Dumping binary:","cp ./ping /system/bin/;");
        exec("Dumping binary:","rm " + mSingleton.BinaryPath);
        buildDefaultDnsConf();
        exec("Configuring Dnsmasq","chmod 644 " + DnsmasqConfig.PATH_HOST_FILE);

        cleanTheKitchenBoy();
        //TODO: faire un controle d'accés binaire : (./nmap --version, ./tcpdump --version, etc...)
    }

    private void                buildDefaultDnsConf() {
        mActivity.monitor("Configuring Dnsmasq");
        exec( /* Dnsmasq default configuration */
            "echo \"nameserver `getprop net.dns1`\" > " + DnsmasqConfig.PATH_RESOLV_FILE + " && " +
                 "echo \"no-dhcp-interface=\" > " + DnsmasqConfig.PATH_CONF_FILE + " && " +
                 "echo \"server=8.8.8.8\" >> " + DnsmasqConfig.PATH_CONF_FILE + " && " +
                 "echo \"port=8053\" >> " + DnsmasqConfig.PATH_CONF_FILE + " && " +
                 "echo \"no-hosts\" >> " + DnsmasqConfig.PATH_CONF_FILE + " && " +
                 "echo \"addn-hosts=" + DnsmasqConfig.PATH_HOST_FILE + "\" >> " + DnsmasqConfig.PATH_CONF_FILE + " && " +
                 "chmod 644 " + DnsmasqConfig.PATH_CONF_FILE);
        exec( /* Dnsmasq default configuration manipulate DnsRequest */
            "echo \"192.168.0.29 www.microsof.com microsoft.com\" > " + DnsmasqConfig.PATH_HOST_FILE + " && " +
                 "echo \"192.168.0.30 www.any.domain any.domain\" >> " + DnsmasqConfig.PATH_HOST_FILE + " && " +
                 "echo \"192.168.0.30 www.test.fr test.fr\" >> " + DnsmasqConfig.PATH_HOST_FILE + " && " +
                 "chmod 644 " + DnsmasqConfig.PATH_HOST_FILE);
    }

    private void                dumpBuildSystem() {
        Log.i(TAG, "Build.FINGERPRINT::" + Build.FINGERPRINT);
        Log.i(TAG, "Build.CPU_ABI::" + Build.CPU_ABI);
        Log.i(TAG, "Build.CPU_ABI2::" + Build.CPU_ABI2);
        Log.i(TAG, "BOARD::" + Build.BOARD);
        for (String supported64BitAbi : Build.SUPPORTED_ABIS) {
            Log.i(TAG, "Build.SUPPORTED_ABIS::" + supported64BitAbi);
        }
        for (String supported32BitAbi : Build.SUPPORTED_32_BIT_ABIS) {
            Log.i(TAG, "Build.SUPPORTED_32_BIT_ABIS::" + supported32BitAbi);
        }
        for (String supported64BitAbi : Build.SUPPORTED_64_BIT_ABIS) {
            Log.i(TAG, "Build.SUPPORTED_64_BIT_ABIS::" + supported64BitAbi);
        }
    }

    private InputStream         getCepterRessource() {
        InputStream cepter;
        dumpBuildSystem();
        cepter = mActivity.getResources().openRawResource(R.raw.ping);//cepter_android_21_armeabi);
        if (Build.CPU_ABI.contains("x86")) {
            Log.d(TAG, "GIVING X86 BINARI");
            return mActivity.getResources().openRawResource(R.raw.ping);//cepter_android_21_x86);
        }
        if (Build.CPU_ABI.contains("arm64")) {
            Log.d(TAG, "GIVING arm64");
            return mActivity.getResources().openRawResource(R.raw.ping);//cepter_android_21_arm64_v8a);
        }
        Log.d(TAG, "GIVING arm BINARI");
        return cepter;
    }

    public static void          buildPath(Activity activity) {
        Singleton singleton = Singleton.getInstance();
        singleton.FilesPath = activity.getFilesDir().getPath() + '/';
        singleton.BinaryPath = singleton.FilesPath;
        singleton.PcapPath = Environment.getExternalStorageDirectory().getPath() + "/Pcap/";
        singleton.userPreference = new PreferenceControler(singleton.FilesPath);
    }

    private void                buildFile(String nameFile, int ressource) throws IOException, InterruptedException {
        mActivity.monitor("Build " + nameFile);
        File file = new File(mSingleton.FilesPath + nameFile);
        file.delete();
        file.createNewFile();
        InputStream inputStream = (nameFile.contains("cepter")) ?
                getCepterRessource() : mActivity.getResources().openRawResource(ressource);
        int sizeOfInputStram = inputStream.available();
        byte[] bufferDroidSheep = new byte[sizeOfInputStram];
        Arrays.fill(bufferDroidSheep, (byte) 0 );
        int size = inputStream.read(bufferDroidSheep, 0, sizeOfInputStram);
        FileOutputStream out = new FileOutputStream(file);
        out.write(bufferDroidSheep, 0, size);
        out.flush();
        inputStream.close();
        out.close();
        file.setExecutable(true, false);
        Log.d(TAG, "Building[" + nameFile + "] chmod::+x::" + file.canExecute() + " and size: " + (file.length() / 1024) + "kb");
    }

    private void                unzipNmap(String nameArchive) throws IOException {
        String stdout;
        RootProcess process = new RootProcess("Setup", mSingleton.FilesPath);
        Log.d(TAG, mSingleton.FilesPath + "busybox unzip " + mSingleton.FilesPath + nameArchive);
        process.exec(mSingleton.FilesPath + "busybox unzip " + nameArchive).closeProcess();
/*        Log.d(TAG, "UNZIP[nmap]:");
        while ((stdout = reader.readLine()) != null) {
            Log.d(TAG, "\t:" + stdout);
        }
        process.closeProcess();*/
    }

    private void                buildFiles() throws IOException, InterruptedException  {
        mActivity.monitor("Building Busybox");
        buildFile("busybox", R.raw.busybox);
        mActivity.monitor("Building tcpdump");
        buildFile("tcpdump", R.raw.tcpdump);
        mActivity.monitor("Building macchanger");
        buildFile("macchanger", R.raw.macchanger);
        buildFile("usernames", R.raw.usernames);
        mActivity.monitor("Building arpspoof");
        buildFile("arpspoof", R.raw.arpspoof);

        mActivity.monitor("Building nmap");
        buildFile("archive_nmap.zip", R.raw.nmap);
        unzipNmap("archive_nmap.zip");
        mActivity.monitor("Building ettercap");
        buildFile("ettercap_archive", R.raw.ettercap_archive);
        unzipNmap("ettercap_archive");
        Log.d(TAG, "chmod 744 " + mSingleton.BinaryPath + "nmap/*" + "::exit::" + exec("chmod 744 " + mSingleton.BinaryPath + "nmap/*"));
    }

    private void                cleanTheKitchenBoy() {
        mActivity.monitor("Cleaning installation");
        Log.d(TAG, "busybox killall cepter::exit::" + exec(mSingleton.BinaryPath + "busybox killall cepter"));
        Log.d(TAG, "busybox killall tcpdump::exit::" + exec(mSingleton.BinaryPath + "busybox killall tcpdump"));
        Log.d(TAG, "busybox killall arpspoof::exit::" + exec(mSingleton.BinaryPath + "busybox killall arpspoof"));
        Log.d(TAG, "rm -f " + mSingleton.FilesPath + "Raw/*::exit::" + exec("rm -f " + mSingleton.FilesPath + "Raw/*"));
        Log.d(TAG, "rm -f " + mSingleton.FilesPath + "dnss::exit::" + exec("rm -f " + mSingleton.FilesPath + "dnss"));
        Log.d(TAG, "rm -f " + mSingleton.FilesPath + "hostlist::exit::" + exec("rm -f " + mSingleton.FilesPath + "hostlist"));
        Log.d(TAG, "rm -f " + mSingleton.FilesPath + "*Activity::exit::" + exec("rm -f " + mSingleton.FilesPath + "*Activity"));
        Log.d(TAG, "rm -f " + mSingleton.FilesPath + "archive_nmap::exit::" + exec("rm -f " + mSingleton.FilesPath + "archive_nmap"));
        Log.d(TAG, "rm -f " + mSingleton.FilesPath + "ettercap_archive::exit::" + exec("rm -f " + mSingleton.FilesPath + "ettercap_archive"));
        Log.d(TAG, "echo '" + mSingleton.VERSION + "' > " + mSingleton.FilesPath + "version::exit::" + exec("echo '" + mSingleton.VERSION + "' > " + mSingleton.FilesPath + "version"));
    }

    private int                 exec(String TAG, String cmd) {
        return exec(cmd);
    }

    private int                 exec(String cmd) {
        if (cmd.contains(" && ")) {
            for (String line : cmd.split(" && ")) {
                Log.d(TAG, line.replace(mSingleton.FilesPath, "./files/").replace(mSingleton.BinaryPath, "./binary/"));
            }
        } else {
            Log.d(TAG, cmd);//.replace(mSingleton.FilesPath, "./files/").replace(mSingleton.BinaryPath, "./binary/"));
        }
        return new RootProcess("Setup").exec(cmd).closeProcess();
    }
}
