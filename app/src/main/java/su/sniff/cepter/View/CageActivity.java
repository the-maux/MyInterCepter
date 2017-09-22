package su.sniff.cepter.View;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import su.sniff.cepter.Controller.System.Singleton;
import su.sniff.cepter.Controller.System.Wrapper.RootProcess;
import su.sniff.cepter.Controller.System.MyActivity;
import su.sniff.cepter.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class                CageActivity extends MyActivity {
    int                     run = 0;

    public void             onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cage);
        initThreads();
        ((TextView) findViewById(R.id.monitor)).setTextSize(2, (float) 12);
        Singleton.getInstance().lock = 0;
    }

    private void            initThreads() {
        if (Singleton.getInstance().lock == 0) {
            Singleton.getInstance().lock = 1;
        } else {
            while (Singleton.getInstance().lock == 1) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Singleton.getInstance().lock = 1;
        }
    }

    public void             OnRun(View v) {
        if (run == 0) {
            run = 1;
            File fDroidSheep = new File(Singleton.getInstance().FilesPath + "exitc.id");
            if (fDroidSheep.exists()) {
                fDroidSheep.delete();
            }
            ((Button) findViewById(R.id.button6)).setText("Stop");
            runCage();
            return;
        }
        ((Button) findViewById(R.id.button6)).setText("Run ARP Cage");
        run = 0;
        closeOnCage();
    }

    private void            runCage() {
        final TextView tv = (TextView) findViewById(R.id.monitor);
        new Thread(new Runnable() {
            public void run() {
                RootProcess process = new RootProcess("onCage", Singleton.getInstance().FilesPath);
                BufferedReader reader = new BufferedReader(process.getInputStreamReader());
                try {
                    process.exec(Singleton.getInstance().FilesPath + "cepter " +
                            Singleton.getInstance().nbrInteface +
                            " cage " + Singleton.getInstance().network.gateway)
                            .exec("exit");
                    while (true) {
                        final String read = reader.readLine();
                        if (read != null) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    tv.append(read + '\n');
                                }
                            });
                        } else {
                            reader.close();
                            process.waitFor();
                            return;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void            closeOnCage() {
        try {
            openFileOutput("exitc.id", 0).close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }
}
