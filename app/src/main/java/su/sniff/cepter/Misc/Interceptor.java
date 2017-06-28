package su.sniff.cepter.Misc;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.ScrollView;
import android.widget.TextView;
import org.w3c.dom.Text;
import su.sniff.cepter.R;
import su.sniff.cepter.Utils.OpenFileDialog;
import su.sniff.cepter.View.MainActivity;
import su.sniff.cepter.globalVariable;

import java.io.*;

public class                    Interceptor implements OpenFileDialog.OnFileSelectedListener {
    private String              TAG = "Interceptor";
    private MainActivity        activity;
    private TextView            tvHello;

    public                      Interceptor(MainActivity activity, TextView tvHello) {
        this.activity = activity;
        this.tvHello = tvHello;
    }

    public void                 onFileSelected(File f) {
        String sc;
        if (globalVariable.savepcap == 1) {
            sc = " w ";
        } else {
            sc = " ";
        }
        tvHello.setTextSize(2, (float) globalVariable.raw_textsize);
        ((TextView)activity.findViewById(R.id.textView1)).setTextSize(2, (float) globalVariable.raw_textsize);
        File fDroidSheep = new File(globalVariable.path + "/exits.id");
        if (fDroidSheep.exists()) {
            fDroidSheep.delete();
        }
        try {
            final Process process = Runtime.getRuntime().exec("su", null, new File(globalVariable.path + ""));
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(globalVariable.path + "/cepter " + f.getAbsolutePath() + " " + Integer.toString(globalVariable.resurrection) + "\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            activity.sniff_process = process;
            new Thread(new Runnable() {
                public void run() {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    while (true) {
                        try {
                            String line = reader.readLine();
                            if (line == null) {
                                reader.close();
                                process.waitFor();
                                return;
                            }
                            final String temp = line;
                            Log.d(TAG, "INTERCEPTOR:" + temp);
                            if (temp.indexOf("###STAT###") != -1) {
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        int b = temp.indexOf("###STAT###") + 11;
                                        ((TextView) activity.findViewById(R.id.textView1)).setText(temp.substring(b, (temp.length() - b) + 11));
                                    }
                                });
                            } else if (temp.indexOf("REQ###") != -1) {
                                if (globalVariable.showhttp == 1) {
                                    activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            tvHello.append(temp.substring(6, temp.length() - 6));
                                            tvHello.append("\n");
                                        }
                                    });
                                }
                            } else if (temp.indexOf("Cookie###") != -1) {
                                final String domain = reader.readLine();
                                final String ip = reader.readLine();
                                final String getreq = reader.readLine();
                                final String coo = reader.readLine();
                                String z = reader.readLine();
                                if (!ip.equals(globalVariable.own_ip)) {
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
                                                if (globalVariable.lock == 0) {
                                                    globalVariable.lock = 1;
                                                } else {
                                                    while (globalVariable.lock == 1) {
                                                        try {
                                                            Thread.sleep(1);
                                                        } catch (InterruptedException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                    globalVariable.lock = 1;
                                                }
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
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Log.d(TAG, "");
                                        if (temp.indexOf("intercepted") != -1) {
                                            Spannable WordtoSpan = new SpannableString(temp);
                                            WordtoSpan.setSpan(new ForegroundColorSpan(-1), 0, temp.length(), 33);
                                            tvHello.append(WordtoSpan);
                                        } else {
                                            tvHello.append(temp);
                                        }
                                        tvHello.append("\n");
                                        ScrollView scrollview = (ScrollView) activity.findViewById(R.id.scrollview);
                                        if (globalVariable.raw_autoscroll == 1) {
                                            scrollview.scrollTo(0, tvHello.getHeight());
                                        }
                                    }
                                });
                            }
                        } catch (IOException e) {
                        } catch (InterruptedException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }
}
