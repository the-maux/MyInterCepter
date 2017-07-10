package su.sniff.cepter.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import su.sniff.cepter.*;
import su.sniff.cepter.Controller.DNSSpoofingActivity;
import su.sniff.cepter.Utils.OpenFileDialog;
import su.sniff.cepter.Utils.OpenFileDialog.OnFileSelectedListener;

public class                ExpertSettingsActivity extends Activity {
    private Activity        mInstance = this;
    private String          TAG = "ExpertSettingsActivity";

    public void             onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);
        initXml();
    }

    private void            initXml() {
        if (globalVariable.strip == 1) {
            ((CheckBox) findViewById(R.id.SSLStrip_CB)).setChecked(true);
        }
    }

    public void             OnForce(View v) {
        CheckBox ForceDL_checkBox = (CheckBox) findViewById(R.id.ForcedDL_CB);
        if (ForceDL_checkBox.isChecked()) {
            ForceDL_checkBox.setChecked(false);
            new OpenFileDialog(this, globalVariable.PCAP_PATH, new String[]{BuildConfig.FLAVOR},
                    new OnFileSelectedListener() {
                        @Override
                        public void onFileSelected(File file) {
                            Toast.makeText(mInstance, file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                            ((CheckBox) findViewById(R.id.ForcedDL_CB)).setChecked(true);
                            globalVariable.strip = 1;
                            ((CheckBox) findViewById(R.id.SSLStrip_CB)).setChecked(true);
                            try {
                                FileOutputStream out = openFileOutput("force", 0);
                                out.write(file.getAbsolutePath().getBytes());
                                out.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        }
                    }).show();
            return;
        }
        File ck = new File(globalVariable.path + "/force");
        if (ck.exists()) {
            ck.delete();
        }
    }

    public void             OnCookieKiller(View v) {
        if (globalVariable.CookieKillerOption == 0) {
            globalVariable.CookieKillerOption = 1;
            Log.d(TAG, "KillerSwitchOption" + globalVariable.CookieKillerOption);
            globalVariable.strip = 1;
            ((CheckBox) findViewById(R.id.SSLStrip_CB)).setChecked(true);
            try {
                openFileOutput("ck", 0).close();
            }  catch (IOException e2) {
                e2.printStackTrace();
            }
        } else {
            globalVariable.CookieKillerOption = 0;
            Log.d(TAG, "KillerSwitchOption" + globalVariable.CookieKillerOption);
            File ck = new File(globalVariable.path + "/ck");
            if (ck.exists()) {
                ck.delete();
            }
        }
    }

    public void             OnStrip(View v) {
        if (globalVariable.strip == 0) {
            globalVariable.strip = 1;
        } else {
            globalVariable.strip = 0;
        }
    }

    public void             OnDNS(View v) {
        startActivityForResult(new Intent(mInstance, DNSSpoofingActivity.class), 1);
    }

    public void             OnInject(View v) {
        globalVariable.strip = 1;
        ((CheckBox) findViewById(R.id.SSLStrip_CB)).setChecked(true);
        startActivityForResult(new Intent(mInstance, InjectHTTPActivity.class), 1);
    }

    public boolean          onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
