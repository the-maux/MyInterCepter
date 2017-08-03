package su.sniff.cepter.Controller.Network;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
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
import su.sniff.cepter.View.adapter.DNSAdapter;
import su.sniff.cepter.globalVariable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class                            DNSSpoofingActivity extends Activity {
    private DNSSpoofingActivity         mInstance = this;
    private String                      TAG = "DNSSpoofingActivity";
    private ArrayAdapter<String>        DNSAdapter;
    private ArrayList<String>           listDNSSpoof;
    private String                      m_Text = BuildConfig.FLAVOR;
    private CheckBox                    mySwitch;
    public ListView                     listViewDNSSpoof;

    public void                         onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        initXml();
        initThreadBehavior();
        fillListDNSSpoofedFromFile();
        globalVariable.lock = 0;
    }

    private void                        initXml() {
        listDNSSpoof = new ArrayList<>();
        listViewDNSSpoof = (ListView) findViewById(R.id.listHosts);
        mySwitch = (CheckBox) findViewById(R.id.checkBox);
        if (globalVariable.dnss == 1) {
            mySwitch.setChecked(true);
        } else {
            mySwitch.setChecked(false);
        }
        mySwitch.setOnCheckedChangeListener(onCheckedchangeListener());
        DNSAdapter = new DNSAdapter(this, listDNSSpoof);
        listViewDNSSpoof.setAdapter(DNSAdapter);
        listViewDNSSpoof.setOnItemLongClickListener(onLongClick());
    }

    private void                        initThreadBehavior() {
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
    }

    private void                        fillListDNSSpoofedFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(globalVariable.path + "/dnss")));
            while (true) {
                String read = reader.readLine();
                if (read == null) {
                    break;
                }
                Log.d(TAG, "Add DNS SPOOF:" + read);
                listDNSSpoof.add(read);
                DNSAdapter.notifyDataSetChanged();
            }
            reader.close();
        }  catch (IOException e3) {
            e3.printStackTrace();
        }
    }

    private OnItemLongClickListener     onLongClick() {
        return new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                listDNSSpoof.remove(position);
                DNSAdapter.notifyDataSetChanged();
                return true;
            }
        };
    }

    private OnCheckedChangeListener     onCheckedchangeListener() {
        return new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    globalVariable.dnss = 1;
                } else {
                    globalVariable.dnss = 0;
                }
            }
        };
    }

    public void                         OnClear(View v) {
        listDNSSpoof.clear();
        DNSAdapter.notifyDataSetChanged();
    }

    public void                         OnAdd(View v) {
        Builder builder = new Builder(this);
        builder.setTitle("Add new record");
        final EditText input = new EditText(this);
        input.setInputType(1);
        builder.setView(input);
        builder.setPositiveButton("Add", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                listDNSSpoof.add(m_Text);
                DNSAdapter.notifyDataSetChanged();
            }
        });
        builder.setNegativeButton("Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    public boolean                      onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        try {
            FileOutputStream out = openFileOutput("dnss", 0);
            for (int i = 0; i < this.listDNSSpoof.size(); i++) {
                out.write((listDNSSpoof.get(i)).getBytes());
                out.write("\n".getBytes());
            }
            out.close();
        }  catch (IOException e) {
            e.printStackTrace();
        }
        finish();
        return true;
    }
}
