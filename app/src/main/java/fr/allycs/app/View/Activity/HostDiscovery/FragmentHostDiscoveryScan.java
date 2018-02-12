package fr.allycs.app.View.Activity.HostDiscovery;

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

import java.util.ArrayList;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Core.Database.DBSession;
import fr.allycs.app.Core.Network.Discovery.NetworkDiscoveryControler;
import fr.allycs.app.Core.Network.NetUtils;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.Model.Unix.Os;
import fr.allycs.app.R;
import fr.allycs.app.View.Activity.Scan.NmapActivity;
import fr.allycs.app.View.Behavior.Fragment.MyFragment;
import fr.allycs.app.View.Widget.Adapter.HostDiscoveryAdapter;
import fr.allycs.app.View.Widget.Adapter.OSFilterAdapter;
import fr.allycs.app.View.Widget.Dialog.RV_dialog;

public class                        FragmentHostDiscoveryScan extends MyFragment {
    private String                  TAG = "FragmentHostDiscoveryScan";
    private HostDiscoveryActivity   mActivity;
    private Singleton               mSingleton = Singleton.getInstance();
    private ArrayList<Host>         mHosts = new ArrayList<>();
    private HostDiscoveryAdapter    mHostAdapter;
    private RecyclerView            mHost_RV;
    private TextView                mEmptyList;
    private SwipeRefreshLayout      mSwipeRefreshLayout;
    private ArrayList<Os>           mListOS = new ArrayList<>();
    private NetworkDiscoveryControler mScannerControler;
    private String                  mTitle, mSubtitle;
    boolean                         mHostLoaded = false;

    public View                     onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_hostdiscovery_scan, container, false);
        initXml(rootView);
        this.mActivity = (HostDiscoveryActivity) getActivity();
        mScannerControler = NetworkDiscoveryControler.getInstance(this);
//TODO: else si premier scan du SSID alors re scann (ou last scan past from 1 week)
        initSwipeRefresh();
        mActivity.initSettingsButton();
        if (mSingleton.DebugMode && !mHostLoaded) {
            mActivity.showSnackbar("Debug mode: auto scan started");
            startNetworkScan();
        }
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

    private void                    initXml(View rootView) {
        mHost_RV = rootView.findViewById(R.id.recycler_view);
        mEmptyList = rootView.findViewById(R.id.emptyList);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
    }

    private void                    initSwipeRefresh() {
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.dnsSpoofPrimary,
                R.color.NmapPrimary,
                R.color.webserverSpoofPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mActivity.isWaiting()) {
                    mHosts.clear();
                    mEmptyList.setVisibility(View.GONE);
                    if (mHostAdapter != null)
                        mHostAdapter.notifyDataSetChanged();
                    mActivity.initMonitor();
                    if (startNetworkScan())
                        return;
                }
                mActivity.showSnackbar("Scanning already started");
                mSwipeRefreshLayout.setRefreshing(false);
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

    public boolean                  start() {
        return startNetworkScan();
    }

    public boolean                  startNetworkScan() {
        Log.d(TAG, "startNetworkScan");
        if (!mActivity.isWaiting()) {
                if ((mSingleton.network != null && NetUtils.initNetworkInfo(mActivity)) &&
                        mSingleton.network.updateInfo().isConnectedToNetwork()) {
                    mActivity.initTimer();
                    initHostsRecyclerView();
                    setTitleToolbar("Scanner", "Discovering network");
                    mActivity.progressAnimation();
                    return mScannerControler.run(mHosts);
                } else {
                    mActivity.showSnackbar("You need to be connected");
                    mEmptyList.setVisibility(View.VISIBLE);
                    mEmptyList.setText("No connection detected");
                }
        } else {
            mActivity.showSnackbar("Patientez, loading en cours");
        }
        return false;
    }

    public ArrayList<Host>         getTargetFromHostList() {
        ArrayList<Host> selectedHost = new ArrayList<>();
        try {
            boolean noTargetSelected = true;
            for (Host host : mHosts) {
                if (host.selected) {
                    selectedHost.add(host);
                    noTargetSelected = false;
                }
            }
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

    public void                     onHostActualized(final ArrayList<Host> hosts) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHosts = hosts;
                mHostLoaded = true;
                mScannerControler.inLoading = false;
                mActivity.setProgressState(mActivity.MAXIMUM_PROGRESS*2);
                mSingleton.selectedHostsList = mHosts;
                mHostAdapter.updateHostList(mSingleton.selectedHostsList);
                mEmptyList.setVisibility((mHosts == null || mHosts.size() == 0) ?
                        View.VISIBLE : View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                mActivity.stopTimer();
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
        mActivity.initToolbarButton();
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
                .addItem(2, "Settings", R.mipmap.ic_leave)
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
                            case "Settings":
                                mActivity.initFragmentSettings();
                        }
                    }
                })
                .expandOnStart(true)
                .createDialog();
    }

}

