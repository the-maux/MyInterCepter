package su.sniff.cepter.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import su.sniff.cepter.Controller.RootProcess;
import su.sniff.cepter.Misc.IntercepterReader;
import su.sniff.cepter.R;
import su.sniff.cepter.Utils.RawDetails;
import su.sniff.cepter.adapter.ProtocolAdapter;
import su.sniff.cepter.globalVariable;

public class                    TsharkActivity extends Activity {
    private String              TAG = "TsharkActivity";
    private TsharkActivity      mInstance = this;
    private String              cmd, orig_str;
    private Thread              readCepterRawThread = null;
    private ListView            tvList;
    private ProtocolAdapter     adapter;
    private boolean             isAlreadyLaunched = false;

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(3);
        setContentView(R.layout.tshark_activity);
        getWindow().setFeatureDrawableResource(3, R.drawable.shark);
        tvList = (ListView) findViewById(R.id.listHosts);
        mInstance = this;
        cmd = getIntent().getExtras().getString("Key_String");
        orig_str = getIntent().getExtras().getString("Key_String_origin");
    }

    private boolean             isAlreadyLauched() throws IOException {
        if (isAlreadyLaunched) {
            Log.d(TAG, "TSHARK is already launched");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ((ImageView) findViewById(R.id.onDefendIcon)).setImageResource(R.drawable.start);
            readCepterRawThread.interrupt();
            isAlreadyLaunched = false;
            return true;
        }
        return false;
    }

    public void                 OnRaw(View v) throws IOException {
        if (isAlreadyLauched())
            return ;
        isAlreadyLaunched = true;
        ((ImageView) findViewById(R.id.onDefendIcon)).setImageResource(R.drawable.stop);
        EditText txt = (EditText) findViewById(R.id.primaryMonitor);
        FileOutputStream out = openFileOutput("pf", 0);
        out.write(txt.getEditableText().toString().getBytes());
        out.close();
        final ArrayList<String> lst = new ArrayList<>();
        adapter = new ProtocolAdapter(mInstance.getApplication(), R.layout.raw_list, lst);
        initArrayList();
        readCepterRawThread = new Thread(execParseCepter(lst));
        readCepterRawThread.start();
    }

    private Runnable            execParseCepter(final ArrayList<String> lst) {
        return new Runnable() {
            public void run() {
                Log.d(TAG, "execParseCepter started");
                int offsetLine = 0;
                String line;
                try {
                    RootProcess processRoot = new RootProcess("cepter RAW MODE(tshark)", globalVariable.path + "");String sc = " w "; //Dump le pcap sous forme de fichier .pcap mais ca fait segfault, bisare
                    processRoot.exec(globalVariable.path + "/cepter " + Integer.toString(globalVariable.adapt_num) + " 3 raw" );//+ sc
                    InputStreamReader reader = processRoot.getInputStreamReader();
                    Log.i(TAG, "RAW MODE reader ready:" + reader.ready());
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    while (isAlreadyLaunched && (line = bufferedReader.readLine()) != null) {
                        Log.d(TAG, "line:" + line);
                        if (line.contains("###STAT###")) {
                            IntercepterReader.parseStat(line, mInstance);
                        } else if (line.contains("  Cookie###")) {
                            IntercepterReader.parseCookie(bufferedReader, mInstance);
                        } else {
                            addToListView(lst, offsetLine, line);
                            offsetLine++;
                        }
                    }
                    Log.d(TAG, "Cepter Raw listener Over");
                    bufferedReader.close();
                    processRoot.waitFor();
                    addToListView(lst, offsetLine, "***\t Quiting");
                } catch (IOException e) {
                    e.getStackTrace();
                }
            }
        };
    }

    private void                initArrayList() {
        runOnUiThread(new Runnable() {
            public void run() {
                tvList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
        tvList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String it = parent.getItemAtPosition(position).toString();
                int offset = it.indexOf(" ");
                if (offset > 0) {
                    String index = it.substring(0, offset);
                    try {
                        int a = Integer.valueOf(index) ;
                        Intent i = new Intent(mInstance, RawDetails.class);
                        i.putExtra("Key_Int", Integer.valueOf(index));
                        startActivityForResult(i, 1);
                        globalVariable.lock = 0;
                    } catch (NumberFormatException e) {
                        e.getStackTrace();
                    }
                }
            }
        });
    }

    private void                addToListView(final ArrayList<String> lst, final int offsetLine, final String line) {
        Log.d(TAG, "listView add at:" + offsetLine + ": " + line);
        runOnUiThread(new Runnable() {
            public void run() {
                lst.add(offsetLine, line);
                globalVariable.adapter.notifyDataSetChanged();
                //if (globalVariable.raw_autoscroll == 1) {
                    tvList.setSelection(offsetLine);
                //}
            }
        });
    }

    private void                closeActualProcess() {
        if (isAlreadyLaunched) {
            readCepterRawThread.interrupt();
            isAlreadyLaunched = false;
        }
        Intent i = new Intent(mInstance, ScanActivity.class);
        i.putExtra("Key_String", orig_str);
        startActivity(i);
        finish();
    }

    public boolean              onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        closeActualProcess();
        return false;
    }

}
