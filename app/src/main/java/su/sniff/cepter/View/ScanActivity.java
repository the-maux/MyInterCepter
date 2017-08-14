package su.sniff.cepter.View;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import su.sniff.cepter.Controller.CepterControl.IntercepterWrapper;
import su.sniff.cepter.Controller.Network.IPv4CIDR;
import su.sniff.cepter.Controller.Network.NetUtils;
import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Controller.System.RootProcess;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.Controller.Network.ScanNetmask;
import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.HostScanAdapter;
import su.sniff.cepter.globalVariable;

/**
 * TODO:    + Add manual target
 *          + filterOs scrollView (bottom or top ?)
 *          + filter Text as SearchView
 *          + Button add -> No target mode / Settings /
 *          + detect target onFly ?
 *          + better Os detection
 */
public class                        ScanActivity extends MyActivity {
    private String                  TAG = "ScanActivity";
    private ScanActivity            mInstance = this;
    private List<Host>              mHosts;
    private HostScanAdapter adapter;
    private RecyclerView            hostsRecyclerView;
    private LinearLayout            filterLL;
    private String                  origin_str, monitor;
    private FloatingActionButton    progressBar;
    private TextView                TxtMonitor;
    private int                     progress = 0;
    private boolean                 hostLoaded = false, inLoading = false, doWeWaitForMyOwnScan = true;
    private SwipeRefreshLayout      swipeRefreshLayout;

