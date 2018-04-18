package fr.dao.app.View.WebTracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Configuration.Words;
import fr.dao.app.Core.Dnsmasq.DnsmasqControl;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.SniffActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Behavior.ViewAnimate;
import fr.dao.app.View.ZViewController.Adapter.DnsLogsAdapter;
import fr.dao.app.View.ZViewController.Adapter.DnsSpoofConfAdapter;
import fr.dao.app.View.ZViewController.Dialog.DialogQuestionWithInput;

//https://danielmiessler.com/study/bettercap/
public class TrackerActivity extends SniffActivity {
    private String                      TAG = "DnsActivity";
    private TrackerActivity mInstance = this;
    private CoordinatorLayout           mCoordinatorLayout;
    private AppBarLayout                appBarLayout;
    private Toolbar                     mToolbar;
    private SearchView                  mSearchView;
    private ImageButton                 mAction_add_host, mSettingsBtn;
    private TabLayout                   mTabs;
    private RecyclerView                mDnsSpoof_RV;
    private Singleton                   mSingleton = Singleton.getInstance();
    private DnsmasqControl              mDnsSpoof = mSingleton.getDnsControler();
    private DnsSpoofConfAdapter         mDnsSpoofAdapter;
    private DnsLogsAdapter              mDnsConsoleAdapter;
    private String                      NAME_CONF_MENU = "Domains intercepted:", NAME_LOGS_MENU = "Dnsmasq logs:";

    public void                         onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spysniff);
        initXml();
        init();
    }

    private void                        init() {
        initFab();
        initTabs();
        initRVConfiguration();
        initSearchView();
        initNavigationBottomBar(SCANNER, true);
    }

    private void                        initXml() {
        mCoordinatorLayout = findViewById(R.id.coordinatorLayout);
        mToolbar = findViewById(R.id.toolbar2);
        mSearchView = findViewById(R.id.searchView);
        mAction_add_host = findViewById(R.id.action_add_host);
        mSettingsBtn = findViewById(R.id.history);
        mFab = findViewById(R.id.fab);
        mDnsSpoof_RV = findViewById(R.id.dnsSpoof_RV);
        mTabs = findViewById(R.id.tabs);
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
        appBarLayout = findViewById(R.id.appBarLayout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(findViewById(R.id.topToolbar), 4);
                ViewCompat.setElevation(appBarLayout, 4);
            }
        });
    }

    private void                        initFab() {
//        ViewAnimate.setVisibilityToVisibleQuick(mFab);
        ViewAnimate.FabAnimateReveal(mInstance, mFab);
//        mFab.show();
        if (mSingleton.isDnsControlstarted()) {
            mFab.setImageResource(R.mipmap.ic_stop);
        } else {
            mFab.setImageResource(R.drawable.ic_media_play);
        }
        mFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Utils.vibrateDevice(mInstance);
                updateNotifications();
            }
        });
    }

    private void                        initTabs() {
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getText().toString()) {
                    case "GLOBAL":
                        //title.setText("GLOBAL");
                        initRVConfiguration();
                        break;
                    case "CREDENTIALS":
                        //title.setText("CREDENTIALS");
                        break;
                    case "RESSOURCES":
                        //title.setText("RESSOURCES");
                        break;
                }
            }
            public void onTabUnselected(TabLayout.Tab tab) {}
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void                        onAddHostDialog() {
        final DialogQuestionWithInput dialog = new DialogQuestionWithInput(mInstance)
                .setIcon(R.drawable.dns)
                .setTitle("Ajouter un title")
                .setHintToTILFirstQuestion("Domain")
                .setHintToEDFirstQuestion("Ex: google.com")
                .setHintToTILSecoundQuestion("Ip address")
                .setHintToEDSecoundQuestion("Ex: " + mSingleton.network.myIp);

        dialog.onPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
            }
        })
        .show();
    }

    private void                        initRVConfiguration() {
        //mDnsSpoofAdapter = new DnsSpoofConfAdapter(this, mDnsSpoof.getDnsConf().listDomainSpoofable);
        ViewAnimate.setVisibilityToVisibleQuick(mDnsSpoof_RV);
        mDnsSpoof_RV.setAdapter(mDnsSpoofAdapter);
        mDnsSpoof_RV.setHasFixedSize(true);
        mDnsSpoof_RV.setLayoutManager(new LinearLayoutManager(mInstance));

        mDnsConsoleAdapter = new DnsLogsAdapter(this, mDnsSpoof.mDnsLogs);
        mDnsSpoof.setRV_Adapter(mDnsConsoleAdapter);
    }

    private void                        initSearchView() {
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                return false;
            }
        });
    }

    public void                         setToolbarTitle(final String title, final String subtitle) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (title != null)
                    mToolbar.setTitle(title);
                if (subtitle != null)
                    mToolbar.setSubtitle(subtitle);
            }
        });
    }

    public void                         onBackPressed() {
        super.onBackPressed();
        //Check if sniffing was loading
    }

    public void                         onError(String error) {
        Snackbar.make(mCoordinatorLayout, error, Snackbar.LENGTH_SHORT).show();
        mInstance.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateNotifications();
                new AlertDialog.Builder(mInstance)
                        .setTitle("Sniffing error detected")
                        .setMessage("Would you like to restart the process ?")
                        .setPositiveButton(Words.yes(mInstance), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mDnsSpoof.start();
                            }
                        })
                        .setNegativeButton(Words.no(mInstance), null)
                        .show();
            }
        });
    }
}