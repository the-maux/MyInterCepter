package fr.allycs.app.View.Activity.Scan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import fr.allycs.app.Core.Configuration.Singleton;
import fr.allycs.app.Core.Configuration.Utils;
import fr.allycs.app.Core.Nmap.NmapControler;
import fr.allycs.app.Model.Target.Host;
import fr.allycs.app.R;
import fr.allycs.app.View.Behavior.Activity.SniffActivity;
import fr.allycs.app.View.Behavior.ViewAnimate;
import fr.allycs.app.View.Widget.Dialog.DialogQuestionWithInput;

public class                    NmapActivity extends SniffActivity {
    private String              TAG = "NmapActivity";
    private NmapActivity        mInstance = this;
    private Singleton           mSingleton = Singleton.getInstance();
    private CoordinatorLayout   mCoordinatorLayout;
    private NmapOutputFragment  nmapOutputFragment;
    private TextView            monitorHostTargeted, monitorNmapParam;
    private TextView            MonitorInoptionTheTarget;
    private RelativeLayout      mNmapConfEditorLayout, nmapConfLayout;
    private Toolbar             mToolbar;
    private List<Host>          mListHostSelected = new ArrayList<>();
    private TabLayout           mTabs;
    private ImageView           mSettingsMenu, mScript, mScanType;
    private NmapControler       nmapControler;
    private ProgressBar         mProgressBar;
    private boolean             isExternalTarget;

    protected void              onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        initXml();
        init();
    }

    private void                initXml() {
        mCoordinatorLayout = findViewById(R.id.Coordonitor);
        monitorHostTargeted = (EditText) findViewById(R.id.hostEditext);
        monitorNmapParam = (EditText) findViewById(R.id.nmapMonitorParameter);
        mTabs = findViewById(R.id.tabs);
        mToolbar = findViewById(R.id.toolbar);
        mProgressBar = findViewById(R.id.progressBar);
        mSettingsMenu = findViewById(R.id.settingsMenu);
        MonitorInoptionTheTarget = findViewById(R.id.targetMonitor);
        mScript = findViewById(R.id.script);
        mScanType = findViewById(R.id.scanType);
        mNmapConfEditorLayout = findViewById(R.id.nmapConfEditorLayout);
        nmapConfLayout = findViewById(R.id.nmapConfLayout);
        mFab = findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNmap();
            }
        });
        monitorNmapParam.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            public boolean      onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    startNmap();
                    return true;
                }
                return false;
            }
        });
    }

    private void                init() {
        nmapControler = new NmapControler(false);
        initFragment();
        mScript.setOnClickListener(onClickScript());
        mScanType.setOnClickListener(onClickTypeScript());
        if (mSingleton.selectedHostsList == null || mSingleton.selectedHostsList.isEmpty()) {
            MonitorInoptionTheTarget.setText("No target selected");
            isExternalTarget = true;
            askForExternalTarget();
        } else {
            isExternalTarget = false;
            mListHostSelected = mSingleton.selectedHostsList;
            initTabswithTargets(mListHostSelected);
            monitorNmapParam.setText(nmapControler.getNmapParamFromMenuItem(nmapControler.getMenuCommmands().get(0)));
            initUIWithTarget(mListHostSelected.get(0));
            ViewAnimate.setVisibilityToVisibleQuick(mFab);
        }
        initNavigationBottomBar(SCANNER, true);
    }

    private void                initFragment() {
        try {
            nmapOutputFragment = new NmapOutputFragment();
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

    private View.OnClickListener onClickTypeScript() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> list = nmapControler.getMenuCommmands();
                final CharSequence[] charSequenceItems = list.toArray(new CharSequence[list.size()]);
                int i = 0;
                for (; i < charSequenceItems.length; i++) {
                    if (charSequenceItems[i].toString().contains(nmapControler.getActualCmd()))
                        break;
                }
                new AlertDialog.Builder(mInstance)
                        .setSingleChoiceItems(charSequenceItems, i, null)
                        .setTitle("Type of scan")
                        .setIcon(R.drawable.ic_nmap_icon_tabbutton)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                String typeScan = charSequenceItems[((AlertDialog)dialog).getListView().getCheckedItemPosition()].toString();
                                Log.d(TAG, "NEW Nmap TypeScan:" + typeScan);
                                String param = nmapControler.getNmapParamFromMenuItem(typeScan);
                                Log.d(TAG, "NEW Nmap Param:" + param);
                                monitorNmapParam.setText(param);
                                nmapControler.setmActualItemMenu(typeScan);
                                if (param != null)
                                    setToolbarTitle(null, typeScan);
                            }
                        })
                        .show();
            }
        };
    }

    private View.OnClickListener    onClickScript() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        };
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
        setToolbarTitle("Nmap v6.40", nmapControler.getActualCmd());
        List<Host> TmpHost = new ArrayList<>();
        TmpHost.add(host);
        nmapControler.setHosts(TmpHost);
        MonitorInoptionTheTarget.setText(host.ip);
        monitorHostTargeted.setText(host.ip);
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

    public void                 startNmap() {
        Utils.vibrateDevice(mInstance);
        nmapControler.start(nmapOutputFragment, mProgressBar);
        mProgressBar.setVisibility(View.VISIBLE);
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
                ViewAnimate.setVisibilityToVisibleQuick(mFab);
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

    public int                  getContentViewId() {
        return R.layout.activity_nmap;
    }

    public void                 onBackPressed() {
        if (mSingleton.isSniffServiceActif(this)) {
             new AlertDialog.Builder(this)
                        .setTitle("Warning: Sniffing service is active")
                        .setMessage("You press ok we will terminate every process for you")
                        .setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mSingleton.closeEverySniffService(mInstance);
                                onBackPressed();
                            }
                        })
                        .setNegativeButton(getResources().getString(android.R.string.cancel), null)
                        .show();
        } else {
            super.onBackPressed();
        }
    }
}
