package su.sniff.cepter.View;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import su.sniff.cepter.BuildConfig;
import su.sniff.cepter.Controller.MiscUtils.OpenFileDialog;
import su.sniff.cepter.Controller.MiscUtils.OpenFileDialog.OnFileSelectedListener;
import su.sniff.cepter.Controller.Singleton;
import su.sniff.cepter.Controller.System.ThreadUtils;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

public class                        InjectHTTPActivity extends Activity {
    private String                  TAG = getClass().getName();
    private InjectHTTPActivity      mInstance = this;
    private ArrayAdapter<String>    InjectionAdapter;
    private ArrayList<String>       ListOfInjection;
    public ListView                 listViewInjections;
    private Spinner                 numberSpinner, patternSpinner;


    public void                     onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inject_http);
        ThreadUtils.lock();
        initXml();
        initInj();
        globalVariable.lock = 0;
    }

    private void                    initInj() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(new File(Singleton.FilesPath + "/inj")));
            String read;
            while ((read = reader.readLine()) != null) {
                Log.d(TAG, "Inj File read:");
                ListOfInjection.add(read);
                InjectionAdapter.notifyDataSetChanged();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void                    initXml() {
        patternSpinner = ((Spinner) findViewById(R.id.patternSpinner));
        numberSpinner = ((Spinner) findViewById(R.id.numberSpinner));
        ListOfInjection = new ArrayList();
        listViewInjections = (ListView) findViewById(R.id.listHosts);
        InjectionAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, ListOfInjection);
        listViewInjections.setAdapter(InjectionAdapter);
        listViewInjections.setOnItemLongClickListener(onItemLongClick());
        ArrayAdapter<String> numberSpinnerArray = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "20", "30", "40", "50", "100", "500", "1000"});
        numberSpinner.setAdapter(numberSpinnerArray);
        ArrayAdapter<String> patternSpinnerArray = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new String[]{"*select pattern*", ".js", ".jpg", ".jpeg", ".png", ".exe", ".html", ".htm", ".txt"});
        patternSpinner.setAdapter(patternSpinnerArray);
    }

    public void                     OnAdd(View v) {
        new OpenFileDialog(this, globalVariable.PCAP_PATH, new String[]{BuildConfig.FLAVOR},
                new OnFileSelectedListener() {
                    @Override
                    public void onFileSelected(File f) {
                        String[] content = new String[]{BuildConfig.FLAVOR, "application/javascript", "image/jpeg", "image/jpeg", "image/png", "application/octet-stream", "text/html", "text/html", "text/plain"};
                        String pattern = patternSpinner.getSelectedItem().toString();
                        ListOfInjection.add((pattern + ";" + content[patternSpinner.getSelectedItemPosition()] + ";" + numberSpinner.getSelectedItem().toString() + ";") + f.getAbsolutePath() + ";");
                        InjectionAdapter.notifyDataSetChanged();
                    }
                }).show();
    }

    public void                     OnClear(View v) {
        ListOfInjection.clear();
        InjectionAdapter.notifyDataSetChanged();
    }

    /**
     * Remove the Item
     * @return
     */
    private OnItemLongClickListener onItemLongClick() {
        return new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                ListOfInjection.remove(position);
                InjectionAdapter.notifyDataSetChanged();
                return true;
            }
        };
    }


    public boolean                  onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        try {
            FileOutputStream out = openFileOutput("inj", 0);
            for (int i = 0; i < ListOfInjection.size(); i++) {
                Log.d(TAG, "dumpIn inj file:" + ListOfInjection.get(i));
                out.write(( ListOfInjection.get(i) + "\n").getBytes());
            }
            out.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        finish();
        return true;
    }
}
