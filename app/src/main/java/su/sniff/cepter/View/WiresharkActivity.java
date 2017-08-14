package su.sniff.cepter.View;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;

import android.support.design.widget.Snackbar;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
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

import su.sniff.cepter.Controller.Network.ArpSpoof;
import su.sniff.cepter.Controller.Network.IPTables;
import su.sniff.cepter.Controller.Network.MyDNSMITM;
import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Controller.System.RootProcess;
import su.sniff.cepter.Model.Pcap.Protocol;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Dialog.HostChoiceDialog;
import su.sniff.cepter.View.TextView.StdoutTcpDump;

/**
 * Created by maxim on 03/08/2017.
 */
public class                    WiresharkActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private WiresharkActivity    mInstance = this;
    private CoordinatorLayout   coordinatorLayout;
    private Map<String, String> params = new HashMap<>();
    private RelativeLayout      targetSelectionner;
    private TextView            host_et, Output, Monitor, cmd, monitorHost;
    private MaterialSpinner     spinner;
    private ProgressBar         progressBar;
    private String              actualParam = "", hostFilter = "", OutputTxt = "";
    private FloatingActionButton fab;
    private RootProcess         tcpDumpProcess;
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
    }

    private void                initXml() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.Coordonitor);
        host_et = (TextView) findViewById(R.id.hostEditext);
        monitorHost = (TextView) findViewById(R.id.monitorHost);
        Output = (TextView) findViewById(R.id.Output);
        Output.setMovementMethod(new ScrollingMovementMethod());
        Monitor = (TextView) findViewById(R.id.Monitor);
        spinner = (MaterialSpinner) findViewById(R.id.spinnerTypeScan);
        cmd = (TextView) findViewById(R.id.cmd);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        targetSelectionner = (RelativeLayout) findViewById(R.id.targetSelectionner);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabBehavior();
            }
        });
        targetSelectionner.setOnClickListener(onClickTarget());
        monitorHost.setOnClickListener(onClickTarget());
        monitorHost.setText(listHostSelected.size() + " target");
    }

    private View.OnClickListener onClickTarget() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(mInstance);
                new HostChoiceDialog().ShowDialog(mInstance, alert, listHostSelected, monitorHost);
            }
        };
    }

    private void                initSpinner() {
        final ArrayList<String> cmds = new ArrayList<>();
        cmds.add("Arp Filter");
        params.put(cmds.get(0), INTERFACE + VERBOSE_v2 + " \' arp ");
        cmds.add("DNS Filter");
        params.put(cmds.get(1), INTERFACE + VERBOSE_v2 + "\' dst port 53 ");
        cmds.add("DNS Intercepter");
        params.put(cmds.get(2), INTERFACE + STDOUT_BUFF + VERBOSE_v2 + SNARF + VERBOSE_v3 + "\' dst port 53 ");
        cmds.add("HTTP Filter");
        params.put(cmds.get(3), INTERFACE + VERBOSE_v2 + " \' http ");
        cmds.add("Display Format");
        params.put(cmds.get(4), INTERFACE + STDOUT_BUFF + VERBOSE_v2 + SNARF + VERBOSE_v3 + "\'");
        cmds.add("TCP Filter");
        params.put(cmds.get(5), INTERFACE + VERBOSE_v2 + " \' tcp ");
        cmds.add("UDP Filter");
        params.put(cmds.get(6), INTERFACE + VERBOSE_v2 + " \' udp ");
        cmds.add("Custom Filter");
        params.put(cmds.get(7), INTERFACE + "\' ");
        cmds.add("No Filter");
        params.put(cmds.get(8), INTERFACE + STDOUT_BUFF + VERBOSE_v2 + SNARF + VERBOSE_v3 + "\' ");
        spinner.setItems(cmds);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String typeScan) {
                cmd.setText(params.get(typeScan));
                Monitor.setText("tcpdump " + params.get(typeScan));
            }
        });
        cmd.setText(params.get(cmds.get(0)));
        Monitor.setText("tcpdump " + params.get(cmds.get(0)));
    }

    private void                fabBehavior() {
        if (!isRunning) {
            if (initParams()) {
                progressBar.setVisibility(View.VISIBLE);
                onTcpDumpStart();
                fab.setImageResource(android.R.drawable.ic_media_pause);
                isRunning = true;
            }
        } else {
            onTcpDumpStop();
        }
    }

    private boolean             initParams() {
        actualParam = cmd.getText().toString();
        cmd.setText(actualParam);
        hostFilter = " and (";
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
        final String cmd = Singleton.FilesPath + "/tcpdump " + actualParam + hostFilter;
        Monitor.setText(cmd.replace(Singleton.FilesPath, ""));
        new Thread(new Runnable() {
            @Override
            public void run() {
                ArpSpoof.launchArpSpoof();
                try {
                    Thread.sleep(2000);//Wait a sec for ARP Catched for target
                    new IPTables().discardForwardding2Port(53);
                    tcpDumpProcess = new RootProcess("TcpDump::DNSSpoof").exec(cmd);
                    BufferedReader reader = tcpDumpProcess.getReader();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        onNewLineTcpDump(line);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
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
        Log.d(TAG, "onNewLineTcpDump::" + line);

        stdOUT(line);
        // IF MITM DNS ACTIVATED
        if (actualParam.contains(STDOUT_BUFF) && actualParam.contains("dst port 53")) {
            mitmDNS(line);
        }
    }

    private void                mitmDNS(String line) {
        StringBuilder reqdata = new StringBuilder();
        String regex = "^.+length\\s+(\\d+)\\)\\s+([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})\\.[^\\s]+\\s+>\\s+([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})\\.[^\\:]+.*";
        Matcher matcher = Pattern.compile(regex).matcher(line);
        if (!matcher.find() && !line.contains("tcpdump")) {
            line = line.substring(line.indexOf(":") + 1).trim().replace(" ", "");
            reqdata.append(line);
        } else {
            if (reqdata.length() > 0) {
                new MyDNSMITM(reqdata.toString());
            }
            reqdata.delete(0, reqdata.length());
        }
    }

    private void                stdOUT(String line) {
        if (line.contains("A?")) {
            OutputTxt = new StdoutTcpDump().stdout(line, Protocol.DNS) + OutputTxt;
        } else {
            OutputTxt = new StdoutTcpDump().stdout(line, Protocol.UNKNOW) + OutputTxt;
        }
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressBar.setVisibility(View.GONE);
                Output.setText(Html.fromHtml(OutputTxt), TextView.BufferType.SPANNABLE);
            }
        });
    }

    private void                onTcpDumpStop() {
        ArpSpoof.stopArpSpoof();
        RootProcess.kill("tcpdump");
        isRunning = false;
        fab.setImageResource(android.R.drawable.ic_media_play);
        Snackbar.make(coordinatorLayout, "Mitm interupted", Snackbar.LENGTH_SHORT);
    }

}
