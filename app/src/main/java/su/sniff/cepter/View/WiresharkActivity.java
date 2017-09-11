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
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
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

/**
 * TODO:    + Add filter
 *          + RecyclerView with addView(TextView.stdout())
 */
public class                    WiresharkActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private WiresharkActivity   mInstance = this;
    private CoordinatorLayout   mCoordinatorLayout;
    private Map<String, String> mParams = new HashMap<>();
    private AppBarLayout        mAppbar;
    private RelativeLayout      mChoiceTarget, mHeaderConfOFF, mHeaderConfON;
    private TextView            mMonitorAgv, mMonitorCmd, mMonitorHost;
    private ImageView           mWiresharkModeButton;
    private RecyclerView        mRV_Wireshark;
    private MaterialSpinner     spinner;
    private ProgressBar         progressBar;
    private FloatingActionButton fab;
    private WiresharkAdapter    adapterWiresharkRV;
    private String              mTypeScan = "No Filter";
    private List<Host>          listHostSelected = new ArrayList<>();
    private TcpdumpWrapper      tcpdump = new TcpdumpWrapper(this);
    private boolean             autoscroll = true;
    private CheckBox            Autoscroll;
    private TextView            tcp_cb, dns_cb, arp_cb, https_cb, udp_cb, ip_cb;

    @Override
    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcpdump);
        initXml();
        initSpinner();
        initRV();
        initFilter();
    }

    private void                initXml() {
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.Coordonitor);
        mAppbar = (AppBarLayout) findViewById(R.id.appbar);
        mMonitorHost = (TextView) findViewById(R.id.monitorHost);
        mRV_Wireshark = (RecyclerView) findViewById(R.id.Output);
        mHeaderConfON = (RelativeLayout) findViewById(R.id.filterPcapLayout);
        mMonitorAgv = (TextView) findViewById(R.id.Monitor);
        spinner = (MaterialSpinner) findViewById(R.id.spinnerTypeScan);
        mHeaderConfOFF = (RelativeLayout) findViewById(R.id.nmapConfEditorLayout);
        mMonitorCmd = (TextView) findViewById(R.id.cmd);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mChoiceTarget = (RelativeLayout) findViewById(R.id.targetSelectionner);
        mWiresharkModeButton = (ImageView) findViewById(R.id.wiresharkMode);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabBehavior();
            }
        });
        mChoiceTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChoiceTarget();
            }
        });
        mMonitorHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChoiceTarget();
            }
        });
        mMonitorHost.setText(listHostSelected.size() + " target");
        mWiresharkModeButton.setOnClickListener(onWiresharkModeActivated());
        Autoscroll = (CheckBox) findViewById(R.id.Autoscroll);
        tcp_cb = (TextView) findViewById(R.id.tcp_cb);
        dns_cb = (TextView) findViewById(R.id.dns_cb);
        arp_cb = (TextView) findViewById(R.id.arp_cb);
        https_cb = (TextView) findViewById(R.id.https_cb);
        udp_cb = (TextView) findViewById(R.id.udp_cb);
        ip_cb = (TextView) findViewById(R.id.ip_cb);
    }

    private void                initRV() {
        adapterWiresharkRV = new WiresharkAdapter(this, tcpdump.listOfTrames);
        mRV_Wireshark.setAdapter(adapterWiresharkRV);
        mRV_Wireshark.hasFixedSize();
        mRV_Wireshark.setLayoutManager(new LinearLayoutManager(mInstance));
    }

    private void                initFilter() {
        Autoscroll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                autoscroll = b;
            }
        });
        tcp_cb.setOnClickListener(onChangePermissionFilter(Protocol.TCP));
        dns_cb.setOnClickListener(onChangePermissionFilter(Protocol.DNS));
        https_cb.setOnClickListener(onChangePermissionFilter(Protocol.HTTP));
        udp_cb.setOnClickListener(onChangePermissionFilter(Protocol.UDP));
        arp_cb.setOnClickListener(onChangePermissionFilter(Protocol.ARP));
        ip_cb.setOnClickListener(onChangePermissionFilter(Protocol.IP));
    }

    private View.OnClickListener onChangePermissionFilter(final Protocol protocol) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapterWiresharkRV.changePermissionFilter(protocol);
            }
        };
    }

    private void                onClickChoiceTarget() {
        new RV_dialog(this)
                .setAdapter(new HostSelectionAdapter(this, Singleton.getInstance().hostsList, listHostSelected))
                .setTitle("Choix des cibles")
                .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (listHostSelected.isEmpty())
                            Snackbar.make(mCoordinatorLayout, "No target selected", Snackbar.LENGTH_LONG).show();
                        else
                            mMonitorHost.setText(listHostSelected.size() + " target");
                    }
                })
                .show();
    }

    private void                initSpinner() {
        Iterator it = tcpdump.getCmdsWithArgsInMap().entrySet().iterator();
        ArrayList<String> cmds = new ArrayList<>();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            mParams.put((String)pair.getKey(), (String)pair.getValue());
            cmds.add((String)pair.getKey());
            it.remove(); // avoids a ConcurrentModificationException
        }
        spinner.setItems(cmds);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String typeScan) {
                mMonitorCmd.setText(mParams.get(typeScan));
                mTypeScan = typeScan;
                mMonitorAgv.setText("./tcpdump " + mParams.get(typeScan).replace("  ", " "));
            }
        });
        mMonitorCmd.setText(mParams.get(cmds.get(0)));
    }

    public void                 fabBehavior() {
        if (!tcpdump.isRunning) {
            if (startTcpdump()) {
                progressBar.setVisibility(View.VISIBLE);
                mHeaderConfOFF.setVisibility(View.GONE);
                mAppbar.setVisibility(View.GONE);
                mHeaderConfON.setVisibility(View.VISIBLE);
                fab.setImageResource(android.R.drawable.ic_media_pause);
            }
        } else {
            progressBar.setVisibility(View.GONE);
            mHeaderConfON.setVisibility(View.GONE);
            mAppbar.setVisibility(View.VISIBLE);
            mHeaderConfOFF.setVisibility(View.VISIBLE);
            tcpdump.onTcpDumpStop();
            fab.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    private View.OnClickListener onWiresharkModeActivated() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner.setSelectedIndex(8);//No filter
                fabBehavior();
            }
        };
    }

    private boolean             startTcpdump() {
        if (listHostSelected.isEmpty()) {
            if (Singleton.getInstance().hostsList.size() == 1) {//Automatic selection when 1 target only
                listHostSelected.add(Singleton.getInstance().hostsList.get(0));
                mMonitorHost.setText(listHostSelected.size() + " target");
            } else {
                Snackbar.make(mCoordinatorLayout, "Selectionner une target", Snackbar.LENGTH_SHORT).setActionTextColor(Color.RED).show();
                onClickChoiceTarget();
                return false;
            }
        }
        String hostFilter = (mTypeScan.contains("No Filter") || mTypeScan.contains("Custom Filter")) ? " (" : " and (";//If no filter, no '&&' in expression
        for (int i = 0; i < listHostSelected.size(); i++) {
            if (i > 0)
                hostFilter += " or ";
            hostFilter += " host " + listHostSelected.get(i).getIp();
        }
        hostFilter += ")\'";
        tcpdump.start(mMonitorCmd.getText().toString(), hostFilter);
        mMonitorAgv.setText("tcpdump " + mMonitorCmd.getText().toString() + hostFilter);
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapterWiresharkRV.notifyDataSetChanged();
            }
        });
        mMonitorCmd.setText(tcpdump.actualParam);
        return true;
    }

    public void                 onNewTrame(final Trame trame) {
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (trame.connectionOver) {

                } else if (trame.initialised) {
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);
                    adapterWiresharkRV.addTrameOnAdapter(trame);
                    mRV_Wireshark.post(new Runnable() {
                        @Override
                        public void run() {
                            adapterWiresharkRV.notifyDataSetChanged();
                            autoscroll();
                        }
                    });
                } else if (!trame.skipped) { /** Error Trame; Over**/
                    Snackbar snackbar = Snackbar.make(mCoordinatorLayout, "Error:" + trame.Errno, Snackbar.LENGTH_LONG);
                    ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                            .setTextColor(ContextCompat.getColor(mInstance, R.color.material_red_400));
                    snackbar.show();
                    progressBar.setVisibility(View.GONE);
                    fab.setImageResource(android.R.drawable.ic_media_play);
                }
            }//else skipped do nothing
        });
    }

    private void                autoscroll() {
        if (autoscroll) {
            mRV_Wireshark.smoothScrollToPosition(0);
        }
    }

}
