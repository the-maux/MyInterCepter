package su.sniff.cepter.View;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Controller.System.Wrapper.TcpdumpWrapper;
import su.sniff.cepter.Model.Pcap.Protocol;
import su.sniff.cepter.Model.Pcap.Trame;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.HostSelectionAdapter;
import su.sniff.cepter.View.Adapter.WiresharkAdapter;
import su.sniff.cepter.View.Dialog.RV_dialog;
import su.sniff.cepter.View.Dialog.Setting_dialog;

/**
 * TODO:    + Add filter
 *          + RecyclerView with addView(TextView.stdout())
 */
public class                    WiresharkActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private WiresharkActivity   mInstance = this;
    private CoordinatorLayout   mCoordinatorLayout;
    private Toolbar             mToolbar;
    private RelativeLayout      mHeaderConfOFF, mHeaderConfON;
    private TextView            mMonitorAgv, mMonitorCmd;
    private RecyclerView        mRV_Wireshark;
    private MaterialSpinner     mSpiner;
    private ProgressBar         mProgressBar;
    private FloatingActionButton mFab;
    private WiresharkAdapter    mAdapterWireshark;
    private String              mTypeScan = "No Filter";
    private List<Host>          mListHostSelected = new ArrayList<>();
    private TcpdumpWrapper      mTcpdump = new TcpdumpWrapper(this);
    private CheckBox            Autoscroll;
    private TextView            tcp_cb, dns_cb, arp_cb, https_cb, http_cb, udp_cb, ip_cb;
    private Singleton           singleton = Singleton.getInstance();

    @Override protected void    onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wireshark);
        initXml();
        initSpinner();
        initRV();
        initFilter();
    }

    private void                initXml() {
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.Coordonitor);
        mRV_Wireshark = (RecyclerView) findViewById(R.id.Output);
        mHeaderConfON = (RelativeLayout) findViewById(R.id.filterPcapLayout);
        mMonitorAgv = (TextView) findViewById(R.id.Monitor);
        mSpiner = (MaterialSpinner) findViewById(R.id.spinnerTypeScan);
        mHeaderConfOFF = (RelativeLayout) findViewById(R.id.nmapConfEditorLayout);
        mMonitorCmd = (TextView) findViewById(R.id.cmd);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabBehavior();
            }
        });
        Autoscroll = (CheckBox) findViewById(R.id.Autoscroll);
        tcp_cb = (TextView) findViewById(R.id.tcp_cb);
        dns_cb = (TextView) findViewById(R.id.dns_cb);
        arp_cb = (TextView) findViewById(R.id.arp_cb);
        https_cb = (TextView) findViewById(R.id.https_cb);
        http_cb = (TextView) findViewById(R.id.http_cb);
        udp_cb = (TextView) findViewById(R.id.udp_cb);
        ip_cb = (TextView) findViewById(R.id.ip_cb);
        findViewById(R.id.showCustomCmd).setOnClickListener(onSwitchHeader());
        findViewById(R.id.settings).setOnClickListener(new Setting_dialog(this, mCoordinatorLayout, mTcpdump));
    }

    private void                initRV() {
        mAdapterWireshark = new WiresharkAdapter(this, mTcpdump.listOfTrames);
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

    private View.OnClickListener onSwitchHeader() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if (mAdapterWireshark.changePermissionFilter(protocol)) {
                    tv.setBackgroundResource(R.drawable.rounded_corner_on);
                } else {
                    tv.setBackgroundResource(R.drawable.rounded_corner_off);
                }
            }
        };
    }

    private void                onClickChoiceTarget() {
        new RV_dialog(this)
                .setAdapter(new HostSelectionAdapter(this, Singleton.getInstance().hostsList, mListHostSelected))
                .setTitle("Choix des cibles")
                .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (mListHostSelected.isEmpty())
                            Snackbar.make(mCoordinatorLayout, "No target selected", Snackbar.LENGTH_LONG).show();
                        else {
                            mToolbar.setSubtitle(mListHostSelected.size() + " target" +
                                    ((mListHostSelected.size() <= 1) ? "" : "s") + " selected");
                            fabBehavior();
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
            cmds.add((String)pair.getKey());
            it.remove(); // avoids a ConcurrentModificationException
        }
        mSpiner.setItems(cmds);
        mSpiner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String typeScan) {
                mMonitorCmd.setText(mParams.get(typeScan));
                mTypeScan = typeScan;
                mMonitorAgv.setText("./mTcpdump " + mParams.get(typeScan).replace("  ", " "));
            }
        });
        mMonitorCmd.setText(mParams.get(cmds.get(0)));
    }

    public void                 fabBehavior() {
        if (!mTcpdump.isRunning) {
            if (startTcpdump()) {
                mMonitorAgv.setVisibility(View.VISIBLE);
                mAdapterWireshark.clear();
                mProgressBar.setVisibility(View.VISIBLE);
                mFab.setImageResource(android.R.drawable.ic_media_pause);
            }
        } else {
            mMonitorAgv.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            mTcpdump.onTcpDumpStop();
            mFab.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private boolean             startTcpdump() {
        if (mListHostSelected.isEmpty()) {
            if (Singleton.getInstance().hostsList.size() == 1) {//Automatic selection when 1 target only
                mListHostSelected.add(Singleton.getInstance().hostsList.get(0));
                mToolbar.setSubtitle(mListHostSelected.size() + " target");
            } else {
                Snackbar.make(mCoordinatorLayout, "Selectionner une target", Snackbar.LENGTH_SHORT).setActionTextColor(Color.RED).show();
                onClickChoiceTarget();
                return false;
            }
        }
        String hostFilter = (mTypeScan.contains("No Filter") || mTypeScan.contains("Custom Filter")) ? " (" : " and (";//If no filter, no '&&' in expression
        for (int i = 0; i < mListHostSelected.size(); i++) {
            if (i > 0)
                hostFilter += " or ";
            hostFilter += " host " + mListHostSelected.get(i).getIp();
        }
        hostFilter += ")\'";
        mTcpdump.start(mMonitorCmd.getText().toString(), hostFilter);
        mMonitorAgv.setText("mTcpdump " + mMonitorCmd.getText().toString() + hostFilter);
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapterWireshark.notifyDataSetChanged();
            }
        });
        mMonitorCmd.setText(mTcpdump.actualParam);
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
                    mAdapterWireshark.addTrameOnAdapter(trame);
                    mRV_Wireshark.post(new Runnable() {
                        @Override
                        public void run() {
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
                    mFab.setImageResource(android.R.drawable.ic_media_play);
                } else {

                }
            }//else skipped do nothing
        });
    }

}
