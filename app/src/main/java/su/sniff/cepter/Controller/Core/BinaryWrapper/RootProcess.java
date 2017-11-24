package su.sniff.cepter.Controller.Core.BinaryWrapper;

import android.util.Log;

import java.io.*;
import java.lang.reflect.Field;

import su.sniff.cepter.Controller.Core.Conf.Singleton;

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

    public                      RootProcess(String LogID, String workingDirectory) {
        this.LogID = LogID;
        try {
            process = Runtime.getRuntime().exec("su", null, new File(workingDirectory));
            os = new DataOutputStream(process.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public                      RootProcess(String LogID, boolean noRoot) {
        this.LogID = LogID;
        try {
            process = Runtime.getRuntime().exec("ls\n", null, new File("/"));
            os = new DataOutputStream(process.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RootProcess          exec(String cmd) {
        try {
            cmd = cmd.replace("//", "/");
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

    public int                  waitFor() {
        try {
            BufferedReader reader = new BufferedReader(getReader());
            String line;
            if (reader.ready()) {
                while ((line = reader.readLine()) != null) {}
            }
            reader.close();
            reader = new BufferedReader(getErrorStreamReader());
            if (reader.ready()) {
                while ((line = reader.readLine()) != null) {}
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            process.waitFor();
            int res = process.exitValue();
            return res;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
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

    public int                 closeProcess() {
        closeDontWait();
        return waitFor();
    }

    public RootProcess          closeDontWait() {
        try {
            //Log.d(TAG, this.LogID + "::Close");
            //os.writeBytes("exit\n");
            //os.flush();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
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
