package su.sniff.cepter.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import su.sniff.cepter.Misc.ThreadUtils;
import su.sniff.cepter.R;
import su.sniff.cepter.Utils.SaveFileDialog;
import su.sniff.cepter.Utils.SaveFileDialog.OnNewFileSelectedListener;
import su.sniff.cepter.globalVariable;


public class                BrowserActivity extends Activity {
    private String          TAG = "BrowserActivity";
    private WebView         mWebView;
    private CookieSyncManager cookieSyncManager;
    private CookieManager   cookieManager;

    public void             onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.browser_layout);
        mWebView = (WebView) findViewById(R.id.webView1);
        ThreadUtils.lock();
        initCookieManager();
        initWebView();
    }
    
    private void            initCookieManager() {
        cookieSyncManager = CookieSyncManager.createInstance(mWebView.getContext());
        cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.removeAllCookie();
        SystemClock.sleep(1000);

    }
    
    private void            initWebView() {
        int position = getIntent().getExtras().getInt("Key_Int", 0);
        String domain = (globalVariable.cookies_domain.get(position)).substring(0, ((globalVariable.cookies_domain.get(position)).indexOf(" :")));
        int start_s = 0;
        String cook = (globalVariable.cookies_value.get(position)) + ";";
        while (true) {
            int off = cook.indexOf(";", start_s);
            if (off == -1) {
                cookieSyncManager.sync();
                String cookie = cookieManager.getCookie(domain);
                mWebView.getSettings().setUseWideViewPort(true);
                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.setWebViewClient(new WebViewClient());
                mWebView.getSettings().setBuiltInZoomControls(true);
                mWebView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 6.1; WOW64) Chrome/23.0.1271.64 Safari/537.11");
                mWebView.loadUrl("http://" + domain);
                ((EditText) findViewById(R.id.primaryMonitor)).setText("http://" + domain);
                ((EditText) findViewById(R.id.primaryMonitor)).addTextChangedListener(onTextChanged());
                globalVariable.lock = 0;
                return;
            }
            cookieManager.setCookie(domain, cook.substring(start_s, off + 1));
            start_s = off + 1;
        }
    }

    private TextWatcher     onTextChanged() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mWebView.loadUrl(((EditText) findViewById(R.id.primaryMonitor)).getText().toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };
    }

    public void             OnSaveCookie(View v) {
        final CharSequence data = ((android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE)).getPrimaryClip().getItemAt(0).getText();
        new SaveFileDialog(this, Environment.getExternalStorageDirectory().getAbsolutePath(), new String[]{".txt"}, new OnNewFileSelectedListener() {
            public void onNewFileSelected(File f) {
                try {
                    Log.d(TAG, "dumping Cookies in:");
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

    @Override
    public void             onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else
            super.onBackPressed();
    }
}
