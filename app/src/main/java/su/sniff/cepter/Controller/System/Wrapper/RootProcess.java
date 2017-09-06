package su.sniff.cepter.Controller.System.Wrapper;

import android.util.Log;

import java.io.*;
import java.lang.reflect.Field;

import su.sniff.cepter.Controller.System.Singleton;

public class                    RootProcess {
    private String              TAG = "RootProcess";
    private Process             process;
    private DataOutputStream    os;
    private int                 pid;
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
            Log.d(TAG, LogID + "::" + cmd);
            os.writeBytes(cmd + " 2>&1 \n");
            os.flush();
            Field f = process.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            pid = f.getInt(process);
            f.setAccessible(false);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.d(TAG, "IllegalAccessException PID");
            e.printStackTrace();
            pid = -1;
        } catch (NoSuchFieldException e) {
            Log.d(TAG, "NoSuchFieldException PID");
            e.printStackTrace();
            pid = -1;
        }
        return this;
    }

    public int                  getPid() {
        return pid;
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

    public static void          kill(int pid) {
        new RootProcess("Kill:" + pid)
                .exec(Singleton.getInstance().BinaryPath + "busybox kill " + pid)
                .closeProcess();
    }

    public static void          kill(String binary) {
        new RootProcess("KILLALL")
                .exec(Singleton.getInstance().BinaryPath + "busybox killall " + binary)
                .closeProcess();
    }
}
