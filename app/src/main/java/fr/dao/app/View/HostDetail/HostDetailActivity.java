package fr.dao.app.View.HostDetail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.security.InvalidParameterException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import fr.dao.app.Core.Configuration.GlideApp;
import fr.dao.app.Core.Configuration.GlideRequest;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Core.Database.DBManager;
import fr.dao.app.Model.Net.Pcap;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.DashBoard.NetDiscoveryHistoricFrgmnt;
import fr.dao.app.View.Scan.NmapActivity;
import fr.dao.app.View.Scan.VulnsScanActivity;
import fr.dao.app.View.Sniff.SniffActivity;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Behavior.ViewAnimate;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;
import fr.dao.app.View.ZViewController.Fragment.PcapListerFragment;

public class                    HostDetailActivity extends MyActivity {
    private String              TAG = "HostDetailActivity";
    private HostDetailActivity  mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private Host                mFocusedHost;
    private CoordinatorLayout   mCoordinator;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionMenu  mMenuFAB;
    private CircleImageView     osHostImage;
    private ImageView           history, settingsMenuDetail;
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
        initXml();
    }

    private void                initXml() {
        mCoordinator = findViewById(R.id.Coordonitor);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinator);
        osHostImage = findViewById(R.id.OsImg);
        history = findViewById(R.id.history);
        settingsMenuDetail = findViewById(R.id.settingsMenuDetail);
        mTabs  = findViewById(R.id.tabs);
        mMenuFAB = findViewById(R.id.fab_menu);
    }

    protected void              onResume() {
        super.onResume();
        init();
    }

    protected void              onPostResume() {
        super.onPostResume();
        try {
            collapsBackground = findViewById(R.id.collapsBackground);
            collapsBackground.postDelayed(new Runnable() {
                public void run() {
                    try {
                        GlideRequest r = GlideApp.with(mInstance)
                                .load(R.drawable.bg1)
                                .centerCrop()
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                        r.into(collapsBackground);
                    } catch (IllegalArgumentException e) {
                        Log.e(TAG, "PostDelayed while Activity is destroyed");
                    }
                }
            }, 800);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
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
            mFocusedHost = DBHost.getDevicesFromMAC(bundle.getString("macAddress"));
            MyGlideLoader.setOsIcon(mFocusedHost, osHostImage);
            initMenuFab();
            initTabs();
            initAppBar();
            displayInfosHost(mFocusedHost.mac);
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
        FabBtn.setPadding(4, 4, 4, 4);
        FabBtn.setColorPressed(getResources().getColor(R.color.generic_background));
        FabBtn.setOnClickListener(onItemMenuClicked(type, FabBtn));
        return FabBtn;
    }

    private View.OnClickListener onItemMenuClicked(final actionActivity classActivity, final FloatingActionButton fabBtn) {
        return new View.OnClickListener() {
            public void onClick(View view) {
                Utils.vibrateDevice(mInstance, 100);
                mMenuFAB.close(true);
                Intent intent = null;
                Class classActivityToLaunch;
                Pair<View, String> p1 = null;
                switch (classActivity) {
                    case NMAP:
                        intent = new Intent(mInstance, NmapActivity.class);
                        p1  = Pair.create((View)fabBtn, "NmapIconTransition");
                        intent.putExtra("macAddress", getIntent().getExtras().getString("macAddress"));
                        break;
                    case VULN_SCAN:
                        p1  = Pair.create((View)fabBtn, "VulnIconTransition");
                        intent = new Intent(mInstance, VulnsScanActivity.class);
                        intent.putExtra("macAddress", getIntent().getExtras().getString("macAddress"));                        break;
                    case BLOCK_INTERNET:
                        intent = new Intent(mInstance, NmapActivity.class);
                        p1  = Pair.create((View)fabBtn, "NmapIconTransition");
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
                int alpha = appBarLayout.getTotalScrollRange() - Math.abs(verticalOffset);
                if (alpha < 40) {
                    if (osImg.getVisibility() == View.VISIBLE) {
                        osImg.animate()
                                .alpha(0.0f)
                                .setDuration(250)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        osImg.setVisibility(View.GONE);
                                    }
                                });
                    }
                } else {
                    if (osImg.getVisibility() == View.GONE)
                        osImg.animate()
                                .alpha(1.0f)
                                .setDuration(250)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        super.onAnimationEnd(animation);
                                        osImg.setVisibility(View.VISIBLE);
                                    }
                                });

                }
            }
        });
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
                Utils.vibrateDevice(mInstance, 100);
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
        MyFragment fragment = new NetDiscoveryHistoricFrgmnt();
        Bundle args = new Bundle();
        args.putString("mode", NetDiscoveryHistoricFrgmnt.HOST_HISTORIC);
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
            @Override
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
