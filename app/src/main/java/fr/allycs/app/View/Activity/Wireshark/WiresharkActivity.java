package fr.allycs.app.View.Activity.Wireshark;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Core.Database.DBSniffSession;
import fr.allycs.app.Core.Tcpdump.Tcpdump;
import fr.allycs.app.R;
import fr.allycs.app.View.Behavior.Activity.SniffActivity;
import fr.allycs.app.View.Behavior.Fragment.MyFragment;
import fr.allycs.app.View.Behavior.MyGlideLoader;
import fr.allycs.app.View.Widget.Adapter.SniffSessionAdapter;
import fr.allycs.app.View.Widget.Dialog.RV_dialog;

public class                    WiresharkActivity extends SniffActivity {
    private String              TAG = this.getClass().getName();
    private WiresharkActivity   mInstance = this;
    private CoordinatorLayout   mCoordinatorLayout;
    private AppBarLayout        mAppBar;
    private Toolbar             mToolbar;
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
        setToolbarTitle("Wireshark", (mSingleton.selectedHostsList == null) ? "0":mSingleton.selectedHostsList.size() + " target");

    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
        mFrame_container = findViewById(R.id.frame_container);
        mProgressBar =  findViewById(R.id.progressBar);
        mAppBar = findViewById(R.id.appbar);
        mToolbar = findViewById(R.id.toolbar);
        mFab =  findViewById(R.id.fab);
        mFab.setOnClickListener(onclickFab());
        MyGlideLoader.loadDrawableInImageView(this, R.drawable.wireshark, (ImageView) findViewById(R.id.OsImg), true);
        findViewById(R.id.script).setOnClickListener(onClickHistory());
    }

    private void                initFragment(MyFragment fragment) {
        try {
            mFragment = (WiresharkFragment) fragment;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, mFragment)
                    //.addToBackStack(fragment.getClass().getName()) TANT KE PAS DE SETTINGS NO BACKSTACK
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
                if (mFragment.start()) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mFab.setImageResource(R.mipmap.ic_pause);
                } else {
                    onTcpdumpstopped();
                }
            }
        };
    }

    public void                 connectionSucceed() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }

    private void                initSettings() {
        final String            PCAP_DUMP = "Write in Pcap file",
                                SSLSTRIP_MODE = "SSLstrip Activated",
                                LOCKSCREEN = "Lockscreen Activated",
                                FILTERING = "Output filtering",
                                GLOBAL_SETTINGS = "Global settings";

        findViewById(R.id.settingsMenu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetBuilder builder = new BottomSheetBuilder(mInstance)
                            .setMode(BottomSheetBuilder.MODE_LIST)
                            .setBackgroundColor(ContextCompat.getColor(mInstance, R.color.material_light_white))
                            .setAppBarLayout(mAppBar)
                            .addTitleItem("Wireshark settings");
                            if (Tcpdump.getTcpdump(mInstance, true) != null)
                                builder.addItem(0, PCAP_DUMP, (Tcpdump.getTcpdump(mInstance, true).isDumpingInFile) ? R.drawable.ic_checkbox_marked_grey600_24dp: R.drawable.ic_checkbox_blank_outline_grey600_24dp);
                            else {
                                builder.addItem(0, PCAP_DUMP, R.drawable.ic_checkbox_marked_grey600_24dp);
                            }
                            builder.addItem(1, SSLSTRIP_MODE, (mSingleton.isSslstripMode()) ? R.drawable.ic_checkbox_marked_grey600_24dp: R.drawable.ic_checkbox_blank_outline_grey600_24dp )
                                .addItem(2, LOCKSCREEN, (mSingleton.isLockScreen()) ? R.drawable.ic_checkbox_marked_grey600_24dp: R.drawable.ic_checkbox_blank_outline_grey600_24dp )
                                .addDividerItem()
                                    .addItem(3, FILTERING, R.drawable.ic_my_icon_play)
                                    .addItem(4, GLOBAL_SETTINGS, R.mipmap.ic_settings_wireshark)
                                .setItemClickListener(new BottomSheetItemClickListener() {
                                    @Override
                                    public void onBottomSheetItemClick(MenuItem menuItem) {
                                        switch (menuItem.getTitle().toString()) {
                                            case FILTERING:
                                                //TODO: create dialog for dialog_wireshark_settings
                                                showSnackbar("FILTERING DIALOG to do", -1);
                                                break;
                                            case SSLSTRIP_MODE:
                                                showSnackbar("SSLSTRIP MODE to do", -1);
                                                break;
                                            case LOCKSCREEN:
                                                showSnackbar("Lockscreen settings to do", -1);
                                                break;
                                            case GLOBAL_SETTINGS:
                                                showSnackbar("Global settings to do", -1);
                                                break;
                                        }
                                    }
                                })
                                .expandOnStart(true)
                                .createDialog().show();
            }
        });
    }

    public void                 onError() {
        Log.d(TAG, "onError");
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateNotifications();
                setToolbarTitle(null, "Error in processing");
                showSnackbar("Error in Sniffing", ContextCompat.getColor(mInstance, R.color.stop_color));
                onTcpdumpstopped();
                new AlertDialog.Builder(mInstance)
                        .setTitle("Tcpdump error detected")
                        .setMessage("Would you like to restart the tcpdump process ?")
                        .setPositiveButton(getResources().getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mFragment.start();
                            }
                        })
                        .setNegativeButton(getResources().getString(android.R.string.no), null)
                        .show();
            }
        });
    }

    private View.OnClickListener onClickHistory() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SniffSessionAdapter adapter = new SniffSessionAdapter(mInstance, DBSniffSession.getAllSniffSession());
                new RV_dialog(mInstance)
                        .setAdapter(adapter, true)
                        .setTitle("Sniffing sessions recorded")
                        .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();
            }
        };
    }

    public void                 onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {
        if (requestCode == 0x42 && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "receiving Pcap");
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i(TAG, "Uri: " + uri.toString());
                Log.i(TAG, "Uri:EncodedPath:: " + uri.getEncodedPath());
                Log.i(TAG, "Uri:Path:: " + uri.getPath());
            }
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

    public void                 onTcpdumpstopped() {
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
                mFab.setImageResource(R.mipmap.ic_play);
            }
        });
    }

    public void                 onBackPressed() {
        super.onBackPressed();
    }
}
