package su.sniff.cepter.View;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import su.sniff.cepter.Controller.Network.ArpSpoof;
import su.sniff.cepter.Controller.Network.IPTables;
import su.sniff.cepter.Controller.Network.MyDNSMITM;
import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Controller.System.RootProcess;
import su.sniff.cepter.Model.Host;
import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.Model.Wireshark.DNS;
import su.sniff.cepter.R;
import su.sniff.cepter.View.adapter.TcpdumpHostCheckerADapter;

/**
 * Created by maxim on 03/08/2017.
 */
public class                    WiresharkActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private WiresharkActivity   mInstance = this;
    private CoordinatorLayout   coordinatorLayout;
    private TextView            host_et, Output, Monitor, cmd;
    private RecyclerView        RV_host;
    private MaterialSpinner     spinnerTypeScan;
    private String              actualParam = "", hostFilter = "", OutputTxt = "";
    private FloatingActionButton fab;
    private RootProcess         tcpDumpProcess;
    private List<Host>          listHostSelected = new ArrayList<>();
    private boolean             isRunning = false;

    @Override protected void    onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcpdump);
        initXml();
        initRecyHost();
    }

    private void                initXml() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.Coordonitor);
        host_et = (TextView) findViewById(R.id.hostEditext);
        RV_host = (RecyclerView) findViewById(R.id.RV_host);
        Output = (TextView) findViewById(R.id.Output);
        Output.setMovementMethod(new ScrollingMovementMethod());
        Monitor = (TextView) findViewById(R.id.Monitor);
        spinnerTypeScan = (MaterialSpinner) findViewById(R.id.spinnerTypeScan);
        cmd = (TextView) findViewById(R.id.cmd);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabBehavior();
            }
        });
    }

    private void                fabBehavior() {
        if (!isRunning) {
            if (initParams()) {
                onTcpDumpStart();
                fab.setImageResource(android.R.drawable.ic_media_pause);
                isRunning = true;
            }
        } else {
            onTcpDumpStop();
        }
    }

    private void                initRecyHost() {
        TcpdumpHostCheckerADapter adapter = new TcpdumpHostCheckerADapter(this, Singleton.hostsList, listHostSelected);
        RV_host.setAdapter(adapter);
        RV_host.setHasFixedSize(true);
        RV_host.setLayoutManager(new LinearLayoutManager(mInstance));
    }


    private boolean             initParams() {
        actualParam =   " -i wlan0 " +  //  Focus interfacte
                        "-l " +         //  Make stdoutDNS line buffered.  Useful if you want to see  the  data in live
                        "-vv  " +       //  Even more verbose output.
                        "-s 0 " +       //  Snarf snaplen bytes of data from each  packet , no idea what this mean
                        "-vvvx ";       //  -vvv is verbose
                                        /*  -x When parsing and printing, in addition to printing  the  headers
                                        of  each  packet,  print the data of each packet (minus its link
                                        level header) in hex.*/
        cmd.setText(actualParam);
        hostFilter = "\' dst port 53 and (";
        if (listHostSelected.isEmpty()) {
            Snackbar.make(coordinatorLayout, "Selectionner une target", Snackbar.LENGTH_SHORT).setActionTextColor(Color.RED).show();
            return false;
        }
        for (int i = 0; i < listHostSelected.size(); i++) {
            if (i > 0)
                hostFilter += " or ";
            hostFilter += " host " + listHostSelected.get(i).getIp();
        }        hostFilter += ")\'";
        return true;
    }

    /**
     * Start ARPSpoof
     * Bloque le port des trames DNS
     * Inspect/Alter DNS Query
     * Dispatch the DNS request on network
     */
    private void                       onTcpDumpStart() {
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

    private void                        onTcpDumpStop() {
        ArpSpoof.stopArpSpoof();
        RootProcess.kill("tcpdump");
        isRunning = false;
        fab.setImageResource(android.R.drawable.ic_media_play);
        Snackbar.make(coordinatorLayout, "Mitm interupted", Snackbar.LENGTH_SHORT);
    }

    /**
     * Le traitement + renvoie de la trame DNS
     * doit avoir lieu avant le output
     * @param line
     */
    private void                        onNewLineTcpDump(String line) {
        StringBuilder reqdata = new StringBuilder();
        String regex = "^.+length\\s+(\\d+)\\)\\s+([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})\\.[^\\s]+\\s+>\\s+([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})\\.[^\\:]+.*";
        Matcher matcher = Pattern.compile(regex).matcher(line);
        Log.d(TAG, "TcpDump::" + line);
        if (line.contains("A?"))
            stdoutDNS(new DNS(line));
        if (!matcher.find() && !line.contains("tcpdump")){
            line = line.substring(line.indexOf(":")+1).trim().replace(" ", "");
            reqdata.append(line);
        } else {
            if (reqdata.length()>0){
                new MyDNSMITM(reqdata.toString());
            }
            reqdata.delete(0,reqdata.length());
        }
    }

    private void                        stdoutDNS(DNS trame) {
        OutputTxt = "<p>" +
                "<font color='green'>" + trame.time + "</font>" +
                "<font color='red'>" + "   " + trame.ipSrc + " > " + trame.ipDst + "</font>" +
                "<font color='white'>" + " : " + trame.domain + "</font>" +
                "</p>" + OutputTxt;

        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Output.setText(Html.fromHtml(OutputTxt), TextView.BufferType.SPANNABLE);
            }
        });
    }

}
