package su.sniff.cepter.Controller;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import com.github.clans.fab.FloatingActionButton;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

import java.io.BufferedReader;
import java.io.IOException;

public class                        PortScan extends Activity {
    private String                  TAG = "PortScan";
    private PortScan                mInstance = this;
    private FloatingActionButton    FAB;
    private TextView                tv;

    public void                     onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.portscan);
        initThread();
        initXml();
        progressAnimation();
        scanPort(getIntent().getExtras().getString("Key_Int")).start();
        globalVariable.lock = 0;
    }

    private void                    initXml() {
        FAB = (FloatingActionButton) findViewById(R.id.fab);
        tv = (TextView) findViewById(R.id.textView1);
        tv.setTextSize(2, (float) (globalVariable.raw_textsize + 3));
    }

    private void                    initThread() {
        if (globalVariable.lock == 0) {
            Log.d(TAG, "initThread::globalVariable.lock:0");
            globalVariable.lock = 1;
        } else {
            Log.d(TAG, "initThread::WhileLockThread");
            while (globalVariable.lock == 1) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "initThread::LockThreadOver");
            globalVariable.lock = 1;
        }
    }

    private Thread                  scanPort(final String target) {
        return new Thread(new Runnable() {
            public void run() {
                RootProcess process = new RootProcess("ScanPort", globalVariable.path + "");
                process.exec(globalVariable.path + "/cepter " + Integer.toString(globalVariable.adapt_num) + " -ps " + target)
                        .exec("exit");
                BufferedReader reader = new BufferedReader(process.getInputStreamReader());
                try {
                    while (true) {
                        final String read = reader.readLine();
                        Log.d(TAG, "Reader:" + read);
                        if (read != null) {
                            mInstance.runOnUiThread(new Runnable() {
                                public void run() {
                                    tv.append(read + "\n");
                                }
                            });
                        } else {
                            Log.d(TAG, "ScanPort ./Cepter nothing to read");
                            reader.close();
                            process.waitFor();
                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void                    progressAnimation() {
        FAB.setProgress(0, false);
        FAB.setMax(18000);
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
                    mInstance.runOnUiThread(new Runnable() {
                        public void run() {
                            FAB.setProgress(prog2, true);
                        }
                    });
                }
            }
        }).start();
    }

    public boolean                  onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        finish();
        return true;
    }
}

    /*private Thread          scanPort(final String target) {
        return new Thread(new Runnable() {
            public void run() {
                BufferedReader bufferedReader;
                IOException e;
                InterruptedException e2;
                try {
                    Process process2 = Runtime.getRuntime().exec("su", null, new File(globalVariable.path + ""));
                    DataOutputStream os = new DataOutputStream(process2.getOutputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(process2.getInputStream()));
                    try {
                        os.writeBytes(globalVariable.path + "/cepter " + Integer.toString(globalVariable.adapt_num) + " -ps " + target + "\n");
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
        });
    }*/