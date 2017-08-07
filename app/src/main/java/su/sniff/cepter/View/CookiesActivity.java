package su.sniff.cepter.View;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import su.sniff.cepter.R;
import su.sniff.cepter.globalVariable;

public class                    CookiesActivity extends Activity {
    private CookiesActivity     mInstance = this;
    private ListView            tvList;

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(3);
        setContentView(R.layout.activity_cookies);
        getWindow().setFeatureDrawableResource(3, R.drawable.shark);
        super.onCreate(savedInstanceState);
        initThreadsBehavior();
        initXml();
        loopNotifyDataSetChangedDeamon();
    }

    private void                initXml() {
        tvList = (ListView) findViewById(R.id.listHosts);
        tvList.setAdapter(globalVariable.adapter);
        tvList.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                globalVariable.cookies_domain.remove(position);
                globalVariable.cookies_getreq.remove(position);
                globalVariable.cookies_ip.remove(position);
                globalVariable.cookies_value.remove(position);
                globalVariable.cookies_c--;
                globalVariable.cookies_domain2.remove(position);
                globalVariable.cookies_getreq2.remove(position);
                globalVariable.cookies_ip2.remove(position);
                globalVariable.cookies_value2.remove(position);
                globalVariable.adapter.notifyDataSetChanged();
                globalVariable.adapter2.notifyDataSetChanged();
                return true;
            }
        });
        tvList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View itemClicked, int position, long id) {
                if (VERSION.SDK_INT < 11) {
                    ((ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                            .setText((CharSequence) globalVariable.cookies_value2.get(position));
                } else {
                    ((android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE))
                            .setPrimaryClip(ClipData.newPlainText((CharSequence) globalVariable.cookies_domain2.get(position),
                                    (CharSequence) globalVariable.cookies_value2.get(position)));
                }
                Intent i = new Intent(mInstance, BrowserActivity.class);
                i.putExtra("Key_Int", position);
                startActivityForResult(i, 1);
                globalVariable.lock = 0;
            }
        });
    }

    private void                initThreadsBehavior() {
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
        globalVariable.lock = 0;
    }

    private void                loopNotifyDataSetChangedDeamon() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            globalVariable.adapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }).start();
    }

    public void                 OnClearCookie(View v) {
        globalVariable.cookies_domain.clear();
        globalVariable.cookies_value.clear();
        globalVariable.adapter.notifyDataSetChanged();
        globalVariable.cookies_getreq.clear();
        globalVariable.cookies_ip.clear();
        globalVariable.cookies_c = 0;
        globalVariable.cookies_domain2.clear();
        globalVariable.cookies_value2.clear();
        globalVariable.adapter2.notifyDataSetChanged();
        globalVariable.cookies_getreq2.clear();
        globalVariable.cookies_ip2.clear();
    }

    public void                 OnShowMore(View v) {
        if (globalVariable.cookies_show == 0) {
            ((Button) findViewById(R.id.clearButton)).setText("Show Less");
            globalVariable.cookies_show = 1;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvList.setAdapter(globalVariable.adapter2);
                    globalVariable.adapter2.notifyDataSetInvalidated();
                }
            });
            return;
        }
        ((Button) findViewById(R.id.clearButton)).setText("Show More");
        globalVariable.cookies_show = 0;
        tvList.setAdapter(globalVariable.adapter);
        globalVariable.adapter.notifyDataSetInvalidated();
    }

    public boolean              onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
