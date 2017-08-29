package su.sniff.cepter.Model.Pcap;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import su.sniff.cepter.Controller.System.RootProcess;
import su.sniff.cepter.Model.Target.Host;

public class            DoraProcess {
    private String      TAG = getClass().getName();
    public boolean      running = false;
    public Host         host;
    public RootProcess  pingProcess;
    public Date         uptime;
    public volatile int rcv = 0x00, sent = 0x00;

    public              DoraProcess(RootProcess process, Host host) {
        this.pingProcess = process;
        this.host = host;
        reset();
    }
    public void         exec() {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        uptime = Calendar.getInstance().getTime();
                        running = true;
                        pingProcess.exec("ping -fi 0.2 " + host.getIp());
                        int tmpLine;
                        boolean over = false;
                        while (!over) {
                            InputStreamReader reader = pingProcess.getInputStreamReader();
                            if (reader.ready()) {
                                while ((tmpLine = reader.read()) != -1) {//need to refrest the InputReader to know if process still alive
                                    Log.d(TAG, "Dump IS:[" + tmpLine + "]rcv:" + rcv + "&sent:" + sent);
                                    if (tmpLine == '.')//SENT
                                        sent += 1;
                                    else if (tmpLine == ' ')//WAIT
                                        rcv -= 1;
                                    else if (tmpLine == '\b')//RCV=>ACK
                                        rcv += 1;
                                    else
                                        Log.d(TAG, "ELSE:" + (char) tmpLine);
                                }
                            }
                            Log.d(TAG, "refresh reader");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        running = false;
                        pingProcess.closeProcess();
                    }
                }
            }).start();
    }
    public void         reset() {
        rcv = 0;
        sent = 0;
        uptime = Calendar.getInstance().getTime();
    }
    public int          getPourcentage() {
        //Si not started, return 0
        return (rcv == 0 || sent == 0) ? 0 : (rcv / sent) * 100;
    }

    public String       getUptime() {
        if (running) {
            Date tmp = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("H:m:s", Locale.FRANCE);
            tmp.setTime(uptime.getTime() - tmp.getTime());
            return sdf.format(tmp);
        } else
            return "0:00:00";
    }
}
