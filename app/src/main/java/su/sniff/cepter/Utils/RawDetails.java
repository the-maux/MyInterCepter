package su.sniff.cepter.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.TextView;

import su.sniff.cepter.Controller.RootProcess;
import su.sniff.cepter.Misc.ThreadUtils;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class                    RawDetails extends Activity {

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raw_details);
        int position = getIntent().getExtras().getInt("Key_Int", 0);
        ThreadUtils.lock();
        TextView monitor = (TextView) findViewById(R.id.monitor);
        monitor.setTypeface(Typeface.MONOSPACE);
        monitor.setTextSize(2, (float) globalVariable.raw_textsize);
        StringBuilder text = new StringBuilder();
        try {
            RootProcess process = new RootProcess("RawDetails", globalVariable.path + "");
            BufferedReader reader = new BufferedReader(process.getInputStreamReader());
            process.exec(globalVariable.path + "/busybox cat " + globalVariable.path + "/Raw/" + position + ".dat");
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
