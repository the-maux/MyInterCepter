package fr.dao.app.Core;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;

import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.View.Terminal.TerminalActivity;
import fr.dao.app.View.Terminal.TerminalFrgmnt;

public class                        Shell {
    private String                  TAG = "Shell";
    private TerminalActivity        mActivity;
    private TerminalFrgmnt          frgmnt;
    public String                   actualOutput;
    private String                  USER = "shell";
    public String                   PROMPT = "<font color='red'>" + USER + "</font> " + "<font color='cyan'> $> </font>";
    private RootProcess             mProcess;

    public  Shell(TerminalActivity terminalActivity, TerminalFrgmnt frgmnt) {
        this.frgmnt = frgmnt;
        this.mActivity = terminalActivity;
        actualOutput = PROMPT;
        mProcess = new RootProcess("Shell").exec("sh");
        startProcess();
    }

    private void                    startProcess() {
        new Thread(new Runnable() {
            public void run() {
                BufferedReader reader = mProcess.getReader();
                String read;
                try {
                    while ((read = reader.readLine()) != null) {

                        Log.d(TAG, "STDOUT[" + read + "]");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void                     exec(String cmd) {
        Log.d(TAG, "exec:" + cmd);
//        frgmnt.stdin();
        if (cmd.contains("cd "))
            updatePath();
        frgmnt.stdin("Output: " + cmd);
        updateUser();
        mProcess.shell(cmd);


    }

    private void                    updateUser() {
        //mProcess.getUser();
    }

    private void                    updatePath() {
        //mProcess.getPath();
    }
}
