package fr.dao.app.View.Activity.Wireshark;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import fr.dao.app.View.Behavior.Activity.SniffActivity;
import fr.dao.app.View.Behavior.Fragment.MyFragment;
import fr.dao.app.View.Behavior.MyGlideLoader;
import fr.dao.app.View.Behavior.ViewAnimate;
import fr.dao.app.View.Widget.Fragment.PcapListerFragment;

public class                    WiresharkActivity extends SniffActivity {
    private String              TAG = this.getClass().getName();
    private WiresharkActivity   mInstance = this;
    private CoordinatorLayout   mCoordinatorLayout;
    private AppBarLayout        mAppBar;
    private Toolbar             mToolbar;
    private ProgressBar         mProgressBar;
    private Singleton           mSingleton = Singleton.getInstance();
    private MyFragment          mFragment = null;
    private ImageView           SwitchViewBackBtn;
    private boolean             readerFragment = false;
    protected void              onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wireshark);
        initXml();
        init(getIntent());
    }

    /**
     * /data/user/0/fr.dao.app/files/tcpdump -w /storage/emulated/0/Pcap/SFR-4328_19_mars_19h50m32.pcap ' ( host 192.168.0.24)'
     * /data/user/0/fr.dao.app/files/tcpdump -w /storage/emulated/0/Pcap/SFR-4328_19_mars_19h50m32.pcap ' ( host 192.168.0.24)'
     * /data/user/0/fr.dao.app/files/tcpdump -w /storage/emulated/0/Pcap/SFR-4328_19_mars_19h49m17.pcap ' ( host 192.168.0.24)'
     * @param intent
     */
    private void                init(Intent intent) {
        String PcapFilePath = intent  == null ? null : intent.getStringExtra("Pcap");
        if (PcapFilePath == null) {
            mFragment = new WiresharkLiveFragment();
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
            } else {
                showBottomBar();
                setToolbarTitle("Wireshark", (mSingleton.hostList == null) ? "0" : mSingleton.hostList.size() + " target");
            }
            initSettings();
            initNavigationBottomBar(SNIFFER, true);
            ViewAnimate.setVisibilityToVisibleQuick(mFab);
        } else {
            hideBottomBar();
            ViewAnimate.setVisibilityToGoneQuick(mFab);
            findViewById(R.id.navigation).setVisibility(View.GONE);
            readerFragment = true;
            mFragment = new WiresharkReaderFragment();
            Bundle bundle = new Bundle();
            bundle.putString("Pcap", PcapFilePath);
            mFragment.setArguments(bundle);
            setToolbarTitle(PcapFilePath.replace(mSingleton.PcapPath, "")
                    .replace("_", " ").replace(".pcap", ""),"Loading");
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
        mAppBar = findViewById(R.id.appbar);
        mToolbar = findViewById(R.id.toolbar);
        mFab =  findViewById(R.id.fab);
        mFab.setOnClickListener(onclickFab());
        MyGlideLoader.loadDrawableInImageView(this, R.drawable.ic_sniff_barbutton, (ImageView) findViewById(R.id.OsImg), true);
        SwitchViewBackBtn = findViewById(R.id.SwitchViewBackBtn);
        findViewById(R.id.history).setOnClickListener(onClickHistory());
        onSwitchViewClicked();
    }

    private void                onSwitchViewClicked() {
        SwitchViewBackBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!readerFragment) {
                    boolean isDashboard = ((WiresharkLiveFragment)mFragment).onSwitchView();
                    int res = isDashboard ? R.drawable.ic_flip_to_front_svg: R.drawable.ic_flip_to_back_black_svg;
                    MyGlideLoader.loadDrawableInImageView(mInstance, res, SwitchViewBackBtn, false);
                    Log.d(TAG, "swithed has " + ((isDashboard) ? "dashboard" : "Live packets" ));
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
            public void onClick(View v) {
                if (Tcpdump.isRunning()) {
                    showSnackbar("You cant read while sniffing", -1);
                } else {
                    PcapListerFragment.newInstance().show(getSupportFragmentManager(), "");

                }
                /*SniffSessionAdapter adapter = new SniffSessionAdapter(mInstance, DBSniffSession.getAllSniffSession());
                new RV_dialog(mInstance)
                        .setAdapter(adapter, true)
                        .setTitle("Sniffing sessions recorded")
                        .onPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).show();*/
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

    public void                 onTcpdumpstopped() {
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.GONE);
                mFab.setImageResource(R.drawable.ic_media_play);
            }
        });
    }

    public void                 onBackPressed() {
        super.onBackPressed();
    }
}
