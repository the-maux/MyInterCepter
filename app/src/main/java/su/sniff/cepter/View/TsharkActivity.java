package su.sniff.cepter.View;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import su.sniff.cepter.*;
import su.sniff.cepter.Utils.OpenFileDialog;
import su.sniff.cepter.Utils.OpenFileDialog.OnFileSelectedListener;
import su.sniff.cepter.Utils.RawDetails;
import su.sniff.cepter.adapter.ProtocolAdapter;

public class                TsharkActivity extends Activity {
    private String          TAG = "TsharkActivity";
    private TsharkActivity  mInstance = this;
    private String          cmd;
    private String          orig_str;
    private Process         sniff_process = null;
    private ListView        tvList;

    class C01292 implements OnFileSelectedListener {
        C01292() {
        }

        public void onFileSelected(File f) {
            String sc;
            if (globalVariable.savepcap == 1) {
                sc = " w ";
            } else {
                sc = " ";
            }
            ((ImageView) findViewById(R.id.onDefendIcon)).setImageResource(R.drawable.stop);
            File fDroidSheep = new File(globalVariable.path + "/exitr.id");
            if (fDroidSheep.exists()) {
                fDroidSheep.delete();
            }
            try {
                final Process process = Runtime.getRuntime().exec("su", null, new File(globalVariable.path + ""));
                DataOutputStream os = new DataOutputStream(process.getOutputStream());
                os.writeBytes(globalVariable.path + "/cepter " + f.getAbsolutePath() + " 3 raw\n");
                os.flush();
                os.writeBytes("exit\n");
                os.flush();
                os.close();
                sniff_process = process;
                new Thread(new Runnable() {

                    class C00852 implements OnItemClickListener {
                        C00852() {
                        }

                        public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
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
                    }

                    class C00885 implements Runnable {
                        C00885() {
                        }

                        public void run() {
                            ((ImageView) findViewById(R.id.onDefendIcon)).setImageResource(R.drawable.start);
                        }
                    }

                    public void run() {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                        final ArrayList<String> lst = new ArrayList();
                        int c = 0;
                        final ProtocolAdapter adapter = new ProtocolAdapter(this, R.layout.raw_list, lst);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                tvList.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                            }
                        });
                        tvList.setOnItemClickListener(new C00852());
                        while (true) {
                            try {
                                String line = reader.readLine();
                                if (line == null) {
                                    reader.close();
                                    process.waitFor();
                                    runOnUiThread(new C00885());
                                    sniff_process = null;
                                    return;
                                }
                                final String temp = line;
                                if (temp.indexOf("###STAT###") != -1) {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            int b = temp.indexOf("###STAT###") + 11;
                                            ((TextView) findViewById(R.id.monitor)).setText(temp.substring(b, (temp.length() - b) + 11));
                                        }
                                    });
                                } else {
                                    final int c2 = c;
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            lst.add(c2, temp);
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                                    c++;
                                }
                            } catch (IOException e) {
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                }).start();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void             onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(3);
        setContentView(R.layout.raw_layout);
        getWindow().setFeatureDrawableResource(3, R.drawable.shark);
        tvList = (ListView) findViewById(R.id.listHosts);
        ArrayList<String> lst = new ArrayList();
        mInstance = this;
        cmd = getIntent().getExtras().getString("Key_String");
        orig_str = getIntent().getExtras().getString("Key_String_origin");
    }
    
    public void             OnRaw(View v) throws IOException {
        if (sniff_process != null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ((ImageView) findViewById(R.id.onDefendIcon)).setImageResource(R.drawable.start);
            openFileOutput("exitr.id", 0).close();
            sniff_process = null;
            return;
        }
        String sc;
        ((ImageView) findViewById(R.id.onDefendIcon)).setImageResource(R.drawable.stop);
        File fDroidSheep = new File(globalVariable.path + "/exitr.id");
        if (fDroidSheep.exists()) {
            fDroidSheep.delete();
        }
        if (globalVariable.savepcap == 1) {
            sc = " w ";
        } else {
            sc = " ";
        }
        EditText txt = (EditText) findViewById(R.id.primaryMonitor);
        FileOutputStream out = openFileOutput("pf", 0);
        out.write(txt.getEditableText().toString().getBytes());
        out.close();
        final Process process = Runtime.getRuntime().exec("su", null, new File(globalVariable.path + ""));
        DataOutputStream os = new DataOutputStream(process.getOutputStream());
        os.writeBytes(globalVariable.path + "/cepter " + Integer.toString(globalVariable.adapt_num) + " 3 raw" + sc + "\n");
        os.flush();
        os.writeBytes("exit\n");
        os.flush();
        os.close();
        sniff_process = process;
        doThings(process);
        new Thread().start();
    }

    private void            doThings(final Process process) {
        return new Runnable() {

            class C00782 implements OnItemClickListener {
                C00782() {
                }

                public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
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
            }

            public void run() {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                final ArrayList<String> lst = new ArrayList();
                int c = 0;
                final ProtocolAdapter adapter = new ProtocolAdapter(this, R.layout.raw_list, lst);
                runOnUiThread(new Runnable() {
                    public void run() {
                        tvList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                });
                tvList.setOnItemClickListener(new C00782());
                while (true) {
                    try {
                        String line = bufferedReader.readLine();
                        final int c2;
                        if (line == null) {
                            bufferedReader.close();
                            process.waitFor();
                            c2 = c;
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    lst.add(c2, "***");
                                    adapter.notifyDataSetChanged();
                                    if (globalVariable.raw_autoscroll == 1) {
                                        tvList.setSelection(c2);
                                    }
                                }
                            });
                            return;
                        }
                        final String temp = line;
                        if (temp.indexOf("###STAT###") != -1) {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    int b = temp.indexOf("###STAT###") + 11;
                                    ((TextView) findViewById(R.id.monitor)).setText(temp.substring(b, (temp.length() - b) + 11));
                                }
                            });
                        } else if (temp.indexOf("Cookie###") != -1) {
                            final String domain = bufferedReader.readLine();
                            final String ip = bufferedReader.readLine();
                            final String getreq = bufferedReader.readLine();
                            final String coo = bufferedReader.readLine();
                            String z = bufferedReader.readLine();
                            int dub = 0;
                            for (int i = 0; i < globalVariable.cookies_c; i++) {
                                if (((String) globalVariable.cookies_value.get(i)).equals(coo)) {
                                    dub = 1;
                                    break;
                                }
                            }
                            if (dub != 1) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
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
                                        globalVariable.cookies_domain.add(globalVariable.cookies_c, domain + " : " + ip);
                                        globalVariable.cookies_domain2.add(globalVariable.cookies_c, "<font color=\"#00aa00\"><b>" + domain + " : " + ip + "</b></font><br>" + "<font color=\"#397E7E\">" + coo + "</font>");
                                        globalVariable.adapter.notifyDataSetChanged();
                                        globalVariable.adapter2.notifyDataSetChanged();
                                        globalVariable.cookies_getreq.add(globalVariable.cookies_c, getreq);
                                        globalVariable.cookies_value.add(globalVariable.cookies_c, coo);
                                        globalVariable.cookies_ip.add(globalVariable.cookies_c, ip);
                                        globalVariable.cookies_getreq2.add(globalVariable.cookies_c, getreq);
                                        globalVariable.cookies_value2.add(globalVariable.cookies_c, coo);
                                        globalVariable.cookies_ip2.add(globalVariable.cookies_c, ip);
                                        globalVariable.cookies_c++;
                                        globalVariable.lock = 0;
                                    }
                                });
                            }
                        } else {
                            c2 = c;
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    lst.add(c2, temp);
                                    adapter.notifyDataSetChanged();
                                    if (globalVariable.raw_autoscroll == 1) {
                                        tvList.setSelection(c2);
                                    }
                                }
                            });
                            c++;
                        }
                    } catch (IOException e) {
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
            }
        };
    }

    public void             OnBack(View v) throws IOException {
        if (sniff_process != null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ((ImageView) findViewById(R.id.onDefendIcon)).setImageResource(R.drawable.start);
            openFileOutput("exitr.id", 0).close();
            sniff_process = null;
        }
        Intent i = new Intent(mInstance, ScanActivity.class);
        i.putExtra("Key_String", orig_str);
        startActivity(i);
        finish();
    }

    public void             OnOpenCap(View v) {
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
        new OpenFileDialog(this, globalVariable.PCAP_PATH, new String[]{".pcap"}, new C01292()).show();
    }

    public boolean          onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        try {
            openFileOutput("exits.id", 0).close();
            openFileOutput("exitr.id", 0).close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        Intent i = new Intent(mInstance, ScanActivity.class);
        i.putExtra("Key_String", orig_str);
        startActivity(i);
        finish();
        return false;
    }

}
