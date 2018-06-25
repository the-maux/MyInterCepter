package fr.dao.app.View.Scan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import fr.dao.app.Core.Configuration.Singleton;
import fr.dao.app.Core.Configuration.Utils;
import fr.dao.app.Core.Database.DBHost;
import fr.dao.app.Core.Scan.NmapControler;
import fr.dao.app.Model.Config.NmapParam;
import fr.dao.app.Model.Target.Host;
import fr.dao.app.R;
import fr.dao.app.View.ZViewController.Activity.MyActivity;
import fr.dao.app.View.ZViewController.Behavior.MyGlideLoader;
import fr.dao.app.View.ZViewController.Behavior.ViewAnimate;
import fr.dao.app.View.ZViewController.Dialog.DialogQuestionWithInput;

public class                    NmapActivity extends MyActivity {
    private String              TAG = "NmapActivity";
    private NmapActivity        mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    protected FloatingActionButton mFab;
    private NmapOutputView      nmapOutputFragment;
    private AppBarLayout        appBarLayout;
    private Toolbar             mToolbar;
    private List<Host>          mListHostSelected = new ArrayList<>();
    private TabLayout           mTabs;
    private ImageView           mSettingsMenu, mScript, mScanType;
    private NmapControler       nmapControler;
    private ProgressBar         mProgressBar;
    private boolean             isExternalTarget = false, isInScriptMode = false;

