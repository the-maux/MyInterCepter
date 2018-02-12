package fr.allycs.app.View.Activity.Wireshark;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Core.Tcpdump.Tcpdump;
import fr.allycs.app.R;
import fr.allycs.app.View.Behavior.Activity.SniffActivity;
import fr.allycs.app.View.Behavior.Fragment.MyFragment;
import fr.allycs.app.View.Behavior.MyGlideLoader;
import fr.allycs.app.View.Widget.Dialog.BottomSheet.GeneralSettings;

public class                    WiresharkActivity extends SniffActivity {
    private String              TAG = this.getClass().getName();
    private WiresharkActivity   mInstance = this;
    private CoordinatorLayout   mCoordinatorLayout;
    private Toolbar             mToolbar;
    private RelativeLayout      mHeaderConfOFF, mHeaderConfON;
    private ProgressBar         mProgressBar;
    private Singleton           mSingleton = Singleton.getInstance();
    private FrameLayout         mFrame_container;
    private WiresharkFragment   mFragment = null;


    protected void              onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        initXml();
        mFragment = new WiresharkFragment();
        initFragment(mFragment);
        initSettings();
        initNavigationBottomBar(SNIFFER, true);
        setToolbarTitle(null, mSingleton.selectedHostsList.get(0).getName());
    }


    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
        mHeaderConfON = findViewById(R.id.filterPcapLayout);
        mFrame_container = findViewById(R.id.frame_container);
        mHeaderConfOFF = findViewById(R.id.nmapConfEditorLayout);
        mProgressBar =  findViewById(R.id.progressBar);
        mToolbar = findViewById(R.id.toolbar);
        mFab =  findViewById(R.id.fab);
        mFab.setOnClickListener(onclickFab());
        findViewById(R.id.settings).setOnClickListener(onSwitchHeader());
    }

    private void                initFragment(MyFragment fragment) {
        try {
            mFragment = (WiresharkFragment) fragment;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, mFragment)
                    .addToBackStack(fragment.getClass().getName())
                    .commit();
        } catch (IllegalStateException e) {
            showSnackbar("Error in fragment: " + e.getCause().getMessage(), -1);
            e.getStackTrace();
            super.onBackPressed();
        }
    }

    private View.OnClickListener onclickFab() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFragment.start(false)) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mFab.setImageResource(R.mipmap.ic_pause);
                } else {
                    mProgressBar.setVisibility(View.GONE);
                    mFab.setImageResource(R.mipmap.ic_play);
                }
            }
        };
    }

    public void                 connectionSucceed() {
        if (mProgressBar.getVisibility() == View.VISIBLE)
            mProgressBar.setVisibility(View.GONE);
    }

    private void                initSettings() {
        findViewById(R.id.settingsMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new GeneralSettings(mInstance, mCoordinatorLayout, Tcpdump.getTcpdump(mInstance, true)).show();
            }
        });
    }

    public void                 onTrameError() {
        setToolbarTitle(null, "Error in processing");
        showSnackbar("Error in Sniffing", ContextCompat.getColor(mInstance, R.color.material_red_400));
        mProgressBar.setVisibility(View.GONE);
        mFab.setImageResource(R.mipmap.ic_play);
    }

    private View.OnClickListener onSwitchHeader() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHeaderConfOFF.getVisibility() == View.GONE &&
                     mHeaderConfON.getVisibility() == View.GONE) {
                }
                if (mHeaderConfOFF.getVisibility() == View.GONE) {
                    mHeaderConfOFF.setVisibility(View.VISIBLE);
                    mHeaderConfON.setVisibility(View.GONE);
                } else {
                    mHeaderConfOFF.setVisibility(View.GONE);
                    mHeaderConfON.setVisibility(View.VISIBLE);
                }
            }
        };
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

    public void                 showSnackbar(String txt, int color) {
        if (color == -1) {
            Snackbar.make(mCoordinatorLayout, txt, Toast.LENGTH_SHORT).show();
        }
        Snackbar snackbar = Snackbar.make(mCoordinatorLayout, txt, Snackbar.LENGTH_LONG);
        ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text))
                .setTextColor(color);
        snackbar.show();
    }

    public int                  getContentViewId() {
        return R.layout.activity_wireshark;
    }

}
