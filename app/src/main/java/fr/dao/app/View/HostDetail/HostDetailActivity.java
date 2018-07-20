package fr.dao.app.View.HostDetail;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.IOException;
import java.net.InetAddress;
import java.security.InvalidParameterException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.dao.app.Core.Configuration.GlideApp;
import fr.dao.app.Core.Configuration.GlideRequest;
import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Core.Database.DBManager;
import fr.dao.app.Core.Scan.NmapControler;
import fr.dao.app.Model.Net.Pcap;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.Model.Target.State;
import fr.dao.app.R;
import fr.dao.app.View.DashBoard.HistoricSavedDataFgmnt;
import fr.dao.app.View.Scan.NmapActivity;
import fr.dao.app.View.Scan.VulnsScanActivity;
import fr.dao.app.View.Sniff.SniffActivity;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Behavior.ViewAnimate;
import fr.dao.app.View.ZViewController.Dialog.QuestionMultipleAnswerDialog;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;
import fr.dao.app.View.ZViewController.Fragment.PcapListerFragment;

public class                    HostDetailActivity extends MyActivity {
    private String              TAG = "HostDetailActivity";
    private HostDetailActivity  mInstance = this;
    private Host                mFocusedHost;
    private CoordinatorLayout   mCoordinator;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionMenu  mMenuFAB;
    private CircleImageView     osHostImage;
    private ProgressBar         mProgressBarDetail;
    private ImageView           history, rescan, settingsMenuDetail;
    private TabLayout           mTabs;
    private MyFragment          mCurrentFragment;
    private List<Pcap>          mPcapsList;
    private ImageView           collapsBackground;

    public enum                 actionActivity {
        NMAP(0), BLOCK_INTERNET(1), VULN_SCAN(2), SNIFFER(3);

        private final int value;
        private actionActivity(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hostdetail);
        mFocusedHost = DBHost.getDevicesFromMAC(getIntent().getExtras().getString("macAddress"));
        if (mFocusedHost == null) {
            showSnackbar("User can't be loaded from BDD");
        } else {
            initXml();
            MyGlideLoader.setOsIcon(mFocusedHost, osHostImage);
        }
    }

    private void                initXml() {
        mCoordinator = findViewById(R.id.Coordonitor);
        osHostImage = findViewById(R.id.OsImg);
        history = findViewById(R.id.history);
        settingsMenuDetail = findViewById(R.id.settingsMenuDetail);
        rescan = findViewById(R.id.rescan);
        mTabs  = findViewById(R.id.tabs);
        mMenuFAB = findViewById(R.id.fab_menu);
        collapsBackground = findViewById(R.id.collapsBackground);
        mProgressBarDetail = findViewById(R.id.progressBarDetail);
        printBackground();
        setStatusBarColor(android.R.color.transparent);
    }

    private void                printBackground() {
        int res = R.drawable.bg1;
        switch (mFocusedHost.osType) {
            case Ios:
            case Apple:
                res = R.drawable.bg1_mac;
                break;
            case Android:
            case Mobile:
            case Samsung:
            case Bluebird:
                res = R.drawable.bg1_android;
                break;
            case Windows:
            case WindowsXP:
            case Windows7_8_10:
            case Windows2000:
            case Cisco:
                res = R.drawable.bg1_windows;
                break;
            case Raspberry:
            case Linux_Unix:
            case Ps4:
            case QUANTA:
            case OpenBSD:
            case Unix:
                res = R.drawable.linux3;
                break;
        }
        GlideRequest r = GlideApp.with(mInstance)
                .load(res)
                .centerCrop()
                //.transition(DrawableTransitionOptions.withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        r.into(collapsBackground);
    }

    protected void              onResume() {
        super.onResume();
        init();
        ViewAnimate.setVisibilityToVisibleQuick(settingsMenuDetail, 800);
        ViewAnimate.setVisibilityToVisibleQuick(history, 500);
    }

    private void                init() {
        try {
            bundle = getIntent().getExtras();
            if (bundle == null)
                throw new InvalidParameterException("NO BUNDLE TRANSMITED");
            String mode = bundle.getString("mode");
            if (mode == null) {
                throw new InvalidParameterException("NO MODE TRANSMITED IN BUNDLE");
            } else if (mode.contains("Live")) {
                ViewAnimate.scaleUp(mInstance, mMenuFAB);
            } else if (mode.contains("Recorded")) {
                mMenuFAB.setVisibility(View.GONE);
            }
            initMenuFab();
            initTabs();
            initAppBar();
            refresh();
        } catch (Exception e) {
            Log.e(TAG, "Error in init, Back to previous fragment");
            e.printStackTrace();
            onBackPressed();
        }
    }

