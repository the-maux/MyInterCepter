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
    private boolean                 isComandRunning = false;

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
                StringBuilder buffer = new StringBuilder("");
                String read;
                try {
                    while ((read = reader.readLine()) != null) {
                        if (read.contains("333333333333333333333333333333333333333333")) {
                            isComandRunning = false;
                            Log.d(TAG, "Command over");
                            frgmnt.stdout(buffer.toString());
                            buffer = new StringBuilder("");
                        } else
                            buffer.append(read).append("<br>");
                    }
                    Log.d(TAG, "SHELL PROCESS OVER");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public boolean                  exec(String cmd) {
        if (!isComandRunning) {
            isComandRunning = true;
            Log.d(TAG, "exec:" + cmd);
            if (cmd.contains("cd "))
                updatePath();
            //frgmnt.stdin("Output: " + cmd);
            updateUser();
            mProcess.shell(cmd);
            return isComandRunning;
        } else {
            Log.e(TAG, "Already in start");
            return false;
        }
    }

    private void                    updateUser() {
        //mProcess.getUser();
    }

    private void                    updatePath() {
        //mProcess.getPath();
    }
}
