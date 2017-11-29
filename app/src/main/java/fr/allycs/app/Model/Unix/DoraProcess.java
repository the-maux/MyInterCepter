package fr.allycs.app.Model.Unix;

import android.util.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import fr.allycs.app.Controller.Core.BinaryWrapper.RootProcess;
import fr.allycs.app.Model.Target.MyObject;
import fr.allycs.app.Model.Target.Host;

public class            DoraProcess extends MyObject {
    private String      TAG = getClass().getName();
    public boolean      mIsRunning = false;
    public Host         mhost;
    public RootProcess  mProcess;
    public Date         mUptime;
    public int          mPid;
    public volatile int rcv = 0x00, sent = 0x00, error = 0x00;
    private int         MARGE_ERREUR = 21;//pour ne pas fausser les stats avec le debut du binaire

    public              DoraProcess(Host host) {
        this.mProcess = new RootProcess("Dora:" + host.getIp());
        this.mhost = host;
        reset();
    }
    public void         exec() {
        reset();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String buffer = "";
                try {
                    mUptime = Calendar.getInstance().getTime();
                    mIsRunning = true;
                    mProcess.exec("ping -fi 0.2 " + mhost.getIp());
                    mPid = mProcess.getPid();
                    Log.d(TAG, "Dora:" + mhost.getIp() + " PID:" + mPid);
                    // find mPid pour finir
                    int tmpLine;
                    boolean over = false;
                    while (!over) {
                        InputStreamReader reader = mProcess.getInputStreamReader();
                        if (reader.ready()) {
                            while ((tmpLine = reader.read()) != -1) {//need to refrest the InputReader to know if process still alive
                                if (tmpLine == '.'){//SENT {
                                    if (sent == 20)
                                        rcv = MARGE_ERREUR;
                                    sent += 1;
                                }
                                else if (tmpLine == ' ')//WAIT
                                    rcv -= 1;
                                else if (tmpLine == '\b')//RCV=>ACK
                                    rcv += 1;
                                else if (tmpLine == 'E')//Error
                                    rcv += 1;
                                else {
                                    buffer = buffer + (char) tmpLine;
                                }
                                if (buffer.contains("Terminate"))
                                    over = true;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    Log.d(TAG, "Dora::" + mhost.getIp() + "::Terminated->Dump::rcv:" + rcv + "&sent:" + sent);
                    Log.d(TAG, "with Buffer:" + buffer + "<-");
                    mIsRunning = false;
                    mProcess.closeProcess();
                    Log.d(TAG, "Dora:" + mhost.getIp() + "");
                }
            }
        }).start();
    }
    public void         reset() {
        rcv = 0;
        sent = 0;
        mUptime = Calendar.getInstance().getTime();
    }
    public int          getPourcentage() {
        //Si not started, return 0
        Log.d(TAG, "Dora::POURCENTAGE::" + mhost.getIp() + "::Terminated->Dump::rcv:" + rcv + "&sent:" + sent);
        if (rcv < MARGE_ERREUR+1 && sent < MARGE_ERREUR+1) {
            if (rcv > 0 && sent > 0)
                Log.d(TAG, "%%::" + (rcv / sent) * 100);
            return 0;
        } else {
            float a = (((float) (rcv - MARGE_ERREUR) / ((float)sent - MARGE_ERREUR) * 100));
            if (a < 0)
                return 0;
            if (a > 100)
                return 100;
            return (int) a;
        }
    }

    public String       getmUptime() {
        if (mIsRunning) {
            Date tmp = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("m:s", Locale.FRANCE);
            tmp.setTime(tmp.getTime() - mUptime.getTime());
            return sdf.format(tmp);
        } else
            return "0:00:00";
    }

    public void         kill() {

    }

    public int          getVisu() {
        if (sent == 0 || rcv == 0)
            return 0;
        if (sent - rcv < 0)
            return 0;
        return sent - rcv;
    }
}
