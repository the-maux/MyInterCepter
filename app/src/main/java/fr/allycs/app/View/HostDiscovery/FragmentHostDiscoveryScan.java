package fr.allycs.app.View.HostDiscovery;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Database.DBSession;
import fr.allycs.app.Controller.Network.Discovery.HostDiscoveryScan;
import fr.allycs.app.Controller.Network.NetUtils;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.HostDiscoveryAdapter;
import fr.allycs.app.View.Adapter.OSAdapter;
import fr.allycs.app.View.Dialog.RV_dialog;
import fr.allycs.app.View.HostDetail.HostFocusActivity;
import fr.allycs.app.View.MenuActivity;

public class                        FragmentHostDiscoveryScan extends Fragment {
    private String                  TAG = "FragmentHostDiscoveryScan";
    private HostDiscoveryActivity   mActivity;
    private Singleton               mSingleton = Singleton.getInstance();
    private List<Host>              mHosts = new ArrayList<>();
    private boolean                 mHostLoaded = false;
    private HostDiscoveryAdapter    mHostAdapter;
    private RecyclerView            mHost_RV;
    private TextView                mEmptyList;
    private SwipeRefreshLayout      mSwipeRefreshLayout;
    private ArrayList<String>       mListOS = new ArrayList<>();
    private HostDiscoveryScan       mScannerControler;

