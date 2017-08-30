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
    public volatile int rcv = 0x00, sent = 0x00, error = 0x00;
    private int         MARGE_ERREUR = 19;//pour ne pas fausser les stats avec le debut du binaire
    public              DoraProcess(Host host) {
        this.pingProcess = new RootProcess("Dora:" + host.getIp());
        this.host = host;
        reset();
    }
    public void         exec() {
        reset();
        new Thread(new Runnable() {

            @Override
            public void run() {
                String buffer = "";
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
                    Log.d(TAG, "Dora::" + host.getIp() + "::Terminated->Dump::rcv:" + rcv + "&sent:" + sent);
                    Log.d(TAG, "with Buffer:" + buffer + "<-");
                    running = false;
                    pingProcess.closeProcess();
                    Log.d(TAG, "Dora:" + host.getIp() + "");
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
        Log.d(TAG, "Dora::POURCENTAGE::" + host.getIp() + "::Terminated->Dump::rcv:" + rcv + "&sent:" + sent);
        if (rcv < MARGE_ERREUR+1 && sent < MARGE_ERREUR+1) {
            if (rcv > 0 && sent > 0)
                Log.d(TAG, "%%::" + (rcv / sent) * 100);
            return 0;
        } else {
            float a = (((float) (rcv - MARGE_ERREUR) / ((float)sent - MARGE_ERREUR) * 100));
            if (a < 0)
                return 0;
            return (int) a;
        }
    }

    public String       getUptime() {
        if (running) {
            Date tmp = Calendar.getInstance().getTime();
            SimpleDateFormat sdf = new SimpleDateFormat("m:s", Locale.FRANCE);
            tmp.setTime(tmp.getTime() - uptime.getTime());
            return sdf.format(tmp);
        } else
            return "0:00:00";
    }
}
