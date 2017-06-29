package su.sniff.cepter.Controller;

import android.util.Log;
import su.sniff.cepter.globalVariable;

import java.io.*;

/**
 * Created by maxim on 28/06/2017.
 */
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
            Log.d(TAG, this.LogID + "::" + cmd);
            os.writeBytes(cmd + "\n");
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

    public void                 closeProcess() {
        try {
            Log.d(TAG, this.LogID + "::Close");
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
