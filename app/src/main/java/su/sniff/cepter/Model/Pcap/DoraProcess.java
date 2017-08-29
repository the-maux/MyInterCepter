package su.sniff.cepter.Model.Pcap;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import su.sniff.cepter.Controller.System.RootProcess;
import su.sniff.cepter.Model.Target.Host;

public class            DoraProcess {
    private String      TAG = getClass().getName();
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
                        pingProcess.exec("ping -fi 0.2 " + host.getIp());
                        BufferedReader reader = pingProcess.getReader();
                        int tmpLine;
                        while ((tmpLine = reader.read()) != -1) {
                            Log.d(TAG, "Dump IS:[" + tmpLine + "]rcv:" + rcv + "&sent:" + sent);
                            if (tmpLine == '.')//SENT
                                sent += 1;
                            else if (tmpLine == ' ')//WAIT
                                rcv -= 1;
                            else if (tmpLine == '\b')//RCV=>ACK
                                rcv += 1;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
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
        SimpleDateFormat sdf = new SimpleDateFormat("H:m:s", Locale.FRANCE);
        return sdf.format(uptime);
    }
}
