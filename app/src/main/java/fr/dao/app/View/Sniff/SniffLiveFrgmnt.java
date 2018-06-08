package fr.dao.app.View.Sniff;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Tcpdump.DashboardSniff;
import fr.dao.app.Core.Tcpdump.Tcpdump;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Adapter.HostSelectionAdapter;
import fr.dao.app.View.ZViewController.Adapter.SniffDashboardAdapter;
import fr.dao.app.View.ZViewController.Adapter.SniffPacketsAdapter;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Dialog.RV_dialog;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

/**
 * TODO: tu dois trouver un moye, de flush l'ensemble des trames du dispatcher dans le RV live
 */
public class                    SniffLiveFrgmnt extends MyFragment {
    private String              TAG = "SniffLiveFrgmnt";
    private CoordinatorLayout   mCoordinatorLayout;
    private ConstraintLayout    rootViewForDashboard;
    private RelativeLayout      rootViewForLiveFlux;
    private SniffDispatcher     mTrameDispatcher;
    private SniffActivity       mActivity;
    private RecyclerView        mRV_Wireshark, dashboard_RV;
    private SniffPacketsAdapter mAdapterDetailWireshark;
    private SniffDashboardAdapter mAdapterDashboardWireshark;
    private List<Host>          mListHostSelected = new ArrayList<>();
    private TextView            mMonitorAgv;
    private Tcpdump             mTcpdump;
    private boolean             isDashboardMode = true, wasLaunched = false;
    private CircleImageView     statusIconSniffing;
    private ImageView           headerWifi;
    private TextView            title_sniffer, subtitle_sniffer, bottom_title_sniffer, bottom_subtitle_sniffer;

    public View                 onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wireshark, container, false);
        mActivity = (SniffActivity) getActivity();
        initXml(rootView);
        mTcpdump = Tcpdump.getTcpdump(mActivity, true);
        init();
        return rootView;
    }
    
    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        mRV_Wireshark = rootView.findViewById(R.id.RV_Wireshark);
        mMonitorAgv =  rootView.findViewById(R.id.Monitor);
        rootViewForLiveFlux = rootView.findViewById(R.id.rootViewForLiveFlux);
        rootViewForDashboard = rootView.findViewById(R.id.rootViewForDashboard);
        statusIconSniffing = rootView.findViewById(R.id.statusIconSniffing);
        title_sniffer = rootView.findViewById(R.id.title_sniffer);
        subtitle_sniffer = rootView.findViewById(R.id.subtitle_sniffer);
        bottom_title_sniffer = rootView.findViewById(R.id.bottom_subtitle_sniffer);
        bottom_subtitle_sniffer = rootView.findViewById(R.id.bottom_title_sniffer);
        headerWifi = rootView.findViewById(R.id.headerWifi);
        dashboard_RV = rootView.findViewById(R.id.dashboard_RV);
  //      mMonitorCmd =  rootView.findViewById(R.id.cmd);
