package su.sniff.cepter.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MotionEventCompat;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

import su.sniff.cepter.*;
import su.sniff.cepter.Controller.RootProcess;
import su.sniff.cepter.Misc.CookieThing;
import su.sniff.cepter.Misc.Interceptor;
import su.sniff.cepter.Utils.Net.IpTablesConfStrippedMode;
import su.sniff.cepter.Utils.OpenFileDialog;
import su.sniff.cepter.Utils.SaveFileDialog;
import su.sniff.cepter.Utils.SaveFileDialog.OnNewFileSelectedListener;

public class                    MainActivity extends Activity {
    static TextView             tvHello;
    static ListView             tvList;
    String TAG = "MainActivity";
    String cmd;
    String cmd2;
    Activity mActivity;
    public Context mCtx;
    public Process sniff_process = null;


    class OnNewFileSelectedL implements OnNewFileSelectedListener {
        OnNewFileSelectedL() {
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
        Log.d(TAG, "this.cmd:" + this.cmd);
        this.cmd2 = getIntent().getExtras().getString("Key_String_origin");
        Log.d(TAG, "this.cmd:" + this.cmd2);
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

    public void onInterceptorRunClick(View v) throws IOException {
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
                RootProcess rootProcess = new RootProcess("globalVariable.strip=1");
                rootProcess.exec("iptables -F;iptables -X;iptables -t nat -F;iptables -t nat -X;iptables -t mangle -F;iptables -t mangle -X;iptables -P INPUT ACCEPT;iptables -P FORWARD ACCEPT;iptables -P OUTPUT ACCEPT");
                rootProcess.closeProcess();
            }
            Log.d(TAG, "onInterceptorRunClick::typical over with strip");
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
        File fDroidSheep = new File(globalVariable.path + "/exits.id");
        if (fDroidSheep.exists()) {
            fDroidSheep.delete();
        }
        if (globalVariable.strip == 1) {
            Log.d(TAG, "iptables Conf as full striped");
            new IpTablesConfStrippedMode();
        } else {
            Log.d(TAG, "iptables Conf as partially striped");
            RootProcess process = new RootProcess("IpTableStriped");
            process.exec("iptables -F;iptables -X;iptables -t nat -F;iptables -t nat -X;iptables -t mangle -F;iptables -t mangle -X;iptables -P INPUT ACCEPT;iptables -P FORWARD ACCEPT;iptables -P OUTPUT ACCEPT");
            process.exec("echo '1' > /proc/sys/net/ipv4/ip_forward");
            if (globalVariable.dnss == 1) {
                process.exec("iptables -t nat -A PREROUTING -p udp --destination-port 53 -j REDIRECT --to-port 8053");
            }
            process.closeProcess();
        }

        final Process process = Runtime.getRuntime().exec("su", null, new File(globalVariable.path + ""));
        DataOutputStream os = new DataOutputStream(process.getOutputStream());
        os.writeBytes(globalVariable.path + "/cepter " + Integer.toString(globalVariable.adapt_num) + " " + Integer.toString(globalVariable.resurrection) + sc + this.cmd + "\n");
        os.flush();
        os.writeBytes("exit\n");
        os.flush();
        os.close();
        this.sniff_process = process;
        new CookieThing(this, tvHello, process);
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
        new SaveFileDialog(this, Environment.getExternalStorageDirectory().getAbsolutePath(), new String[]{".txt"}, new OnNewFileSelectedL()).show();
    }

    public void OnOpenCap(View v) {
        new OpenFileDialog(this, globalVariable.PCAP_PATH, new String[]{".pcap"}, new Interceptor(this, tvHello)).show();
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
            Log.d(TAG, "OnDefend::" + read);
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
                    Process process2 = Runtime.getRuntime().exec("su", null, new File(globalVariable.path + ""));
                    DataOutputStream dataOutputStream = new DataOutputStream(process2.getOutputStream());
                    bufferedReader = new BufferedReader(new InputStreamReader(process2.getInputStream()));
                    Log.d(TAG, "Exec: cepter " + Integer.toString(globalVariable.adapt_num) + " -r " + globalVariable.gw_ip);
                    dataOutputStream.writeBytes(globalVariable.path + "/cepter " + Integer.toString(globalVariable.adapt_num) + " -r " + globalVariable.gw_ip + "\n");
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
                    Log.d(TAG, "LD_LIBRARY_PATH=" + globalVariable.path + " " + globalVariable.path +"/busybox arp -s " + globalVariable.gw_ip + " " + m);
                    dataOutputStream.writeBytes("LD_LIBRARY_PATH=" + globalVariable.path + " " + globalVariable.path +"/busybox arp -s " + globalVariable.gw_ip + " " + m + "\n");
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
