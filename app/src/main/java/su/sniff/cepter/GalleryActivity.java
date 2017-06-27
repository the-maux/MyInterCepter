package su.sniff.cepter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import su.sniff.cepter.OpenFileDialog.OnFileSelectedListener;

public class GalleryActivity extends Activity {
    private Context mCtx;

    class C01251 implements OnFileSelectedListener {
        C01251() {
        }

        public void onFileSelected(File f) {
            Toast.makeText(GalleryActivity.this.getBaseContext(), f.getAbsolutePath(), 0).show();
            ((CheckBox) GalleryActivity.this.findViewById(R.id.checkBox3)).setChecked(true);
            globalVariable.strip = 1;
            ((CheckBox) GalleryActivity.this.findViewById(R.id.checkBox1)).setChecked(true);
            try {
                FileOutputStream out = GalleryActivity.this.openFileOutput("force", 0);
                out.write(f.getAbsolutePath().getBytes());
                out.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);
        this.mCtx = this;
        CheckBox c1 = (CheckBox) findViewById(R.id.checkBox1);
        if (globalVariable.strip == 1) {
            c1.setChecked(true);
        }
        c1.setVisibility(4);
        ((CheckBox) findViewById(R.id.checkBox2)).setVisibility(4);
        ((CheckBox) findViewById(R.id.checkBox3)).setVisibility(4);
        ((Button) findViewById(R.id.button1)).setVisibility(4);
        ((Button) findViewById(R.id.button5)).setVisibility(4);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void OnForce(View v) {
        CheckBox c1 = (CheckBox) findViewById(R.id.checkBox3);
        if (c1.isChecked()) {
            c1.setChecked(false);
            new OpenFileDialog(this, globalVariable.PCAP_PATH, new String[]{BuildConfig.FLAVOR}, new C01251()).show();
            return;
        }
        File ck = new File("/data/data/su.sniff.cepter/files/force");
        if (ck.exists()) {
            ck.delete();
        }
    }

    public void OnCookieKiller(View v) {
        if (globalVariable.killer == 0) {
            globalVariable.killer = 1;
            globalVariable.strip = 1;
            ((CheckBox) findViewById(R.id.checkBox1)).setChecked(true);
            try {
                openFileOutput("ck", 0).close();
                return;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return;
            } catch (IOException e2) {
                e2.printStackTrace();
                return;
            }
        }
        globalVariable.killer = 0;
        File ck = new File("/data/data/su.sniff.cepter/files/ck");
        if (ck.exists()) {
            ck.delete();
        }
    }

    public void OnStrip(View v) {
        if (globalVariable.strip == 0) {
            globalVariable.strip = 1;
        } else {
            globalVariable.strip = 0;
        }
    }

    public void OnExpert(View v) {
        ((CheckBox) findViewById(R.id.checkBox2)).setVisibility(0);
        ((CheckBox) findViewById(R.id.checkBox1)).setVisibility(0);
        ((CheckBox) findViewById(R.id.checkBox3)).setVisibility(0);
        ((Button) findViewById(R.id.button1)).setVisibility(0);
        ((Button) findViewById(R.id.button5)).setVisibility(0);
        ((CheckBox) findViewById(R.id.checkBox5)).setVisibility(4);
    }

    public void OnDNS(View v) {
        startActivityForResult(new Intent(this.mCtx, DNSSpoofing.class), 1);
    }

    public void OnInject(View v) {
        startActivityForResult(new Intent(this.mCtx, InjectActivity.class), 1);
        globalVariable.strip = 1;
        ((CheckBox) findViewById(R.id.checkBox1)).setChecked(true);
    }
}
