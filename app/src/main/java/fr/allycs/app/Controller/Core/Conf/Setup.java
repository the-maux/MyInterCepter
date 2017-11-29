package fr.allycs.app.Controller.Core.Conf;

import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import fr.allycs.app.Controller.Core.BinaryWrapper.Dns.DnsConf;
import fr.allycs.app.Controller.Core.BinaryWrapper.RootProcess;
import fr.allycs.app.R;
import fr.allycs.app.View.SetupActivity;

public class                    Setup {
    private String              TAG = "Setup";
    private SetupActivity       mActivity;
    private Singleton           mSingleton = Singleton.getInstance();

    public Setup(SetupActivity activity) {
       this.mActivity = activity;
    }

    public void                 install() throws IOException, InterruptedException {
       /*  Build directory    */
        new RootProcess("initialisation ").exec("mkdir -p /sdcard/Pcap").closeProcess();
        new RootProcess("initialisation ").exec("mkdir -p " + mSingleton.FilesPath ).closeProcess();
        new RootProcess("initialisation ").exec("chmod 777 " + mSingleton.FilesPath).closeProcess();

        buildFiles();

        new RootProcess("initialisation ").exec("mount -o rw,remount /system").closeProcess();
        new RootProcess("initialisation ").exec("cp ./ping /system/bin/;").closeProcess();
        /*  Dns Stuff    */
        new RootProcess("initialisation ").exec("echo \"nameserver `getprop net.dns1`\" > " + DnsConf.PATH_RESOLV_FILE).closeProcess();
        /*  Clean    */
        new RootProcess("initialisation ").exec("rm " + mSingleton.BinaryPath).closeProcess();
        buildDefaultDnsConf();
        new RootProcess("initialisation ").exec("chmod 644 " + DnsConf.PATH_CONF_FILE).closeProcess();
        mActivity.monitor("Cleaning installation");
        cleanTheKitchenBoy();
    }

    private void                buildDefaultDnsConf() {
        new RootProcess("initialisation ")
                .exec("echo \"192.168.0.29 www.microsof.com microsoft.com\" > " + DnsConf.PATH_CONF_FILE + " && " +
                "echo \"192.168.0.30 www.any.domain any.domain\" >> " + DnsConf.PATH_CONF_FILE + " && " +
                "echo \"192.168.0.30 www.test.fr test.fr\" >> " + DnsConf.PATH_CONF_FILE + " && " +
                "chmod 644 " + DnsConf.PATH_CONF_FILE).
                closeProcess();
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
        cepter = mActivity.getResources().openRawResource(R.raw.cepter_android_21_armeabi);
        if (Build.CPU_ABI.contains("x86")) {
            Log.d(TAG, "GIVING X86 BINARI");
            return mActivity.getResources().openRawResource(R.raw.cepter_android_21_x86);
        }
        if (Build.CPU_ABI.contains("arm64")) {
            Log.d(TAG, "GIVING arm64");
            return mActivity.getResources().openRawResource(R.raw.cepter_android_21_arm64_v8a);
        }
        Log.d(TAG, "GIVING arm BINARI");
        return cepter;
    }
    
    private void                buildFile(String nameFile, int ressource) throws IOException, InterruptedException {
        mActivity.monitor("Building " + nameFile);
        File file = new File(mSingleton.FilesPath + nameFile);
        file.delete();
        file.createNewFile();
        InputStream inputStream = (nameFile.contains("cepter")) ? //Choix architecture doit etre fait sur tout les binaires
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
        Log.d(TAG, "buildFile " + nameFile + "chmod::+x::" + file.setExecutable(true, false));
    }

    private void                buildFiles() throws IOException, InterruptedException  {
        Log.d(TAG, "");
        buildFile("busybox", R.raw.busybox);
        buildFile("cepter", R.raw.busybox);
        buildFile("tcpdump", R.raw.tcpdump);
        buildFile("macchanger", R.raw.macchanger);
        buildFile("usernames", R.raw.usernames);
        buildFile("arpspoof", R.raw.arpspoof);

        buildFile("ettercap_archive", R.raw.ettercap_archive);
        //new RootProcess("UNZIP FILES", mSingleton.FilesPath).exec(mSingleton.BinaryPath + "busybox unzip ettercap_archive").closeProcess();
        Log.d(TAG, "unzip ettercap -> " + new RootProcess("UNZIP FILES", mSingleton.FilesPath).exec(mSingleton.BinaryPath + "busybox unzip ettercap_archive").closeProcess());

        buildFile("archive_nmap", R.raw.nmap);
        Log.d(TAG, "unzip nmap -> " + new RootProcess("initialisation ", mSingleton.FilesPath).exec(mSingleton.BinaryPath + "busybox unzip archive_nmap").closeProcess());
//        new RootProcess("initialisation ", mSingleton.FilesPath).exec(mSingleton.BinaryPath + "busybox unzip archive_nmap").closeProcess();
        Log.d(TAG, "chmod 744 " + mSingleton.BinaryPath + "/nmap/*" + " -> " + new RootProcess("initialisation ").exec("chmod 744 " + mSingleton.BinaryPath + "/nmap/*").closeProcess());
//        new RootProcess("initialisation ").exec("chmod 744 " + mSingleton.BinaryPath + "/nmap/*").closeProcess();

        /*  ping binary    */
        buildFile("ping", R.raw.arpspoof);

    }

    private void                cleanTheKitchenBoy() {
        new RootProcess("initialisation ").exec(mSingleton.BinaryPath + "busybox killall cepter").closeProcess();
        new RootProcess("initialisation ").exec(mSingleton.BinaryPath + "busybox killall tcpdump").closeProcess();
        new RootProcess("initialisation ").exec(mSingleton.BinaryPath + "busybox killall arpspoof").closeProcess();
        new RootProcess("initialisation ").exec("rm -f " + mSingleton.FilesPath + "Raw/*").closeProcess();
        new RootProcess("initialisation ").exec("rm -f " + mSingleton.FilesPath + "dnss").closeProcess();
        new RootProcess("initialisation ").exec("rm -f " + mSingleton.FilesPath + "hostlist").closeProcess();
        new RootProcess("initialisation ").exec("rm -f " + mSingleton.FilesPath + "*Activity").closeProcess();
        new RootProcess("initialisation ").exec("rm -f " + mSingleton.FilesPath + "archive_nmap").closeProcess();
        new RootProcess("initialisation ").exec("rm -f " + mSingleton.FilesPath + "ettercap_archive").closeProcess();
        new RootProcess("initialisation ").exec("echo '" + mSingleton.VERSION + "' > " + mSingleton.FilesPath + "version").closeProcess();
    }
}
