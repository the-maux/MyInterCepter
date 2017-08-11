package su.sniff.cepter.Controller.CepterControl;

import android.app.Activity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;

import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Controller.System.RootProcess;
import su.sniff.cepter.Controller.System.ThreadUtils;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Created by maxim on 28/06/2017.
 */
public class                    IntercepterReader extends Thread {
    private TextView            monitor;
    private static String       TAG = "IntercepterReader";
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
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("###STAT###")) {
                parseStat(line, activity);
            } else if (line.contains("REQ###")) {
                parseREQ(line, activity, monitor);
            } else if (line.contains("Cookie###")) {
                parseCookie(reader, activity);
            } else {
                parseOther(line, activity, monitor);
            }
        }
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

    public static void        parseStat(final String temp, final Activity activity) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                int b = temp.indexOf("###STAT###") + 11;
                //Log.d(TAG, "parseStat::" + temp.substring(b, (temp.length() - b) + 11));
                ((TextView) activity.findViewById(R.id.monitor)).setText(temp.substring(b, (temp.length() - b) + 11));
            }
        });
    }

    public static void         parseREQ(final String temp, final Activity activity, final TextView monitor) {
        if (globalVariable.showhttp == 1) {
            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Log.d(TAG, "parseREQ::" + temp.substring(6, temp.length() - 6));
                    monitor.append(temp.substring(6, temp.length() - 6) + "\n");
                }
            });
        }
    }

    public static void         parseOther(final String temp, final Activity activity, final TextView monitor) {
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

    public static void          parseCookie(BufferedReader reader, Activity activity) throws IOException {
        final String domain = reader.readLine();
        final String ip = reader.readLine();
        final String getreq = reader.readLine();
        final String coo = reader.readLine();
        String z = reader.readLine();
        if (!ip.equals(Singleton.network.myIp)) {
            int dub = 0;

            for (int i = 0; i < globalVariable.cookies_c; i++) {
                if (((String) globalVariable.cookies_value.get(i)).equals(coo)) {
                    dub = 1;
                    break;
                }
            }
            if (dub != 1) {
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        ThreadUtils.lock();
                        globalVariable.cookies_domain.add(globalVariable.cookies_c, domain + " : " + ip);
                        globalVariable.cookies_domain2.add(globalVariable.cookies_c, "<font color=\"#00aa00\"><b>" + domain + " : " + ip + "</b></font><br>" + "<font color=\"#397E7E\">" + coo + "</font>");
                        globalVariable.adapter.notifyDataSetChanged();
                        globalVariable.adapter2.notifyDataSetChanged();
                        globalVariable.cookies_getreq.add(globalVariable.cookies_c, getreq);
                        globalVariable.cookies_value.add(globalVariable.cookies_c, coo);
                        globalVariable.cookies_ip.add(globalVariable.cookies_c, ip);
                        globalVariable.cookies_getreq2.add(globalVariable.cookies_c, getreq);
                        globalVariable.cookies_value2.add(globalVariable.cookies_c, coo);
                        globalVariable.cookies_ip2.add(globalVariable.cookies_c, ip);
                        globalVariable.cookies_c++;
                        globalVariable.lock = 0;
                    }
                });
            }
        }
    }
}
