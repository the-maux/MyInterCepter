package fr.dao.app.View.Sniff;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.adapter.BottomSheetItemClickListener;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Core.Tcpdump.Tcpdump;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MITMActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Behavior.ViewAnimate;
import fr.dao.app.View.ZViewController.Dialog.PcapListerDialogFragment;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

public class                    SniffActivity extends MITMActivity {
    private String              TAG = "SniffActivity";
    private SniffActivity       mInstance = this;
    private CoordinatorLayout   mCoordinatorLayout;
    private AppBarLayout        mAppBar;
    private Toolbar             mToolbar;
    private ProgressBar         mProgressBar;
    private Singleton           mSingleton = Singleton.getInstance();
    private MyFragment          mFragment = null;
    private ImageButton         SwitchViewBackBtn;
    private boolean             readerFragment = false;

    /**
     * KAN STOP LE VOYANT RESTE VERT ET LE TIMER CONTINUE + Le layout header est décalé vers le bas
     * Pk lest Notes ne s'inscrive plus ?
     * @param savedInstanceState
     */
    protected void              onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sniffer);
        initXml();
        init(getIntent());
    }

    private void                init(Intent intent) {
        String PcapFilePath = intent  == null ? null : intent.getStringExtra("Pcap");
        if (PcapFilePath == null) {
            mFragment = new SniffLiveFrgmnt();
            hideBottomBar();
            if (intent != null && intent.getStringExtra("macAddress") != null) {// FROM HOSTDETAILACTIVITY
                int position = DBHost.getPositionFromMacaddress(mSingleton.hostList, intent.getStringExtra("macAddress"));
                Bundle args = new Bundle();
                args.putInt("position", position);
                mFragment.setArguments(args);
                setToolbarTitle("Sniffer", mSingleton.hostList.get(position).getName());
            } else if (getIntent() != null && getIntent().getExtras() != null &&
                    getIntent().getExtras().getInt("position", -1) != -1) {//MODE: FROM MITM STATION SINGLE TARGET
                int position = getIntent().getExtras().getInt("position", 0);
                Bundle args = new Bundle();
                args.putInt("position", position);
                mFragment.setArguments(args);
                setToolbarTitle("Sniffer", mSingleton.hostList.get(position).getName());
                showBottomBar();
            } else {
                showBottomBar();
                setToolbarTitle("Sniffer", (mSingleton.hostList == null) ? "0" : mSingleton.hostList.size() + " target");
            }
            initSettings();
            initNavigationBottomBar(SNIFFER);
            ViewAnimate.FabAnimateReveal(mInstance, mFab);
        } else {
            Log.d(TAG, "Pcap Reading mode activated");
            hideBottomBar();
            ViewAnimate.FabAnimateHide(mInstance, mFab);
            findViewById(R.id.navigation).setVisibility(View.GONE);
            readerFragment = true;
            mFragment = new SniffReaderFrgmnt();
            Bundle bundle = new Bundle();
            bundle.putString("Pcap", PcapFilePath);
            mFragment.setArguments(bundle);
            String Title = PcapFilePath.replace(mSingleton.Settings.PcapPath, "")
                    .replace("_", " ").replace(".pcap", "");
            String subtitle = Title.substring(PcapFilePath.replace(mSingleton.Settings.PcapPath, "").indexOf("_")+1, Title.length());//TODO: better cut than _, maybe ___ :p ?
            Title = PcapFilePath.replace(mSingleton.Settings.PcapPath, "").substring(0, PcapFilePath.replace(mSingleton.Settings.PcapPath, "").indexOf("_"));
            setToolbarTitle(Title,subtitle);
        }
        if (!readerFragment)
            ViewAnimate.setVisibilityToVisibleQuick(SwitchViewBackBtn);
        initFragment(mFragment);
    }

    protected void              onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onIntent Pcap::" + intent.getStringExtra("Pcap"));
        init(intent);
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
        //mFrame_container = findViewById(R.id.frame_container);
        mProgressBar =  findViewById(R.id.progressBar);
        mAppBar = findViewById(R.id.appBarLayout);
        mToolbar = findViewById(R.id.toolbar);
        mFab =  findViewById(R.id.fab);
        mFab.setOnClickListener(onclickFab());
        MyGlideLoader.loadDrawableInImageView(this, R.drawable.ic_sniff_barbutton, (ImageView) findViewById(R.id.OsImg), true);
        SwitchViewBackBtn = findViewById(R.id.SwitchViewBackBtn);
        findViewById(R.id.history).setOnClickListener(onClickPcaps());
        onSwitchViewClicked();
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(findViewById(R.id.appBarConstraint), 2);
                ViewCompat.setElevation(mAppBar, 4);
            }
        });
    }

    private void                onSwitchViewClicked() {
        SwitchViewBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!readerFragment) {
                    boolean isDashboard = ((SniffLiveFrgmnt)mFragment).onSwitchView();
                    int res = isDashboard ? R.drawable.ic_flip_to_front_svg: R.drawable.ic_flip_to_back_black_svg;
                    MyGlideLoader.loadDrawableInImageView(mInstance, res, SwitchViewBackBtn, false);
                    Log.d(TAG, "swithed has " + ((isDashboard) ? "dashboard" : "Live packets" ));
                    ((SniffLiveFrgmnt)mFragment).switchOutputType(isDashboard);
                } else {
                    Log.d(TAG, "not in readerFragment");
                }
            }
        });
    }

    private void                initFragment(MyFragment fragment) {
        try {
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

    private View.OnClickListener onClickPcaps() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                if (Tcpdump.isRunning()) {
                    showSnackbar("You cant load pcap while sniffing", -1);
                } else {
                    PcapListerDialogFragment.newInstance().show(getSupportFragmentManager(), "");
                }
            }
        };
    }

    public void                 setToolbarTitle(final String title, final String subtitle) {
        mInstance.runOnUiThread(new Runnable() {
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

    public void                 onTcpdumpstopped() {
        mInstance.runOnUiThread(new Runnable() {
            public void run() {
                mProgressBar.setVisibility(View.GONE);
                mFab.setImageResource(R.drawable.ic_media_play);

            }
        });
    }

    public void                 onBackPressed() {
        super.onBackPressed();
    }

    public void                 ReadingMode() {
    }
}
