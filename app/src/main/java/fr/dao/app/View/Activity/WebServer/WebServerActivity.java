package fr.dao.app.View.Activity.WebServer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.WebServer.GenericServer;
import fr.dao.app.R;
import fr.dao.app.View.Behavior.Activity.SniffActivity;
import fr.dao.app.View.Behavior.MyGlideLoader;
import fr.dao.app.View.Behavior.ViewAnimate;
import fr.dao.app.View.Widget.MyWebViewClient;

public class                    WebServerActivity extends SniffActivity {
    private String              TAG = "WebServerActivity";
    private int                 PORT = 8081;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    private AppBarLayout        appBarLayout;
    private String              myUrl = "http://" + mSingleton.network.myIp + ":" + PORT;
    private Toolbar             mToolbar;
    private GenericServer       mWebServer;
    private ProgressBar         mProgressBar;
    private BroadcastReceiver   broadcastReceiverNetworkState;
    private WebView             mWebview;

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webserver);
        initXml();
        init();
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
        mWebview = findViewById(R.id.webViewToSeeWebsite);
        mProgressBar = findViewById(R.id.progressBar);
        mFab = findViewById(R.id.fab);
        ViewAnimate.FabAnimateReveal(mInstance, mFab);
        mToolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(appBarLayout, 4);
            }
        });
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
                ViewAnimate.setVisibilityToVisibleQuick(mProgressBar);
                if (!mSingleton.iswebSpoofed() && startAndroidWebServer()) {
                    mSingleton.setwebSpoofed(true);
                    showWebView();
                    mFab.setBackgroundTintList(ContextCompat.getColorStateList(WebServerActivity.this, R.color.start_color));
                } else if (stopAndroidWebServer()) {
                    mSingleton.setwebSpoofed(false);
                    unShowWebView();
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
        ViewAnimate.setVisibilityToGoneQuick(mProgressBar);
        ViewAnimate.setVisibilityToVisibleQuick(mWebview);
        updateNotifications();
        updateWebView();
    }

    private void                updateWebView() {
        mWebview.loadUrl(myUrl);
    }
    private void                unShowWebView() {
        ViewAnimate.setVisibilityToGoneQuick(mWebview);
        mFab.setBackgroundTintList(ContextCompat.getColorStateList(WebServerActivity.this, R.color.stop_color));
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

}
