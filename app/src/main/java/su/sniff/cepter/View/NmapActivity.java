package su.sniff.cepter.View;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.TextViewCompat;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import su.sniff.cepter.R;

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
    private TextView            host_et, params_et;

    @Override protected void    onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmap);
        initParams();
        initXml();
        initSpinner();
    }

    private void                initXml() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.Coordonitor);
        spinner = (MaterialSpinner) findViewById(R.id.spinnerTypeScan);
        host_et = (EditText) findViewById(R.id.hostEditext);
        params_et = (EditText) findViewById(R.id.binParamsEditText);
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
        cmd.add("Intense Scan");
        cmd.add("Intense scan plus UDP");
        cmd.add("Intense scan, all TCP ports");
        cmd.add("Intense scan, no ping");
        cmd.add("Ping scan");
        cmd.add("Quick scan");
        cmd.add("Quick scan plus");
        cmd.add("Quick traceroute");
        cmd.add("Regular scan");
        cmd.add("Intrusive scan");
        params.put(cmd.get(0), " -T4 -A -v");
        params.put(cmd.get(1), " -sS -sU -T4 -A -v");
        params.put(cmd.get(2), " -p 1-65535 -T4 -A -v");
        params.put(cmd.get(3), " -T4 -A -v -Pn");
        params.put(cmd.get(4), " -sn");
        params.put(cmd.get(5), " -T4 -F");
        params.put(cmd.get(6), " -sV -T4 -O -F --version-light");
        params.put(cmd.get(7), " -sn --traceroute");
        params.put(cmd.get(8), "");
        params.put(cmd.get(9), " -sS -sU -T4 -A -v -PE -PP -PS80,443 -PA3389 -PU40125 -PY -g 53 ");
    }

}
