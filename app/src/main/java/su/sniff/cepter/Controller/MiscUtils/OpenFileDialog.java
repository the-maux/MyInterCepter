package su.sniff.cepter.Controller.MiscUtils;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import su.sniff.cepter.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenFileDialog extends Dialog implements OnClickListener {
    private static final String FILE_KEY = "filename";
    private static final String IMAGE_KEY = "fileimage";
    private static final String TAG = "OpenFileDialog";
    private File currentDir = new File("/");
    private FilenameFilter filter = null;
    private OnFileSelectedListener onFileSelectedListener = null;
    private ListView view = null;

    class C00701 implements OnItemClickListener {
        C00701() {
        }

        public void onItemClick(AdapterView<?> a, View v, int position, long id) {
            File file = new File(OpenFileDialog.this.currentDir.getAbsolutePath() + File.separator + ((String) ((Map) a.getItemAtPosition(position)).get(OpenFileDialog.FILE_KEY)));
            if (!OpenFileDialog.this.browseTo(file) && OpenFileDialog.this.onFileSelectedListener != null) {
                OpenFileDialog.this.onFileSelectedListener.onFileSelected(file);
                OpenFileDialog.this.dismiss();
            }
        }
    }

    public interface OnFileSelectedListener {
        void onFileSelected(File file);
    }

    public OpenFileDialog(Context context, String dir, String[] fileExt, OnFileSelectedListener listener) {
        super(context);
        init(dir, fileExt, listener);
    }

    private void init(String dir, String[] fileExt, OnFileSelectedListener listener) {
        this.onFileSelectedListener = listener;
        if (dir != null && new File(dir).exists()) {
            this.currentDir = new File(dir);
        }
        prepareFileFilter(fileExt);
        setContentView(R.layout.dialog_openfile);
        setTitle(R.string.ofd_title);
        this.view = (ListView) findViewById(R.id.ofd_list);
        browseTo(this.currentDir);
        this.view.setOnItemClickListener(new C00701());
        ((Button) findViewById(R.id.ofd_go_up)).setOnClickListener(this);
    }

    public void onClick(View v) {
        browseUp();
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
        ((TextView) findViewById(R.id.ofd_current_path)).setText(this.currentDir.getAbsolutePath());
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
        this.view.setAdapter(new SimpleAdapter(getContext(), list, R.layout.item_dialog_openfile, new String[]{FILE_KEY, IMAGE_KEY}, new int[]{R.id.ofd_item_text, R.id.ofd_item_image}));
        return true;
    }

    private void handleException(Exception ex) {
        Log.e(TAG, ex.getMessage());
    }
}
