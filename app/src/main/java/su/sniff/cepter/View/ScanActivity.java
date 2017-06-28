package su.sniff.cepter.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import su.sniff.cepter.*;
import su.sniff.cepter.Controller.IPv4;
import su.sniff.cepter.Controller.PortScan;
import su.sniff.cepter.Utils.TabActivitys;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class                        ScanActivity extends Activity {
    private String                  TAG = "ScanActivity";
    private ArrayAdapter<String>    glob_adapter;
    private boolean[]               itemToggled;
    private ArrayList<String>       lst;
    private Context                 mCtx;
    private int                     mask2;
    private String                  origin_str, monitor;
    private ListView                tvList1;
    private ProgressBar             progressBar;

    public void onCreate(Bundle savedInstanceState) {
        IOException e;
        super.onCreate(savedInstanceState);
        requestWindowFeature(3);
        setContentView(R.layout.scan_layout);
        getWindow().setFeatureDrawableResource(3, R.drawable.ico);
        this.mCtx = this;
        this.itemToggled = new boolean[2048];
        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        Arrays.fill(this.itemToggled, false);
        monitor = getIntent().getExtras().getString("Key_String");
        this.origin_str = monitor;

        TextView t = (TextView) findViewById(R.id.Message);
        WifiManager wifiManager = (WifiManager) getSystemService("wifi");
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        String vendor = BuildConfig.FLAVOR;
        String GWMAC;
        try {
            Log.d(TAG, "Reading /proc/net/arp");
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
            BufferedReader reader;
            String mac = "";
            try {

                String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
                while (true) {
                    String read = bufferedReader.readLine();
                    if (read == null) {
                        break;
                    }
                    Matcher matcher = Pattern.compile(String.format(MAC_RE, new Object[]{read.substring(0, read.indexOf(" ")).replace(".", "\\.")})).matcher(read);
                    if (matcher.matches()) {
                        mac = matcher.group(1);
                        if (globalVariable.own_ip.equals(globalVariable.gw_ip)) {
                            break;
                        }
                    }
                }
                GWMAC = mac;
                bufferedReader.close();
                Process process = Runtime.getRuntime().exec("su", null, new File(globalVariable.path + ""));
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                os.writeBytes(globalVariable.path + "/cepter getv " + GWMAC + "\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();
                os.close();
                vendor = reader.readLine();
                reader.close();
            } catch (IOException e2) {
                e = e2;
                reader = bufferedReader;
                e.printStackTrace();
                if (monitor.indexOf("WiFi") == -1) {
                    monitor += "\nNon LMFR: " + wifiInfo.getSSID() + ", GW: " + globalVariable.gw_ip + "/" + globalVariable.netmask + "\nVendor: " + vendor;
                }
                t.setText(monitor);
                try {
                    this.mask2 = Integer.bitCount(getIPAsInteger(globalVariable.netmask));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (this.mask2 < 24) {
                    runOnUiThread(new ToastNetMask());
                }
                this.tvList1 = (ListView) findViewById(R.id.listView1);
            }
        } catch (IOException e3) {
            e = e3;
            e.printStackTrace();
            if (!monitor.contains("WiFi")) {
                monitor += "\nWiFi: " + wifiInfo.getSSID() + ", GW: " + globalVariable.gw_ip + "/" + globalVariable.netmask + "\nVendor: " + vendor;
            }
            t.setText(monitor);
            try {
                this.mask2 = Integer.bitCount(getIPAsInteger(globalVariable.netmask));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            if (this.mask2 < 24) {
                runOnUiThread(new ToastNetMask());
            }
            this.tvList1 = (ListView) findViewById(R.id.listView1);
        }
        if (!monitor.contains("WiFi")) {
            monitor += "\nWiFi: " + wifiInfo.getSSID() + ", GW: " + globalVariable.gw_ip + "/" + globalVariable.netmask + "\nVendor: " + vendor;
        }
        t.setText(monitor);
        try {
            this.mask2 = Integer.bitCount(getIPAsInteger(globalVariable.netmask));
            if (this.mask2 < 24) {
                runOnUiThread(new ToastNetMask());
            }
        } catch (Exception e4) {
            e4.printStackTrace();
        }
        this.tvList1 = (ListView) findViewById(R.id.listView1);
    }

    protected void onStart() {
        super.onStart();
        progressBar.setVisibility(View.GONE);
        Toast.makeText(getApplicationContext(), "Sniffer Enabled", 0).show();
    }
    
    class ToastNetMask implements Runnable {
        ToastNetMask() {
        }

        public void run() {
            Toast.makeText(ScanActivity.this.getApplicationContext(), "Due to netmask the scanning might take a while, be patient", 1).show();
        }
    }

    class EmptyClass implements Runnable {
        EmptyClass() {
        }

        public void run() {
        }
    }

    class selectOrUnselectTarget implements OnItemClickListener {
        selectOrUnselectTarget() {
        }

        public void onItemClick(AdapterView<?> adapterView, View itemClicked, int position, long id) {
            ScanActivity.this.itemToggled[position] = !ScanActivity.this.itemToggled[position];
            ImageView imageView1 = (ImageView) itemClicked.findViewById(R.id.icon2);
            imageView1.setImageResource(R.drawable.selected);
            imageView1.setImageResource(ScanActivity.this.itemToggled[position] ? R.drawable.selected : R.drawable.unselected);
        }
    }

    class possibleClickTarget implements OnItemLongClickListener {
        possibleClickTarget() {
        }

        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            String it = parent.getItemAtPosition(position).toString();
            int offset = it.indexOf(" ");
            if (offset > 0) {
                String index = it.substring(0, offset);
                try {
                    Intent i = new Intent(ScanActivity.this.mCtx, PortScan.class);
                    i.putExtra("Key_Int", index);
                    ScanActivity.this.startActivityForResult(i, 1);
                    globalVariable.lock = 0;
                } catch (NumberFormatException e) {
                }
            }
            return true;
        }
    }

    private class pictureFromOsTypeArray<T> extends ArrayAdapter<T> {
        public pictureFromOsTypeArray(Context context, int resource, int textViewResourceId, List<T> objects) {
            super(context, resource, textViewResourceId, objects);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = super.getView(position, convertView, parent);
            ((ImageView) itemView.findViewById(R.id.icon2)).setImageResource(ScanActivity.this.itemToggled[position] ? R.drawable.selected : R.drawable.unselected);
            ImageView imageView2 = (ImageView) itemView.findViewById(R.id.icon);
            imageView2.setImageResource(R.drawable.monitor);
            TextView tt = (TextView) itemView.findViewById(R.id.label);
            String a = tt.getText().toString();
            tt.setText(a.replaceAll("\\(-\\)", BuildConfig.FLAVOR));
            if (a.contains("Windows")) {
                imageView2.setImageResource(R.drawable.winicon);
            }
            if (!(!a.contains("Unix") && !a.contains("Linux") && !a.contains("BSD"))) {
                imageView2.setImageResource(R.drawable.linuxicon);
            }
            return itemView;
        }
    }

    private static Integer getIPAsInteger(String ip) throws Exception {
        if (ip == null) {
            return null;
        }
        int result = 0;
        StringTokenizer st = new StringTokenizer(ip, ".");
        for (int i = 3; i >= 0; i--) {
            result |= Integer.parseInt(st.nextToken()) << (i * 8);
        }
        return result;
    }
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: " + keyCode);
        if (keyCode == 4) {
            try {
                openFileOutput("exitr.id", 0).close();
                openFileOutput("exits.id", 0).close();
                Thread.sleep(100);
                DataOutputStream os = new DataOutputStream(Runtime.getRuntime().exec("su").getOutputStream());
                Log.d(TAG, "killall cepter");
                os.writeBytes("killall cepter\n");
                os.flush();
                os.writeBytes("killall cepter\n");
                os.flush();
                os.writeBytes("killall cepter\n");
                os.flush();
                if (globalVariable.strip == 1) {
                    Process process2 = Runtime.getRuntime().exec("su");
                    os = new DataOutputStream(process2.getOutputStream());
                    os.writeBytes("iptables -F;iptables -X;iptables -t nat -F;iptables -t nat -X;iptables -t mangle -F;iptables -t mangle -X;iptables -P INPUT ACCEPT;iptables -P FORWARD ACCEPT;iptables -P OUTPUT ACCEPT\n");
                    os.flush();
                    os.writeBytes("exit\n");
                    os.flush();
                    os.close();
                    process2.waitFor();
                }
                File ck = new File(globalVariable.path + "/inj");
                if (ck.exists()) {
                    ck.delete();
                }
                System.exit(0);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onScanNet(View v) throws IOException, InterruptedException {
        Log.d(TAG, "onScanNet");
        BufferedReader bufferedReader;
        this.lst = new ArrayList();
        this.tvList1.setChoiceMode(2);
        final ArrayAdapter<String> adapter = new pictureFromOsTypeArray(this, R.layout.my_list2, R.id.label, this.lst);
        this.tvList1.setAdapter(adapter);
        this.glob_adapter = adapter;
        ((ImageView) findViewById(R.id.imageView1)).setImageResource(R.drawable.scan2);
        ExecutorService service = Executors.newCachedThreadPool();
        IPv4 iPv4 = new IPv4(globalVariable.own_ip, globalVariable.netmask);
//        IPv4 iPv4 = new IPv4(globalVariable.own_ip + "/" + this.mask2);
        Integer num = iPv4.getNumberOfHosts();
        List<String> availableIPs = iPv4.getAvailableIPs(num);
        int count = 0;
        for (final String str : availableIPs) {
            service.submit(new Runnable() {
                public void run() {
                    try {
                        InetAddress.getByName(str).isReachable(1);
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }
                }
            });
            count++;
            if (count > 100) {
                SystemClock.sleep(20);
                count = 0;
            }
        }
        if (num  < 300) {
            for (final String str2 : availableIPs) {
                service.submit(new Runnable() {
                    public void run() {
                        try {
                            InetAddress.getByName(str2).isReachable(1);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        } catch (IOException e2) {
                            e2.printStackTrace();
                        }
                    }
                });
            }
        }
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        progressBar.setMax(8500);
        final ImageView but = (ImageView) findViewById(R.id.imageView1);
        but.setEnabled(false);
        final ProgressBar progressBar2 = progressBar;
        new Thread(new Runnable() {

            class C00972 implements Runnable {
                C00972() {
                }

                public void run() {
                    but.setEnabled(true);
                    ((ImageView) ScanActivity.this.findViewById(R.id.imageView1)).setImageResource(R.drawable.scan);
                }
            }

            public void run() {
                int progress = 0;
                while (progress <= 8500) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progress += 10;
                    final int prog2 = progress;
                    ScanActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            progressBar2.setProgress(prog2);
                        }
                    });
                }
                ScanActivity.this.runOnUiThread(new C00972());
            }
        }).start();
        FileOutputStream out = openFileOutput("hostlist", 0);
        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
            int c = 0;
            String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
            while (true) {
                String read = bufferedReader.readLine();
                if (read == null) {
                    break;
                }
                String ip = read.substring(0, read.indexOf(" "));
                Object[] objArr = new Object[1];
                objArr[0] = ip.replace(".", "\\.");
                Matcher matcher = Pattern.compile(String.format(MAC_RE, objArr)).matcher(read);
                if (matcher.matches()) {
                    String str3 = ip;
                    FileOutputStream fileOutputStream = out;
                    fileOutputStream.write((ip + ":" + matcher.group(1) + "\n").getBytes());
                    int c2 = c;
                    runOnUiThread(new EmptyClass());
                    c++;
                }
            }
            bufferedReader.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Process process2 = Runtime.getRuntime().exec("su", null, new File(globalVariable.path + ""));
        DataOutputStream dataOutputStream = new DataOutputStream(process2.getOutputStream());
        bufferedReader = new BufferedReader(new InputStreamReader(process2.getInputStream()));
        Log.d(TAG, "cepter scan " + globalVariable.adapt_num);
        dataOutputStream.writeBytes(globalVariable.path + "/cepter scan " + Integer.toString(globalVariable.adapt_num) + "\n");
        dataOutputStream.flush();
        dataOutputStream.writeBytes("exit\n");
        dataOutputStream.flush();
        dataOutputStream.close();
        final Process process = process2;
        final BufferedReader bufferedReader2 = bufferedReader;
        new Thread(new Runnable() {
            public void run() {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
                    int c = 0;
                    StringBuffer output = new StringBuffer();
                    while (true) {
                        String read = bufferedReader2.readLine();
                        if (read != null) {
                            int z = read.indexOf(59);
                            int ipo = read.indexOf(32);
                            int l = read.length();
                            if (ipo > 0) {
                                String ip = read.substring(0, ipo);
                                final String z2 = read.replace(": ", "\n").replace(";", ":");
                                final int c2 = c;
                                ScanActivity.this.runOnUiThread(new Runnable() {

                                    class C01001 implements Comparator {
                                        C01001() {
                                        }

                                        public int compare(Object o1, Object o2) {
                                            String p1 = (String) o1;
                                            String p2 = (String) o2;
                                            int z1 = p1.indexOf(32);
                                            int z2 = p2.indexOf(32);
                                            String ip1 = p1.substring(0, z1);
                                            String ip2 = p2.substring(0, z2);
                                            String ip11 = ip1.substring(ip1.lastIndexOf(".") + 1, ip1.length());
                                            String ip22 = ip2.substring(ip2.lastIndexOf(".") + 1, ip2.length());
                                            if (ip11.length() == 0 || ip22.length() == 0 || Integer.parseInt(ip11) == Integer.parseInt(ip22)) {
                                                return 0;
                                            }
                                            if (Integer.parseInt(ip11) > Integer.parseInt(ip22)) {
                                                return 1;
                                            }
                                            return -1;
                                        }
                                    }

                                    public void run() {
                                        ScanActivity.this.lst.add(c2, z2);
                                        adapter.sort(new C01001());
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                                c++;
                            }
                        } else {
                            bufferedReader2.close();
                            process.waitFor();
                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e2) {
                    e2.printStackTrace();
                }
            }
        }).start();
        this.tvList1.setOnItemClickListener(new selectOrUnselectTarget());
        this.tvList1.setOnItemLongClickListener(new possibleClickTarget());
    }

    public void OnScanNext(View v2) throws IOException {
        String selected = BuildConfig.FLAVOR;
        if (this.tvList1.getCount() > 0) {
            int t = 1;
            int total = this.tvList1.getCount();
            FileOutputStream out = openFileOutput("targets", 0);
            for (int i = 0; i < total; i++) {
                if (this.itemToggled[i]) {
                    int z = this.tvList1.getAdapter().getItem(i).toString().indexOf(10);
                    int z2 = this.tvList1.getAdapter().getItem(i).toString().indexOf(32);
                    int z3 = this.tvList1.getAdapter().getItem(i).toString().indexOf("] ");
                    String deb = this.tvList1.getAdapter().getItem(i).toString();
                    String mac = this.tvList1.getAdapter().getItem(i).toString().substring(z + 3, z3);
                    selected = selected + " -t" + Integer.toString(t) + " " + this.tvList1.getAdapter().getItem(i).toString().substring(0, z2);
                    t++;
                    out.write((this.tvList1.getAdapter().getItem(i).toString().substring(0, z2) + ":" + mac + "\n").getBytes());
                }
            }
            out.close();
            if (selected.equals(BuildConfig.FLAVOR)) {
                Toast.makeText(getApplicationContext(), "Choose target!", 0).show();
                return;
            }
            WifiManager wifiManager = (WifiManager) getSystemService("wifi");
            String cmd = "-gw " + globalVariable.gw_ip;
            Intent i2 = new Intent(this.mCtx, TabActivitys.class);
            i2.putExtra("Key_String", cmd);
            i2.putExtra("Key_String_origin", this.origin_str);
            startActivity(i2);
            finish();
            return;
        }
        Toast.makeText(getApplicationContext(), "Scan network!", 0).show();
    }

    public void OnCage(View v2) throws IOException {
        Log.d(TAG, "OnCage");
        String selected = BuildConfig.FLAVOR;
        if (this.tvList1.getCount() > 0) {
            int t = 1;
            int total = this.tvList1.getCount();
            FileOutputStream out = openFileOutput("cage", 0);
            for (int i = 0; i < total; i++) {
                if (this.itemToggled[i]) {
                    int z = this.tvList1.getAdapter().getItem(i).toString().indexOf(10);
                    int z2 = this.tvList1.getAdapter().getItem(i).toString().indexOf(32);
                    int z3 = this.tvList1.getAdapter().getItem(i).toString().indexOf("] ");
                    String deb = this.tvList1.getAdapter().getItem(i).toString();
                    String mac = this.tvList1.getAdapter().getItem(i).toString().substring(z + 3, z3);
                    selected = selected + " -t" + Integer.toString(t) + " " + this.tvList1.getAdapter().getItem(i).toString().substring(0, z2);
                    t++;
                    out.write((this.tvList1.getAdapter().getItem(i).toString().substring(0, z2) + ":" + mac + "\n").getBytes());
                }
            }
            out.close();
            if (selected.equals(BuildConfig.FLAVOR)) {
                Toast.makeText(getApplicationContext(), "Choose target!", 0).show();
                return;
            } else {
                startActivityForResult(new Intent(this.mCtx, CageActivity.class), 1);
                return;
            }
        }
        Toast.makeText(getApplicationContext(), "Scan network!", 0).show();
    }

    public void OnSkip(View v2) {
        Intent i = new Intent(this.mCtx, TabActivitys.class);
        i.putExtra("Key_String", BuildConfig.FLAVOR);
        i.putExtra("Key_String_origin", this.origin_str);
        startActivity(i);
        finish();
    }

    public void OnSelectAll(View v2) throws IOException {
        String selected = BuildConfig.FLAVOR;
        if (this.tvList1.getCount() > 0) {
            int total = this.tvList1.getCount();
            WifiManager wifiManager = (WifiManager) getSystemService("wifi");
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            for (int i = 0; i < total; i++) {
                boolean z;
                int i2;
                boolean[] zArr = this.itemToggled;
                if (this.itemToggled[i]) {
                    z = false;
                } else {
                    z = true;
                }
                zArr[i] = z;
                ImageView imageView1 = (ImageView) this.tvList1.getChildAt(i).findViewById(R.id.icon2);
                if (this.itemToggled[i]) {
                    i2 = R.drawable.selected;
                } else {
                    i2 = R.drawable.unselected;
                }
                imageView1.setImageResource(i2);
            }
            return;
        }
        Toast.makeText(getApplicationContext(), "Scan network!", 0).show();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void OnPics(View v) {
        startActivityForResult(new Intent(this.mCtx, GalleryActivity.class), 1);
    }
}