    private void                initMenuFab() {
        mMenuFAB.removeAllMenuButtons();
        mMenuFAB.addMenuButton(initMenuBtn("Sniffing", R.mipmap.ic_hearing, actionActivity.SNIFFER), 0);
        mMenuFAB.addMenuButton(initMenuBtn("Strip connection", R.mipmap.ic_cut_internet, actionActivity.BLOCK_INTERNET), 1);
        mMenuFAB.addMenuButton(initMenuBtn("Nmap", R.mipmap.ic_eye_nosvg, actionActivity.NMAP), 2);
        mMenuFAB.addMenuButton(initMenuBtn("Vulnerability Scanner", R.drawable.target_pad_30_white, actionActivity.VULN_SCAN), 3);
    }

    private FloatingActionButton initMenuBtn(String title, int logo, actionActivity type) {
        FloatingActionButton FabBtn = new FloatingActionButton(this);
        FabBtn.setButtonSize(FloatingActionButton.SIZE_MINI);
        FabBtn.setLabelText(title);
        FabBtn.setImageResource(logo);
        FabBtn.setColorNormal(getResources().getColor(R.color.fab_color));
        if (title.contentEquals("Vulnerability Scanner") || title.contentEquals("Strip connection"))
            FabBtn.setPadding(8, 8, 8, 8);
        else
           FabBtn.setPadding(4, 4, 4, 4);
        FabBtn.setColorPressed(getResources().getColor(R.color.generic_background));
        FabBtn.setOnClickListener(onItemMenuClicked(type, FabBtn));
        return FabBtn;
    }

