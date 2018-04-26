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
import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Core.Database.DBManager;
import fr.dao.app.Model.Net.Pcap;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.DashBoard.NetDiscoveryHistoricFrgmnt;
import fr.dao.app.View.Scan.NmapActivity;
import fr.dao.app.View.Sniff.SniffActivity;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Behavior.ViewAnimate;
import fr.dao.app.View.ZViewController.Fragment.MyFragment;

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
                    GlideRequest r = GlideApp.with(mInstance)
                            .load(R.drawable.bg1)
                            .centerCrop()
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
                    r.into(collapsBackground);
                }
            }, 800);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        ViewAnimate.setVisibilityToVisibleQuick(settingsMenuDetail, 800);
        ViewAnimate.setVisibilityToVisibleQuick(history, 500);
    }

    private void                init() {
        Bundle bundle = getIntent().getExtras();
        try {
            if (bundle == null)
                throw new InvalidParameterException("NO BUNDLE TRANSMITED");
            String mode = bundle.getString("mode");
            if (mode == null) {
                throw new InvalidParameterException("NO MODE TRANSMITED IN BUNDLE");
            } else if (mode.contains("Live")) {
                Animation scaleUp = AnimationUtils.loadAnimation(this, R.anim.fab_scale_up);
                scaleUp.setDuration(1250);
                scaleUp.setAnimationListener(new Animation.AnimationListener() {
                    public void onAnimationStart(Animation animation) {
                        mMenuFAB.setVisibility(View.VISIBLE);
                    }
                    public void onAnimationEnd(Animation animation) {}
                    public void onAnimationRepeat(Animation animation) {}
                });
                mMenuFAB.startAnimation(scaleUp);
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
        FloatingActionButton nmapFAB = new FloatingActionButton(this);
        FloatingActionButton cutInternet = new FloatingActionButton(this);
        FloatingActionButton vulnerabilityScanner = new FloatingActionButton(this);
        FloatingActionButton sniffingFAB = new FloatingActionButton(this);

        nmapFAB.setButtonSize(FloatingActionButton.SIZE_MINI);
        nmapFAB.setLabelText("Nmap");
        nmapFAB.setImageResource(R.mipmap.ic_eye_nosvg);
        nmapFAB.setColorNormal(getResources().getColor(R.color.fab_color));
        nmapFAB.setPadding(4, 4, 4, 4);
        nmapFAB.setColorPressed(getResources().getColor(R.color.generic_background));
        nmapFAB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mMenuFAB.close(true);
                Intent intent = new Intent(mInstance, NmapActivity.class);
                intent.putExtra("macAddress", getIntent().getExtras().getString("macAddress"));
                startActivity(intent);
            }
        });
        cutInternet.setButtonSize(FloatingActionButton.SIZE_MINI);
        cutInternet.setLabelText("Strip connection");
        cutInternet.setImageResource(R.mipmap.ic_cut_internet);
        cutInternet.setColorNormal(getResources().getColor(R.color.fab_color));
        cutInternet.setPadding(4, 4, 4, 4);
        cutInternet.setColorPressed(getResources().getColor(R.color.generic_background));
        cutInternet.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Snackbar.make(findViewById(R.id.Coordonitor), "Not implemented yet", Snackbar.LENGTH_LONG).show();
                mMenuFAB.close(true);
            }
        });
        vulnerabilityScanner.setButtonSize(FloatingActionButton.SIZE_MINI);
        vulnerabilityScanner.setLabelText("Vulnerability Scanner");
        vulnerabilityScanner.setImageResource(R.drawable.ic_loop_search);
        vulnerabilityScanner.setColorNormal(getResources().getColor(R.color.fab_color));
        vulnerabilityScanner.setColorPressed(getResources().getColor(R.color.generic_background));
        vulnerabilityScanner.setPadding(4, 4, 4, 4);
        vulnerabilityScanner.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Snackbar.make(findViewById(R.id.Coordonitor), "Not implemented yet", Snackbar.LENGTH_LONG).show();
                mMenuFAB.close(true);
            }
        });
        sniffingFAB.setButtonSize(FloatingActionButton.SIZE_MINI);
        sniffingFAB.setLabelText("Sniffing");
        sniffingFAB.setImageResource(R.mipmap.ic_hearing);
        sniffingFAB.setPadding(4, 4, 4, 4);
        sniffingFAB.setColorNormal(getResources().getColor(R.color.fab_color));
        sniffingFAB.setColorPressed(getResources().getColor(R.color.generic_background));
        sniffingFAB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mMenuFAB.close(true);
                Intent intent = new Intent(mInstance, SniffActivity.class);
                Log.d(TAG, "Sending mac[" + getIntent().getExtras().getString("macAddress") + "]");
                intent.putExtra("macAddress", getIntent().getExtras().getString("macAddress"));
                startActivity(intent);
            }
        });
        mMenuFAB.addMenuButton(nmapFAB, 0);
        mMenuFAB.addMenuButton(sniffingFAB, 1);
        mMenuFAB.addMenuButton(cutInternet, 2);
        mMenuFAB.addMenuButton(vulnerabilityScanner, 3);
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
                Log.d(TAG, "tab.getFirstInputQuestion().toString():" + tab.getText().toString());
                switch (tab.getText().toString().toLowerCase()) {
                    case "historic":
                        displayHistoric(mFocusedHost.mac);
                        break;
                    case "notes":
                        displayNotes(mFocusedHost.mac);
                        break;
                    case "pcap":
                        displayPcap();
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

    private void                displayPcap() {
        List<Pcap> pcapList = DBManager.getListPcapFormHost(mFocusedHost);
        if (pcapList == null || pcapList.isEmpty())
            Snackbar.make(mCoordinator, "No Pcap Recorded for " + mFocusedHost.getName(), Snackbar.LENGTH_LONG).show();
        else {
            //TODO: faire l'adapter de pcap
        }
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
                }
                public void onAnimationRepeat(Animation animation) {}
            });
            onBackMe();

        }
    }

    protected void              onBackMe() {
        super.onBackPressed();
    }
}
