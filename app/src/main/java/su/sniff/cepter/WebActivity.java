package su.sniff.cepter;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;

public class WebActivity extends Activity {
    Context mCtx;
    WebView mWebView2;
    public ListView tvList;

    class C01081 implements Runnable {

        class C01071 implements Runnable {
            C01071() {
            }

            public void run() {
                globalVariable.adapter.notifyDataSetChanged();
            }
        }

        C01081() {
        }

        public void run() {
            while (true) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                WebActivity.this.runOnUiThread(new C01071());
            }
        }
    }

    class C01092 implements OnItemLongClickListener {
        C01092() {
        }

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
    }

    class C01103 implements OnItemClickListener {
        C01103() {
        }

        public void onItemClick(AdapterView<?> adapterView, View itemClicked, int position, long id) {
            if (VERSION.SDK_INT < 11) {
                ((ClipboardManager) WebActivity.this.getSystemService("clipboard")).setText((CharSequence) globalVariable.cookies_value2.get(position));
            } else {
                ((android.content.ClipboardManager) WebActivity.this.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText((CharSequence) globalVariable.cookies_domain2.get(position), (CharSequence) globalVariable.cookies_value2.get(position)));
            }
            Intent i = new Intent(WebActivity.this.mCtx, BrowserActivity.class);
            i.putExtra("Key_Int", position);
            WebActivity.this.startActivityForResult(i, 1);
            globalVariable.lock = 0;
        }
    }

    class C01114 implements Runnable {
        C01114() {
        }

        public void run() {
            WebActivity.this.tvList.setAdapter(globalVariable.adapter2);
            globalVariable.adapter2.notifyDataSetInvalidated();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(3);
        setContentView(R.layout.web_layout);
        getWindow().setFeatureDrawableResource(3, R.drawable.shark);
        super.onCreate(savedInstanceState);
        this.mCtx = this;
        this.mWebView2 = new WebView(this);
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
        this.tvList = (ListView) findViewById(R.id.listView1);
        this.tvList.setAdapter(globalVariable.adapter);
        new Thread(new C01081()).start();
        this.tvList.setOnItemLongClickListener(new C01092());
        this.tvList.setOnItemClickListener(new C01103());
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 4) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void OnClearCookie(View v) {
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

    public void OnShowMore(View v) {
        if (globalVariable.cookies_show == 0) {
            ((Button) findViewById(R.id.button2)).setText("Show Less");
            globalVariable.cookies_show = 1;
            runOnUiThread(new C01114());
            return;
        }
        ((Button) findViewById(R.id.button2)).setText("Show More");
        globalVariable.cookies_show = 0;
        this.tvList.setAdapter(globalVariable.adapter);
        globalVariable.adapter.notifyDataSetInvalidated();
    }
}
