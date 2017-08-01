package su.sniff.cepter.View;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.github.clans.fab.FloatingActionButton;
import su.sniff.cepter.*;
import su.sniff.cepter.Controller.IPv4;
import su.sniff.cepter.Controller.NetUtils;
import su.sniff.cepter.Controller.RootProcess;
import su.sniff.cepter.Model.Host;
import su.sniff.cepter.Network.ScanNetmask;
import su.sniff.cepter.Utils.Net.IpUtils;
import su.sniff.cepter.Utils.TabActivitys;
import su.sniff.cepter.adapter.HostAdapter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class                        ScanActivity extends Activity {
    private String                  TAG = "ScanActivity";
    private ScanActivity            mInstance = this;
    private boolean[]               itemToggled = new boolean[2048];
    private ArrayList<Host>         lst;//Liste de String contenant Ip(Hostname)\n[MAC][OS] : Info
    private List<Host>              hosts; // Liste de Host contenant Ip(Hostname)\n[MAC][OS] : Info
    private RecyclerView            hostsRecyclerView;
    private String                  origin_str, monitor;
    private FloatingActionButton    progressBar;
    private int                     progress = 0;
    private boolean                 hostLoaded = false;

    public void                     onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(3);
            setContentView(R.layout.scan_layout);
            getWindow().setFeatureDrawableResource(3, R.drawable.ico);
            this.origin_str = monitor;
            init();
        } catch (Exception e) {
            Log.e(TAG, "Big error dans l'init");
            e.printStackTrace();
        }
    }

    private void                    init() throws Exception {
        progressBar = (FloatingActionButton) findViewById(R.id.fab);
        hostsRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        Arrays.fill(itemToggled, false); //clear tab of bool
        Cepter.startCepter(NetUtils.getMac());
        initMonitor(((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo());
    }

    /**
     * Monitor is the basic network Information
     * @param wifiInfo
     */
    private void                    initMonitor(WifiInfo wifiInfo) throws Exception{
        Log.d(TAG, "Init Monitor");
        monitor = getIntent().getExtras().getString("Key_String");
        Log.d(TAG, "Monitor [" + monitor + "]");
        if (!monitor.contains("WiFi")) {
            monitor += "\nNon LMFR: " + wifiInfo.getSSID() + ", GW: " + globalVariable.gw_ip + "/" + globalVariable.netmask;
        }
        ((TextView)findViewById(R.id.Message)).setText(monitor);
        if (Integer.bitCount(IpUtils.getIPAsInteger(globalVariable.netmask)) < 24) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mInstance, "Due to netmask the scanning might take a while, be patient", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void                    initHostsRecyclerView() {
        hostsRecyclerView.setHasFixedSize(true);
        hostsRecyclerView.setLayoutManager(new LinearLayoutManager(mInstance));
    }

    private void                    startNetworkScan() throws IOException, InterruptedException {
        //lst = new ArrayList<>();
        hosts = new ArrayList<>();
        final HostAdapter adapter = new HostAdapter(hosts, mInstance);
        //hostsListView.setAdapter(adapter);
        hostsRecyclerView.setAdapter(adapter);
        progressAnimation();
        IPv4 iPv4 = new IPv4(globalVariable.own_ip, globalVariable.netmask);//IPv4 iPv4 = new IPv4(globalVariable.own_ip + "/" + this.mask2);
        new ScanNetmask(iPv4);
        progress = 1000;
        readARPTable();
        progress = 1500;
        readingCepterAnalysFromArp(adapter);
        progress = 2000;
        initHostsRecyclerView();
        //initHostListView();
    }

    /*
        Guessing list of host by reading ARP table
     */
    private void                    readARPTable() {
        Log.i(TAG, "Dump list host from Arp Table");
        try {
            FileOutputStream hostListFile = openFileOutput("hostlist", 0);
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
            String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
            String read;
            while ((read = bufferedReader.readLine()) != null) {
                String ip = read.substring(0, read.indexOf(" "));
                Object[] objArr = new Object[1];
                objArr[0] = ip.replace(".", "\\.");
                Matcher matcher = Pattern.compile(String.format(MAC_RE, objArr)).matcher(read);
                if (matcher.matches()) {
                    Log.i(TAG, "DUMP:" + ip + " " + matcher.group(1));
                    hostListFile.write((ip + ":" + matcher.group(1) + "\n").getBytes());
                }
            }
            bufferedReader.close();
            hostListFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void                    readingCepterAnalysFromArp(final HostAdapter adapter)  throws IOException, InterruptedException {
        final BufferedReader bufferedReader2 = Cepter.searchDevices(Integer.toString(globalVariable.adapt_num));
        Log.d(TAG, "reading list host from Cepter");
        new Thread(new Runnable() {
            public void run() {
                try {
                    String read;
                    while ((read = bufferedReader2.readLine()) != null) {//sanityze: at least 3 '.' for x.x.x.x : Ip
                        if ((read.length() - read.replace(".", "").length()) >= 3) {//Format : 10.16.186.3 	(-) \n [D8-FC-93-26-D4-EB] [Windows 7\8\10] : Intel Corporate \n
                            Host hostObj = new Host(read);
                            hosts.add(hostObj);
                        }
                    }
                    mInstance.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            sortHosts();
                            adapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void                    progressAnimation() {
        //TODO: RefreshOnSwipe
        Log.d(TAG, "progress Animation");
        progressBar.setImageResource(android.R.drawable.ic_menu_search);
        progressBar.setProgress(0, true);
        progressBar.setMax(4000);
        new Thread(new Runnable() {
            public void run() {
                progress = 0;
                while (progress <= 4000) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progress += 10;
                    final int prog2 = progress;
                    mInstance.runOnUiThread(new Runnable() {
                        public void run() {
                            progressBar.setProgress(prog2, true);
                        }
                    });
                }
                mInstance.runOnUiThread(new Runnable() {
                    public void run() {
                        progressBar.setImageResource(android.R.drawable.ic_media_play);
                        hostLoaded = true;
                    }
                });
            }
        }).start();
    }

    public void                     onFabClick(View v) throws IOException, InterruptedException  {
        if (!hostLoaded) {
            startNetworkScan();
        } else {
            startAttack();
        }
    }

    /**
     * Create a file "./targets" dumping the hostList than start the TabActivitys
     * @throws IOException
     */
    private void                    startAttack() throws IOException {
        boolean noTargetSelected = true;
        FileOutputStream out = openFileOutput("targets", 0);
        for (Host host : hosts) {
            if (host.isSelected()) {
                noTargetSelected = false;
                String dumpHost = host.getIp() + ":" + host.getMac() + "\n";
                Log.d(TAG, "Dumpin File(./targets):" + dumpHost);
                out.write(dumpHost.getBytes());
            }
        }
        out.close();
        if (noTargetSelected) {
            Toast.makeText(getApplicationContext(), "No target selected!", Toast.LENGTH_SHORT).show();
            return;
        }
        String cmd = "-gw " + globalVariable.gw_ip;
        Intent i2 = new Intent(mInstance, TabActivitys.class);
        Log.i(TAG, cmd);
        i2.putExtra("Key_String", cmd);
        i2.putExtra("Key_String_origin", origin_str);
        startActivity(i2);
        finish();
    }

    private void                    sortHosts() {
        mInstance.runOnUiThread(new Runnable() {
            public void run() {
                Collections.sort(hosts, new Comparator<Host>() {
                    @Override
                    public int compare(Host o1, Host o2) {
                        String ip1[] = o1.getIp().replace(".", "::").split("::");
                        String ip2[] = o2.getIp().replace(".", "::").split("::");
                        if (Integer.parseInt(ip1[2]) > Integer.parseInt(ip2[2]))
                            return 1;
                        else if (Integer.parseInt(ip1[2]) < Integer.parseInt(ip2[2]))
                            return -1;
                        else if (Integer.parseInt(ip1[3]) > Integer.parseInt(ip2[3]))
                            return 1;
                        else if (Integer.parseInt(ip1[3]) < Integer.parseInt(ip2[3]))
                            return -1;
                        return 0;
                    }
                });
            }
        });
    }

    public void                     OnCage(View v2) throws IOException {
        Log.d(TAG, "OnCage");
        boolean noTargetSelected = true;
        FileOutputStream out = openFileOutput("cage", 0);

        for (Host host : hosts) {
            if (host.isSelected()) {
                noTargetSelected = false;
                String dumpHost = host.getIp() + ":" + host.getMac() + "\n";
                Log.d(TAG, "dumpHost:" + dumpHost);
                out.write(dumpHost.getBytes());
            }
        }
        out.close();
        if (hosts.size() == 0)
            Toast.makeText(getApplicationContext(), "Scan network", Toast.LENGTH_SHORT).show();
        else if (noTargetSelected)
            Toast.makeText(getApplicationContext(), "Choose target!", Toast.LENGTH_SHORT).show();
        else
            startActivityForResult(new Intent(mInstance, CageActivity.class), 1);

    }

    public boolean                  onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: " + keyCode);
        if (keyCode == 4) {
            try {
                openFileOutput("exits.id", 0).close();
                Thread.sleep(100);
                RootProcess process = new RootProcess("LEAVE APP")
                        .exec("killall cepter")
                        .exec("killall cepter")
                        .exec("killall cepter");
                if (globalVariable.strip == 1) {
                    process.exec("iptables -F;" +
                            "iptables -X; " +
                            "iptables -t nat -F;" +
                            "iptables -t nat -X;" +
                            "iptables -t mangle -F;" +
                            "iptables -t mangle -X;" +
                            "iptables -P INPUT ACCEPT;"+
                            "iptables -P FORWARD ACCEPT;"+
                            "iptables -P OUTPUT ACCEPT");
                }
                process.closeProcess();
                File ck = new File(globalVariable.path + "/inj");
                if (ck.exists()) {
                    ck.delete();
                }
                finish();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
