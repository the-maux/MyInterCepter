package su.sniff.cepter.Misc;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.ScrollView;
import android.widget.TextView;
import su.sniff.cepter.R;
import su.sniff.cepter.View.MainActivity;
import su.sniff.cepter.globalVariable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by maxim on 28/06/2017.
 */
public class                CookieThing extends Thread {
    private final TextView  tvHello;
    
    public CookieThing(final MainActivity activity, final TextView tvHello, final Process process) {
        this.tvHello = tvHello;
        new Thread(new Runnable() {

            class C00591 implements Runnable {
                C00591() {
                }

                public void run() {
                    tvHello.append("*\n");
                }
            }

            public void run() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                while (true) {
                    try {
                        String line = reader.readLine();
                        if (line == null) {
                            reader.close();
                            process.waitFor();
                            activity.runOnUiThread(new C00591());
                            return;
                        }
                        final String temp = line;
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
                                        scrollview.scrollTo(0, tvHello.getHeight() + 50);
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
    }
}
