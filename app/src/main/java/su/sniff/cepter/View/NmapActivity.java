package su.sniff.cepter.View;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import su.sniff.cepter.Controller.Singleton;
import su.sniff.cepter.Controller.System.RootProcess;
import su.sniff.cepter.Model.Host;
import su.sniff.cepter.R;
import su.sniff.cepter.View.adapter.HostCheckBoxAdapter;
import su.sniff.cepter.globalVariable;

/**
 * Created by maxim on 03/08/2017.
 */
public class                    NmapActivity extends Activity {
    private String              TAG = this.getClass().getName();
    private NmapActivity        mInstance = this;
    private CoordinatorLayout   coordinatorLayout;
    private MaterialSpinner     spinner;
    private Map<String, String> params =  new HashMap<>();
    private ArrayList<String>   cmd = new ArrayList<>();
    private TextView            host_et, params_et, Output, Monitor;
    private RecyclerView        RV_host;
    private FloatingActionButton fab;
    private Host                actualTarget = null;


    @Override protected void    onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmap);
        initParams();
        initXml();
        initSpinner();
        initRecyHost();
        host_et.setText(Singleton.hostsList.get(0).getIp());
        params_et.setText(params.get(cmd.get(0)));
    }

    private void                initXml() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.Coordonitor);
        spinner = (MaterialSpinner) findViewById(R.id.spinnerTypeScan);
        host_et = (EditText) findViewById(R.id.hostEditext);
        params_et = (EditText) findViewById(R.id.binParamsEditText);
        RV_host = (RecyclerView) findViewById(R.id.RV_host);
        Output = (TextView) findViewById(R.id.Output);
        Monitor = (TextView) findViewById(R.id.Monitor);
        findViewById(R.id.fab).setOnClickListener(onStartCmd());
    }

    private void                initSpinner() {
        spinner.setItems(cmd);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String typeScan) {
                params_et.setText(params.get(typeScan));
            }
        });
    }

    private void                initParams() {
        cmd.add("Ping scan");
        cmd.add("Quick scan");
        cmd.add("Quick scan plus");
        cmd.add("Quick traceroute");
        cmd.add("Regular scan");
        cmd.add("Intrusive scan");
        cmd.add("Intense Scan");
        cmd.add("Intense scan plus UDP");
        cmd.add("Intense scan, all TCP ports");
        cmd.add("Intense scan, no ping");
        params.put(cmd.get(0), " -sn");
        params.put(cmd.get(1), " -T4 -F");
        params.put(cmd.get(2), " -sV -T4 -O -F --version-light");
        params.put(cmd.get(3), " -sn --traceroute");
        params.put(cmd.get(4), " ");
        params.put(cmd.get(5), " -sS -sU -T4 -A -v -PE -PP -PS80,443 -PA3389 -PU40125 -PY -g 53 ");
        params.put(cmd.get(6), " -T4 -A -v");
        params.put(cmd.get(7), " -sS -sU -T4 -A -v");
        params.put(cmd.get(8), " -p 1-65535 -T4 -A -v");
        params.put(cmd.get(9), " -T4 -A -v -Pn");
    }

    private void                initRecyHost() {
        HostCheckBoxAdapter adapter = new HostCheckBoxAdapter(this, Singleton.hostsList);
        RV_host.setAdapter(adapter);
        RV_host.setHasFixedSize(true);
        RV_host.setLayoutManager(new LinearLayoutManager(mInstance));
        /*RV_host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/
    }

    public void                 newTarget(Host host) {
        actualTarget = host;
        host_et.setText(host.getIp());
    }

    private View.OnClickListener onStartCmd() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Output.setText("Wait...");
                final String cmd = globalVariable.path + "/nmap/nmap " + host_et.getText() + " " + params_et.getText() + " ";
                Monitor.setText(cmd);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BufferedReader reader = new BufferedReader(new RootProcess("Nmap", globalVariable.path)//Exec and > in BufferedReader
                                    .exec(cmd).exec("exit").getInputStreamReader());
                            String dumpOutput = "", tmp;
                            while ((tmp = reader.readLine()) != null && !tmp.contains("Nmap done")) {
                                dumpOutput += tmp + '\n';
                                Log.d(TAG, "output:In::" + dumpOutput);
                            }
                            dumpOutput += tmp;
                            final String finalDumpOutput = dumpOutput;
                            mInstance.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Output.setText(finalDumpOutput);
                                }
                            });
                            Log.d(TAG, "output:" + dumpOutput);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        };
    }

}
