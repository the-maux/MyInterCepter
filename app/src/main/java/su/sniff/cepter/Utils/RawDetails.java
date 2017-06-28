package su.sniff.cepter.Utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.TextView;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class RawDetails extends Activity {
    Context mCtx;
    public ListView tvList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.raw_details);
        int position = getIntent().getExtras().getInt("Key_Int", 0);
        if (globalVariable.lock == 0) {
            globalVariable.lock = 1;
        } else {
            while (globalVariable.lock == 1) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            globalVariable.lock = 1;
        }
        TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setTypeface(Typeface.MONOSPACE);
        tv.setTextSize(2, (float) globalVariable.raw_textsize);
        StringBuilder text = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec("su", null, new File("/data/data/su.sniff.cepter/files"));
            DataOutputStream os = new DataOutputStream(process.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            os.writeBytes("/data/data/su.sniff.cepter/files/busybox cat /data/data/su.sniff.cepter/files/Raw/" + position + ".dat\n");
            os.flush();
            os.writeBytes("exit\n");
            os.flush();
            os.close();
            while (true) {
                String read = reader.readLine();
                if (read == null) {
                    break;
                }
                text.append(read);
                text.append("\n");
            }
            reader.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        tv.setText(text.toString());
        globalVariable.lock = 0;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        finish();
        return true;
    }
}
