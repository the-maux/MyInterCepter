package su.sniff.cepter.Controller.System;

import android.util.Log;

import java.io.*;

public class                    RootProcess {
    private String              TAG = "RootProcess";
    private Process             process;
    private DataOutputStream    os;
    private String              LogID;

    public                      RootProcess(String LogID) {
        this.LogID = LogID;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public                      RootProcess(String LogID, String path) {
        this.LogID = LogID;
        try {
            process = Runtime.getRuntime().exec("su", null, new File(path));
            os = new DataOutputStream(process.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RootProcess          exec(String cmd) {
        try {
            Log.d(TAG, LogID + "::" + cmd + " 2>&1 ");
            os.writeBytes(cmd + " 2>&1 \n");
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public void                 waitFor() {
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public BufferedReader       getReader() {
        return new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    public InputStreamReader    getInputStreamReader() {
        return new InputStreamReader(process.getInputStream());
    }

    public InputStreamReader    getErrorStreamReader() {
        return new InputStreamReader(process.getErrorStream());
    }

    public void                 closeProcess() {
        closeDontWait();
        waitFor();
    }
    public void                 closeDontWait() {
        try {
            Log.d(TAG, this.LogID + "::Close");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public Process              getActualProcess() {
        return process;
    }

    public static void          kill(String binary) {
        new RootProcess("ARP::kill")
                .exec(Singleton.BinaryPath + "busybox killall " + binary)
                .closeProcess();
    }
}