    protected void              onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nmap);
        initXml();
    }

    protected void              onResume() {
        super.onResume();
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
        mFab.setOnClickListener(onClickFAB());
        appBarLayout = findViewById(R.id.appBar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                ViewCompat.setElevation(findViewById(R.id.relativeLayout), 2);
                ViewCompat.setElevation(appBarLayout, 4);
            }
        });
        mScript.setOnClickListener(onClickScript());
        mScanType.setOnClickListener(onClickTypeOfScan());
        MyGlideLoader.coordoBackgroundXMM(this, mCoordinatorLayout);
        findViewById(R.id.rootView).setBackgroundResource(android.R.drawable.alert_dark_frame);
        setStatusBarColor(R.color.NmapPrimary);
    }

    private void                init() {
        if (nmapControler == null)
            nmapControler = new NmapControler();
        initFragment();
        initHostBehavior();
    }

    private void                initHostBehavior() {
        isExternalTarget = false;
        if (getIntent() != null && getIntent().getExtras() != null) {//MODE: FOCUSED TARGETx
            bundle = getIntent().getExtras();
            if (bundle == null || bundle.getString("macAddress") == null) {
                Log.e(TAG, "No target selected");
                showSnackbar("No target selected");
            } else {
                mListHostSelected.add(DBHost.getDevicesFromMAC(bundle.getString("macAddress")));
                Log.d(TAG, "UNIQUE TARGET" + mListHostSelected.toString());
            }
        } else { //MODE: TARGET LIST {
            Log.d(TAG, "MODE: LIST OF TARGET:" + mListHostSelected.toString());
            mListHostSelected = mSingleton.hostList;
        }
        if (mListHostSelected.size() > 1) {
            mTabs.setVisibility(View.VISIBLE);
            initTabswithTargets(mListHostSelected);
        }
        initUIWithTarget(mListHostSelected.get(0));
        ViewAnimate.FabAnimateReveal(mInstance, mFab);
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

    public void                 initTabswithTargets(final List<Host> hosts) {
        mTabs.removeAllTabs();
        for (Host host : hosts) {
            TabLayout.Tab tabItem = mTabs.newTab();
            tabItem.setText(host.ip);
            mTabs.addTab(tabItem);
        }
        mTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            public void onTabSelected(TabLayout.Tab tab) {
                String ipHostSelected = tab.getText().toString().replace("\n", " ");
                Log.d(TAG, "onSelectedTab host :[" + ipHostSelected + "]");
                for (Host host : hosts) {
                    if (host.ip.contentEquals(ipHostSelected)) {
                        initUIWithTarget(host);
                        nmapOutputFragment.refresh(host);
                        break;
                    }
                }
            }
            public void onTabUnselected(TabLayout.Tab tab) {}
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    public void                 initUIWithTarget(Host host) {
        setToolbarTitle(host.getName(), nmapControler.getActualCmd());
        List<Host> TmpHost = new ArrayList<>();
        TmpHost.add(host);
        nmapControler.setHosts(TmpHost);
        nmapOutputFragment.refresh(host);
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

    private void                askForExternalTarget() {
        final DialogQuestionWithInput dialog = new DialogQuestionWithInput(this)
                .hideSecondInput()
                .setIcon(R.drawable.dns) //IMAGE HOST
                .setTitle("Choose your target")
                .setHintToEDFirstQuestion("")
                .setHintToTILFirstQuestion("");
        dialog.onPositiveButton("Ajouter", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface d, int which) {
                onAddExternal(dialog.getFirstInputQuestion());
            }
        }).show();
    }

    private void                onAddExternal(String externalHost) {
        int nbrPoint = StringUtils.countMatches(externalHost, ".");
        InetAddress address = null;
        if (nbrPoint == 3) {
            try {
                address = InetAddress.getByName(externalHost);
                String ip = address.getHostAddress();
                //TODO: what when we add someone
                List<String> listExternalIp = new ArrayList<>();
                //new NmapControler(listExternalIp);
                //ViewAnimate.setVisibilityToVisibleQuick(mFab);
                ViewAnimate.FabAnimateReveal(mInstance, mFab);
                //mFab.show();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                addExternalHostFailed(externalHost + ": Name or service unknow");
                return ;
            }
            addExternalHostFailed(externalHost + ": Name or service unknow");
        }
    }
    private void                addExternalHostFailed(String msg) {
        showSnackbar(msg);
        askForExternalTarget();
    }

    private View.OnClickListener onClickFAB() {
        return new View.OnClickListener() {
            public void onClick(View v) {
                Utils.vibrateDevice(mInstance);
                mProgressBar.setVisibility(View.VISIBLE);
                if (!isInScriptMode) {
                    if (!nmapControler.startScan(nmapOutputFragment, mProgressBar))
                       showSnackbar("Need to implemente the nmap scan");

                } else if (!nmapControler.startScan(nmapOutputFragment, mProgressBar))
                    showSnackbar("No target selected");
            }
        };
    }

    private View.OnClickListener onClickTypeOfScan() {
        return new View.OnClickListener() {
            public void onClick(View view) {
                final CharSequence[] cmdItems = nmapControler.getMenuCommmands().toArray(new CharSequence[nmapControler.getMenuCommmands().size()]);
                new AlertDialog.Builder(mInstance)
                        .setSingleChoiceItems(cmdItems, NmapParam.getFocusedScan(nmapControler.getActualCmd()), null)
                        .setTitle("Scan available")
                        .setIcon(R.drawable.ic_nmap_icon_tabbutton)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                isInScriptMode = false;
                                String typeScan = cmdItems[((AlertDialog)dialog).getListView().getCheckedItemPosition()].toString();
                                Log.d(TAG, "NEW Nmap TypeScan:" + typeScan);
                                String param = nmapControler.getParamOfScan(typeScan);
                                Log.d(TAG, "NEW Nmap Param:" + param);
                                nmapControler.setmActualScan(typeScan);
                                if (param != null)
                                    setToolbarTitle(null, typeScan);
                            }
                        }).show();
            }
        };
    }

    private View.OnClickListener onClickScript() {
        return new View.OnClickListener() {
            public void onClick(View view) {
                final CharSequence[] scriptItems = nmapControler.getMenuScripts().toArray(new CharSequence[nmapControler.getMenuScripts().size()]);
                new AlertDialog.Builder(mInstance)
                        .setSingleChoiceItems(scriptItems, NmapParam.getFocusedScript(nmapControler.getActualScript()), null)
                        .setTitle("Script available")
                        .setIcon(R.drawable.ic_nmap_icon_tabbutton)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                isInScriptMode = true;
                                dialog.dismiss();
                                String typeScan = scriptItems[((AlertDialog)dialog).getListView().getCheckedItemPosition()].toString();
                                Log.d(TAG, "NEW Nmap Script:" + typeScan);
                                String param = nmapControler.getParamOfScan(typeScan);
                                Log.d(TAG, "NEW Nmap Script Param:" + param);
                                nmapControler.setmActualScan(typeScan);
                                if (param != null)
                                    setToolbarTitle(null, typeScan);
                            }
                        }).show();
            }
        };
    }

    public void                 onBackPressed() {
            super.onBackPressed();
        //TODO; detect if command is still run
    }
}