    @Override
    public View                     onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);
        if (mSingleton.DebugMode && !mHostLoaded) {
            Snackbar.make(mActivity.mCoordinatorLayout, "debug enabled, starting Scan automaticaly", Toast.LENGTH_SHORT).show();
            startNetworkScan();
        }
        initSwipeRefresh();
        return rootView;
    }

    public void                     init(HostDiscoveryActivity activity) {
        this.mActivity = activity;
        mScannerControler = new HostDiscoveryScan(activity);
    }

    private void                    initXml(View rootView) {
        mHost_RV = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mEmptyList = (TextView) rootView.findViewById(R.id.emptyList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
    }

    private void                    initSwipeRefresh() {
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.material_green_200,
                R.color.material_green_500,
                R.color.material_green_900);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!mScannerControler.inLoading) {
                    Log.d(TAG, "clearing Refresh");
                    mHosts.clear();
                    mEmptyList.setVisibility(View.GONE);
                    if (mHostAdapter != null)
                        mHostAdapter.notifyDataSetChanged();
                    mActivity.initMonitor();
                    mActivity.setProgressState(0);
                    startNetworkScan();
                }
            }
        });
    }

    private void                    initHostsRecyclerView() {
        mHosts.clear();
        mHostAdapter = new HostDiscoveryAdapter(this, mHost_RV, false);
        mHost_RV.setAdapter(mHostAdapter);
        mHost_RV.setHasFixedSize(true);
        mHost_RV.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    public void                     startNetworkScan() {
        mActivity.setProgressState(-1);
        if (!mScannerControler.inLoading) {
            try {
                if (mSingleton.network == null && !NetUtils.initNetworkInfo(mActivity)) {
                    Snackbar.make(mActivity.mCoordinatorLayout, "You need to be connected", Toast.LENGTH_SHORT).show();
                    mEmptyList.setVisibility(View.VISIBLE);
                    return;
                }
                if (mSingleton.network.updateInfo().isConnectedToNetwork()) {
                    mScannerControler.inLoading = true;
                    if (mActivity.typeScan != HostDiscoveryScan.typeScan.Services)
                        initHostsRecyclerView();
                    mActivity.progressAnimation();
                    mActivity.setToolbarTitle(null, "Scanning network");
                    mScannerControler.run(typeScan, mHosts);
                    mActivity.setProgressState(1000);
                } else {
                    Snackbar.make(mActivity.mCoordinatorLayout, "You need to be connected", Toast.LENGTH_SHORT).show();
                    mEmptyList.setVisibility(View.VISIBLE);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            Snackbar.make(mActivity.mCoordinatorLayout, "Patientez, loading en cours", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean                  start() {
        if (!mHostLoaded) {
            startNetworkScan();
            return true;
        }
        return false;
    }

    public void                     onHostActualized(final List<Host> hosts) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHosts = hosts;
                mActivity.setProgressState(mActivity.MAXIMUM_PROGRESS);
                String monitor = "Gateway: " + mSingleton.network.gateway;
                mHostAdapter.updateHostList(mHosts);
                mScannerControler.inLoading = false;
                mEmptyList.setVisibility((mHosts == null || mHosts.size() == 0) ? View.VISIBLE : View.GONE);
                final ArrayList<String> listOs = mHostAdapter.getOsList();
                monitor += "\n IP:" + mSingleton.network.myIp;
                mActivity.setBottombarTitle(monitor);
                Log.d(TAG, "scan Over with " + mHosts.size() + " possible target");
                final String SSID = ((WifiManager)mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                        .getConnectionInfo().getSSID().replace("\"", "");
                mActivity.setToolbarTitle(SSID, mHosts.size() + " device" + ((mHosts.size() > 1) ? "s": ""));
                mActivity.mActualSession = DBSession.buildSession(
                        SSID, mSingleton.network.gateway, hosts, "Icmp", mHostAdapter.getOsList());
                mSwipeRefreshLayout.setRefreshing(false);
                mHostLoaded = true;
            }
        });

    }

    public void                     osFilterDialog() {
        final RecyclerView.Adapter adapter = new OSAdapter(mActivity, mHostAdapter.getOsList(), mListOS);
        new RV_dialog(mActivity)
                .setAdapter(adapter)
                .setTitle("Choix des cibles")
                .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mListOS.size() > 0) {
                            mHostAdapter.filterByOs(mListOS);
                            mListOS.clear();
                        }
                    }
                }).show();
    }

    public void                     focusOneTarget(Host host) {
        mSingleton.actualSession = mActivity.mActualSession;
        if (mSingleton.hostsList == null)
            mSingleton.hostsList = new ArrayList<>();
        else
            mSingleton.hostsList.clear();
        mSingleton.hostsList.add(host);
        Intent intent = new Intent(mActivity, HostFocusActivity.class);
        startActivity(intent);
    }

    public void                     onCheckAddedHost(String addedHost) {
        Snackbar.make(mActivity.mCoordinatorLayout, "Fonctionnalité non implémenté:" + addedHost, Toast.LENGTH_SHORT).show();
    }

    public ArrayList<Host>          startSniffSession() throws IOException {
        ArrayList<Host> selectedHost = new ArrayList<>();
        boolean noTargetSelected = true;
        FileOutputStream out = mActivity.openFileOutput("targets", 0);
        for (Host host : mHosts) {
            if (host.selected) {
                selectedHost.add(host);
                noTargetSelected = false;
                String dumpHost = host.ip + ":" + host.mac + "\n";
                out.write(dumpHost.getBytes());
            }
        }
        out.close();
        if (noTargetSelected) {
            Snackbar.make(mActivity.mCoordinatorLayout, "No target selected!", Toast.LENGTH_SHORT).show();
            return null;
        }
        return selectedHost;
    }

    public SearchView.OnQueryTextListener  getOnQueryTextListener() {
        return new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mHostAdapter.filterByString(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        };
    }

    public SearchView.OnCloseListener    getOnCloseListener() {
        return new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mHostAdapter.filterByString("");
                return false;
            }
        };
    }

    public BottomSheetMenuDialog    onSettingsClick(final AppBarLayout mAppbar) {
        return new BottomSheetBuilder(mActivity)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setBackgroundColor(ContextCompat.getColor(mActivity, R.color.material_light_white))
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
                                startActivity(new Intent(mActivity, MenuActivity.class));
                                break;
                        }
                    }
                })
                .expandOnStart(true)
                .createDialog();
    }
}

