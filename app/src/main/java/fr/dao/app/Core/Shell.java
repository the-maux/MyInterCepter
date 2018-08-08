package fr.dao.app.Core;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.dao.app.Core.Configuration.RootProcess;
import fr.dao.app.View.Terminal.TerminalActivity;
import fr.dao.app.View.Terminal.TerminalFrgmnt;

public class                        Shell {
    private String                  TAG = "Shell";
    private RootProcess             mProcess;
    private TerminalActivity        mActivity;
    private TerminalFrgmnt          frgmnt;
    private String                  USER = "shell";
    private boolean                 isComandRunning = false;
    public String                   PROMPT;
    public String                   actualOutput;
    public ArrayList<String>        mHistory = new ArrayList<>();

    public  Shell(TerminalActivity terminalActivity, TerminalFrgmnt frgmnt) {
        this.frgmnt = frgmnt;
        this.mActivity = terminalActivity;
        actualOutput = "";
        updatePrompt();
        mProcess = new RootProcess("Shell", false);
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
                            actualOutput = actualOutput + buffer.toString().substring(0, buffer.toString().indexOf("111111111111111111111111111111111111111111"));
                            updatePath(buffer.toString());
                            frgmnt.stdout(actualOutput, true);
                            buffer = new StringBuilder("");
                        } else {
                            buffer.append(read).append("<br>");
                        }
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
            actualOutput += PROMPT + " " + cmd + "<br>";
            frgmnt.stdout(actualOutput, false);
            if (!cmd.contentEquals("exit")) {
                mProcess.shell(cmd, true);
                mHistory.add(cmd);
                isComandRunning = true;
                Log.d(TAG, "exec:" + cmd);
            }
            //TODO if user not root, close actual terminal
            return true;
        } else {
            Log.e(TAG, "Already in start, dumping HISTORY");
            for (String s : mHistory) {
                Log.e(TAG, "\t" + s);
            }
            return false;
        }
    }

    private void                    updatePath(String s) {
        Log.d(TAG, s);
        String path = s.substring(s.indexOf("111111111111111111111111111111111111111111")+"111111111111111111111111111111111111111111".length(), s.indexOf("222222222222222222222222222222222222222222")).replace("<br>", "");
        Pattern p = Pattern.compile(".*uid=0\\( *(.*) *\\) gid.");
        Matcher m = p.matcher(s);
        if (m.find())
            USER = m.group(1);
        if (USER.contentEquals("root"))
            USER = "root ";
        Log.i(TAG, "PATH[" + path + "]");
        mActivity.setToolbarTitle(null, path);
        Log.i(TAG, "USER[" + USER + "]");
        updatePrompt();
        frgmnt.updateStdout();
    }

    private void                    updatePrompt() {
        PROMPT = "<font color='red'>" + USER + "</font> " + "<font color='cyan'> $> </font>";
    }

    public void                     close() {
        mProcess.closeDontWait();
    }
}
