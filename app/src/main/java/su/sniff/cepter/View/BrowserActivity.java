package su.sniff.cepter.View;

import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ListView;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import su.sniff.cepter.R;
import su.sniff.cepter.Utils.SaveFileDialog;
import su.sniff.cepter.Utils.SaveFileDialog.OnNewFileSelectedListener;
import su.sniff.cepter.globalVariable;


public class BrowserActivity extends Activity {
    Context mCtx;
    WebView mWebView;
    WebView mWebView2;
    public ListView tvList;

    class C00481 implements TextWatcher {
        C00481() {
        }

        public void afterTextChanged(Editable s) {
            BrowserActivity.this.mWebView.loadUrl(((EditText) BrowserActivity.this.findViewById(R.id.editText1)).getText().toString());
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_layout);
        this.mWebView = (WebView) findViewById(R.id.webView1);
        CookieSyncManager cookieSyncManager = CookieSyncManager.createInstance(this.mWebView.getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        SystemClock.sleep(1000);
        int position = getIntent().getExtras().getInt("Key_Int", 0);
        String domain = ((String) globalVariable.cookies_domain.get(position)).substring(0, ((String) globalVariable.cookies_domain.get(position)).indexOf(" :"));
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
        int start_s = 0;
        String cook = ((String) globalVariable.cookies_value.get(position)) + ";";
        while (true) {
            int off = cook.indexOf(";", start_s);
            if (off == -1) {
                cookieSyncManager.sync();
                String cookie = cookieManager.getCookie(domain);
                this.mWebView.getSettings().setUseWideViewPort(true);
                this.mWebView.getSettings().setJavaScriptEnabled(true);
                this.mWebView.setWebViewClient(new WebViewClient());
                this.mWebView.getSettings().setBuiltInZoomControls(true);
                this.mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) Chrome/23.0.1271.64 Safari/537.11");
                this.mWebView.loadUrl("http://" + domain);
                ((EditText) findViewById(R.id.editText1)).setText("http://" + domain);
                ((EditText) findViewById(R.id.editText1)).addTextChangedListener(new C00481());
                globalVariable.lock = 0;
                return;
            }
            cookieManager.setCookie(domain, cook.substring(start_s, off + 1));
            start_s = off + 1;
        }
    }

    public void OnBack(View v) {
        if (this.mWebView.canGoBack()) {
            this.mWebView.goBack();
        }
    }

    public void OnSaveCookie(View v) {
        final CharSequence data;
        if (VERSION.SDK_INT < 11) {
            data = ((ClipboardManager) getSystemService("clipboard")).getText();
        } else {
            data = ((android.content.ClipboardManager) getSystemService("clipboard")).getPrimaryClip().getItemAt(0).getText();
        }
        new SaveFileDialog(this, Environment.getExternalStorageDirectory().getAbsolutePath(), new String[]{".txt"}, new OnNewFileSelectedListener() {
            public void onNewFileSelected(File f) {
                try {
                    FileWriter writer = new FileWriter(f);
                    writer.append(data);
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).show();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 4) {
            return super.onKeyDown(keyCode, event);
        }
        finish();
        return true;
    }
}
