package su.sniff.cepter.Controller;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class PortScan extends Activity {
    Context mCtx;
    public ListView tvList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portscan);
        String ip = getIntent().getExtras().getString("Key_Int");
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
        final TextView tv = (TextView) findViewById(R.id.textView1);
        tv.setTextSize(2, (float) (globalVariable.raw_textsize + 3));
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        progressBar.setProgress(0);
        progressBar.setMax(18000);
        final String target = ip;
        new Thread(new Runnable() {
            public void run() {
                int progress = 0;
                while (progress <= 18000) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    progress += 10;
                    final int prog2 = progress;
                    PortScan.this.runOnUiThread(new Runnable() {
                        public void run() {
                            progressBar.setProgress(prog2);
                        }
                    });
                }
            }
        }).start();
        new Thread(new Runnable() {
            public void run() {
                BufferedReader bufferedReader;
                IOException e;
                InterruptedException e2;
                try {
                    Process process2 = Runtime.getRuntime().exec("su", null, new File("/data/data/su.sniff.cepter/files"));
                    DataOutputStream os = new DataOutputStream(process2.getOutputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process2.getInputStream()));
                    try {
                        os.writeBytes("/data/data/su.sniff.cepter/files/cepter " + Integer.toString(globalVariable.adapt_num) + " -ps " + target + "\n");
                        os.flush();
                        os.writeBytes("exit\n");
                        os.flush();
                        os.close();
                        InputStreamReader inputStreamReader = new InputStreamReader(process2.getInputStream());
                        StringBuffer output = new StringBuffer();
                        while (true) {
                            String read = reader.readLine();
                            if (read != null) {
                                final String read2 = read;
                                PortScan.this.runOnUiThread(new Runnable() {
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
