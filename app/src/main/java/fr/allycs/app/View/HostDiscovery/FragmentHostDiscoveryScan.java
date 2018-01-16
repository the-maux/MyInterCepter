package fr.allycs.app.View.HostDiscovery;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
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

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import fr.allycs.app.Controller.Core.Conf.Singleton;
import fr.allycs.app.Controller.Core.Database.DBSession;
import fr.allycs.app.Controller.Misc.MyFragment;
import fr.allycs.app.Controller.Network.Discovery.NetworkDiscoveryControler;
import fr.allycs.app.Controller.Network.NetUtils;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;
import fr.allycs.app.View.Adapter.HostDiscoveryAdapter;
import fr.allycs.app.View.Adapter.OSAdapter;
import fr.allycs.app.View.Dialog.AddDnsDialog;
import fr.allycs.app.View.Dialog.RV_dialog;
import fr.allycs.app.View.MenuActivity;

public class                        FragmentHostDiscoveryScan extends MyFragment {
    private String                  TAG = "FragmentHostDiscoveryScan";
    private HostDiscoveryActivity   mActivity;
    private Singleton               mSingleton = Singleton.getInstance();
    private ArrayList<Host>         mHosts = new ArrayList<>();
    private boolean                 mHostLoaded = false;
    private HostDiscoveryAdapter    mHostAdapter;
    private RecyclerView            mHost_RV;
    private TextView                mEmptyList;
    private SwipeRefreshLayout      mSwipeRefreshLayout;
    private ArrayList<String>       mListOS = new ArrayList<>();
    private NetworkDiscoveryControler mScannerControler;

    @Override
    public View                     onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_hostdiscovery_scan, container, false);
        initXml(rootView);
        this.mActivity = (HostDiscoveryActivity) getActivity();
        mScannerControler = new NetworkDiscoveryControler(this);
        if (mSingleton.DebugMode && !mHostLoaded) {
            mActivity.showSnackbar("debug enabled, starting Scan automaticaly");
            startNetworkScan();
        }
        initSwipeRefresh();
        mActivity.initToolbarButton();
        return rootView;
    }

    public boolean                  start() {
        if (!mHostLoaded) {
            startNetworkScan();
            return true;
        }
        return false;
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
        mHostAdapter = new HostDiscoveryAdapter(getActivity(), mHost_RV, false);
        mHost_RV.setAdapter(mHostAdapter);
        mHost_RV.setHasFixedSize(true);
        mHost_RV.setLayoutManager(new LinearLayoutManager(mActivity));
    }
    @Override
    public BottomSheetMenuDialog    onSettingsClick(final AppBarLayout mAppbar, Activity activity) {
        return new BottomSheetBuilder(activity)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setBackgroundColor(ContextCompat.getColor(activity, R.color.material_light_white))
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
    @Override public void           initSearchView(SearchView mSearchView) {
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

    public void                     startNetworkScan() {
        mActivity.setProgressState(-1);
        if (!mScannerControler.inLoading) {
            try {
                if (mSingleton.network == null && !NetUtils.initNetworkInfo(mActivity)) {
                    mActivity.showSnackbar("You need to be connected");
                    mEmptyList.setVisibility(View.VISIBLE);
                    return;
                }
                if (mSingleton.network.updateInfo().isConnectedToNetwork()) {
                    mScannerControler.inLoading = true;
                    if (mActivity.typeScan != NetworkDiscoveryControler.typeScan.Services)
                        initHostsRecyclerView();
                    mActivity.progressAnimation();
                    mActivity.setToolbarTitle(null, "Scanning network");
                    mScannerControler.run(mActivity.typeScan, mHosts);
                    mActivity.setProgressState(1000);
                } else {
                    mActivity.showSnackbar("You need to be connected");
                    mEmptyList.setVisibility(View.VISIBLE);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            mActivity.showSnackbar("Patientez, loading en cours");
        }
    }

    public void                     onHostActualized(final ArrayList<Host> hosts) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHosts = hosts;
                mActivity.setProgressState(mActivity.MAXIMUM_PROGRESS);
                mHostAdapter.updateHostList(mHosts);
                mScannerControler.inLoading = false;
                mEmptyList.setVisibility((mHosts == null || mHosts.size() == 0) ? View.VISIBLE : View.GONE);
                final ArrayList<String> listOs = mHostAdapter.getOsList();
                Log.d(TAG, "scan Over with " + mHosts.size() + " possible target");
                final String SSID = ((WifiManager)mActivity.getApplicationContext().getSystemService(Context.WIFI_SERVICE))
                        .getConnectionInfo().getSSID().replace("\"", "");
                mActivity.setToolbarTitle(SSID, mHosts.size() + " device" + ((mHosts.size() > 1) ? "s": ""));
                mActivity.mActualSession = DBSession.buildSession(
                        SSID, mSingleton.network.gateway, hosts, "Icmp", mHostAdapter.getOsList());
                mSwipeRefreshLayout.setRefreshing(false);
                mHostLoaded = true;
                mSingleton.hostsList = mHosts;
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

    public void                     onCheckAddedHost(String addedHost) {
        mActivity.showSnackbar("Fonctionnalité non implémenté:"+ addedHost);
    }

    public void                     onAddButtonClick(View mAddHostBtn) {
        mAddHostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mActivity.typeScan) {
                    case Arp:
                        final AddDnsDialog dialog = new AddDnsDialog(mActivity)
                                .setTitle("Add target");
                        dialog.onPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int which) {
                                onCheckAddedHost(dialog.getHost());
                            }
                        }).show();
                        break;
                    default:
                        mActivity.showSnackbar("Not implemented");
                        break;
                }
            }
        });
    }

    @Override public void           stop() {

    }

}