    public void                     onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(3);
            setContentView(R.layout.activity_scan);
            getWindow().setFeatureDrawableResource(3, R.drawable.ico);
            this.origin_str = monitor;
            init();
            if (globalVariable.DEBUG) {
                Log.d(TAG, "debug enabled, starting Scan automaticaly");
                startNetworkScan();
            }
        } catch (Exception e) {
            Log.e(TAG, "Big error dans l'init");
            e.printStackTrace();
        }
    }

    private void                    init() throws Exception {
        progressBar = (FloatingActionButton) findViewById(R.id.fab);
        hostsRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        filterLL = (LinearLayout) findViewById(R.id.filterLL);
        TxtMonitor = ((TextView) findViewById(R.id.Message));
        TxtMonitor.setText("");
        if (Singleton.network.myIp == null) {
            Toast.makeText(getApplicationContext(), "You need to be connected to a network",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        IntercepterWrapper.initCepter(NetUtils.getMac(Singleton.network.myIp, Singleton.network.gateway));
        initMonitor();
        initSwipeRefresh();
    }

    /**
     * Monitor is the basic network Information
     */
    private void                    initMonitor() {
        Log.d(TAG, "Init Monitor");
        WifiInfo wifiInfo = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        monitor = wifiInfo.getSSID().replace("\"", "") + ':' + getIntent().getExtras().getString("Key_String");
        if (!monitor.contains("WiFi")) {
            monitor += "\n" + "GW: " + Singleton.network.gateway + "/" + Singleton.network.netmask;
        } else {
            monitor += "Not Connected";
        }
        TxtMonitor.setText(monitor);
    }

    private void                    initSwipeRefresh() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setColorSchemeResources(
                R.color.material_green_200,
                R.color.material_green_500,
                R.color.material_green_900);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!inLoading) {
                    Log.d(TAG, "clearing Refresh");
                    mHosts.clear();
                    filterLL.removeAllViews();
                    adapter.notifyDataSetChanged();
                    initMonitor();
                    progress = 0;
                    startNetworkScan();
                }
            }
        });
    }

    private void                    initHostsRecyclerView() {
        mHosts = new ArrayList<>();
        adapter = new HostScanAdapter(this);
        hostsRecyclerView.setAdapter(adapter);
        hostsRecyclerView.setHasFixedSize(true);
        hostsRecyclerView.setLayoutManager(new LinearLayoutManager(mInstance));
    }

    /**
     * BuildIPv4 objet to scan netmask
     * Get list of potential target from arp table file
     * get Cepter scan
     */
    private void                    startNetworkScan() {
        if (!inLoading) {
            inLoading = true;
            initHostsRecyclerView();
            progressAnimation();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new ScanNetmask(new IPv4CIDR(Singleton.network.myIp, Singleton.network.netmask), mInstance);
                    progress = 1000;
                }
            }).start();
        }
    }

    public void                     onFabClick(View v)  {
        if (!hostLoaded) {
            startNetworkScan();
        } else {
            try {
                startAttack();
            } catch (IOException e) {
                Log.e(TAG, "Error in start attack");
                e.getStackTrace();
            }
        }
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
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    public void                     onReachableScanOver(ArrayList<String> ipReachable) {
        Log.e(TAG, "tu dois toujours faire le scan par cepter des reachable que ta trouvé en ping ;)");
        //Apparament non
        NetUtils.dumpListHostFromARPTableInFile(mInstance, ipReachable);
        progress = 1500;
        IntercepterWrapper.fillHostListWithCepterScan(mInstance);
        progress = 2000;
    }

    public void                     onHostActualized(final List<Host> hosts) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHosts = hosts;
                monitor = "GW: " + Singleton.network.gateway + ": " + mHosts.size() + " device" + ((mHosts.size() > 1) ? "s": "") + " found";// oui je fais des ternaires pour faire le pluriel
                TxtMonitor.setText(monitor);
                adapter.updateHostList(mHosts);
                buildFilterLayout();
                inLoading = false;
                Log.d(TAG, "scan Over with " + mHosts.size() + " possible target");
            }
        });
    }

    private void                    buildFilterLayout() {
        final ArrayList<String> listOs = adapter.getOsList();
        monitor += "\n" + listOs.size() +" Os détected";
        TxtMonitor.setText(monitor);
        for (final String os : listOs) {
            View OsView = View.inflate(this, R.layout.item_host_checkbox, null);
            Host.setOsIcon(this, os, (CircleImageView)OsView.findViewById(R.id.imageOS));
            ((TextView)OsView.findViewById(R.id.nameOS)).setText(os.replace("_", "/"));
            final CheckBox cb = ((CheckBox)OsView.findViewById(R.id.checkBox));
            OsView.setOnClickListener(filtereffect(cb, os, listOs));
            cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    filtereffect(cb, os, listOs);
                }
            });
            filterLL.addView(OsView, 200, 40);
        }
        findViewById(R.id.ScrollViewFilter).setVisibility(View.VISIBLE);
        ((EditText)findViewById(R.id.filterText)).addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                adapter.filterByString(editable.toString());
            }
        });
    }

    private View.OnClickListener    filtereffect(final CheckBox cb, final String os, final ArrayList<String> listOs) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb.setChecked(!cb.isChecked());
                if (!cb.isChecked())
                    listOs.remove(os);
                else
                    listOs.add(os);
                adapter.filterByOs(listOs);
            }
        };
    }
    /**
     * Create a file "./targets" dumping the hostList than start the TabActivitys
     * @throws IOException
     */
    private void                    startAttack() throws IOException {
        ArrayList<Host> selectedHost = new ArrayList<>();
        boolean noTargetSelected = true;
        FileOutputStream out = openFileOutput("targets", 0);
        for (Host host : mHosts) {
            if (host.isSelected()) {
                selectedHost.add(host);
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
        String cmd = "-gw " + Singleton.network.gateway;
        Intent i2 = new Intent(mInstance, MenuActivity.class);
        Log.i(TAG, cmd);
        i2.putExtra("Key_String", cmd);
        Singleton.hostsList = selectedHost;
        startActivity(i2);

    }

    public void                     OnCage(View v2) throws IOException {
        Log.d(TAG, "OnCage");
        boolean noTargetSelected = true;
        FileOutputStream out = openFileOutput("cage", 0);

        for (Host host : mHosts) {
            if (host.isSelected()) {
                noTargetSelected = false;
                String dumpHost = host.getIp() + ":" + host.getMac() + "\n";
                Log.d(TAG, "dumpHost:" + dumpHost);
                out.write(dumpHost.getBytes());
            }
        }
        out.close();
        if (mHosts.size() == 0)
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
                File ck = new File(Singleton.FilesPath + "/inj");
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
