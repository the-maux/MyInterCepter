package su.sniff.cepter.View.HostDiscovery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabItem;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import su.sniff.cepter.Controller.Core.BinaryWrapper.Intercepter;
import su.sniff.cepter.Controller.Network.Fingerprint;
import su.sniff.cepter.Controller.Network.IPv4CIDR;
import su.sniff.cepter.Controller.Network.NetUtils;
import su.sniff.cepter.Controller.Core.Singleton;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.Controller.Network.ScanNetmask;
import su.sniff.cepter.Controller.Core.MyActivity;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.ScanHostAdapter;
import su.sniff.cepter.View.Adapter.OSAdapter;
import su.sniff.cepter.View.Dialog.RV_dialog;
import su.sniff.cepter.View.Dialog.TIL_dialog;
import su.sniff.cepter.View.MenuActivity;

/**
 * TODO:    + Add manual target
 *          + filterOs scrollView (bottom or top ?)
 *          + filter Text as SearchView
 *          + Button add -> No target mode / Settings /
 *          + detect target onFly ?
 *          + better Os detection
 */
public class                        HostDiscoveryActivity extends MyActivity {
    private String                  TAG = "HostDiscoveryActivity";
    private HostDiscoveryActivity   mInstance = this;
    private Singleton               singleton = Singleton.getInstance();
    private CoordinatorLayout       mCoordinatorLayout;
    private AppBarLayout            mAppbar;
    private List<Host>              mHosts;
    ScanHostAdapter                 mHostAdapter;
    private RecyclerView            mHost_RV;
    private String                  monitor;
    private FloatingActionButton    mFab;
    private TextView                mEmptyList, mBottomMonitor;
    private ArrayList<String>       mListOS = new ArrayList<>();
    private int                     mProgress = 0;
    private boolean                 mHostLoaded = false, inLoading = false;
    private SwipeRefreshLayout      mSwipeRefreshLayout;
    private ImageButton             mAddHostBtn, mSettingsBtn;
    private SearchView              mSearchView;
    private Toolbar                 toolbar2;
    private TabItem                 offlinemode;

