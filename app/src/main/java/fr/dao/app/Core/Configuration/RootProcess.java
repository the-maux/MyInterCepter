package fr.dao.app.Core.Configuration;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public class                    RootProcess {
    private String              TAG = "RootProcess";
    private Process             mProcess;
    private DataOutputStream    mOutputStream;
    private int                 mPid;
    private String              mLogID;
    private boolean             mDebugLog = false;

    public                      RootProcess(String LogID) {
        this.mLogID = LogID;
        try {
            mProcess = Runtime.getRuntime().exec("su");
            mOutputStream = new DataOutputStream(mProcess.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public                      RootProcess(String LogID, String workingDirectory) {
        this.mLogID = LogID;
        try {
            mProcess = Runtime.getRuntime().exec("su", null, new File(workingDirectory));
            mOutputStream = new DataOutputStream(mProcess.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public                      RootProcess(String LogID, boolean noRoot) {
        /* Ye noRoot is never used, why bother ? */
        this.mLogID = LogID;
        try {
            mProcess = Runtime.getRuntime().exec("ls\n", null, new File("/"));
            mOutputStream = new DataOutputStream(mProcess.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public RootProcess          exec(String cmd) {
        try {
            cmd = cmd.replace("//", "/");
            if (mDebugLog)
                Log.d(TAG, mLogID + "::" + cmd);
            mOutputStream.writeBytes(cmd + " 2>&1 ; exit \n");
            mOutputStream.flush();
            Field f = mProcess.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            mPid = f.getInt(mProcess);
            if (mDebugLog)
                Log.d(TAG, mLogID + "[PID:" + mPid + "]::" + cmd);
            f.setAccessible(false);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.d(TAG, "IllegalAccessException PID");
            e.printStackTrace();
            mPid = -1;
        } catch (NoSuchFieldException e) {
            Log.d(TAG, "NoSuchFieldException PID");
            e.printStackTrace();
            mPid = -1;
        }
        return this;
    }

    public RootProcess          noDebugOutput() {
        mDebugLog = false;
        return this;
    }

    private int                 waitFor() {
        /* Pro-Tip: You want to close process ? Purge all fd first */
        try {
            BufferedReader reader = new BufferedReader(getReader());
            if (reader.ready()) {
                while (reader.readLine() != null) {}
            }
            reader.close();
            reader = new BufferedReader(getErrorStreamReader());
            if (reader.ready()) {
                while (reader.readLine() != null) {}
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mProcess.waitFor();
            int res = mProcess.exitValue();
            return res;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public BufferedReader       getReader() {
        return new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
    }

    public InputStreamReader    getInputStreamReader() {
        return new InputStreamReader(mProcess.getInputStream());
    }

    private InputStreamReader   getErrorStreamReader() {
        return new InputStreamReader(mProcess.getErrorStream());
    }

    public int                  getmPid() {
        return mPid;
    }

    public int                  closeProcess() {
        closeDontWait();
        return waitFor();
    }

    RootProcess                 closeDontWait() {
        try {
            //Log.d(TAG, this.mLogID + "::Close");
            //mOutputStream.writeBytes("exit\n");
            //mOutputStream.flush();
            mOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
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