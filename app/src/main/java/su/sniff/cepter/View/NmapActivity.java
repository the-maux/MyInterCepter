package su.sniff.cepter.View;

import android.app.Activity;
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
import android.view.inputmethod.InputMethodManager;
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

import su.sniff.cepter.Controller.Core.Conf.Singleton;
import su.sniff.cepter.Controller.Core.BinaryWrapper.RootProcess;
import su.sniff.cepter.Model.Target.Host;
import su.sniff.cepter.Controller.Misc.MyActivity;
import su.sniff.cepter.R;
import su.sniff.cepter.View.Adapter.HostSelectionAdapter;
import su.sniff.cepter.View.Dialog.RV_dialog;

/**
 * Created by maxim on 03/08/2017.
 */
public class                    NmapActivity extends MyActivity {
    private String              TAG = this.getClass().getName();
    private NmapActivity        mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    private MaterialSpinner     mSpinner;
    private Map<String, String> mParams =  new HashMap<>();
    private ArrayList<String>   mCmd = new ArrayList<>();
    private RelativeLayout      mNmapConfEditorLayout;
    private TextView            host_et, params_et, Output, Monitor, targetMonitor;
    private RelativeLayout      mRL_host;
    private Host mActualTarget = null;
    private List<Host>          mListHostSelected = new ArrayList<>();
    private ImageView           settingsMenu;
    private ImageButton         settings;
    
    @Override protected void    onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmap);
        initParams();
        initXml();
        initSpinner();
        initRecyHost();
        if (mSingleton.hostsList == null) {
            targetMonitor.setText("no target");
        } else {
            host_et.setText(mSingleton.hostsList.get(0).getIp());
            params_et.setText(mParams.get(mCmd.get(0)));
            targetMonitor.setText(mListHostSelected.size() + " target");
        }
    }

    private void                initXml() {
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.Coordonitor);
        mSpinner = (MaterialSpinner) findViewById(R.id.spinnerTypeScan);
        host_et = (EditText) findViewById(R.id.hostEditext);
        params_et = (EditText) findViewById(R.id.binParamsEditText);
        mRL_host = (RelativeLayout) findViewById(R.id.RL_host);
        Output = (TextView) findViewById(R.id.Output);
        Output.setMovementMethod(new ScrollingMovementMethod());
        Monitor = (TextView) findViewById(R.id.Monitor);
        settingsMenu = (ImageView) findViewById(R.id.settingsMenu);
        targetMonitor = (TextView) findViewById(R.id.targetMonitor);
        settings = (ImageButton) findViewById(R.id.settings);
        mNmapConfEditorLayout = (RelativeLayout) findViewById(R.id.nmapConfEditorLayout);
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execNmap();
            }
        });
        settings.setOnClickListener(onSwitchHeader());
        params_et.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.d(TAG, "onEditorAction : " + actionId + " event:" + event);
                if (actionId == EditorInfo.IME_ACTION_DONE
                        /*|| event.getAction() == KeyEvent.ACTION_DOWN*/) {
                    execNmap();
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
                mNmapConfEditorLayout.setVisibility(
                        (mNmapConfEditorLayout.getVisibility() == View.VISIBLE) ?
                                View.GONE : View.VISIBLE);
                if (mNmapConfEditorLayout.getVisibility() == View.GONE) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                }
            }
        };
    }

    private void                initSpinner() {
        mSpinner.setItems(mCmd);
        mSpinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String typeScan) {
                params_et.setText(mParams.get(typeScan));
            }
        });
    }

    private void                initParams() {
        mCmd.add("Ping scan");
        mCmd.add("Quick scan");
        mCmd.add("Quick scan plus");
        mCmd.add("Quick traceroute");
        mCmd.add("Regular scan");
        mCmd.add("Intrusive scan");
        mCmd.add("Intense Scan");
        mCmd.add("Intense scan plus UDP");
        mCmd.add("Intense scan, all TCP ports");
        mCmd.add("Intense scan, no ping");
        mParams.put(mCmd.get(0), " -sn");
        mParams.put(mCmd.get(1), " -T4 -F");
        mParams.put(mCmd.get(2), " -sV -T4 -O -F --version-light");
        mParams.put(mCmd.get(3), " -sn --traceroute");
        mParams.put(mCmd.get(4), " ");
        mParams.put(mCmd.get(5), " -sS -sU -T4 -A -v -PE -PP -PS80,443 -PA3389 -PU40125 -PY -g 53 ");
        mParams.put(mCmd.get(6), " -T4 -A -v");
        mParams.put(mCmd.get(7), " -sS -sU -T4 -A -v");
        mParams.put(mCmd.get(8), " -p 1-65535 -T4 -A -v");
        mParams.put(mCmd.get(9), " -T4 -A -v -Pn");
    }

    private void                initRecyHost() {
        if (mSingleton.hostsList == null || mSingleton.hostsList.isEmpty()) {
            Snackbar.make(mCoordinatorLayout, "Vous n'avez pas de targets selectionne", Snackbar.LENGTH_SHORT).show();
        } else  {
                mRL_host.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new RV_dialog(mInstance)
                                .setAdapter(new HostSelectionAdapter(mInstance, mSingleton.hostsList, mListHostSelected))
                                .setTitle("Choix des cibles")
                                .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if (mListHostSelected.isEmpty())
                                            Snackbar.make(mCoordinatorLayout, "No target selected", Snackbar.LENGTH_LONG).show();
                                        else {
                                            newTarget(mListHostSelected.get(0));
                                            targetMonitor.setText(mListHostSelected.size() + " target");
                                            Snackbar.make(mCoordinatorLayout, mListHostSelected.size() + " target", Snackbar.LENGTH_LONG).show();
                                        }
                                    }
                                }).show();
                    }
                });
            }
        }

    public void                 newTarget(Host host) {
        mActualTarget = host;
        host_et.setText(host.getIp());
    }

    private void                execNmap() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        Output.setText("Wait...");
        final String cmd = mSingleton.FilesPath + "nmap/nmap " + host_et.getText() + " " + params_et.getText() + " ";
        Monitor.setVisibility(View.VISIBLE);
        Monitor.setText(cmd.replace("nmap/nmap","nmap").replace("\n", "").replace(mSingleton.FilesPath, ""));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader reader = new BufferedReader(new RootProcess("Nmap", mSingleton.FilesPath)//Exec and > in BufferedReader
                            .exec(cmd).getInputStreamReader());
                    String dumpOutput = "", tmp;
                    while ((tmp = reader.readLine()) != null && !tmp.contains("Nmap done")) {
                        dumpOutput += tmp + '\n';
                    }
                    dumpOutput += tmp;
                    Log.d(TAG, "Nmap final stdouT" + dumpOutput);
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
}
