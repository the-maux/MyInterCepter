package fr.dao.app.View.Terminal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Configuration.MitManager;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Scan.NmapControler;
import fr.dao.app.Model.Config.NmapParam;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.Scan.NmapOutputView;
import fr.dao.app.View.ZViewController.Activity.MITMActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Behavior.ViewAnimate;
import fr.dao.app.View.ZViewController.Dialog.DialogQuestionWithInput;

public class TerminalActivity extends MITMActivity {
    private String              TAG = "NmapActivity";
    private TerminalActivity mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    private NmapOutputView nmapOutputFragment;
    private AppBarLayout        appBarLayout;
    private Toolbar             mToolbar;
    private TabLayout           mTabs;
    private ImageView           mSettingsMenu, mScript, mScanType;
    private ProgressBar         mProgressBar;

    protected void              onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmap);
        initXml();
        init();
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        mTabs = findViewById(R.id.tabs);
        mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progressBar);
        mSettingsMenu = findViewById(R.id.settingsMenu);
        mScript = findViewById(R.id.scriptBtn);
        mScanType = findViewById(R.id.typeScanBtn);
        mFab = findViewById(R.id.fab);
        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(findViewById(R.id.relativeLayout), 2);
                ViewCompat.setElevation(appBarLayout, 4);
            }
        });
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
    }

    private void                init() {
        initFragment();
        ViewAnimate.FabAnimateReveal(mInstance, mFab);
        hideBottomBar();
    }

    private void                initFragment() {
        try {
            nmapOutputFragment = new NmapOutputView();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, nmapOutputFragment)
                    .commit();
        } catch (IllegalStateException e) {
            showSnackbar("Error in fragment: " + e.getCause().getMessage());
            e.getStackTrace();
            super.onBackPressed();
        }
    }

    public void                 setToolbarTitle(final String title, final String subtitle) {
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (title != null)
                    mToolbar.setTitle(title);
                if (subtitle != null)
                    mToolbar.setSubtitle(subtitle);
            }
        });
    }

    public void                 showSnackbar(String txt) {
        Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
    }

}
