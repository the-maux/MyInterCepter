package fr.dao.app.View.HostDiscovery;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetMenuDialog;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;

import java.util.ArrayList;

import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Database.DBNetwork;
import fr.dao.app.Core.Network.Discovery.NetworkDiscoveryControler;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.Model.Target.State;
import fr.dao.app.R;
import fr.dao.app.View.Scan.NmapActivity;
import fr.dao.app.View.ZViewController.Adapter.HostDiscoveryAdapter;
import fr.dao.app.View.ZViewController.Adapter.OSFilterAdapter;
import fr.dao.app.View.ZViewController.Dialog.RV_dialog;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;
import fr.dao.app.View.ZViewController.MSkeleton.RecyclerViewSkeletonScreen;
import fr.dao.app.View.ZViewController.MSkeleton.Skeleton;

public class HostDiscoveryScanFrgmnt extends MyFragment {
    private String TAG = "HostDiscoveryScanFrgmnt";
    private HostDiscoveryActivity mActivity;
    private ArrayList<Host> mHosts = new ArrayList<>();
    private HostDiscoveryAdapter mHostAdapter = null;
    private RecyclerView mHost_RV;
    private TextView mEmptyList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NetworkDiscoveryControler mScannerControler;
    boolean mHostLoaded = false;
    private RecyclerViewSkeletonScreen skeletonScreen;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "onCreateView:mSingleton.hostList " + ((mSingleton.hostList == null) ? "null" : mSingleton.hostList.size()));
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_hostdiscovery_scan, container, false);
        initXml(rootView);
        mActivity = (HostDiscoveryActivity) getActivity();
        init();
        return rootView;
    }

    public void init() {
        mScannerControler = NetworkDiscoveryControler.getInstance(this);
        mActivity.initSettingsButton();
        initHostsRecyclerView();
        initSwipeRefresh();
        if (mSingleton.hostList != null && !mSingleton.hostList.isEmpty()) {
            mHostLoaded = true;
            mActivity.actualNetwork = mSingleton.CurrentNetwork;
            Log.d(TAG, "Host already loaded");
            onHostActualized(mSingleton.hostList);
        } else if (!mHostLoaded) {//To not reload for nothing
            start();
            Log.d(TAG, "Host already NOT loaded SKELETON");
            skeletonScreen = Skeleton.bind(mHost_RV)
                    .adapter(mHostAdapter)
                    .shimmer(true)
                    .angle(5)
                    .frozen(false)
                    .duration(1000)
                    .count(13)
                    .load(R.layout.item_hostdiscovery_skeleton)
                    .show();
        }
    }

    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume::scan discovery host :" + mHostAdapter.getItemCount());
        Log.d(TAG, "onResume::scan discovery hostList :" + mHosts.size());
        if (mHosts.size() == 0) {
            mActivity.setToolbarTitle(mSingleton.NetworkInformation.ssid,
                    "Searching devices");
        } else {
            mActivity.setToolbarTitle(mSingleton.NetworkInformation.ssid,
                    mHosts.size() + " device" + ((mHosts.size() > 1) ? "s" : ""));
        }
        //if (mHosts == null || mHosts.isEmpty())
        if (mHost_RV.getAdapter() == null) {
            mHost_RV.setAdapter(mHostAdapter);
            //mHostAdapter.updateHostList(mHosts);
        }
        mActivity.initToolbarButton();
    }

    private void initXml(View rootView) {
        mHost_RV = rootView.findViewById(R.id.recycler_view);
        mEmptyList = rootView.findViewById(R.id.emptyList);
        mSwipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshLayout);
    }

    private void initSwipeRefresh() {
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.dnsSpoofPrimary,
                R.color.NmapPrimary,
                R.color.webserverSpoofPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            public void onRefresh() {
                if (!mScannerControler.inLoading) {
                    mEmptyList.setVisibility(View.GONE);
                    mActivity.initMonitor();
                    if (start())
                        return;
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void initHostsRecyclerView() {
        if (mHostAdapter == null)
            mHostAdapter = new HostDiscoveryAdapter(getActivity(), mHost_RV, false, mActivity.mFab);
        //mHost_RV.setAdapter(mHostAdapter);
        //mHost_RV.setHasFixedSize(true);new GridLayoutManager(this, 2)
        mHost_RV.setLayoutManager(new GridLayoutManager(mActivity,
                mActivity.getResources().getBoolean(R.bool.is_tab) ? 2 : 1));
        if (mHosts != null && !mHosts.isEmpty()) {
            mHostAdapter.updateHostList(mHosts);
            Log.e(TAG, "TARGET ALLOWED");
        }
        /**
         *
         * .shimmer(true)      // whether show shimmer animation.                      default is true
         .count(10)          // the recycler view item count.                        default is 10
         .color(color)       // the shimmer color.                                   default is #a2878787
         .angle(20)          // the shimmer angle.                                   default is 20;
         .duration(1000)     // the shimmer animation duration.                      default is 1000;
         .frozen(false)      // whether frozen recyclerView during skeleton showing  default is true;
         */
    }

    public void initSearchView(SearchView searchView, final Toolbar toolbar) {
        searchView.setGravity(Gravity.END);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.vibrateDevice(mActivity, 100);
                toolbar.setVisibility(View.GONE);
            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            public boolean onQueryTextSubmit(String query) {
                mHostAdapter.filterByString(query);
                return false;
            }

            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                mHostAdapter.filterByString("");
                toolbar.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    public boolean start() {
        mActivity.setToolbarTitle("Scanner", "Discovering NetworkInformation");
        if (!isWaiting()) {
            if (mScannerControler.run(false)) {
                init_prologueScan();
                mActivity.initMonitor();
                mActivity.onScanStarted();
                //mActivity.progressAnimation();
                Log.d(TAG, "Scanning is started");
                mHostLoaded = false;
                return true;
            } else {
                Log.d(TAG, "Scanning is not launched");
                mEmptyList.setVisibility(View.VISIBLE);
                mEmptyList.setText("No connection detected");
            }
        } else {
            Log.d(TAG, "Scanning, already loading => Interupt the double start");
            mActivity.showSnackbar("Patientez, loading en cours");
        }
        return false;
    }

    private void init_prologueScan() {
        /**
         * TODO:
         * + Get Last list of host from this SSID where Gateway.mac.equals(mSingleton.NetworkInformation.gateway.mac)
         *      -> If not same Mac Alert we détected a roaming. Have to restart the process
         * - IF ITS THE FIST SCAN print the list of host in degraded mode
         * - IF You already have a lit:
         *      - If HOST IS KNEW -> Host.state = ONLINE
         *      - If Host Is NEW -> Host.state = ONLINE and Host.degraded =>  True
         */
    }

    public ArrayList<Host> getTargetSelectedFromHostList() {
        ArrayList<Host> selectedHost = new ArrayList<>();
        for (Host host : mHosts) {
            if (host.selected)
                selectedHost.add(host);
        }
        if (selectedHost.isEmpty()) {
            mActivity.showSnackbar("No target selected!");
            return null;
        }
        return selectedHost;
    }

    public void updateStateOfHostAfterIcmp(Network actualNetwork) {
        mSingleton.CurrentNetwork = actualNetwork;
        mActivity.actualNetwork = actualNetwork;
        mHosts = actualNetwork.listDevices();
        mHostAdapter.updateHostList(actualNetwork.listDevices());
    }

    public void onHostActualized(final ArrayList<Host> hosts) {
        mActivity.runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "onHostActualized: " + ((hosts == null) ? "null" : hosts.size()));
                int online = 0;
                for (Host host : hosts) {
                    if (host.state == State.ONLINE)
                        online++;
                }
                if (skeletonScreen != null)
                    skeletonScreen.hide();
                mHosts = hosts;
                mHostLoaded = true;
                mSingleton.hostList = mHosts;
                mActivity.setToolbarTitle(mSingleton.NetworkInformation.ssid,
                        "(" + online + "/" + hosts.size() + ") device" + ((hosts.size() > 1) ? "s" : ""));
                mHostAdapter.updateHostList(mHosts);
                mEmptyList.setVisibility((mHosts == null || mHosts.size() == 0) ? View.VISIBLE : View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                mActivity.onScanOver();
                DBNetwork.updateHostOfSessions(mSingleton.CurrentNetwork, hosts, mHostAdapter.getOsList());
            }
        });
    }

    public void osFilterDialog() {
        if (!mScannerControler.inLoading) {
            final ArrayList<Integer> osList = mHostAdapter.getOsList();
            final OSFilterAdapter adapter = new OSFilterAdapter(mActivity, osList);
            new RV_dialog(mActivity)
                    .setAdapter(adapter, false)
                    .setTitle("Choix des cibles")
                    .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (osList.size() > 0) {
                                Utils.vibrateDevice(mActivity, 100);
                                mActivity.showSnackbar(mHostAdapter.filterByOs(adapter.getSelected()) + " devices found");
                            }
                        }
                    })
                    .show();
        } else {
            mActivity.showSnackbar("You can't filter while scanning");
        }
    }

    public BottomSheetMenuDialog onSettingsClick(final AppBarLayout mAppbar, Activity activity) {
        return new BottomSheetBuilder(activity)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setBackgroundColor(ContextCompat.getColor(activity, R.color.material_light_white))
                .setAppBarLayout(mAppbar)
                .addTitleItem("Settings")
                .addItem(0, "Select all", R.mipmap.ic_select_all)
                .addItem(1, "Mode offline", R.mipmap.ic_leave)
                .addItem(2, "Settings", R.mipmap.ic_leave)
                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(MenuItem menuItem) {
                        Log.d(TAG, "STRING:" + menuItem.getTitle().toString());
                        switch (menuItem.getTitle().toString()) {
                            case "Select all":
                                mHostAdapter.selectAll();
                                break;
                            case "Mode offline":
                                startActivity(new Intent(mActivity, NmapActivity.class));
                                break;
                            case "Settings":
                                mActivity.initFragmentSettings();
                                break;
                        }
                    }
                })
                .expandOnStart(true)
                .createDialog();
    }

    public boolean isWaiting() {
        return mScannerControler.inLoading;
    }

}

