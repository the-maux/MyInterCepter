package fr.allycs.app.View.HostDiscovery;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.io.FileOutputStream;
import java.util.ArrayList;

import fr.allycs.app.Controller.AndroidUtils.MyFragment;
import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Database.DBSession;
import fr.allycs.app.Controller.Network.Discovery.NetworkDiscoveryControler;
import fr.allycs.app.Controller.Network.NetUtils;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Unix.Os;
import fr.allycs.app.R;
import fr.allycs.app.View.Scan.NmapActivity;
import fr.allycs.app.View.TargetMenu.TargetMenuActivity;
import fr.allycs.app.View.Widget.Adapter.HostDiscoveryAdapter;
import fr.allycs.app.View.Widget.Adapter.OSFilterAdapter;
import fr.allycs.app.View.Widget.Dialog.RV_dialog;

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
    private ArrayList<Os>           mListOS = new ArrayList<>();
    private NetworkDiscoveryControler mScannerControler;
    private String                  mTitle, mSubtitle;

    public View                     onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_hostdiscovery_scan, container, false);
        initXml(rootView);
        this.mActivity = (HostDiscoveryActivity) getActivity();
        mScannerControler = NetworkDiscoveryControler.getInstance(this);
        if (mSingleton.DebugMode && !mHostLoaded) {
            mActivity.showSnackbar("Debug mode: auto scan started");
            startNetworkScan();
        }//TODO: else si premier scan du SSID alors re scann (ou last scan past from 1 week)
        initSwipeRefresh();
        mActivity.initSettingsButton();
        return rootView;
    }

    public void                     onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mHost_RV.setAdapter(mHostAdapter);
        mHost_RV.setHasFixedSize(true);
        mHost_RV.setLayoutManager(new LinearLayoutManager(mActivity));
        pushToolbar();
    }

    /*public boolean                  start() {
        if (!mHostLoaded && !mScannerControler.inLoading) {
            startNetworkScan();
            return true;
        }
        if (mSingleton.DebugMode) {
            Log.d(TAG, "mHostLoaded:" + mHostLoaded);
            Log.d(TAG, "mScannerControler.inLoading:" + mScannerControler.inLoading);
        }
        return mScannerControler.inLoading;
    }*/

    private void                    initXml(View rootView) {
        mHost_RV = rootView.findViewById(R.id.recycler_view);
        mEmptyList = rootView.findViewById(R.id.emptyList);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
    }

    private void                    initSwipeRefresh() {
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.material_green_200,
                R.color.material_deep_teal_200,
                R.color.material_deep_teal_500);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mActivity.isWaiting()) {
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
        Log.d(TAG, "ADAPTER_RV OK");
    }

    public void                     initSearchView(SearchView mSearchView) {
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
        Log.d(TAG, "startNetworkScan");
        if (!mActivity.isWaiting()) {
            try {
                if (mSingleton.network == null && !NetUtils.initNetworkInfo(mActivity)) {
                    mActivity.showSnackbar("You need to be connected");
                    mEmptyList.setVisibility(View.VISIBLE);
                    return;
                }
                if (mSingleton.network.updateInfo().isConnectedToNetwork()) {
                    initHostsRecyclerView();
                    setTitleToolbar("Scanner", "Discovering network");
                    mScannerControler.run(mHosts);
                    mActivity.progressAnimation();
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

    private ArrayList<Host>         extractAndDumpSelectedHost(ArrayList<Host> hostList) {
        ArrayList<Host> selectedHost = new ArrayList<>();
        try {
            boolean noTargetSelected = true;
            FileOutputStream out = mActivity.openFileOutput("targets", 0);
            for (Host host : hostList) {
                if (host.selected) {
                    selectedHost.add(host);
                    noTargetSelected = false;
                    String dumpHost = host.ip + ":" + host.mac + "\n";
                    out.write(dumpHost.getBytes());
                }
            }
            out.close();
            if (noTargetSelected) {
                mActivity.showSnackbar("No target selected!");
                return null;
            }
            return selectedHost;
        } catch (Exception e) {
            e.getStackTrace();
            return null;
        }
    }

    public void                     launchMenu() {
        Log.d(TAG, "launchMenu()");
        mSingleton.selectedHostsList = extractAndDumpSelectedHost(mHosts);
        if (mSingleton.DebugMode) {
            Log.d(TAG, "mSingleton.selectedHostsList" + mSingleton.selectedHostsList);
            Log.d(TAG, "mSingleton.hostsListSize:" +
                    ((mSingleton.selectedHostsList != null) ? mSingleton.selectedHostsList.size() : "0"));
        }
    }

    public void                     onHostActualized(final ArrayList<Host> hosts) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHosts = hosts;
                mActivity.setProgressState(mActivity.MAXIMUM_PROGRESS);
                mSingleton.selectedHostsList = mHosts;
                mHostAdapter.updateHostList(mSingleton.selectedHostsList);
                mScannerControler.inLoading = false;
                mEmptyList.setVisibility((mHosts == null || mHosts.size() == 0) ?
                        View.VISIBLE : View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                mHostLoaded = true;
                mActivity.actualSession =
                        DBSession.buildSession(mSingleton.network.Ssid,
                                mSingleton.network.gateway,
                                hosts,
                                "Icmp",
                                mHostAdapter.getOsList());
                mSingleton.actualSession = mActivity.actualSession;
            }
        });
    }

    private void                    pushToolbar() {
        mActivity.setToolbarTitle(mTitle, mSubtitle);
        mActivity.initSettingsButton();
    }
    public void                     setTitleToolbar(String title, String subtitle) {
        if (title != null)
            mTitle = title;
        if (subtitle != null)
            mSubtitle = subtitle;
        if (isVisible()) {
            mActivity.setToolbarTitle(title, subtitle);
        }
    }

    public void                     osFilterDialog() {
        final RecyclerView.Adapter adapter = new OSFilterAdapter(mActivity, mHostAdapter.getOsList(), mListOS);
        new RV_dialog(mActivity)
                .setAdapter(adapter, false)
                .setTitle("Choix des cibles")
                .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mListOS.size() > 0) {
                            mActivity.showSnackbar(mHostAdapter.filterByOs(mListOS) + " devices found");
                            mListOS.clear();
                        }
                    }
                })
                .show();
    }

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
                                startActivity(new Intent(mActivity, NmapActivity.class));
                                break;
                        }
                    }
                })
                .expandOnStart(true)
                .createDialog();
    }

}

