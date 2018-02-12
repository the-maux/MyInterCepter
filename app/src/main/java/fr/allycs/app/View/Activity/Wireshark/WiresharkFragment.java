package fr.allycs.app.View.Activity.Wireshark;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Core.Configuration.Utils;
import fr.allycs.app.Core.Tcpdump.Tcpdump;
import fr.allycs.app.Model.Net.Protocol;
import fr.allycs.app.Model.Net.Trame;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;
import fr.allycs.app.View.Behavior.Fragment.MyFragment;
import fr.allycs.app.View.Behavior.WiresharkDispatcher;
import fr.allycs.app.View.Widget.Adapter.HostSelectionAdapter;
import fr.allycs.app.View.Widget.Adapter.WiresharkAdapter;
import fr.allycs.app.View.Widget.Dialog.RV_dialog;


public class                    WiresharkFragment extends MyFragment {
    private String              TAG = "WiresharkFragment";
    private CoordinatorLayout   mCoordinatorLayout;
    private Singleton           mSingleton = Singleton.getInstance();
    private Host                mFocusedHost;//TODO need to be init
    private Context             mCtx;
    private WiresharkActivity   mActivity;
    private MaterialSpinner     mSpiner;
    private RecyclerView        mRV_Wireshark;
    private WiresharkAdapter    mAdapterWireshark;
    private List<Host>          mListHostSelected = new ArrayList<>();
    private TextView            mMonitorAgv, mMonitorCmd;
    private Tcpdump             mTcpdump;
    private String              mTypeScan = "No Filter";
    private CheckBox            Autoscroll;
    private TextView            tcp_cb, dns_cb, arp_cb, https_cb, http_cb, udp_cb, ip_cb;

    
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
        mSpiner =  rootView.findViewById(R.id.spinnerTypeScan);
        mRV_Wireshark = rootView.findViewById(R.id.Output);
        Autoscroll =  rootView.findViewById(R.id.Autoscroll);
        tcp_cb =  rootView.findViewById(R.id.tcp_cb);
        dns_cb =  rootView.findViewById(R.id.dns_cb);
        arp_cb =  rootView.findViewById(R.id.arp_cb);
        https_cb =  rootView.findViewById(R.id.https_cb);
        http_cb =  rootView.findViewById(R.id.http_cb);
        udp_cb =  rootView.findViewById(R.id.udp_cb);
        ip_cb =  rootView.findViewById(R.id.ip_cb);
        mMonitorAgv =  rootView.findViewById(R.id.Monitor);
        mMonitorCmd =  rootView.findViewById(R.id.cmd);
    }

    public void                 init() {
        initSpinner();
        initFilter();
        initRV();
        initTimer();
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
                Log.e("Error", "O.M.G::IndexOutOfBoundsException in RecyclerView happens");
            }
        }
    }
    private void                initRV() {
        mAdapterWireshark = new WiresharkAdapter(mActivity, mRV_Wireshark);
        mRV_Wireshark.setAdapter(mAdapterWireshark);
        //mRV_Wireshark.hasFixedSize();

        mRV_Wireshark.setLayoutManager(new WrapContentLinearLayoutManager(mActivity));
    }

    private void                initFilter() {
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
    private void                initTimer() {

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

    public boolean              start(boolean isResume) {
        Utils.vibrateDevice(mActivity);
        if (!mTcpdump.isRunning) {
            if (startTcpdump()) {
                mMonitorAgv.setVisibility(View.VISIBLE);
                mAdapterWireshark.clear();
                mActivity.updateNotifications();
                return true;
            }
        } else {
            mMonitorAgv.setVisibility(View.GONE);
            mTcpdump.onTcpDumpStop();
            mActivity.setToolbarTitle(null, "Sniffing over");
            mActivity.updateNotifications();
        }
        return false;
    }

    private boolean             startTcpdump() {
        if (mListHostSelected.isEmpty()) {
            if (mSingleton.selectedHostsList.size() == 1) {//Automatic selection when 1 target only
                mListHostSelected.add(mSingleton.selectedHostsList.get(0));
                mActivity.setToolbarTitle(null,"Listenning " + mSingleton.selectedHostsList.get(0).ip);
            } else {
                mActivity.showSnackbar("Selectionner une target", -1);
                onClickChoiceTarget();
                return false;
            }
        }
        Log.d(TAG, "mTcpdump.actualParam::" + mTcpdump.actualParam);
        Log.d(TAG, "mMonitorCmd::" + mMonitorCmd.getText().toString());
        mMonitorCmd.setText(mTcpdump.actualParam);
        Log.d(TAG, "starting tcpdump with monitor:[" + mMonitorCmd.getText().toString() + "]");
        String cmd = mMonitorCmd.getText().toString();
        WiresharkDispatcher trameDispatcher = new WiresharkDispatcher(mAdapterWireshark, mRV_Wireshark, mActivity);
        String argv = mTcpdump
                .initCmd(mListHostSelected, mTypeScan, cmd)
                .start(trameDispatcher);
        mMonitorAgv.setText(argv);
        return true;
    }

    public void                 onNewTrame(final Trame trame) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!trame.connectionOver && trame.Errno == null && trame.initialised) {


                } else if (!trame.skipped) { /** Error Trame; Over**/
                    mActivity.onTrameError();
                } else {
                    Log.d(TAG, "Not inited or skipped:" + trame);
                }
            }//else skipped do nothing
        });
    }

    private void                onClickChoiceTarget() {
        new RV_dialog(mActivity)
                .setAdapter(new HostSelectionAdapter(mActivity, mSingleton.selectedHostsList, mListHostSelected), false)
                .setTitle("Choix des cibles")
                .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mListHostSelected.isEmpty())
                            mActivity.showSnackbar("No target selected", -1);
                        else {
                            mActivity.setToolbarTitle(null, mListHostSelected.size() + " target" +
                                    ((mListHostSelected.size() <= 1) ? "" : "s") + " selected");
                            start(false);
                        }
                    }
                })
                .show();
        mListHostSelected.clear();
    }


}