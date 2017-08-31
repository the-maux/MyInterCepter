package su.sniff.cepter.View;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class SettingsActivity extends MyActivity {
    private String TAG = "SettingsActivity";
    public String cmd;
    private Context mCtx;
    public String orig_str;
    public Process sniff_process = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.cmd = getIntent().getExtras().getString("Key_String");
        this.orig_str = getIntent().getExtras().getString("Key_String_origin");
        ((TextView) findViewById(R.id.monitor)).setText("Home dir: " + getFilesDir().toString());
        this.mCtx = this;
        CheckBox c1 = (CheckBox) findViewById(R.id.SSLStrip_CB);
        if (globalVariable.savepcap == 1) {
            c1.setChecked(true);
        }
        c1 = (CheckBox) findViewById(R.id.checkBox2);
        if (globalVariable.raw_autoscroll == 1) {
            c1.setChecked(true);
        }
        c1 = (CheckBox) findViewById(R.id.ForcedDL_CB);
        if (globalVariable.screenlock == 1) {
            c1.setChecked(true);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter(this, 17367048, new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15"});
        adapter.setDropDownViewResource(17367049);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner3);
        spinner.setAdapter(adapter);
        try {
            InputStreamReader isr = new InputStreamReader(openFileInput("settings"));
            String line = new BufferedReader(isr).readLine();
            isr.close();
            globalVariable.raw_textsize = Integer.parseInt(line);
            spinner.setSelection(globalVariable.raw_textsize - 1);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                globalVariable.raw_textsize = Integer.parseInt(spinner.getSelectedItem().toString());
                SettingsActivity.this.SaveSettings();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinner.setSelection(globalVariable.raw_textsize - 1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter(this, 17367048, new String[]{"sdcard1", "sdcard2", "int memory"});
        adapter2.setDropDownViewResource(17367049);
        final Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setAdapter(adapter2);
        spinner2.setOnItemSelectedListener(new OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String temp = spinner2.getSelectedItem().toString();
                try {
                    FileOutputStream out = SettingsActivity.this.openFileOutput("savepath", 0);
                    String path;
                    if (temp.indexOf("sdcard1") != -1) {
                        path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        out.write(path.getBytes());
                        Toast.makeText(SettingsActivity.this.getBaseContext(), "Save pcap to " + path, 0).show();
                        globalVariable.PCAP_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
                    } else if (temp.indexOf("sdcard2") != -1) {
                        path = Environment.getExternalStorageDirectory().getAbsolutePath();
                        String path2 = path.substring(0, path.length() - 1) + "1";
                        out.write(path2.getBytes());
                        Toast.makeText(SettingsActivity.this.getBaseContext(), "Save pcap to " + path2, 0).show();
                        globalVariable.PCAP_PATH = path2;
                    } else {
                        out.write(".".getBytes());
                        Toast.makeText(SettingsActivity.this.getBaseContext(), "Save pcap to internal memory", 0).show();
                        globalVariable.PCAP_PATH = SettingsActivity.this.getApplicationInfo().dataDir;
                    }
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void SaveSettings() {
        try {
            FileOutputStream out = openFileOutput("settings", 0);
            out.write(Integer.toString(globalVariable.raw_textsize).getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public void OnSave(View v) {
        if (globalVariable.savepcap == 0) {
            globalVariable.savepcap = 1;
        } else {
            globalVariable.savepcap = 0;
        }
    }

    public void OnShowHTTP(View v) {
        if (globalVariable.showhttp == 0) {
            globalVariable.showhttp = 1;
        } else {
            globalVariable.showhttp = 0;
        }
    }

    public void OnRawScroll(View v) {
        if (globalVariable.raw_autoscroll == 0) {
            globalVariable.raw_autoscroll = 1;
        } else {
            globalVariable.raw_autoscroll = 0;
        }
    }

    public void OnLock(View v) {
        if (globalVariable.screenlock == 0) {
            globalVariable.screenlock = 1;
            globalVariable.parent.getWindow().addFlags(128);
            Toast.makeText(getBaseContext(), "Locked", 0).show();
            return;
        }
        globalVariable.screenlock = 0;
        globalVariable.parent.getWindow().clearFlags(128);
        Toast.makeText(getBaseContext(), "Unlocked", 0).show();
    }

    public void OnLogo(View v) {
        Toast.makeText(getBaseContext(), "API " + VERSION.SDK_INT + ":" + Build.CPU_ABI + "\nhttp://sniff.su", 1).show();
    }

    public void OnClearPcap(View v) throws IOException, InterruptedException {
        Process p = Runtime.getRuntime().exec("su");
        DataOutputStream os = new DataOutputStream(p.getOutputStream());
        Log.d(TAG, "LD_LIBRARY_PATH=" + Singleton.getInstance().FilesPath + " " + Singleton.getInstance().FilesPath + "/busybox rm " + Singleton.getInstance().FilesPath + "/*.pcap");
        os.writeBytes("LD_LIBRARY_PATH=" + Singleton.getInstance().FilesPath + " " + Singleton.getInstance().FilesPath + "/busybox rm " + Singleton.getInstance().FilesPath + "/*.pcap\n");
        os.flush();
        os.close();
        p.waitFor();
        Toast.makeText(getBaseContext(), "Captures cleared from homedir", 0).show();
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
