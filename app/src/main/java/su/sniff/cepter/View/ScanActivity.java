package su.sniff.cepter.View;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import su.sniff.cepter.Controller.CepterControl.IntercepterWrapper;
import su.sniff.cepter.Controller.Network.IPv4CIDR;
import su.sniff.cepter.Controller.Network.NetUtils;
import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Controller.System.Wrapper.RootProcess;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.Controller.Network.ScanNetmask;
import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.HostScanAdapter;
import su.sniff.cepter.View.Adapter.OSAdapter;
import su.sniff.cepter.View.Dialog.RV_dialog;
import su.sniff.cepter.View.Dialog.TIL_dialog;
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
    private TIL_dialog              addHostDialog;
    private HostScanAdapter         adapter;
    private RecyclerView            hostsRecyclerView;
    private String                  monitor;
    private FloatingActionButton    fab;
    private TextView                TxtMonitor;
    private TextView                mEmptyList;
    private ArrayList<String>       listOsSelected = new ArrayList<>();
    private int                     progress = 0;
    private boolean                 hostLoaded = false, inLoading = false, isMenu = false;
    private SwipeRefreshLayout      swipeRefreshLayout;

    public void                     onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(3);
            setContentView(R.layout.activity_scan);
            getWindow().setFeatureDrawableResource(3, R.drawable.ico);
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
        fab = (FloatingActionButton) findViewById(R.id.fab);
        hostsRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mEmptyList = (TextView) findViewById(R.id.emptyList);
        TxtMonitor = ((TextView) findViewById(R.id.Message));
        TxtMonitor.setText("");
        if (Singleton.getInstance().network == null || Singleton.getInstance().network.myIp == null) {
            Toast.makeText(getApplicationContext(), "You need to be connected to a network",
                    Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        IntercepterWrapper.initCepter(NetUtils.getMac(Singleton.getInstance().network.myIp, Singleton.getInstance().network.gateway));
        initMonitor();
        initSwipeRefresh();
        initDialog();
        initMenu();
        initSearchView();
    }

    /**
     * Monitor is the basic network Information
     */
    private void                    initMonitor() {
        Log.d(TAG, "Init Monitor");
        WifiInfo wifiInfo = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        monitor = wifiInfo.getSSID().replace("\"", "") + ':' + getIntent().getExtras().getString("Key_String");
        if (!monitor.contains("WiFi")) {
            monitor += "\n" + "GW: " + Singleton.getInstance().network.gateway + "/" + Singleton.getInstance().network.netmask;
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
                    mEmptyList.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                    initMonitor();
                    progress = 0;
                    startNetworkScan();
                }
            }
        });
    }

    private void                    initDialog() {
        addHostDialog = new TIL_dialog(mInstance)
                .setTitle("Add host");
        addHostDialog.onPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onCheckAddedHost(addHostDialog.getText());
            }
        });
    }

    private void                    initMenu() {
        // add host
        ImageButton addHost = (ImageButton) findViewById(R.id.action_add_host);
        addHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { addHostDialog.show();
            }
        });
        // settings
        ImageButton settings = (ImageButton) findViewById(R.id.action_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMenu = true;
                mInstance.findViewById(R.id.clipper).setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
            }
        });
        // os filter
        TextView osFilter = (TextView) findViewById(R.id.action_os_filter);
        osFilter.setOnClickListener(onClickMenuItem());
        // select all
        TextView selectAll = (TextView) findViewById(R.id.action_select_all);
        selectAll.setOnClickListener(onClickMenuItem());
        // offline mode
        TextView offlineMode = (TextView) findViewById(R.id.action_offline_mode);
        offlineMode.setOnClickListener(onClickMenuItem());
        // clipper
        findViewById(R.id.clipper).setOnClickListener(onClickMenuItem());
    }

    private void                    initSearchView() {
        SearchView searchView = (SearchView) findViewById(R.id.filterText);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.contains("cheat")) {
                    for (Host host : mHosts) {
                        host.setSelected(true);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    adapter.filterByString(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                adapter.filterByString("");
                return false;
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
                    new ScanNetmask(new IPv4CIDR(Singleton.getInstance().network.myIp, Singleton.getInstance().network.netmask), mInstance);
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
        fab.setImageResource(android.R.drawable.ic_menu_search);
        fab.setProgress(0, true);
        fab.setMax(4500);
        new Thread(new Runnable() {
            public void run() {
                progress = 0;
                while (progress <= 4500) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progress += 10;
                    final int prog2 = progress;
                    mInstance.runOnUiThread(new Runnable() {
                        public void run() {
                            fab.setProgress(prog2, true);
                        }
                    });
                }
                mInstance.runOnUiThread(new Runnable() {
                    public void run() {
                        fab.setImageResource(android.R.drawable.ic_media_play);
                        hostLoaded = true;
                        swipeRefreshLayout.setRefreshing(false);
                        if (mHosts == null || mHosts.size() == 0) {
                            mEmptyList.setVisibility(View.VISIBLE);
                        } else {
                            mEmptyList.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }).start();
    }

    public void                     onReachableScanOver(ArrayList<String> ipReachable) {
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
                monitor = "GW: " + Singleton.getInstance().     network.gateway + ": " + mHosts.size() + " device" + ((mHosts.size() > 1) ? "s": "") + " found";// oui je fais des ternaires pour faire le pluriel
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
        String cmd = "-gw " + Singleton.getInstance().network.gateway;
        Intent i2 = new Intent(mInstance, MenuActivity.class);
        Log.i(TAG, cmd);
        i2.putExtra("Key_String", cmd);
        Singleton.getInstance().hostsList = selectedHost;
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
                File ck = new File(Singleton.getInstance().FilesPath + "/inj");
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

    private void onCheckAddedHost(String addedHost) {
        Toast.makeText(this, "Added host:" + addedHost, Toast.LENGTH_SHORT).show();
    }

    private View.OnClickListener    onClickMenuItem() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMenu = false;
                mInstance.findViewById(R.id.clipper).setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
                switch (v.getId()) {
                    case R.id.action_offline_mode:
                        Toast.makeText(mInstance, "offlineMode", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_os_filter:
                        final RecyclerView.Adapter adapter = new OSAdapter(mInstance, mInstance.adapter.getOsList(), listOsSelected);
                        new RV_dialog(mInstance)
                                .setAdapter(adapter)
                                .setTitle("Choix des cibles")
                                .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (listOsSelected.size() > 0) {
                                            mInstance.adapter.filterByOs(listOsSelected);
                                            listOsSelected.clear();
                                        }
                                    }
                                }).show();
                        break;
                    case R.id.action_select_all:
                        mInstance.adapter.selectAll();
                        break;
                    default:
                        break;
                }
            }
        };
    }
}
