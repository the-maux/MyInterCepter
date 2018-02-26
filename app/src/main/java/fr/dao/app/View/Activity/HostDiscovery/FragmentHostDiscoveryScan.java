package fr.dao.app.View.Activity.HostDiscovery;

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

import fr.dao.app.Core.Database.DBNetwork;
import fr.dao.app.Core.Network.Discovery.NetworkDiscoveryControler;
import fr.dao.app.Core.Network.NetDiscovering;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.Network;
import fr.dao.app.Model.Unix.Os;
import fr.dao.app.R;
import fr.dao.app.View.Activity.Scan.NmapActivity;
import fr.dao.app.View.Behavior.Fragment.MyFragment;
import fr.dao.app.View.Widget.Adapter.HostDiscoveryAdapter;
import fr.dao.app.View.Widget.Adapter.OSFilterAdapter;
import fr.dao.app.View.Widget.Dialog.RV_dialog;

public class                        FragmentHostDiscoveryScan extends MyFragment {
    private String                  TAG = "FragmentHostDiscoveryScan";
    private HostDiscoveryActivity   mActivity;
    private ArrayList<Host>         mHosts = new ArrayList<>();
    private HostDiscoveryAdapter    mHostAdapter;
    private RecyclerView            mHost_RV;
    private TextView                mEmptyList;
    private SwipeRefreshLayout      mSwipeRefreshLayout;
    private NetworkDiscoveryControler mScannerControler;
    boolean                         mHostLoaded = false;

