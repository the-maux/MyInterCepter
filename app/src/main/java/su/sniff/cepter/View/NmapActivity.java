package su.sniff.cepter.View;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import su.sniff.cepter.Controller.Core.Singleton;
import su.sniff.cepter.Controller.Core.BinaryWrapper.RootProcess;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.Controller.Core.MyActivity;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.HostSelectionAdapter;
import su.sniff.cepter.View.Dialog.RV_dialog;

/**
 * Created by maxim on 03/08/2017.
 */
public class                    NmapActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private NmapActivity        mInstance = this;
    private Singleton           singleton = Singleton.getInstance();
    private CoordinatorLayout   coordinatorLayout;
    private MaterialSpinner     spinner;
    private Map<String, String> params =  new HashMap<>();
    private ArrayList<String>   cmd = new ArrayList<>();
    private RelativeLayout      nmapConfEditorLayout;
    private TextView            host_et, params_et, Output, Monitor, targetMonitor;
    private RelativeLayout      RL_host;
    private Host                actualTarget = null;
    private List<Host>          listHostSelected = new ArrayList<>();
    private ImageView           settingsMenu;
    private ImageButton         settings;
    
    @Override protected void    onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmap);
        initParams();
        initXml();
        initSpinner();
        initRecyHost();
        if (singleton.hostsList == null) {
            targetMonitor.setText("no target");
        } else {
            host_et.setText(singleton.hostsList.get(0).getIp());
            params_et.setText(params.get(cmd.get(0)));
            targetMonitor.setText(listHostSelected.size() + " target");
        }
    }

    private void                initXml() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.Coordonitor);
        spinner = (MaterialSpinner) findViewById(R.id.spinnerTypeScan);
        host_et = (EditText) findViewById(R.id.hostEditext);
        params_et = (EditText) findViewById(R.id.binParamsEditText);
        RL_host = (RelativeLayout) findViewById(R.id.RL_host);
        Output = (TextView) findViewById(R.id.Output);
        Output.setMovementMethod(new ScrollingMovementMethod());
        Monitor = (TextView) findViewById(R.id.Monitor);
        settingsMenu = (ImageView) findViewById(R.id.settingsMenu);
        targetMonitor = (TextView) findViewById(R.id.targetMonitor);
        settings = (ImageButton) findViewById(R.id.settings);
        nmapConfEditorLayout = (RelativeLayout) findViewById(R.id.nmapConfEditorLayout);
        findViewById(R.id.fab).setOnClickListener(onStartCmd());
        settings.setOnClickListener(onSwitchHeader());
        params_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    onStartCmd();
                    return true;
                }
                return false;
            }
        });
    }

    private View.OnClickListener onSwitchHeader() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nmapConfEditorLayout.setVisibility(
                        (nmapConfEditorLayout.getVisibility() == View.VISIBLE) ?
                                View.GONE : View.VISIBLE);
                if (nmapConfEditorLayout.getVisibility() == View.GONE) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }
            }
        };
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
        RL_host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new RV_dialog(mInstance)
                        .setAdapter(new HostSelectionAdapter(mInstance, singleton.hostsList, listHostSelected))
                        .setTitle("Choix des cibles")
                        .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (listHostSelected.isEmpty())
                                    Snackbar.make(coordinatorLayout, "No target selected", Snackbar.LENGTH_LONG).show();
                                else {
                                    newTarget(listHostSelected.get(0));
                                    targetMonitor.setText(listHostSelected.size() + " target");
                                    Snackbar.make(coordinatorLayout, listHostSelected.size() + " target", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        }).show();
            }
        });

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
                final String cmd = singleton.FilesPath + "nmap/nmap " + host_et.getText() + " " + params_et.getText() + " ";
                Monitor.setVisibility(View.VISIBLE);
                Monitor.setText(cmd.replace("\n", "").replace(singleton.FilesPath, ""));
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BufferedReader reader = new BufferedReader(new RootProcess("Nmap", singleton.FilesPath)//Exec and > in BufferedReader
                                    .exec(cmd).getInputStreamReader());
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
                                    Monitor.setVisibility(View.GONE);
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        };
    }

}
