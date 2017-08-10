package su.sniff.cepter.View;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;

import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import su.sniff.cepter.Controller.Network.IPTables;
import su.sniff.cepter.Controller.Network.MyDNSMITM;
import su.sniff.cepter.Controller.Singleton;
import su.sniff.cepter.Controller.System.RootProcess;
import su.sniff.cepter.Model.Host;
import su.sniff.cepter.R;
import su.sniff.cepter.View.adapter.TcpdumpHostCheckerADapter;
import su.sniff.cepter.globalVariable;

/**
 * Created by maxim on 03/08/2017.
 */
public class                    WiresharkActivity extends Activity {
    private String              TAG = this.getClass().getName();
    private WiresharkActivity   mInstance = this;
    private CoordinatorLayout   coordinatorLayout;
    private TextView            host_et, params_et, Output, Monitor;
    private RecyclerView        RV_host;
    private String              actualParam = "", hostFilter = "";
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
        params_et = (EditText) findViewById(R.id.binParamsEditText);
        RV_host = (RecyclerView) findViewById(R.id.RV_host);
        Output = (TextView) findViewById(R.id.Output);
        Monitor = (TextView) findViewById(R.id.Monitor);
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
                try {
                    onTcpDumpStart();
                    fab.setImageResource(android.R.drawable.ic_media_play);
                    isRunning = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        actualParam =   " -i wlan0 " + //Focus interfacte
                        "-l " + //Make stdout line buffered.  Useful if you want to see  the  data in live
                        "-vv  " + //Even more verbose output.
                        "-s 0 " + //Snarf snaplen bytes of data from each  packet , no idea what this mean
                        "-vvvx "; //-vvv is verbose
        /*-x When parsing and printing, in addition to printing  the  headers
        of  each  packet,  print the data of each packet (minus its link
                level header) in hex.*/
        params_et.setText(actualParam);
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

    private void                       onTcpDumpStart() throws IOException {
        String TcpDumpPath = globalVariable.path + "/tcpdump ";
        String cmd = TcpDumpPath + actualParam + hostFilter;
        Monitor.setText(cmd);
        new IPTables().discardForwardding2Port(53);
        tcpDumpProcess = new RootProcess("TcpDump::DNSSpoof").exec(cmd);
        BufferedReader reader = tcpDumpProcess.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            onNewLineTcpDump(line);
        }
        Log.d(TAG, "onTcpDump start over");
    }

    private void                        onTcpDumpStop() {
        tcpDumpProcess.exec("exit")
                .closeProcess();
        isRunning = false;
        fab.setImageResource(android.R.drawable.ic_media_pause);
    }


    private void                        onNewLineTcpDump(String line) {
        StringBuilder reqdata = new StringBuilder();
        String regex = "^.+length\\s+(\\d+)\\)\\s+([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})\\.[^\\s]+\\s+>\\s+([\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3}\\.[\\d]{1,3})\\.[^\\:]+.*";
        Matcher matcher = Pattern.compile(regex).matcher(line);
        Log.d(TAG, "TcpDump::" + line);
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

}