    public View                     onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView:mSingleton.hostList " + ((mSingleton.hostList == null) ? "null" : mSingleton.hostList.size()));
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_hostdiscovery_scan, container, false);
        initXml(rootView);
        mActivity = (HostDiscoveryActivity) getActivity();
        init();
        return rootView;
    }

    public void                     init() {
        mScannerControler = NetworkDiscoveryControler.getInstance(this);
        initSwipeRefresh();
        mActivity.initSettingsButton();
        initHostsRecyclerView();
        if (!mHostLoaded) {
            start();
        }
    }

    public void                     onResume() {
        super.onResume();
        Log.d(TAG, "onResume::scan discovery host :" + mHostAdapter.getItemCount());
        Log.d(TAG, "onResume::scan discovery hostList :" + mHosts.size());
        if (mHosts.size() == 0) {
            mActivity.setToolbarTitle(mSingleton.network.Ssid,
                    "Searching devices");
        } else {
            mActivity.setToolbarTitle(mSingleton.network.Ssid,
                    mHosts.size() + " device" + ((mHosts.size() > 1) ? "s" : ""));
        }
        mHostAdapter.updateHostList(mHosts);
        mHost_RV.setAdapter(mHostAdapter);
        mActivity.initToolbarButton();
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
            public void onRefresh() {
                if (!mScannerControler.inLoading) {
                    mHosts.clear();
                    mEmptyList.setVisibility(View.GONE);
                    if (mHostAdapter != null)
                        mHostAdapter.notifyDataSetChanged();
                    mActivity.initMonitor();
                    if (start())
                        return;
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    private void                    initHostsRecyclerView() {
        mHostAdapter = new HostDiscoveryAdapter(getActivity(), mHost_RV, false);
        mHost_RV.setAdapter(mHostAdapter);
        mHost_RV.setHasFixedSize(true);
        mHost_RV.setLayoutManager(new LinearLayoutManager(mActivity));
    }

    public void                     initSearchView(SearchView mSearchView) {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                mHostAdapter.filterByString(query);
                return false;
            }

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
        mActivity.setToolbarTitle("Scanner", "Discovering Network");
        if (!isWaiting()) {
            if ((NetDiscovering.initNetworkInfo(mActivity)) &&
                    mSingleton.network.updateInfo().isConnectedToNetwork()) {

                //TODO: if its a refreshing don't unload list of host
                init_prologueScan();
                mActivity.initMonitor();
                mActivity.initTimer();
                mActivity.progressAnimation();
                Log.d(TAG, "start -> true");
                return mScannerControler.run(mHosts);
            } else {
                Log.d(TAG, "start -> false no connection");
                mActivity.showSnackbar("You need to be connected");
                mEmptyList.setVisibility(View.VISIBLE);
                mEmptyList.setText("No connection detected");
            }
        } else {
            Log.d(TAG, "start -> false, already loading");
            mActivity.showSnackbar("Patientez, loading en cours");
        }
        return false;
    }

    private void                    init_prologueScan() {
        /**
         * TODO:
         * + Get Last list of host from this SSID where Gateway.mac.equals(mSingleton.Network.gateway.mac)
         *      -> If not same Mac Alert we détected a roaming. Have to restart the process
         * - IF ITS THE FIST SCAN print the list of host in degraded mode
         * - IF You already have a lit:
         *      - If HOST IS KNEW -> Host.state = ONLINE
         *      - If Host Is NEW -> Host.state = ONLINE and Host.degraded =>  True
         */
    }

    public ArrayList<Host>          getTargetSelectedFromHostList() {
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

    public Network                  updateStateOfHostAfterIcmp(ArrayList<String> ipReachables) {
        Network actualNetwork = DBNetwork.getAPFromSSID(mSingleton.network.Ssid);
        int rax = 0;
        for (String ipReachable : ipReachables) {
            rax = rax + 1;
            String ip = ipReachable.split(":")[0];
            String mac = ipReachable.replace(ipReachable.split(":")[0]+":", "").toUpperCase();
            boolean isHostInList = false;
            for (Host host : actualNetwork.listDevices()) {
                if (host.mac.contains(mac)) {
                    host.state = Host.State.FILTERED;
                    isHostInList = true;
                    Log.d(TAG, "updateStateOfHostAfterIcmp::" + host.ip + ":" + host.mac + " => ONLINE");
                    break;
                }
            }
            if (!isHostInList) {
                Host host = new Host();
                host.ip = ip;
                host.mac = mac;
                host.state = Host.State.FILTERED;
                /*host.Network().add(actualNetwork);*/
                Log.d(TAG, "updateStateOfHostAfterIcmp::"+ host.ip + ":" + host.mac +" => is new");
                host.save();
                actualNetwork.listDevices().add(host);
            }
        }
        Log.d(TAG, "(" + (actualNetwork.listDevices().size() - rax) + " offline/ " + actualNetwork.listDevices().size() + "inCache) ");
        mSingleton.actualNetwork = actualNetwork;
        mActivity.actualNetwork = actualNetwork;
        mHostAdapter.updateHostList(actualNetwork.listDevices());
        return actualNetwork;
    }

    public void                     onHostActualized(final ArrayList<Host> hosts) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mHosts = hosts;
                mHostLoaded = true;
                mScannerControler.inLoading = false;
                mActivity.setProgressState(mActivity.MAXIMUM_PROGRESS*2);
                mSingleton.hostList = mHosts;
                mActivity.setToolbarTitle(mSingleton.network.Ssid,
                        hosts.size() + " device" + ((hosts.size() > 1) ? "s" : ""));
                Log.d(TAG, "onHostActualized: " + ((mSingleton.hostList == null) ? "null" : mSingleton.hostList.size()));
                mHostAdapter.updateHostList(mHosts);
                mEmptyList.setVisibility((mHosts == null || mHosts.size() == 0) ? View.VISIBLE : View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                mActivity.onScanOver();
                DBNetwork.updateHostOfSessions(mActivity.actualNetwork, hosts, mHostAdapter.getOsList());
            }
        });
    }

    public void                     osFilterDialog() {
        if (!mScannerControler.inLoading) {
            final ArrayList<Os> osList = mHostAdapter.getOsList();
            final RecyclerView.Adapter adapter = new OSFilterAdapter(mActivity, osList);
            new RV_dialog(mActivity)
                    .setAdapter(adapter, false)
                    .setTitle("Choix des cibles")
                    .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (osList.size() > 0) {
                                mActivity.showSnackbar(mHostAdapter.filterByOs(osList) + " devices found");
                            }
                        }
                        })
                    .show();
        } else {
            mActivity.showSnackbar("You can't filter while scanning");
        }
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

    public boolean                  isWaiting() {
        return mScannerControler.inLoading;
    }

}
