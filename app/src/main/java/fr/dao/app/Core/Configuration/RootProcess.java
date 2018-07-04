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
    private boolean             noRootAllowed = true;
    private String[]            env = {"PATH=/su/bin:/sbin:/system/sbin:/system/bin:/su/xbin:/system/xbin:/system/xbin/su"};

    public                      RootProcess(String LogID) {
        this.mLogID = LogID;
        try {
            mProcess = Runtime.getRuntime().exec("su", null);
            mOutputStream = new DataOutputStream(mProcess.getOutputStream());
            noRootAllowed = false;
        } catch (IOException e) {
            Log.d(TAG, "e:[" + e.getMessage()+ "]");
            e.printStackTrace();
            if (e.getMessage().contains("Permission denied")) {
                Log.e(TAG, "permission denied");
                noRootAllowed = true;
            }
        }
    }
    public                      RootProcess(String LogID, String workingDirectory) {
        this.mLogID = LogID;

        Log.d(TAG, "");
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        } else {*/
            try {
                if (workingDirectory == null) {
                    mProcess = Runtime.getRuntime().exec("su", env);
                } else
                    mProcess = Runtime.getRuntime().exec("su", env, new File(workingDirectory));
                mOutputStream = new DataOutputStream(mProcess.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
      //  }
    }
    public                      RootProcess(String LogID, boolean noRoot) {
        /* Ye noRoot is never used, why bother ? */
        this.mLogID = LogID;
        try {
            mProcess = Runtime.getRuntime().exec("sh \n", env, new File("/"));
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
            if (mOutputStream != null) {
                mOutputStream.writeBytes(cmd + " 2>&1 ; exit \n");
                mOutputStream.flush();
                Field f = mProcess.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                mPid = f.getInt(mProcess);
                if (mDebugLog)
                    Log.d(TAG, mLogID + "[PID:" + mPid + "]::" + cmd);
            }
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

    public RootProcess          shell(String cmd) {
        try {
            cmd = cmd.replace("//", "/");
            if (mDebugLog)
                Log.d(TAG, mLogID + "::" + cmd);
            if (mOutputStream != null) {
                mOutputStream.writeBytes(cmd + " 2>&1 \n");
                mOutputStream.writeBytes("echo \"111111111111111111111111111111111111111111\" \n");
                mOutputStream.writeBytes("pwd \n");
                mOutputStream.writeBytes("echo \"222222222222222222222222222222222222222222\" \n");
                mOutputStream.writeBytes("id \n");
                mOutputStream.writeBytes("echo \"333333333333333333333333333333333333333333\" \n");
                mOutputStream.flush();
                Field f = mProcess.getClass().getDeclaredField("pid");
                f.setAccessible(true);
                mPid = f.getInt(mProcess);
                if (mDebugLog)
                    Log.d(TAG, mLogID + "[PID:" + mPid + "]::" + cmd);
            }
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

    public RootProcess          debugOutput() {
        mDebugLog = true;
        return this;
    }

    private int                 waitFor() {
        if (mDebugLog)
            Log.d(TAG, "waitFor");
        /* Pro-Tip: You want to close process fd ? Purge it */
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
        return mProcess == null ? null : new BufferedReader(new InputStreamReader(mProcess.getInputStream()));
    }

    public InputStreamReader    getInputStreamReader() {
        return mProcess == null ? null : new InputStreamReader(mProcess.getInputStream());
    }

    private InputStreamReader   getErrorStreamReader() {
        return mProcess == null ? null : new InputStreamReader(mProcess.getErrorStream());
    }

    public int                  getmPid() {
        return mPid;
    }

    public int                  closeProcess() {
        if (mDebugLog)
            Log.d(TAG, "closeProcess");
        closeDontWait();
        return mProcess == null ? -1 : waitFor();
    }

    public RootProcess         closeDontWait() {
        if (mDebugLog)
            Log.d(TAG, "closeDontWait");
        try {
            mOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public static void          kill(int pid) {
        new RootProcess("Kill:" + pid)
                .exec(Singleton.getInstance().Settings.BinaryPath + "busybox kill " + pid)
                .closeProcess();
    }
    public static void          kill(String binary) {
        new RootProcess("KILLALL")
                .exec(Singleton.getInstance().Settings.BinaryPath + "busybox killall " + binary)
                .closeProcess();
    }

    public boolean              isNoRootAllowed() {
        return noRootAllowed;
    }
}
