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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import su.sniff.cepter.*;
import su.sniff.cepter.Controller.RootProcess;
import su.sniff.cepter.Misc.IntercepterReader;
import su.sniff.cepter.Utils.OpenFileDialog;
import su.sniff.cepter.Utils.OpenFileDialog.OnFileSelectedListener;
import su.sniff.cepter.Utils.RawDetails;
import su.sniff.cepter.adapter.ProtocolAdapter;

public class                    TsharkActivity extends Activity {
    private String              TAG = "TsharkActivity";
    private TsharkActivity      mInstance = this;
    private String              cmd;
    private String              orig_str;
    private Thread              threadProcess = null;
    private ListView            tvList;
    private ProtocolAdapter     adapter;
    private boolean             isAlreadyLaunched = false;

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(3);
        setContentView(R.layout.raw_layout);
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
            threadProcess.interrupt();
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
        threadProcess = new Thread(execParseCepter(lst));
        threadProcess.start();
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

    private void                dumpingCurrentSniff(final Process process) {
        new Thread(new Runnable() {
            public void run() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                final ArrayList<String> lst = new ArrayList<>();
                int nbrLineDumped = 0;
                final ProtocolAdapter adapter = new ProtocolAdapter(mInstance, R.layout.raw_list, lst);
                runOnUiThread(new Runnable() {
                    public void run() {
                        tvList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });
                tvList.setOnItemClickListener(onListViewItemClicked());
                String line;
                try {
                    while ((line = reader.readLine()) != null) {
                        final String temp = line;
                        if (temp.contains("###STAT###")) {
                            final int b = temp.indexOf("###STAT###") + 11;
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    ((TextView) findViewById(R.id.monitor)).setText(temp.substring(b, (temp.length() - b) + 11));
                                }
                            });
                        } else {
                            final int offsetList = nbrLineDumped;
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    lst.add(offsetList, temp);
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            nbrLineDumped++;
                        }
                    }
                    reader.close();
                    process.waitFor();
                    informDumpStoped();
                } catch (IOException ignored) {
                    ignored.getStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    private void                informDumpStoped() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ((ImageView) findViewById(R.id.onDefendIcon)).setImageResource(R.drawable.start);
                isAlreadyLaunched = false;
            }
        });
    }
    private OnItemClickListener onListViewItemClicked() {
        return new OnItemClickListener() {
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
                    }
                }
            }
        };
    }

    private void                addToListView(final ArrayList<String> lst, final int offsetLine, final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                lst.add(offsetLine, line);
                globalVariable.adapter.notifyDataSetChanged();
                if (globalVariable.raw_autoscroll == 1) {
                    tvList.setSelection(offsetLine);
                }
            }
        });
    }

    private Runnable            execParseCepter(final ArrayList<String> lst) {
        return new Runnable() {
            public void run() {
                Log.d(TAG, "execParseCepter started");
                String sc = " w "; //Dump le pcap sous forme de fichier .pcap mais ca fait segfault, bisare
                RootProcess processRoot = new RootProcess("tShark SNIFF launch to Cepter", globalVariable.path + "");
                processRoot.exec(globalVariable.path + "/cepter " + Integer.toString(globalVariable.adapt_num) + " 3 raw w " );//+ sc
                processRoot.exec("exit");

                BufferedReader bufferedReader = new BufferedReader(processRoot.getInputStreamReader());
                int offsetLine = 0;
                while (true) {
                    try {
                        final String line = bufferedReader.readLine();
                        if (line == null) {
                            Log.d(TAG, "Dump of cepter for tShark is null");
                            bufferedReader.close();
                            processRoot.waitFor();
                            addToListView(lst, offsetLine, "***");
                            return;
                        }
                        if (line.contains("###STAT###")) {
                            IntercepterReader.parseStat(line, mInstance);
                        } else if (line.contains("Cookie###")) {
                            IntercepterReader.parseCookie(bufferedReader, mInstance);
                        } else {
                            addToListView(lst, offsetLine, line);
                            offsetLine++;
                        }
                    } catch (IOException e) {
                        e.getStackTrace();
                    }
                }
            }
        };
    }

    public void                 OnOpenCap(View v) {
        try {
            EditText txt = (EditText) findViewById(R.id.primaryMonitor);
            FileOutputStream out = openFileOutput("pf", 0);
            out.write(txt.getEditableText().toString().getBytes());
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        new OpenFileDialog(this, globalVariable.PCAP_PATH, new String[]{".pcap"}, onFileSelectedListener()).show();
    }

    private OnFileSelectedListener onFileSelectedListener() {
        return new OnFileSelectedListener() {
            @Override
            public void onFileSelected(File file) {
                ((ImageView) findViewById(R.id.onDefendIcon)).setImageResource(R.drawable.stop);
                RootProcess process = new RootProcess("Dump RAW SNIFF", globalVariable.path + "");
                process.exec(globalVariable.path + "/cepter " + file.getAbsolutePath() + " 3 raw w ");
                process.exec("exit");
                dumpingCurrentSniff(process.getActualProcess());

            }
        };
    }

    public void                 OnBack(View v) throws IOException {
        if (isAlreadyLaunched) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ((ImageView) findViewById(R.id.onDefendIcon)).setImageResource(R.drawable.start);
            threadProcess.interrupt();
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
        Intent i = new Intent(mInstance, ScanActivity.class);
        i.putExtra("Key_String", orig_str);
        startActivity(i);
        finish();
        return false;
    }

}