    private View.OnClickListener onItemMenuClicked(final actionActivity classActivity, final FloatingActionButton fabBtn) {
        return new View.OnClickListener() {
            public void onClick(View view) {
                mMenuFAB.close(true);
                Intent intent = null;
                Pair<View, String> p1 = null;
                switch (classActivity) {
                    case NMAP:
                        intent = new Intent(mInstance, NmapActivity.class);
                        p1  = Pair.create((View)fabBtn, "LogoTransition");
                        intent.putExtra("macAddress", getIntent().getExtras().getString("macAddress"));
                        break;
                    case VULN_SCAN:
                        p1  = Pair.create((View)fabBtn, "VulnIconTransition");
                        intent = new Intent(mInstance, VulnsScanActivity.class);
                        intent.putExtra("macAddress", getIntent().getExtras().getString("macAddress"));
                        break;
                    case BLOCK_INTERNET:
                        intent = new Intent(mInstance, NmapActivity.class);
                        p1  = Pair.create((View)fabBtn, "LogoTransition");
                        intent.putExtra("macAddress", getIntent().getExtras().getString("macAddress"));
                        break;
                    case SNIFFER:
                        intent = new Intent(mInstance, SniffActivity.class);
                        p1  = Pair.create((View)fabBtn, "wiresharkIcon");
                        intent.putExtra("macAddress", getIntent().getExtras().getString("macAddress"));
                        break;
                }
                startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(mInstance, p1).toBundle());
            }
        };
    }

    private void                initAppBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        final ImageView osImg = findViewById(R.id.OsImg);
        collapsingToolbarLayout = findViewById(R.id.settings_menu_hostdetail);
        collapsingToolbarLayout.setTitle(mFocusedHost.getName());
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(collapsingToolbarLayout, 4);
            }
        });
        rescan.setOnClickListener(onRescanTarget());
    }

    private void                initTabs() {
        int rax = 0;
        mTabs.removeAllTabs();
        mTabs.addTab(mTabs.newTab().setText("Infos"), rax);
        mTabs.addTab(mTabs.newTab().setText("Historic"), ++rax);
        if (mFocusedHost.Notes != null && !mFocusedHost.Notes.isEmpty())
            mTabs.addTab(mTabs.newTab().setText("Notes"), ++rax);
        mPcapsList = DBManager.getListPcapFormHost(mFocusedHost);
        if (mPcapsList != null && !mPcapsList.isEmpty())
            mTabs.addTab(mTabs.newTab().setText("Pcap"), ++rax);
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "tab.getFirstInputQuestion().toString():" + tab.getText().toString());
                switch (tab.getText().toString().toLowerCase()) {
                    case "historic":
                        displayHistoric(mFocusedHost.mac);
                        break;
                    case "notes":
                        displayNotes(mFocusedHost.mac);
                        break;
                    case "pcap":
                        displayPcap(mFocusedHost.mac);
                        break;
                    case "infos":
                        displayInfosHost(mFocusedHost.mac);
                        break;
                    default:
                        showSnackbar("Not implemented");
                        break;
                }
            }
            public void onTabUnselected(TabLayout.Tab tab) {}
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void                displayNotes(String macOfHostFocused) {
        MyFragment fragment = new HostNotesFragment();
        Bundle args = new Bundle();
        args.putString("macAddress", macOfHostFocused);
        fragment.setArguments(args);
        initFragment(fragment);
    }

    private void                displayHistoric(String macOfHostFocused) {
        MyFragment fragment = new HistoricSavedDataFgmnt();
        Bundle args = new Bundle();
        args.putString("mode", HistoricSavedDataFgmnt.HOST_HISTORIC);
        args.putString("macAddress", macOfHostFocused);
        fragment.setArguments(args);
        initFragment(fragment);
    }

    private void                displayInfosHost(String macOfHostFocused) {
        MyFragment fragment = new HostDetailFragment();
        Bundle args = new Bundle();
        args.putString("macAddress", macOfHostFocused);
        fragment.setArguments(args);
        initFragment(fragment);
    }

    private View.OnClickListener onRescanTarget() {
        return new View.OnClickListener() {
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        final CharSequence[] items = new CharSequence[]{"Discrete", "Basic", "Advanced", "Brutal"};
                        new QuestionMultipleAnswerDialog(mInstance, items,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        dialog.dismiss();
                                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                        Log.d("SettingsDiscovery", "Type of scan: " + items[selectedPosition]);
                                        mFocusedHost.Deepest_Scan = selectedPosition;
                                        mProgressBarDetail.setVisibility(View.VISIBLE);
                                        new Thread(new Runnable() {
                                            public void run() {
                                                try {
                                                    if (InetAddress.getByName(mFocusedHost.ip).isReachable(null, 64, 10000)) {
                                                        new NmapControler(null, mInstance, mFocusedHost, false);
                                                        return;
                                                    } else
                                                        mFocusedHost.state = State.OFFLINE;
                                                } catch (IOException e) {
                                                    mFocusedHost.state = State.OFFLINE;
                                                }
                                                onHostScanned(false);
                                            }
                                            //TODO tu dois detecter quand le truc est hors ligne
                                        }).start();
                                    }
                                }, "Which Scan", 0, R.mipmap.ic_refresh_png);
                    }
                });
            }
        };
    }

    private void                refresh() {
        int res = R.color.offline_color;
        switch (mFocusedHost.state) {
            case OFFLINE:
                res = R.color.offline_color;
                break;
            case FILTERED:
                res = R.color.filtered_color;
                break;
            case ONLINE:
                res = R.color.online_color;
                break;
        }

        osHostImage.setBorderColor(ContextCompat.getColor(mInstance, res));
        displayInfosHost(mFocusedHost.mac);

    }

    /**
     * Si les port 8008 et 8009 sont ouvert c'est une chromecast
     *if 62078 its an iphone ? wtf
     * @param succeed
     */

    public void                 onHostScanned(boolean succeed) {
        if (succeed)
            runOnUiThread(new Runnable() {
                public void run() {
                    mProgressBarDetail.setVisibility(View.GONE);
                    refresh();
                    showSnackbar( mFocusedHost.getName() + " was updated");
                }
            });
        else{

        }
    }

    private void                displayPcap(String macOfHostFocused) {
        Snackbar.make(mCoordinator, "Affiche les Pcap Recorded for " + mFocusedHost.getName(), Snackbar.LENGTH_LONG).show();
        MyFragment fragment = new PcapListerFragment();
        Bundle args = new Bundle();
        args.putString("macAddress", macOfHostFocused);
        fragment.setArguments(args);
        initFragment(fragment);
    }

    private void                initFragment(MyFragment fragment) {
        try {
            mCurrentFragment = fragment;
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_container, fragment)
                    .commit();
        } catch (IllegalStateException e) {
            Log.w("Error MainActivity", "FragmentStack or FragmentManager corrupted");
            Snackbar.make(findViewById(R.id.Coordonitor), "Error in fragment", Snackbar.LENGTH_LONG).show();
            super.onBackPressed();
        }
    }

    public void                 setToolbarTitle(final String title, final String subtitle) {
        mInstance.runOnUiThread(new Runnable() {
            public void run() {
                if (title != null)
                    collapsingToolbarLayout.setTitle(title);
            }
        });
    }

    public void                 showSnackbar(String txt) {
        Snackbar.make(mCoordinator, txt, Toast.LENGTH_SHORT).show();
    }

    public void                 onBackPressed() {
        if (mCurrentFragment == null || mCurrentFragment.onBackPressed()) {
            settingsMenuDetail.setVisibility(View.GONE);
            history.setVisibility(View.GONE);
            if (collapsBackground != null)
                collapsBackground.setImageResource(0);
            Animation scaleDown = AnimationUtils.loadAnimation(mInstance, R.anim.fab_scale_down);
            scaleDown.setDuration(450);
            mMenuFAB.startAnimation(scaleDown);
            scaleDown.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationStart(Animation animation) {}
                public void onAnimationEnd(Animation animation) {
                    mMenuFAB.setVisibility(View.GONE);
                    onBackMe();
                }
                public void onAnimationRepeat(Animation animation) {}
            });
        }
    }

    protected void              onBackMe() {
        super.onBackPressed();
    }
}
