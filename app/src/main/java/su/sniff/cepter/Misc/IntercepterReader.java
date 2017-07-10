package su.sniff.cepter.Misc;

import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import su.sniff.cepter.Controller.IntercepterParser;
import su.sniff.cepter.Controller.RootProcess;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by maxim on 28/06/2017.
 */
public class                    IntercepterReader extends Thread {
    private TextView            monitor;
    private String              TAG = "IntercepterReader";
    private Activity            activity;
    private RootProcess         process;

    public                      IntercepterReader(final Activity activity, final TextView tvHello, final RootProcess process) {
        this.monitor = tvHello;
        this.activity = activity;
        this.process = process;
        new Thread(new Runnable() {
            public void run() {
                try {
                    readThePipe();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void                readThePipe() throws IOException {
        BufferedReader reader = new BufferedReader(process.getInputStreamReader());
        while (true) {
            String line = reader.readLine();
            if (line == null) {
                closeAll(reader, process, activity);
                return;
            }
            if (line.contains("###STAT###")) {
                parseStat(line);
            } else if (line.contains("REQ###")) {
                parseREQ(line);
            } else if (line.contains("Cookie###")) {
                IntercepterParser.parseCookie(reader, activity);
            } else {
                parseOther(line);
            }
        }
    }

    private void                parseStat(final String temp) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                int b = temp.indexOf("###STAT###") + 11;
                Log.d(TAG, "parseStat::" + temp.substring(b, (temp.length() - b) + 11));
                ((TextView) activity.findViewById(R.id.monitor)).setText(temp.substring(b, (temp.length() - b) + 11));
            }
        });
    }

    private void                parseREQ(final String temp) {
        if (globalVariable.showhttp == 1) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d(TAG, "parseREQ::" + temp.substring(6, temp.length() - 6));
                    monitor.append(temp.substring(6, temp.length() - 6) + "\n");
                }
            });
        }
    }

    private void                parseOther(final String temp) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                if (temp.contains("intercepted")) {
                    Log.d(TAG, "parseOther::Coloring::" + temp);
                    Spannable WordtoSpan = new SpannableString(temp);
                    WordtoSpan.setSpan(new ForegroundColorSpan(-1), 0, temp.length(), 33);
                    monitor.append(WordtoSpan);
                } else {
                    Log.d(TAG, "parseOther::Without+Coloring::" + temp);
                    monitor.append(temp);
                }
                monitor.append("\n");
                ScrollView scrollview = (ScrollView) activity.findViewById(R.id.scrollview);
                if (globalVariable.raw_autoscroll == 1) {
                    scrollview.scrollTo(0, monitor.getHeight() + 50);
                }
            }
        });
    }

    private void                closeAll(BufferedReader reader, RootProcess process, Activity activity) throws IOException {
        reader.close();
        process.waitFor();
        Log.d(TAG, "closing");
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                monitor.append("*\n");
            }
        });
    }
}
