package fr.dao.app.View.Activity.Wireshark;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Tcpdump.DashboardSniff;
import fr.dao.app.Core.Tcpdump.Tcpdump;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.Behavior.Fragment.MyFragment;
import fr.dao.app.View.Behavior.WiresharkDispatcher;
import fr.dao.app.View.Widget.Adapter.HostSelectionAdapter;
import fr.dao.app.View.Widget.Adapter.WiresharkAdapter;
import fr.dao.app.View.Widget.Adapter.WireshrakDashboardAdapter;
import fr.dao.app.View.Widget.Dialog.RV_dialog;


public class                    WiresharkLiveFragment extends MyFragment {
    private String              TAG = "WiresharkLiveFragment";
    private CoordinatorLayout   mCoordinatorLayout;
    private Host                mFocusedHost;//TODO need to be init
    private Context             mCtx;
    private WiresharkActivity   mActivity;
    private RecyclerView        mRV_Wireshark;
    private WiresharkAdapter    mAdapterWireshark;
    private WireshrakDashboardAdapter mAdapterDashboardWireshark;
    private List<Host>          mListHostSelected = new ArrayList<>();
    private TextView            mMonitorAgv;//, mMonitorCmd;
    private Tcpdump             mTcpdump;
    private boolean             isDashboardMode = false;
    
    public View                 onCreateView(LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wireshark, container, false);
        mCtx = getActivity();
        initXml(rootView);
        mActivity = (WiresharkActivity) getActivity();
        mTcpdump = Tcpdump.getTcpdump(mActivity, true);
        init();
        return rootView;
    }
    
    private void                initXml(View rootView) {
        mCoordinatorLayout = rootView.findViewById(R.id.Coordonitor);
        mRV_Wireshark = rootView.findViewById(R.id.RV_Wireshark);
        mMonitorAgv =  rootView.findViewById(R.id.Monitor);
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
        //initSpinner();
        //initFilter();
        if (getArguments() != null && getArguments().getInt("position") != -1) {
            Log.d(TAG, "init in mode:[" + getArguments().getInt("position") + "]");
            mListHostSelected.clear();
            mListHostSelected.add(mSingleton.hostList.get(getArguments().getInt("position")));
        }
        mActivity.findViewById(isDashboardMode ? R.id.rootViewForLiveFlux : R.id.rootViewForDashboard).setVisibility(View.GONE);
        initDashboard();
        initRV();
        initTimer();
    }

    private void                initDashboard() {
        mAdapterDashboardWireshark = new WireshrakDashboardAdapter(mActivity);
        mRV_Wireshark.setAdapter(mAdapterWireshark);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        mRV_Wireshark.setLayoutManager(layoutManager);
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
                        mAdapterWireshark.notifyDataSetChanged();
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
        mAdapterWireshark = new WiresharkAdapter(mActivity, mRV_Wireshark);
        mRV_Wireshark.setAdapter(mAdapterWireshark);
        WrapContentLinearLayoutManager layoutManager = new WrapContentLinearLayoutManager(mActivity);
        layoutManager.setAutoMeasureEnabled(false);
        mRV_Wireshark.setItemAnimator(null);
        mRV_Wireshark.setLayoutManager(layoutManager);
    }
    private void                initTimer() {

    }

    public boolean              start() {
        Utils.vibrateDevice(mActivity);
        if (!mTcpdump.isRunning) {
            mAdapterWireshark.clear();
            if (startTcpdump()) {
                mMonitorAgv.setVisibility(View.VISIBLE);
                return true;
            }
        } else {
            mMonitorAgv.setVisibility(View.GONE);
            mTcpdump.onTcpDumpStop();
            mActivity.setToolbarTitle(null, "Sniffing finished");
            mActivity.updateNotifications();
        }
        return false;
    }

    private boolean             startTcpdump() {
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
        Log.d(TAG, "mTcpdump.actualParam::" + mTcpdump);
        WiresharkDispatcher trameDispatcher = new WiresharkDispatcher(mAdapterWireshark, mRV_Wireshark, mActivity);
        String argv = mTcpdump.initCmd(mListHostSelected);
        DashboardSniff dashboardSniff = mTcpdump.start(trameDispatcher);
        mMonitorAgv.setText(argv.replace(mSingleton.PcapPath, ""));
        mActivity.updateNotifications();
        return true;
    }

    private void                onClickChoiceTarget() {
        new RV_dialog(mActivity)
                .setAdapter(new HostSelectionAdapter(mActivity, mSingleton.hostList, mListHostSelected), false)
                .setTitle("Choix des cibles")
                .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
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

    /*private void                initFilter() {
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
                        (mAdapterWireshark.changePermissionFilter(protocol)) ?
                                R.drawable.rounded_corner_on : R.drawable.rounded_corner_off);
                tv.setPadding(pL, pT, pR, pB);
            }
        };
    }


/*    private void                initSpinner() {
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
    }*/

}