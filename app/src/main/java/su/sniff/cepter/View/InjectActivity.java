package su.sniff.cepter.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import su.sniff.cepter.BuildConfig;
import su.sniff.cepter.Utils.OpenFileDialog;
import su.sniff.cepter.Utils.OpenFileDialog.OnFileSelectedListener;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

public class InjectActivity extends Activity {
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    Context mCtx;
    private String m_Text = BuildConfig.FLAVOR;
    private CheckBox mySwitch;
    public ListView tvList1;

    class C00551 implements OnItemLongClickListener {
        C00551() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
            InjectActivity.this.arrayList.remove(position);
            InjectActivity.this.adapter.notifyDataSetChanged();
            return true;
        }
    }

    class C01262 implements OnFileSelectedListener {
        C01262() {
        }

        public void onFileSelected(File f) {
            String[] content = new String[]{BuildConfig.FLAVOR, "application/javascript", "image/jpeg", "image/jpeg", "image/png", "application/octet-stream", "text/html", "text/html", "text/plain"};
            Spinner spinner1 = (Spinner) InjectActivity.this.findViewById(R.id.spinner);
            Spinner spinner2 = (Spinner) InjectActivity.this.findViewById(R.id.spinner4);
            String pattern = spinner1.getSelectedItem().toString();
            InjectActivity.this.arrayList.add((pattern + ";" + content[spinner1.getSelectedItemPosition()] + ";" + spinner2.getSelectedItem().toString() + ";") + f.getAbsolutePath() + ";");
            InjectActivity.this.adapter.notifyDataSetChanged();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inject);
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
        this.arrayList = new ArrayList();
        this.tvList1 = (ListView) findViewById(R.id.listView1);
        this.adapter = new ArrayAdapter(getApplicationContext(), 17367048, this.arrayList);
        this.tvList1.setAdapter(this.adapter);
        this.tvList1.setOnItemLongClickListener(new C00551());
        ArrayAdapter<String> adapter = new ArrayAdapter(this, 17367048, new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "20", "30", "40", "50", "100", "500", "1000"});
        adapter.setDropDownViewResource(17367049);
        ((Spinner) findViewById(R.id.spinner4)).setAdapter(adapter);
        ArrayAdapter<String> adapter2 = new ArrayAdapter(this, 17367048, new String[]{"*select pattern*", ".js", ".jpg", ".jpeg", ".png", ".exe", ".html", ".htm", ".txt"});
        adapter2.setDropDownViewResource(17367049);
        ((Spinner) findViewById(R.id.spinner)).setAdapter(adapter2);
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(globalVariable.path + "/inj")));
            while (true) {
                String read = reader.readLine();
                if (read == null) {
                    break;
                }
                this.arrayList.add(read);
                adapter.notifyDataSetChanged();
            }
            reader.close();
        } catch (FileNotFoundException e2) {
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        globalVariable.lock = 0;
    }

    public void OnAdd(View v) {
        new OpenFileDialog(this, globalVariable.PCAP_PATH, new String[]{BuildConfig.FLAVOR}, new C01262()).show();
    }

    public void OnClear(View v) {
        this.arrayList.clear();
        this.adapter.notifyDataSetChanged();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        try {
            FileOutputStream out = openFileOutput("inj", 0);
            for (int i = 0; i < this.arrayList.size(); i++) {
                out.write(((String) this.arrayList.get(i)).getBytes());
                out.write("\n".getBytes());
            }
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        finish();
        return true;
    }
}
