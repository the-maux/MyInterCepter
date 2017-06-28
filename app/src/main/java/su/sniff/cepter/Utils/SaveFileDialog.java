package su.sniff.cepter.Utils;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import su.sniff.cepter.R;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SaveFileDialog extends Dialog implements OnClickListener {
    private static final String FILE_KEY = "filename";
    private static final String IMAGE_KEY = "fileimage";
    private static final String TAG = "SaveFileDialog";
    private File currentDir = new File("/");
    private EditText edit = null;
    private FilenameFilter filter = null;
    private File newfile = null;
    private OnNewFileSelectedListener onNewFileSelectedListener = null;
    private ListView view = null;

    class C00901 implements OnItemClickListener {
        C00901() {
        }

        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
            String text = (String) ((Map) a.getItemAtPosition(position)).get(SaveFileDialog.FILE_KEY);
            if (!SaveFileDialog.this.browseTo(new File(SaveFileDialog.this.currentDir.getAbsolutePath() + File.separator + text))) {
                SaveFileDialog.this.edit.setText(text);
            }
        }
    }

    class C00923 implements DialogInterface.OnClickListener {
        C00923() {
        }

        public void onClick(DialogInterface dialog, int which) {
            SaveFileDialog.this.returnNewFile(SaveFileDialog.this.newfile);
        }
    }

    public interface OnNewFileSelectedListener {
        void onNewFileSelected(File file);
    }

    public SaveFileDialog(Context context, String dir, String[] fileExt, OnNewFileSelectedListener listener) {
        super(context);
        init(dir, fileExt, listener);
    }

    private void init(String dir, String[] fileExt, OnNewFileSelectedListener listener) {
        this.onNewFileSelectedListener = listener;
        if (dir != null && new File(dir).exists()) {
            this.currentDir = new File(dir);
        }
        prepareFileFilter(fileExt);
        setContentView(R.layout.sfd_layout);
        setTitle(R.string.sfd_title);
        this.edit = (EditText) findViewById(R.id.sfd_file_name);
        Calendar cal = Calendar.getInstance();
        int day = cal.get(5);
        int month = cal.get(2) + 1;
        int year = cal.get(1);
        int hour = cal.get(11);
        this.edit.setText("log_" + day + "_" + month + "_" + year + "_" + hour + "_" + cal.get(12) + ".txt");
        this.view = (ListView) findViewById(R.id.sfd_list);
        browseTo(this.currentDir);
        this.view.setOnItemClickListener(new C00901());
        ((Button) findViewById(R.id.sfd_go_up)).setOnClickListener(this);
        ((Button) findViewById(R.id.sfd_new_dir)).setOnClickListener(this);
        ((Button) findViewById(R.id.sfd_save)).setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v == findViewById(R.id.sfd_go_up)) {
            browseUp();
        } else if (v == findViewById(R.id.sfd_new_dir)) {
            createNewDirectory(this.edit.getText().toString());
        } else if (v == findViewById(R.id.sfd_save)) {
            File f = createNewFile(this.edit.getText().toString());
            if (f != null && this.onNewFileSelectedListener != null) {
                returnNewFile(f);
            }
        }
    }

    private void prepareFileFilter(final String[] ext) {
        if (ext != null) {
            this.filter = new FilenameFilter() {
                public boolean accept(File dir, String filename) {
                    if (new File(dir + File.separator + filename).isDirectory()) {
                        return true;
                    }
                    for (String e : ext) {
                        if (filename.endsWith(e)) {
                            return true;
                        }
                    }
                    return false;
                }
            };
        }
    }

    private boolean browseTo(File dir) {
        if (!dir.isDirectory()) {
            return false;
        }
        if (!fillListView(dir)) {
            return true;
        }
        this.currentDir = dir;
        ((TextView) findViewById(R.id.sfd_current_path)).setText(this.currentDir.getAbsolutePath());
        return true;
    }

    private void browseUp() {
        if (this.currentDir.getParentFile() != null) {
            browseTo(this.currentDir.getParentFile());
        }
    }

    private boolean fillListView(File dir) {
        List<Map<String, ?>> list = new ArrayList();
        String[] files = null;
        try {
            files = this.filter != null ? dir.list(this.filter) : dir.list();
        } catch (SecurityException e) {
            handleException(e);
        }
        if (files == null) {
            return false;
        }
        for (String file : files) {
            Map<String, Object> item = new HashMap();
            item.put(FILE_KEY, file);
            item.put(IMAGE_KEY, Integer.valueOf(new File(new StringBuilder().append(dir.getAbsolutePath()).append(File.separator).append(file).toString()).isDirectory() ? R.drawable.ic_osdialogs_dir : R.drawable.ic_osdialogs_file));
            list.add(item);
        }
        this.view.setAdapter(new SimpleAdapter(getContext(), list, R.layout.sfd_list_item, new String[]{FILE_KEY, IMAGE_KEY}, new int[]{R.id.sfd_item_text, R.id.sfd_item_image}));
        return true;
    }

    private void createNewDirectory(String name) {
        if (name != null) {
            File newdir = new File(this.currentDir + File.separator + name);
            if (!newdir.exists()) {
                newdir.mkdirs();
                this.edit.setText(null);
                browseTo(newdir);
            }
        }
    }

    private File createNewFile(String name) {
        File file = null;
        this.newfile = new File(this.currentDir + File.separator + name);
        if (this.newfile.exists()) {
            new Builder(getContext()).setIcon(17301543).setTitle(R.string.sfd_confirmation_title).setMessage(R.string.sfd_confirmation_message).setPositiveButton(R.string.sfd_btn_yes, new C00923()).setNegativeButton("Non", new OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }).show();
            return file;
        }
        try {
            this.newfile.createNewFile();
            return this.newfile;
        } catch (IOException e) {
            handleException(e);
            return file;
        } catch (SecurityException e2) {
            handleException(e2);
            return file;
        }
    }

    private void returnNewFile(File f) {
        if (this.onNewFileSelectedListener != null) {
            this.onNewFileSelectedListener.onNewFileSelected(f);
        }
        dismiss();
    }

    private void handleException(Exception ex) {
        Log.e(TAG, ex.getMessage());
    }
}
