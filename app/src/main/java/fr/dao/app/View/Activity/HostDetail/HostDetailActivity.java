package fr.dao.app.View.Activity.HostDetail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.List;

import fr.dao.app.Core.Configuration.GlideApp;
import fr.dao.app.Core.Configuration.GlideRequest;
import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Database.DBManager;
import fr.dao.app.Core.Nmap.Fingerprint;
import fr.dao.app.Model.Net.Pcap;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.Activity.HostDiscovery.FragmentHistoric;
import fr.dao.app.View.Activity.Scan.NmapActivity;
import fr.dao.app.View.Activity.Wireshark.WiresharkActivity;
import fr.dao.app.View.Behavior.Activity.MyActivity;
import fr.dao.app.View.Behavior.Fragment.MyFragment;
import fr.dao.app.View.Behavior.MyGlideLoader;

public class                    HostDetailActivity extends MyActivity {
    private String              TAG = "HostDetailActivity";
    private HostDetailActivity  mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private Host                mFocusedHost;
    private CoordinatorLayout   mCoordinator;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionMenu  mMenuFAB;
    private ImageView           osHostImage;
    private TabLayout           mTabs;
    private MyFragment          mCurrentFragment;
    private List<Pcap>          mPcapsList;

    public void                 onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        initXml();
    }

    private void                initXml() {
        mCoordinator = findViewById(R.id.Coordonitor);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinator);
        osHostImage = findViewById(R.id.OsImg);
//                CoordinatorLayout.LayoutParams params =
//                (CoordinatorLayout.LayoutParams) osHostImage.getLayoutParams();
//        osHostImage.requestLayout();
        mTabs  = findViewById(R.id.tabs);
        mMenuFAB = findViewById(R.id.fab_menu);
    }

    protected void              onResume() {
        super.onResume();
        init();
    }

    private void                init() {
        try {
            int position = getIntent().getExtras().getInt("position");
            mFocusedHost = mSingleton.hostList.get(position);
            Fingerprint.setOsIcon(this, mFocusedHost, osHostImage);
            initMenuFab();
            initTabs();
            initAppBar();
            displayInfosHost(position);
        } catch (Exception e) {
            Snackbar.make(findViewById(R.id.Coordonitor), "Vous n'avez selectionner aucune target", Snackbar.LENGTH_LONG).show();
            Log.e(TAG, "Error in init, Back to previous fragment");
            onBackPressed();
        }
    }

    private void                initMenuFab() {
        FloatingActionButton nmapFAB = new FloatingActionButton(this);
        FloatingActionButton fingerprintFAB = new FloatingActionButton(this);
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
                Intent intent = new Intent(mInstance, NmapActivity.class);
                intent.putExtra("position", getIntent().getExtras().getInt("position"));
                startActivity(intent);
            }
        });
        fingerprintFAB.setButtonSize(FloatingActionButton.SIZE_MINI);
        fingerprintFAB.setLabelText("Fingerprint");
        fingerprintFAB.setImageResource(R.mipmap.ic_fingerprint_nosvg);
        fingerprintFAB.setColorNormal(getResources().getColor(R.color.fab_color));
        fingerprintFAB.setPadding(4, 4, 4, 4);
        fingerprintFAB.setColorPressed(getResources().getColor(R.color.generic_background));
        fingerprintFAB.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Snackbar.make(findViewById(R.id.Coordonitor), "Not implemented yet", Snackbar.LENGTH_LONG).show();
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
                Intent intent = new Intent(mInstance, WiresharkActivity.class);
                startActivity(intent);
            }
        });
        mMenuFAB.addMenuButton(nmapFAB, 0);
        mMenuFAB.addMenuButton(sniffingFAB, 1);
        mMenuFAB.addMenuButton(fingerprintFAB, 2);
        mMenuFAB.addMenuButton(vulnerabilityScanner, 3);
    }

    private void                initAppBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ImageView collapsBackground = findViewById(R.id.collapsBackground);
        GlideRequest r = GlideApp.with(this)
                .load(R.drawable.bg1)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);
        r.into(collapsBackground);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        final ImageView osImg = findViewById(R.id.OsImg);
        collapsingToolbarLayout = findViewById(R.id.settings_menu_hostdetail);
        collapsingToolbarLayout.setTitle(mFocusedHost.getName());
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int alpha = appBarLayout.getTotalScrollRange() - Math.abs(verticalOffset);
                if (alpha < 40) {
                    if (osImg.getVisibility() == View.VISIBLE)
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
                        displayHistoric(getIntent().getExtras().getInt("position"));
                        break;
                    case "notes":
                        displayNotes(getIntent().getExtras().getInt("position"));
                        break;
                    case "pcap":
                        displayPcap();
                        break;
                    case "infos":
                        displayInfosHost(getIntent().getExtras().getInt("position"));
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

    private void                displayNotes(int position) {
        MyFragment fragment = new HostNotesFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        fragment.setArguments(args);
        initFragment(fragment);
    }

    private void                displayHistoric(int position) {
        MyFragment fragment = new FragmentHistoric();
        Bundle args = new Bundle();
        args.putString("mode", FragmentHistoric.HOST_HISTORIC);
        args.putInt("position", position);
        fragment.setArguments(args);
        initFragment(fragment);
    }

    private void                displayInfosHost(int position) {
        MyFragment fragment = new HostDetailFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
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
//                if (subtitle != null)
//                    mToolbar.setSubtitle(subtitle);
            }
        });
    }

    public void                 showSnackbar(String txt) {
        Snackbar.make(mCoordinator, txt, Toast.LENGTH_SHORT).show();
    }

    public void                 onBackPressed() {
        if (mCurrentFragment == null || mCurrentFragment.onBackPressed()) {
            super.onBackPressed();
        }
    }

}
