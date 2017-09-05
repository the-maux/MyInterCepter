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
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import su.sniff.cepter.Controller.System.Wrapper.ArpSpoof;
import su.sniff.cepter.Controller.Network.IPTables;
import su.sniff.cepter.Controller.Network.MyDNSMITM;
import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Controller.System.Wrapper.RootProcess;
import su.sniff.cepter.Model.Pcap.Trame;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.TcpdumpHostCheckerADapter;
import su.sniff.cepter.View.Adapter.WiresharkAdapter;
import su.sniff.cepter.View.Dialog.RV_dialog;

/**
 * TODO:    + Add filter
 *          + RecyclerView with addView(TextView.stdout())
 */
public class                    WiresharkActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private WiresharkActivity   mInstance = this;
    private CoordinatorLayout   coordinatorLayout;
    private Map<String, String> params = new HashMap<>();
    private AppBarLayout        appbar;
    private RelativeLayout      targetSelectionner, nmapConfEditorLayout, filterPcapLayout;
    private TextView            Monitor, cmd, monitorHost;
    private ImageView           wiresharkMode;
    private RecyclerView        RV_Wireshark;
    private MaterialSpinner     spinner;
    private ProgressBar         progressBar;
    private FloatingActionButton fab;
    private RootProcess         tcpDumpProcess;
    private ArrayList<Trame>    listOfTrames = new ArrayList<>();
    private WiresharkAdapter    adapterWiresharkRV;
    private String              actualParam = "", hostFilter = "", mTypeScan = "";
    private List<Host>          listHostSelected = new ArrayList<>();
    private boolean             isRunning = false;

    String INTERFACE = "-i wlan0 ";    //  Focus interfacte;
    String STDOUT_BUFF = "-l ";        //  Make stdOUT line buffered.  Useful if you want to see  the  data in live
    String VERBOSE_v1 = "-v ";          //  Verbose mode 1
    String VERBOSE_v2 = "-vv  ";        //  Even more verbose output.
    String VERBOSE_v3 = "-vvvx  ";      //  Print trame in HEXA<->ASCII
    /*  -x When parsing and printing, in addition to printing  the  headers
        of  each  packet,  print the data of each packet (minus its link
        level header) in hex.*/
    String SNARF = "-s 0 ";             //  Snarf snaplen bytes of data from each  packet , no idea what this mean

    @Override
    protected void              onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcpdump);
        initXml();
        initSpinner();
        initRV();
    }

    private void                initXml() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.Coordonitor);
        appbar = (AppBarLayout) findViewById(R.id.appbar);
        monitorHost = (TextView) findViewById(R.id.monitorHost);
        RV_Wireshark = (RecyclerView) findViewById(R.id.Output);
        filterPcapLayout = (RelativeLayout) findViewById(R.id.filterPcapLayout);
        Monitor = (TextView) findViewById(R.id.Monitor);
        spinner = (MaterialSpinner) findViewById(R.id.spinnerTypeScan);
        nmapConfEditorLayout = (RelativeLayout) findViewById(R.id.nmapConfEditorLayout);
        cmd = (TextView) findViewById(R.id.cmd);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        targetSelectionner = (RelativeLayout) findViewById(R.id.targetSelectionner);
        wiresharkMode = (ImageView) findViewById(R.id.wiresharkMode);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabBehavior();
            }
        });
        targetSelectionner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChoiceTarget();
            }
        });
        monitorHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickChoiceTarget();
            }
        });
        monitorHost.setText(listHostSelected.size() + " target");
        wiresharkMode.setOnClickListener(onWiresharkModeActivated());
    }

    private void                initRV() {
        adapterWiresharkRV = new WiresharkAdapter(this, listOfTrames);
        RV_Wireshark.setAdapter(adapterWiresharkRV);
        RV_Wireshark.hasFixedSize();
        RV_Wireshark.setLayoutManager(new LinearLayoutManager(mInstance));
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

    private void                onClickChoiceTarget() {
        RecyclerView.Adapter adapter = new TcpdumpHostCheckerADapter(this, Singleton.getInstance().hostsList, listHostSelected);
        new RV_dialog(this)
                .setAdapter(adapter)
                .setTitle("Choix des cibles")
                .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        monitorHost.setText(listHostSelected.size() + " target");
                    }
                })
                .show();
    }

    private void                initSpinner() {
        final ArrayList<String> cmds = new ArrayList<>();
        cmds.add("Arp Filter");
        params.put(cmds.get(0), INTERFACE + " \' arp ");
        cmds.add("DNS Filter");
        params.put(cmds.get(1), INTERFACE + "\' dst port 53 ");
        cmds.add("DNS Intercepter");
        params.put(cmds.get(2), INTERFACE + STDOUT_BUFF + VERBOSE_v2 + SNARF + VERBOSE_v3 + "\' dst port 53 ");
        cmds.add("HTTP Filter");
        params.put(cmds.get(3), INTERFACE + " \' (port 80 or port 443 or dst port 53) ");
        cmds.add("Display Format");
        params.put(cmds.get(4), INTERFACE + STDOUT_BUFF + VERBOSE_v2 + SNARF + VERBOSE_v3 + "\'");
        cmds.add("TCP Filter");
        params.put(cmds.get(5), INTERFACE  + " \' tcp ");
        cmds.add("UDP Filter");
        params.put(cmds.get(6), INTERFACE + " \' udp ");
        cmds.add("Custom Filter");
        params.put(cmds.get(7), INTERFACE + "\' ");
        cmds.add("No Filter");
        params.put(cmds.get(8), INTERFACE  + "\' ");
        spinner.setItems(cmds);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String typeScan) {
                cmd.setText(params.get(typeScan));
                mTypeScan = typeScan;
                Monitor.setText("./tcpdump " + params.get(typeScan).replace("  ", " "));
            }
        });

        cmd.setText(params.get(cmds.get(0)));

    }

    public void                 fabBehavior() {
        if (!isRunning) {
            if (initParams()) {
                progressBar.setVisibility(View.VISIBLE);
                onTcpDumpStart();
                nmapConfEditorLayout.setVisibility(View.GONE);
                appbar.setVisibility(View.GONE);
                filterPcapLayout.setVisibility(View.VISIBLE);
                fab.setImageResource(android.R.drawable.ic_media_pause);
                isRunning = true;
            } else {
                onClickChoiceTarget();
            }
        } else {
            progressBar.setVisibility(View.GONE);
            filterPcapLayout.setVisibility(View.GONE);
            appbar.setVisibility(View.VISIBLE);
            nmapConfEditorLayout.setVisibility(View.VISIBLE);
            onTcpDumpStop();
        }
    }

    private boolean             initParams() {
        actualParam = cmd.getText().toString();
        cmd.setText(actualParam);
        hostFilter = (mTypeScan.contains("No Filter")) ? " (" : " and (" ;//If no filter, no '&&' in expression
        if (listHostSelected.isEmpty()) {
            Snackbar.make(coordinatorLayout, "Selectionner une target", Snackbar.LENGTH_SHORT).setActionTextColor(Color.RED).show();
            return false;
        }
        for (int i = 0; i < listHostSelected.size(); i++) {
            if (i > 0)
                hostFilter += " or ";
            hostFilter += " host " + listHostSelected.get(i).getIp();
        }
        hostFilter += ")\'";
        return true;
    }

    /**
     * Start ARPSpoof
     * Bloque le port des trames DNS
     * Inspect/Alter DNS Query
     * Dispatch the DNS request on network
     */
    private void                onTcpDumpStart() {
        final String cmd = Singleton.getInstance().FilesPath + "/tcpdump " + actualParam + hostFilter;
        Monitor.setText(cmd.replace(Singleton.getInstance().FilesPath, ""));
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArpSpoof.launchArpSpoof();
                try {
                    Thread.sleep(2000);//Wait a sec for ARP Catched for target
                    if (actualParam.contains(STDOUT_BUFF) && actualParam.contains("dst port 53")) {
                        Log.i(TAG, "DNS REQUEST MITM");
                       new IPTables().discardForwardding2Port(53); //MITM DNS
                     }

                    listOfTrames.clear();
                    tcpDumpProcess = new RootProcess("TcpDump::DNSSpoof").exec(cmd);
                    BufferedReader reader = tcpDumpProcess.getReader();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        final String finalLine = line;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                onNewLineTcpDump(finalLine);
                            }
                        }).start();
                    }
                    Log.d(TAG, "./Tcpdump finish");
                    onNewLineTcpDump("Quiting...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    tcpDumpProcess.closeProcess();
                }
                Log.d(TAG, "onTcpDump start over");
            }
        }).start();
    }

    /**
     * Le traitement + renvoie de la trame DNS
     * doit avoir lieu avant le output
     *
     * @param line
     */
    private void                onNewLineTcpDump(String line) {
        if (line.contains("Quiting...")) {
            mInstance.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(coordinatorLayout, "Mitm stopped", Snackbar.LENGTH_LONG).show();
                }
            });
            return;
        }
        if (actualParam.contains(STDOUT_BUFF) && actualParam.contains("dst port 53")) {
            MITM_DNS(line);
        }
        Trame trame = new Trame(line, listOfTrames.size(), 0);
        if (trame.initialised) {
            stdOUT(trame);
        } else {
            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Error:" + trame.Errno, Snackbar.LENGTH_LONG);
            ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                    .setTextColor(ContextCompat.getColor(mInstance, R.color.material_red_400));
            snackbar.show();
            progressBar.setVisibility(View.GONE);
            onTcpDumpStop();
        }
    }

    /**
     * Renvoie la trame mais peut altérer la réponse
     * @param line
     */
    private void                MITM_DNS(String line) {
        StringBuilder reqdata = new StringBuilder();
        String regex = "^.+length\\s+(\\d+)\\)\\s+([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})\\.[^\\s]+\\s+>\\s+([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})\\.[^\\:]+.*";
        Matcher matcher = Pattern.compile(regex).matcher(line);
        if (!matcher.find() && !line.contains("tcpdump")) {
            line = line.substring(line.indexOf(":") + 1).trim().replace(" ", "");
            reqdata.append(line);
        } else {
            if (reqdata.length() > 0) {

            }
            reqdata.delete(0, reqdata.length());
        }
        new MyDNSMITM(reqdata.toString());
    }

    private void                stdOUT(final Trame trame) {
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (trame.initialised) {
                    listOfTrames.add(0, trame);
                    trame.offsett = listOfTrames.size();
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);
                    if (RV_Wireshark.computeVerticalScrollOffset() == 0) {//Smart insert
                        adapterWiresharkRV.notifyItemInserted(0);
                        RV_Wireshark.smoothScrollToPosition(0);
                    } else {
                        adapterWiresharkRV.notifyItemInserted(0);
                }
            }
        }});
    }

    private void                onTcpDumpStop() {
        ArpSpoof.stopArpSpoof();
        RootProcess.kill("tcpdump");
        isRunning = false;
        fab.setImageResource(android.R.drawable.ic_media_play);
        Snackbar.make(coordinatorLayout, "Mitm interupted", Snackbar.LENGTH_SHORT);
    }

}
