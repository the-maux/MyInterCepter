package su.sniff.cepter.View;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;

import su.sniff.cepter.Controller.Singleton;
import su.sniff.cepter.Controller.System.RootProcess;
import su.sniff.cepter.Controller.System.ThreadUtils;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

import java.io.BufferedReader;
import java.io.IOException;

public class TsharkDetailsActivity extends Activity {

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adapter_dns);
        int position = getIntent().getExtras().getInt("Key_Int", 0);
        ThreadUtils.lock();
        TextView monitor = (TextView) findViewById(R.id.monitor);
        monitor.setTypeface(Typeface.MONOSPACE);
        monitor.setTextSize(2, (float) globalVariable.raw_textsize);
        StringBuilder text = new StringBuilder();
        try {
            RootProcess process = new RootProcess("TsharkDetailsActivity", Singleton.FilesPath);
            BufferedReader reader = new BufferedReader(process.getInputStreamReader());
            process.exec(Singleton.FilesPath + "/busybox cat " + Singleton.FilesPath + "/Raw/" + position + ".dat");
            process.closeDontWait();
            while (true) {
                String read = reader.readLine();
                if (read == null) {
                    break;
                }
                text.append(read).append('\n');
            }
            reader.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        monitor.setText(text.toString());
        globalVariable.lock = 0;
    }

    public boolean                  onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        finish();
        return true;
    }
}
