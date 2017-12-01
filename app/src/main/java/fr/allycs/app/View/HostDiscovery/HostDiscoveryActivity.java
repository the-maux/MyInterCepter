package fr.allycs.app.View.HostDiscovery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
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
import java.util.Calendar;
import java.util.List;

import fr.allycs.app.Controller.Core.BinaryWrapper.Intercepter;

import fr.allycs.app.Controller.Misc.MyGlideLoader;
import fr.allycs.app.Controller.Network.Discovery.HostDiscoveryScan;
import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Controller.Misc.MyActivity;
import fr.allycs.app.Model.Target.HostDiscoverySession;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.HostDiscoveryAdapter;
import fr.allycs.app.View.Adapter.OSAdapter;
import fr.allycs.app.View.Dialog.RV_dialog;
import fr.allycs.app.View.Dialog.AddDnsDialog;
import fr.allycs.app.View.HostDetail.HostFocusActivity;
import fr.allycs.app.View.MenuActivity;
import fr.allycs.app.View.WiresharkActivity;

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
    private Singleton               mSingleton = Singleton.getInstance();
    public CoordinatorLayout        mCoordinatorLayout;
    private AppBarLayout            mAppbar;
    private List<Host>              mHosts = new ArrayList<>();
    private HostDiscoveryAdapter    mHostAdapter;
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
    private Toolbar                 mToolbar;
    private HostDiscoveryScan       mScannerControler = new HostDiscoveryScan(this);
    private TabLayout               mTabs;
    private HostDiscoveryScan.typeScan typeScan = HostDiscoveryScan.typeScan.Arp;

    public void                     onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostdiscovery);
        initXml();
        try {
            init();
            if (mSingleton.DebugMode && !mHostLoaded) {
                Snackbar.make(mCoordinatorLayout, "debug enabled, starting Scan automaticaly", Toast.LENGTH_SHORT).show();
                startNetworkScan();
            }
        } catch (Exception e) {
            Log.e(TAG, "onCreate::Error");
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
        MyGlideLoader.coordoBackground(this, mCoordinatorLayout);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mAddHostBtn = (ImageButton) findViewById(R.id.action_add_host);
        mSettingsBtn = (ImageButton) findViewById(R.id.settings);
        mSearchView = (SearchView) findViewById(R.id.searchView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar2);
        mTabs = (TabLayout) findViewById(R.id.tabs);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mHostLoaded) {
                    startNetworkScan();
                } else {
                    try {
                        launchMenu();
                    } catch (IOException e) {
                        Log.e(TAG, "Error in start attack");
                        e.getStackTrace();
                    }
                }
            }
        });
    }

    private void                    init()  throws Exception {
        if (mSingleton.network == null || mSingleton.network.myIp == null) {
            Snackbar.make(mCoordinatorLayout, "You need to be connected to a network", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Intercepter.initCepter(mSingleton.network.mac);
            initMonitor();
            initSwipeRefresh();
            initTabs();
            initSearchView();
        }
        initToolbarButton();
    }

    private void                    initTabs() {
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "tab.getHost().toString():" + tab.getText().toString());
                switch (tab.getText().toString()) {
                    case "arp\nDiscovery":
                        Log.d(TAG, "Nmap Tab");
                        typeScan = HostDiscoveryScan.typeScan.Nmap;
                        break;
                    case "Icmp\ndiscovery":
                        Log.d(TAG, "ARP TAB");
                        typeScan = HostDiscoveryScan.typeScan.Arp;
                        break;
                    case "Services\nDiscovery":
                        Log.d(TAG, "Service TAB");
                        typeScan = HostDiscoveryScan.typeScan.Services;
                        break;

                }
                startNetworkScan();
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void                    initMonitor() {
        Log.d(TAG, "Init Monitor");
        WifiInfo wifiInfo = null;
        if (((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)) != null) {
            wifiInfo = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo();
        }
        monitor = wifiInfo.getSSID().replace("\"", "") + " : " + mSingleton.network.gateway;
        if (!monitor.contains("WiFi")) {
            monitor += "\n" + " MyIp : " + mSingleton.network.myIp;
        } else {
            monitor += "Not Connected";
        }
        if (Singleton.getInstance().network.isConnectedToNetwork())
            mBottomMonitor.setText(monitor);
        else
            mBottomMonitor.setText(wifiInfo.getSSID() + ": No connection");
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
        final AddDnsDialog dialog = new AddDnsDialog(mInstance)
                .setTitle("Add target");
        dialog.onPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {

                            onCheckAddedHost(dialog.getHost());
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
                mHostAdapter.filterByString(query);
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
        mHosts.clear();
        mHostAdapter = new HostDiscoveryAdapter(this, mHost_RV);
        mHost_RV.setAdapter(mHostAdapter);
        mHost_RV.setHasFixedSize(true);
        mHost_RV.setLayoutManager(new LinearLayoutManager(mInstance));
    }

    private void                    onCheckAddedHost(String addedHost) {
        Snackbar.make(mCoordinatorLayout, "Fonctionnalité non implémenté:" + addedHost, Toast.LENGTH_SHORT).show();
    }

    private void                    startNetworkScan() {
        if (!inLoading) {
            if (mSingleton.network.updateInfo().isConnectedToNetwork()) {
                inLoading = true;
                if (typeScan != HostDiscoveryScan.typeScan.Services)
                    initHostsRecyclerView();
                progressAnimation();
                mToolbar.setSubtitle("Scanning network");
                new HostDiscoveryScan(this).run(typeScan, mHosts);
                mProgress = 1000;
            } else {
                Snackbar.make(mCoordinatorLayout, "You need to be connected", Toast.LENGTH_SHORT).show();
                mEmptyList.setVisibility(View.VISIBLE);
            }
        } else {
            Snackbar.make(mCoordinatorLayout, "Patientez, loading en cours", Toast.LENGTH_SHORT).show();
        }
    }
    final int MAXIMUM_PROGRESS = 6500;
    private void                    progressAnimation() {

        mFab.setImageResource(android.R.drawable.ic_menu_search);
        mFab.setProgress(0, true);
        mFab.setMax(MAXIMUM_PROGRESS);
        new Thread(new Runnable() {
            public void run() {
                mProgress = 0;
                while (mProgress <= MAXIMUM_PROGRESS) {
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

    public void                     onHostActualized(final List<Host> hosts) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHosts = hosts;
                mProgress = MAXIMUM_PROGRESS;
                mToolbar.setSubtitle("Choose targets");
                monitor = "Gateway: " + mSingleton.network.gateway;// oui je fais des ternaires pour faire le pluriel
                mBottomMonitor.setText(monitor);
                mHostAdapter.updateHostList(mHosts);
                inLoading = false;
                mEmptyList.setVisibility((mHosts == null || mHosts.size() == 0) ? View.VISIBLE : View.GONE);
                final ArrayList<String> listOs = mHostAdapter.getOsList();
                monitor += "\n IP:" + mSingleton.network.myIp;
                mBottomMonitor.setText(monitor);
                Log.d(TAG, "scan Over with " + mHosts.size() + " possible target");
                HostDiscoverySession session = new HostDiscoverySession(Calendar.getInstance().getTime(), mHosts);
                mToolbar.setTitle(((WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID().replace("\"", ""));
                mToolbar.setSubtitle(mHosts.size() + " device" + ((mHosts.size() > 1) ? "s": ""));
                //TODO: DUMP IT !
            }
        });
    }

    private void                    launchMenu() throws IOException {
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
        mSingleton.hostsList = selectedHost;
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

    public void                     monitor(final String msg) {
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mToolbar.setSubtitle(msg);
            }
        });
    }

    public void                     setProgressState(int progress){
        this.mProgress = progress;
    }

    public void                     notifiyServiceAllScaned() {
        Snackbar.make(mCoordinatorLayout, "Scanning service on network finished", Toast.LENGTH_SHORT).show();
        inLoading = false;
    }

    public void                     focusOneTarget(Host host) {
        if (mSingleton.hostsList == null)
            mSingleton.hostsList = new ArrayList<>();
        else
            mSingleton.hostsList.clear();
        mSingleton.hostsList.add(host);
        Intent intent = new Intent(mInstance, HostFocusActivity.class);
        startActivity(intent);
    }
}