    public void                     onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostdiscovery);
        initXml();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void                  onResume() {
    try {
        super.onResume();
        init();
        if (singleton.DebugMode && !mHostLoaded) {
            Snackbar.make(mCoordinatorLayout, "debug enabled, starting Scan automaticaly", Toast.LENGTH_SHORT).show();
            startNetworkScan();
        }
    } catch (Exception e) {
        Log.e(TAG, "Big error dans l'initXml");
        Snackbar.make(mCoordinatorLayout, "Big error lors de l'init:", Toast.LENGTH_SHORT).show();
        e.printStackTrace();
    }
}

    private void                    initXml() {
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mHost_RV = (RecyclerView) findViewById(R.id.recycler_view);
        mAppbar = (AppBarLayout) findViewById(R.id.appbar);
        mEmptyList = (TextView) findViewById(R.id.emptyList);
        mBottomMonitor = ((TextView) findViewById(R.id.Message));
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mAddHostBtn = (ImageButton) findViewById(R.id.action_add_host);
        mSettingsBtn = (ImageButton) findViewById(R.id.settings);
        mSearchView = (SearchView) findViewById(R.id.searchView);
        toolbar2 = (Toolbar) findViewById(R.id.toolbar2);
        offlinemode = (TabItem) findViewById(R.id.offlinemodeItem);
    }

    private void                    init()  throws Exception {
        if (singleton.network == null || singleton.network.myIp == null) {
            Snackbar.make(mCoordinatorLayout, "You need to be connected to a network", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Intercepter.initCepter(NetUtils.getMac(singleton.network.myIp, singleton.network.gateway));
            initMonitor();
            initSwipeRefresh();

            initSearchView();
            if (offlinemode != null)//TODO: wtf is this null
                offlinemode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(mInstance, MenuActivity.class));
                    }
                });

        }
        initToolbarButton();
    }

    /**
     * Monitor is the basic network Information
     */
    private void                    initMonitor() {
        Log.d(TAG, "Init Monitor");
        WifiInfo wifiInfo = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        monitor = wifiInfo.getSSID().replace("\"", "") + " : " + singleton.network.myIp;
        if (!monitor.contains("WiFi")) {
            monitor += "\n" + "GW: " + singleton.network.gateway + "/" + singleton.network.netmask;
        } else {
            monitor += "Not Connected";
        }
        mBottomMonitor.setText(monitor);
    }

    private void                    initSwipeRefresh() {
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.material_green_200,
                R.color.material_green_500,
                R.color.material_green_900);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!inLoading) {
                    Log.d(TAG, "clearing Refresh");
                    mHosts.clear();
                    mEmptyList.setVisibility(View.GONE);
                    mHostAdapter.notifyDataSetChanged();
                    initMonitor();
                    mProgress = 0;
                    startNetworkScan();
                }
            }
        });
    }

    private void                    showAddHostDialog() {
        final TIL_dialog dialog = new TIL_dialog(mInstance)
                .setTitle("Add target");
        dialog.onPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {

                            onCheckAddedHost(dialog.getText());
                        }
                }).show();

    }

    private void                    initToolbarButton() {
        final BottomSheetMenuDialog bottomSheet = new BottomSheetBuilder(mInstance)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setBackgroundColor(ContextCompat.getColor(mInstance, R.color.material_light_white))
                .setAppBarLayout(mAppbar)
                .addTitleItem("Settings")
                .addItem(0, "Os filter", R.mipmap.ic_os_filter)
                .addItem(1, "Select all", R.mipmap.ic_select_all)
                .addItem(2, "Mode offline", R.mipmap.ic_leave)
                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem menuItem) {
                        Log.d(TAG, "STRING:"+menuItem.getTitle().toString());
                        switch (menuItem.getTitle().toString()) {
                            case "Os filter":
                                osFilterDialog();
                                break;
                            case "Select all":
                                mHostAdapter.selectAll();
                                break;
                            case "Mode offline":
                                startActivity(new Intent(mInstance, MenuActivity.class));
                                break;
                        }
                    }
                })
                .expandOnStart(true)
                .createDialog();
        mSettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "show settings");
                bottomSheet.show();
            }
        });
        mAddHostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddHostDialog();
            }
        });

    }

    private void                    initSearchView() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.contains("cheat")) {
                    for (Host host : mHosts) {
                        host.setSelected(true);
                    }
                    mHostAdapter.notifyDataSetChanged();
                } else {
                    mHostAdapter.filterByString(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mHostAdapter.filterByString("");
                return false;
            }
        });
    }

    private void                    initHostsRecyclerView() {
        mHosts = new ArrayList<>();
        mHostAdapter = new ScanHostAdapter(this, mHost_RV);
        mHost_RV.setAdapter(mHostAdapter);
        mHost_RV.setHasFixedSize(true);
        mHost_RV.setLayoutManager(new LinearLayoutManager(mInstance));
    }

    private void                    onCheckAddedHost(String addedHost) {
        Snackbar.make(mCoordinatorLayout, "Fonctionnalité non implémenté:" + addedHost, Toast.LENGTH_SHORT).show();
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
            toolbar2.setSubtitle("Scanning network");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    new ScanNetmask(new IPv4CIDR(singleton.network.myIp, singleton.network.netmask), mInstance);
                    mProgress = 1000;

                }
            }).start();
        }
    }

    public void                     onFabClick(View v)  {
        if (!mHostLoaded) {
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
        mFab.setImageResource(android.R.drawable.ic_menu_search);
        mFab.setProgress(0, true);
        mFab.setMax(5500);
        new Thread(new Runnable() {
            public void run() {
                mProgress = 0;
                while (mProgress <= 5500) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mProgress += 10;
                    final int prog2 = mProgress;
                    mInstance.runOnUiThread(new Runnable() {
                        public void run() {
                            mFab.setProgress(prog2, true);
                        }
                    });
                }
                mInstance.runOnUiThread(new Runnable() {
                    public void run() {
                        mFab.setImageResource(android.R.drawable.ic_media_play);
                        mHostLoaded = true;
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    public void                     onReachableScanOver(ArrayList<String> ipReachable) {
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toolbar2.setSubtitle("Target Identification");
            }
        });
        NetUtils.dumpListHostFromARPTableInFile(mInstance, ipReachable);
        mProgress = 1500;
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                toolbar2.setSubtitle("Fingerprint scan");
            }
        });
        Fingerprint.guessHostFingerprint(mInstance);
        mProgress = 2000;
    }

    public void                     onHostActualized(final List<Host> hosts) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHosts = hosts;
                toolbar2.setSubtitle("Choose targets");
                monitor = "GW: " + singleton.network.gateway + ": " + mHosts.size() + " device" + ((mHosts.size() > 1) ? "s": "") + " found";// oui je fais des ternaires pour faire le pluriel
                mBottomMonitor.setText(monitor);
                mHostAdapter.updateHostList(mHosts);
                inLoading = false;
                mEmptyList.setVisibility((mHosts == null || mHosts.size() == 0) ? View.VISIBLE : View.GONE);
                mProgress = 5450;
                final ArrayList<String> listOs = mHostAdapter.getOsList();
                monitor += "\n" + listOs.size() +" Os détected";
                mBottomMonitor.setText(monitor);
                Log.d(TAG, "scan Over with " + mHosts.size() + " possible target");
            }
        });
    }

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
            Snackbar.make(mCoordinatorLayout, "No target selected!", Toast.LENGTH_SHORT).show();
            return;
        }
        singleton.hostsList = selectedHost;
        startActivity(new Intent(mInstance, MenuActivity.class));
    }

    public void                     osFilterDialog() {
        final RecyclerView.Adapter adapter = new OSAdapter(mInstance, mInstance.mHostAdapter.getOsList(), mListOS);
        new RV_dialog(mInstance)
                .setAdapter(adapter)
                .setTitle("Choix des cibles")
                .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mListOS.size() > 0) {
                            mInstance.mHostAdapter.filterByOs(mListOS);
                            mListOS.clear();
                        }
                    }
                }).show();
    }
}
