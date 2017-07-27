package su.sniff.cepter.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import su.sniff.cepter.BuildConfig;
import su.sniff.cepter.Controller.CepterControl.onDefend;
import su.sniff.cepter.Controller.CepterControl.onInterceptRun;
import su.sniff.cepter.Controller.FilesUtils.InterceptorFileSelected;
import su.sniff.cepter.Controller.RootProcess;
import su.sniff.cepter.R;
import su.sniff.cepter.Utils.OpenFileDialog;
import su.sniff.cepter.Utils.SaveFileDialog;
import su.sniff.cepter.Utils.SaveFileDialog.OnNewFileSelectedListener;
import su.sniff.cepter.globalVariable;

public class                    MainActivity extends Activity {
    private MainActivity        mInstance = this;
    private String              TAG = "MainActivity";
    private static TextView     monitorIntercepter;
    private String              gateway;
    private String              cmd2;
    public RootProcess          sniff_process = null;

    protected void              onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(3);
        setContentView(R.layout.activity_main);
        getWindow().setFeatureDrawableResource(3, R.drawable.ico);
        monitorIntercepter = (TextView) findViewById(R.id.primaryMonitor);
        initAdapters();
        gateway = getIntent().getExtras().getString("Key_String");
        Log.d(TAG, "this.gateway:" + gateway);
        this.cmd2 = getIntent().getExtras().getString("Key_String_origin");
        Log.d(TAG, "this.cmd2:" + this.cmd2);
        monitorIntercepter.setTypeface(Typeface.MONOSPACE);
    }

    private void                initAdapters() {
        globalVariable.adapter = new ArrayAdapter<String>(this, R.layout.raw_list2, R.id.label, globalVariable.cookies_domain) {
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(R.layout.raw_list2, parent, false);
                }
                TextView tt = (TextView) convertView.findViewById(R.id.label);
                Log.d(TAG, "initAdapters::adapter1::Item(" + position + "):" +  getItem(position));
                tt.setText(Html.fromHtml((String) getItem(position)));
                tt.setTextSize(2, (float) globalVariable.raw_textsize);
                tt.setTypeface(Typeface.MONOSPACE);
                return convertView;
            }
        };
        Log.d(TAG, "");
        globalVariable.adapter2 = new ArrayAdapter<String>(this, R.layout.raw_list2, R.id.label, globalVariable.cookies_domain2) {
            public View getView(int position, View convertView, @NonNull ViewGroup parent) {
                if (convertView == null) {
                    convertView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                            .inflate(R.layout.raw_list2, parent, false);
                }
                Log.d(TAG, "initAdapters::adapter2::Item(" + position + "):" +  getItem(position));
                TextView tt = (TextView) convertView.findViewById(R.id.label);
                tt.setText(Html.fromHtml( getItem(position)));
                tt.setTextSize(2, (float) globalVariable.raw_textsize);
                tt.setTypeface(Typeface.MONOSPACE);
                return convertView;
            }
        };
    }

    public void                 onInterceptorRunClick(View v) throws IOException {
        RootProcess process = new onInterceptRun(this, sniff_process, ((ImageView) findViewById(R.id.runIcon)),
                monitorIntercepter).run(gateway);
        if (process != null)
            sniff_process = process;
    }

    public void                 OnCLS(View v) {
        monitorIntercepter.setText(BuildConfig.FLAVOR);
    }

    public void                 OnBack(View v) throws IOException {
        if (this.sniff_process != null) {
            openFileOutput("exitr.id", 0).close();
            this.sniff_process = null;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Intent i = new Intent(mInstance, ScanActivity.class);
        i.putExtra("Key_String", this.cmd2);
        startActivity(i);
        finish();
    }

    public void                 OnSave(View v) {
        new SaveFileDialog(this, Environment.getExternalStorageDirectory().getAbsolutePath(), new String[]{".txt"},
                getFileSelectedListerner()).show();
    }

    public void                 OnOpenCap(View v) {
        new OpenFileDialog(this, globalVariable.PCAP_PATH, new String[]{".pcap"}
                , new InterceptorFileSelected(this, monitorIntercepter)).show();
    }

    public void                 OnDefend(View v) throws IOException, InterruptedException {
       new onDefend(monitorIntercepter);
    }

    public boolean              onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        try {
            openFileOutput("exits.id", 0).close();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "at the end");
        Intent i = new Intent(mInstance, ScanActivity.class);
        i.putExtra("Key_String", this.cmd2);
        startActivity(i);
        finish();
        return false;
    }

    private                     OnNewFileSelectedListener getFileSelectedListerner() {
        return new OnNewFileSelectedListener() {

            @Override
            public void onNewFileSelected(File file) {
                String buf = monitorIntercepter.getText().toString();
                try {
                    FileWriter writer = new FileWriter(file);
                    writer.append(buf);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