//        mSpiner =  rootView.findViewById(R.id.spinnerTypeScan);
//        Autoscroll =  rootView.findViewById(R.id.Autoscroll);
//        tcp_cb =  rootView.findViewById(R.id.tcp_cb);
//        dns_cb =  rootView.findViewById(R.id.dns_cb);
//        arp_cb =  rootView.findViewById(R.id.arp_cb);
//        https_cb =  rootView.findViewById(R.id.https_cb);
//        http_cb =  rootView.findViewById(R.id.http_cb);
//        udp_cb =  rootView.findViewById(R.id.udp_cb);
//        ip_cb =  rootView.findViewById(R.id.ip_cb);
    }

    public void                 init() {
        if (getArguments() != null && getArguments().getInt("position") != -1) {
            Log.d(TAG, "init in mode:[" + getArguments().getInt("position") + "]");
            mListHostSelected.clear();
            mListHostSelected.add(mSingleton.hostList.get(getArguments().getInt("position")));
        }
        rootViewForDashboard.setVisibility((isDashboardMode) ? View.VISIBLE : View.GONE);
        rootViewForLiveFlux.setVisibility((isDashboardMode) ? View.GONE : View.VISIBLE);
        initRV();
        initDashboard();
        if (Tcpdump.isRunning()) {
            mTcpdump.flushToAdapter();
        }
    }

    private void                initDashboard() {
        int res = (Tcpdump.isRunning()) ? R.color.online_color : R.color.offline_color;
        statusIconSniffing.setImageResource(res);
        if (mAdapterDashboardWireshark == null) {
            putTitle(title_sniffer);
            subtitle_sniffer.setText("No packets recorded");
            bottom_title_sniffer.setText("Not started");
            bottom_subtitle_sniffer.setText("");
            MyGlideLoader.loadDrawableInImageView(mActivity, R.drawable.wireshark, headerWifi, false);
            mAdapterDashboardWireshark = new SniffDashboardAdapter(mActivity, subtitle_sniffer,
                    bottom_title_sniffer, bottom_subtitle_sniffer, statusIconSniffing);
            dashboard_RV.setAdapter(mAdapterDashboardWireshark);
            LinearLayoutManager layoutManager = new GridLayoutManager(mActivity, 3);
            dashboard_RV.setLayoutManager(layoutManager);
        }
    }

    private void                putTitle(TextView title_sniffer) {
        title_sniffer.setText(mSingleton.NetworkInformation.ssid);
        if (mListHostSelected.size() == 0) {
            if (mSingleton.hostList.isEmpty())
                title_sniffer.setText("No target selected");
            else if (mSingleton.hostList.size() == 1)
                title_sniffer.setText(mSingleton.hostList.get(0).getName());
            else
                title_sniffer.setText(mSingleton.hostList.size() + " targets");
        } else if (mListHostSelected.size() == 1)
            title_sniffer.setText(mListHostSelected.get(0).getName());
        else
            title_sniffer.setText(mListHostSelected.size() + " targets");

    }

    public boolean              onSwitchView() {
        isDashboardMode = !isDashboardMode;
        init();
        return isDashboardMode;
    }

    public void                 switchOutputType(boolean isDashboard) {
        mTcpdump.switchOutputType(isDashboard);
    }

    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context) {
            super(context);
        }

        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                mRV_Wireshark.post(new Runnable() {
                    @Override
                    public void run() {
                        mAdapterDetailWireshark.notifyDataSetChanged();
                    }
                });
                Log.d("ERROR", e.getMessage());
                Log.e("ERROR", "O.M.G::IndexOutOfBoundsException in RecyclerView happens");
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                mActivity.onBackPressed();
            }
        }
    }
    private void                initRV() {
        if (mAdapterDetailWireshark == null) {
            mAdapterDetailWireshark = new SniffPacketsAdapter(mActivity, mRV_Wireshark);
            mRV_Wireshark.setAdapter(mAdapterDetailWireshark);
            WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(mActivity);
            layoutManager.setAutoMeasureEnabled(false);
            mRV_Wireshark.setItemAnimator(null);
            mRV_Wireshark.setLayoutManager(layoutManager);
        }
    }
    
    public boolean              start() {
        Utils.vibrateDevice(mActivity);
        if (!Tcpdump.isRunning()) {
            if (startTcpdump()) {
                if (isDashboardMode) {
                    int color = R.color.filtered_color;
                    statusIconSniffing.setImageResource(color);
                }
                mMonitorAgv.setVisibility(View.VISIBLE);
                wasLaunched = true;
                return true;
            }
        } else {
            mMonitorAgv.setVisibility(View.GONE);
            mTcpdump.stop();
            mActivity.setToolbarTitle(null, "Sniffing finished");
            mActivity.updateNotifications();
        }
        return false;
    }

    private boolean             startTcpdump() {
        if (mSingleton.hostList == null || mSingleton.hostList.isEmpty()) {
            mActivity.showSnackbar("No target available");
            return false;
        }
        if (mListHostSelected.isEmpty()) {
            if (mSingleton.hostList.size() == 1) {//Automatic selection when 1 target only
                mListHostSelected.clear();
                mListHostSelected.add(mSingleton.hostList.get(0));
                mActivity.setToolbarTitle(null,"Listenning " + mSingleton.hostList.get(0).ip);
            } else {
                mActivity.showSnackbar("Selectionner une target", -1);
                onClickChoiceTarget();
                return false;
            }
        }
        if (mTrameDispatcher == null) {
            mTrameDispatcher = new SniffDispatcher(mAdapterDetailWireshark, isDashboardMode, mRV_Wireshark, mActivity);
        } else if (wasLaunched && !Tcpdump.isRunning()) {/* Clear shit its a restart*/
            mAdapterDetailWireshark.reset();
            mAdapterDashboardWireshark.reset();
            mTrameDispatcher.reset();
        }
        String argv = mTcpdump.initCmd(mListHostSelected);
        DashboardSniff dashboardSniff = mTcpdump.start(mTrameDispatcher);
        mAdapterDashboardWireshark.setDashboard(dashboardSniff);
        dashboardSniff.setAdapter(mAdapterDashboardWireshark);
        bottom_subtitle_sniffer.setText(mListHostSelected.size() + " clients");
        mMonitorAgv.setText(argv.replace(mSingleton.Settings.PcapPath, ""));
        mActivity.updateNotifications();
        return true;
    }

    private void                onClickChoiceTarget() {
        new RV_dialog(mActivity)
                .setAdapter(new HostSelectionAdapter(mActivity, mSingleton.hostList, mListHostSelected), false)
                .setTitle("Choix des cibles")
                .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Utils.vibrateDevice(mActivity, 100);
                        if (mListHostSelected.isEmpty())
                            mActivity.showSnackbar("No target selected", -1);
                        else {
                            mActivity.setToolbarTitle(null, mListHostSelected.size() + " target" +
                                    ((mListHostSelected.size() <= 1) ? "" : "s") + " selected");
                            start();
                        }
                    }
                })
                .show();
        mListHostSelected.clear();
    }


    /**
     *
     * private void                initFilter() {
     tcp_cb.setOnClickListener(onChangePermissionFilter(Protocol.TCP, tcp_cb));
     dns_cb.setOnClickListener(onChangePermissionFilter(Protocol.DNS, dns_cb));
     http_cb.setOnClickListener(onChangePermissionFilter(Protocol.HTTP, http_cb));
     https_cb.setOnClickListener(onChangePermissionFilter(Protocol.HTTPS, https_cb));
     udp_cb.setOnClickListener(onChangePermissionFilter(Protocol.UDP, udp_cb));
     arp_cb.setOnClickListener(onChangePermissionFilter(Protocol.ARP, arp_cb));
     ip_cb.setOnClickListener(onChangePermissionFilter(Protocol.IP, ip_cb));
     }
     private View.OnClickListener onChangePermissionFilter(final Protocol protocol, final TextView tv) {
     return new View.OnClickListener() {
    @Override
    public void onClick(View view) {
    int pL = tv.getPaddingLeft();
    int pT = tv.getPaddingTop();
    int pR = tv.getPaddingRight();
    int pB = tv.getPaddingBottom();
    tv.setBackgroundResource(
    (mAdapterDetailWireshark.changePermissionFilter(protocol)) ?
    R.drawable.rounded_corner_on : R.drawable.rounded_corner_off);
    tv.setPadding(pL, pT, pR, pB);
    }
    };
     }
     private void                initSpinner() {
     final Map<String, String> mParams = new HashMap<>();
     Iterator it = mTcpdump.getCmdsWithArgsInMap().entrySet().iterator();
     ArrayList<String> cmds = new ArrayList<>();
     while (it.hasNext()) {
     Map.Entry pair = (Map.Entry)it.next();
     mParams.put((String)pair.getKey(), (String)pair.getValue());
     Log.d(TAG, "initspinner::add to cmds:" + pair.getKey());
     cmds.add((String)pair.getKey());
     it.remove(); // avoids a ConcurrentModificationException
     }
     if (!cmds.isEmpty()) {
     mSpiner.setItems(cmds);
     mSpiner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
    @Override
    public void onItemSelected(MaterialSpinner view, int position, long id, String typeScan) {
    mMonitorCmd.setText(mParams.get(typeScan));
    mTypeScan = typeScan;
    mMonitorAgv.setText("./tcpdump " + mParams.get(typeScan).replace("  ", " "));
    }
    });
     mMonitorCmd.setText(mParams.get(cmds.get(0)));
     }
     Log.d(TAG, "initSpinner:: monitor::" + mMonitorCmd.getText().toString());
     }
     *
     */


}