package su.sniff.cepter.View;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class CageActivity extends Activity {
    int run = 0;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cage);
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
        ((TextView) findViewById(R.id.monitor)).setTextSize(2, (float) (globalVariable.raw_textsize + 3));
        globalVariable.lock = 0;
    }

    public void OnRun(View v) {
        if (this.run == 0) {
            this.run = 1;
            final TextView tv = (TextView) findViewById(R.id.monitor);
            File fDroidSheep = new File(globalVariable.path + "/exitc.id");
            if (fDroidSheep.exists()) {
                fDroidSheep.delete();
            }
            ((Button) findViewById(R.id.button6)).setText("Stop");
            new Thread(new Runnable() {
                public void run() {
                    IOException e;
                    InterruptedException e2;
                    try {
                        Process process2 = Runtime.getRuntime().exec("su", null, new File(globalVariable.path + ""));
                        DataOutputStream os = new DataOutputStream(process2.getOutputStream());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process2.getInputStream()));
                        BufferedReader bufferedReader;
                        try {
                            os.writeBytes(globalVariable.path + "/cepter " + Integer.toString(globalVariable.adapt_num) + " cage " + globalVariable.gw_ip + "\n");
                            os.flush();
                            os.writeBytes("exit\n");
                            os.flush();
                            os.close();
                            StringBuffer output = new StringBuffer();
                            while (true) {
                                String read = reader.readLine();
                                if (read != null) {
                                    final String read2 = read;
                                    CageActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            tv.append(read2);
                                            tv.append("\n");
                                        }
                                    });
                                } else {
                                    reader.close();
                                    process2.waitFor();
                                    bufferedReader = reader;
                                    return;
                                }
                            }
                        } catch (IOException e3) {
                            e = e3;
                            bufferedReader = reader;
                            e.printStackTrace();
                        } catch (InterruptedException e4) {
                            e2 = e4;
                            bufferedReader = reader;
                            e2.printStackTrace();
                        }
                    } catch (IOException e5) {
                        e = e5;
                        e.printStackTrace();
                    }
                }
            }).start();
            return;
        }
        ((Button) findViewById(R.id.button6)).setText("Run ARP Cage");
        this.run = 0;
        try {
            openFileOutput("exitc.id", 0).close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        try {
            openFileOutput("exitc.id", 0).close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        finish();
        return true;
    }
}
