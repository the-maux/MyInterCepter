package fr.dao.app.View.Activity.WebServer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.WebServer.GenericServer;
import fr.dao.app.R;
import fr.dao.app.View.Behavior.Activity.SniffActivity;
import fr.dao.app.View.Behavior.ViewAnimate;
import fr.dao.app.View.Widget.MyWebViewClient;

public class                    WebServerActivity extends SniffActivity {
    private String              TAG = "WebServerActivity";
    private int                 PORT = 8081;

    private Singleton           mSingleton = Singleton.getInstance();
    private ConstraintLayout    mCoordinatorLayout;
    private String              myUrl = "http://" + mSingleton.network.myIp + ":" + PORT;
    private Toolbar             mToolbar;
    private GenericServer       mWebServer;
    private BroadcastReceiver   broadcastReceiverNetworkState;
    private WebView             mWebview;

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        initXml();

        init();
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        mWebview = findViewById(R.id.webViewToSeeWebsite);
        mFab = findViewById(R.id.fab);
        mToolbar = findViewById(R.id.toolbar);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void                init() {
        mToolbar.setSubtitle(myUrl);
        mFab.setOnClickListener(onFabClick());
        initNavigationBottomBar(WEB, true);
        WebSettings webSettings = mWebview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebview.setWebViewClient(new MyWebViewClient(mToolbar));
    }

    private View.OnClickListener onFabClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.vibrateDevice(mInstance);
                if (!mSingleton.iswebSpoofed() && startAndroidWebServer()) {
                    mSingleton.setwebSpoofed(true);
                    mFab.setBackgroundTintList(ContextCompat.getColorStateList(WebServerActivity.this, R.color.start_color));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showWebView();
                        }
                    }, 2000);
                } else if (stopAndroidWebServer()) {
                    mSingleton.setwebSpoofed(false);
                    mFab.setBackgroundTintList(ContextCompat.getColorStateList(WebServerActivity.this, R.color.stop_color));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            unShowWebView();
                        }
                    }, 1000);
                }
            }
        };
    }


    private boolean             startAndroidWebServer() {
        if (!mSingleton.iswebSpoofed()) {
                try {
                    mWebServer = new GenericServer(PORT);
                    mWebServer.start();
                    return true;
                } catch (Exception io) {
                    io.getStackTrace();
                    showSnackbar("Error in server booting");
                }
        } else {
            showSnackbar("Server already launched");
        }
        return false;
    }
    private boolean             stopAndroidWebServer() {
        if (mSingleton.iswebSpoofed() && mWebServer != null) {
            mWebServer.stop();
            mSingleton.setwebSpoofed(false);
            return true;
        }
        return false;
    }

    private void                showWebView() {
        ViewAnimate.setVisibilityToVisibleLong(mWebview);
        updateNotifications();
        updateWebView();
    }

    private void                updateWebView() {
        mWebview.loadUrl(myUrl);
    }
    private void                unShowWebView() {
        ViewAnimate.setVisibilityToGoneQuick(mWebview);
        updateNotifications();

    }



    public void                 showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

    protected void              onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        stopAndroidWebServer();
        mSingleton.setwebSpoofed(false);
   }

    public int                  getContentViewId() {
        return R.layout.activity_webserver;
    }

    public int                  getNavigationMenuItemId() {
        return R.id.navigation_webserver;
    }

}
