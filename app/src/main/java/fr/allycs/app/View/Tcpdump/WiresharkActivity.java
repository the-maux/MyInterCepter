package fr.allycs.app.View.Tcpdump;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fr.allycs.app.Controller.AndroidUtils.MyActivity;
import fr.allycs.app.Controller.AndroidUtils.MyGlideLoader;
import fr.allycs.app.Controller.AndroidUtils.Utils;
import fr.allycs.app.Controller.Core.Configuration.Singleton;
import fr.allycs.app.Controller.Core.Tcpdump.Tcpdump;
import fr.allycs.app.Model.Net.Protocol;
import fr.allycs.app.Model.Net.Trame;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;
import fr.allycs.app.View.Widget.Adapter.HostSelectionAdapter;
import fr.allycs.app.View.Widget.Adapter.WiresharkAdapter;
import fr.allycs.app.View.Widget.Dialog.BottomSheet.GeneralSettings;
import fr.allycs.app.View.Widget.Dialog.RV_dialog;

public class                    WiresharkActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private WiresharkActivity   mInstance = this;
    private CoordinatorLayout   mCoordinatorLayout;
    private Toolbar             mToolbar;
    private RelativeLayout      mHeaderConfOFF, mHeaderConfON;
    private ProgressBar         mProgressBar;
    private TextView            mMonitorAgv, mMonitorCmd;
    private FloatingActionButton mFab;
    private MaterialSpinner     mSpiner;
    private CheckBox            Autoscroll;
    private TextView            tcp_cb, dns_cb, arp_cb, https_cb, http_cb, udp_cb, ip_cb;
    private RecyclerView        mRV_Wireshark;
    private WiresharkAdapter    mAdapterWireshark;
    private String              mTypeScan = "No Filter";
    private List<Host>          mListHostSelected = new ArrayList<>();
    private Tcpdump             mTcpdump;
    private Singleton           mSingleton = Singleton.getInstance();

    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wireshark);
        initXml();
        mTcpdump = Tcpdump.getTcpdump(this, true);
        initSpinner();
        initFilter();
        initSettings();
        initRV();
        setToolbarTitle(null, mSingleton.selectedHostsList.get(0).getName());
        if (mTcpdump.isRunning)
            mFab.setImageResource(R.mipmap.ic_pause);
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
        mRV_Wireshark = findViewById(R.id.Output);
        mHeaderConfON = findViewById(R.id.filterPcapLayout);
        mMonitorAgv =  findViewById(R.id.Monitor);
        mSpiner =  findViewById(R.id.spinnerTypeScan);
        mHeaderConfOFF = findViewById(R.id.nmapConfEditorLayout);
        mMonitorCmd =  findViewById(R.id.cmd);
        mProgressBar =  findViewById(R.id.progressBar);
        mToolbar = findViewById(R.id.toolbar);
        mFab =  findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.vibrateDevice(mInstance);
                startWireshark(false);
            }
        });
        Autoscroll =  findViewById(R.id.Autoscroll);
        tcp_cb =  findViewById(R.id.tcp_cb);
        dns_cb =  findViewById(R.id.dns_cb);
        arp_cb =  findViewById(R.id.arp_cb);
        https_cb =  findViewById(R.id.https_cb);
        http_cb =  findViewById(R.id.http_cb);
        udp_cb =  findViewById(R.id.udp_cb);
        ip_cb =  findViewById(R.id.ip_cb);
        findViewById(R.id.settings).setOnClickListener(onSwitchHeader());
    }

    private void                initRV() {
        mAdapterWireshark = new WiresharkAdapter(this, mTcpdump.listOfTrames, mRV_Wireshark);
        mRV_Wireshark.setAdapter(mAdapterWireshark);
        mRV_Wireshark.hasFixedSize();
        mRV_Wireshark.setLayoutManager(new LinearLayoutManager(mInstance));
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

    private void                initSettings() {
        findViewById(R.id.settingsMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GeneralSettings(mInstance, mCoordinatorLayout, mTcpdump).show();
            }
        });
    }

    private View.OnClickListener onSwitchHeader() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHeaderConfOFF.getVisibility() == View.GONE &&
                     mHeaderConfON.getVisibility() == View.GONE) {
                }
                if (mHeaderConfOFF.getVisibility() == View.GONE) {
                    mHeaderConfOFF.setVisibility(View.VISIBLE);
                    mHeaderConfON.setVisibility(View.GONE);
                } else {
                    mHeaderConfOFF.setVisibility(View.GONE);
                    mHeaderConfON.setVisibility(View.VISIBLE);
                }
            }
        };
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

    private void                onClickChoiceTarget() {
        new RV_dialog(this)
                .setAdapter(new HostSelectionAdapter(this, mSingleton.selectedHostsList, mListHostSelected), false)
                .setTitle("Choix des cibles")
                .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mListHostSelected.isEmpty())
                            Snackbar.make(mCoordinatorLayout, "No target selected", Snackbar.LENGTH_LONG).show();
                        else {
                            setToolbarTitle(null, mListHostSelected.size() + " target" +
                                    ((mListHostSelected.size() <= 1) ? "" : "s") + " selected");
                            startWireshark(false);
                        }
                    }
                })
                .show();
        mListHostSelected.clear();
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

    public void                 startWireshark(boolean isResume) {
        if (!mTcpdump.isRunning) {
            if (startTcpdump()) {
                mMonitorAgv.setVisibility(View.VISIBLE);
                mAdapterWireshark.clear();
                mProgressBar.setVisibility(View.VISIBLE);
                mFab.setImageResource(R.mipmap.ic_pause);
            }
        } else {
            mMonitorAgv.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mTcpdump.onTcpDumpStop();
            mFab.setImageResource(R.mipmap.ic_play);
        }
    }

    private boolean             startTcpdump() {
        if (mListHostSelected.isEmpty()) {
            if (mSingleton.selectedHostsList.size() == 1) {//Automatic selection when 1 target only
                mListHostSelected.add(mSingleton.selectedHostsList.get(0));
                mToolbar.setSubtitle("Processing");
            } else {
                Snackbar.make(mCoordinatorLayout, "Selectionner une target", Snackbar.LENGTH_SHORT)
                        .setActionTextColor(Color.RED).show();
                onClickChoiceTarget();
                return false;
            }
        }
        Log.d(TAG, "mTcpdump.actualParam::" + mTcpdump.actualParam);
        Log.d(TAG, "mMonitorCmd::" + mMonitorCmd.getText().toString());
        mMonitorCmd.setText(mTcpdump.actualParam);
        Log.d(TAG, "starting tcpdump with monitor:[" + mMonitorCmd.getText().toString() + "]");

        mMonitorAgv.setText(mTcpdump.start(mMonitorCmd.getText().toString(), mListHostSelected, mTypeScan));
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapterWireshark.notifyDataSetChanged();
            }
        });
        return true;
    }

    public void                 onNewTrame(final Trame trame) {
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (trame.connectionOver && trame.Errno == null) {

                } else if (trame.initialised) {
                    if (mProgressBar.getVisibility() == View.VISIBLE)
                        mProgressBar.setVisibility(View.GONE);
                    mRV_Wireshark.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapterWireshark.addTrameOnAdapter(trame);
                            mAdapterWireshark.notifyDataSetChanged();
                            if (Autoscroll.isChecked()) {
                                mRV_Wireshark.smoothScrollToPosition(0);
                            }
                        }
                    });
                } else if (!trame.skipped) { /** Error Trame; Over**/
                    Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Error:" + trame.Errno, Snackbar.LENGTH_LONG);
                    ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                            .setTextColor(ContextCompat.getColor(mInstance, R.color.material_red_400));
                    snackbar.show();
                    mProgressBar.setVisibility(View.GONE);
                    mFab.setImageResource(R.mipmap.ic_play);
                } else {
                    Log.d(TAG, "Not inited or skipped:" + trame);
                }
            }//else skipped do nothing
        });
    }

    public void                 setToolbarTitle(final String title, final String subtitle) {
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (title != null)
                    mToolbar.setTitle(title);
                if (subtitle != null)
                    mToolbar.setSubtitle(subtitle);
            }
        });
    }
}
