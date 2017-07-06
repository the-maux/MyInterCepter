package su.sniff.cepter.Controller;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import su.sniff.cepter.BuildConfig;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DNSSpoofing extends Activity {
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    Context mCtx;
    private String m_Text = BuildConfig.FLAVOR;
    private CheckBox mySwitch;
    public ListView tvList1;

    class C00511 implements OnCheckedChangeListener {
        C00511() {
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                globalVariable.dnss = 1;
            } else {
                globalVariable.dnss = 0;
            }
        }
    }

    class C00522 implements OnItemLongClickListener {
        C00522() {
        }

        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
            DNSSpoofing.this.arrayList.remove(position);
            DNSSpoofing.this.adapter.notifyDataSetChanged();
            return true;
        }
    }

    class C00544 implements OnClickListener {
        C00544() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dns_layout);
        this.arrayList = new ArrayList();
        this.tvList1 = (ListView) findViewById(R.id.listHosts);
        this.mySwitch = (CheckBox) findViewById(R.id.checkBox);
        if (globalVariable.dnss == 1) {
            this.mySwitch.setChecked(true);
        } else {
            this.mySwitch.setChecked(false);
        }
        this.mySwitch.setOnCheckedChangeListener(new C00511());
        this.adapter = new ArrayAdapter(getApplicationContext(), 17367048, this.arrayList);
        this.tvList1.setAdapter(this.adapter);
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
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(globalVariable.path + "/dnss")));
            while (true) {
                String read = reader.readLine();
                if (read == null) {
                    break;
                }
                this.arrayList.add(read);
                this.adapter.notifyDataSetChanged();
            }
            reader.close();
        } catch (FileNotFoundException e2) {
        } catch (IOException e3) {
            e3.printStackTrace();
        }
        this.tvList1.setOnItemLongClickListener(new C00522());
        globalVariable.lock = 0;
    }

    public void OnClear(View v) {
        this.arrayList.clear();
        this.adapter.notifyDataSetChanged();
    }

    public void OnAdd(View v) {
        Builder builder = new Builder(this);
        builder.setTitle("Add new record");
        final EditText input = new EditText(this);
        input.setInputType(1);
        builder.setView(input);
        builder.setPositiveButton("Add", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                DNSSpoofing.this.m_Text = input.getText().toString();
                DNSSpoofing.this.arrayList.add(DNSSpoofing.this.m_Text);
                DNSSpoofing.this.adapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new C00544());
        builder.show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        try {
            FileOutputStream out = openFileOutput("dnss", 0);
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
